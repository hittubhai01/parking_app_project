from flask import Blueprint, request, jsonify
from .models import ParkingSession, UserVehicle, ParkingLotDetails, Slot, Floor, Row
from . import db, ma
from flask_jwt_extended import jwt_required, get_jwt_identity
from marshmallow import post_load, validates, ValidationError
from .config import setup_logging
import logging
import uuid
from datetime import datetime
import random
import string

# Setup logging
setup_logging()
logger = logging.getLogger(__name__)

# Create blueprint
sessions_bp = Blueprint('sessions', __name__, url_prefix='/user/sessions')

# Marshmallow Schema for Session Check-in
class SessionCheckInSchema(ma.Schema):
    vehicle_id = ma.Int(required=True)
    parkinglot_id = ma.Raw(required=True)  # Accept both string and int

    @validates('vehicle_id')
    def validate_vehicle_id(self, value):
        if not value or value <= 0:
            raise ValidationError('Valid vehicle_id is required')
        return value

    @validates('parkinglot_id')
    def validate_parkinglot_id(self, value):
        # Convert string to int if needed
        if isinstance(value, str):
            try:
                value = int(value)
            except ValueError:
                raise ValidationError('parkinglot_id must be a valid integer')
        
        if not value or value <= 0:
            raise ValidationError('Valid parkinglot_id is required')
        return value

    @post_load
    def convert_parkinglot_id(self, data, **kwargs):
        """Ensure parkinglot_id is an integer"""
        if 'parkinglot_id' in data and isinstance(data['parkinglot_id'], str):
            try:
                data['parkinglot_id'] = int(data['parkinglot_id'])
            except ValueError:
                pass  # Will be caught by validation
        return data

# Marshmallow Schema for Session Check-out
class SessionCheckOutSchema(ma.Schema):
    ticket_id = ma.Str(required=True)
    payment_method = ma.Str(missing='card')

    @validates('ticket_id')
    def validate_ticket_id(self, value):
        if not value or len(value.strip()) == 0:
            raise ValidationError('ticket_id is required')
        return value.strip()

    @validates('payment_method')
    def validate_payment_method(self, value):
        if value is not None:
            allowed_methods = ['card', 'upi', 'cash', 'wallet']
            if value.lower() not in allowed_methods:
                raise ValidationError(f'Payment method must be one of: {", ".join(allowed_methods)}')
        return value.lower() if value else 'card'

# Initialize schema instances
session_checkin_schema = SessionCheckInSchema()
session_checkout_schema = SessionCheckOutSchema()

def generate_ticket_id():
    """Generate a unique ticket ID"""
    timestamp = datetime.utcnow().strftime('%Y%m%d%H%M%S')
    random_suffix = ''.join(random.choices(string.ascii_uppercase + string.digits, k=6))
    return f"TKT{timestamp}{random_suffix}"

def allocate_parking_slot(parkinglot_id, vehicle_type='car'):
    """
    Allocate an available parking slot in the specified parking lot
    Returns the allocated slot or None if no slots available
    """
    try:
        # Find available slots in the parking lot
        available_slot = db.session.query(Slot).filter(
            Slot.parkinglot_id == parkinglot_id,
            Slot.status == 0  # 0 = free, 1 = occupied
        ).first()
        
        if available_slot:
            # Mark slot as occupied
            available_slot.status = 1
            db.session.flush()  # Flush to get the changes without committing
            return available_slot
        
        return None
    except Exception as e:
        logger.error(f"Error allocating parking slot: {str(e)}")
        return None

def free_parking_slot(slot_id):
    """
    Free up a parking slot by setting its status to available
    """
    try:
        slot = Slot.query.get(slot_id)
        if slot:
            slot.status = 0  # 0 = free
            slot.vehicle_reg_no = None
            slot.ticket_id = None
            db.session.flush()
            return True
        return False
    except Exception as e:
        logger.error(f"Error freeing parking slot {slot_id}: {str(e)}")
        return False

def get_parking_lot_rates(parking_lot):
    """
    Extract parking rates from parking lot data
    Returns hourly rate for the vehicle type
    """
    try:
        # Parse car parking charge - format might be "First hour: €2.50, Each additional hour: €1.50"
        if parking_lot.car_parking_charge:
            # Simple parsing - extract first number as hourly rate
            import re
            rates = re.findall(r'€?(\d+\.?\d*)', parking_lot.car_parking_charge)
            if rates:
                return float(rates[0])
        
        # Default rate if parsing fails
        return 2.50
    except Exception as e:
        logger.error(f"Error parsing parking rates: {str(e)}")
        return 2.50

def calculate_parking_cost(duration_hours, hourly_rate, daily_max=None):
    """
    Calculate parking cost based on duration and rates
    """
    try:
        # Round up to next hour for billing
        import math
        billing_hours = math.ceil(duration_hours)
        
        # Calculate cost
        total_cost = billing_hours * hourly_rate
        
        # Apply daily maximum if specified
        if daily_max and total_cost > daily_max:
            total_cost = daily_max
        
        return round(total_cost, 2)
    except Exception as e:
        logger.error(f"Error calculating parking cost: {str(e)}")
        return 0.0

def process_payment(user_id, amount, payment_method='card'):
    """
    Mock payment processing - in real implementation, integrate with payment gateway
    """
    try:
        # Mock payment processing
        payment_result = {
            'success': True,
            'method': payment_method,
            'transaction_id': f"TXN{datetime.utcnow().strftime('%Y%m%d%H%M%S')}{random.randint(1000, 9999)}",
            'receipt_url': f"/receipts/{user_id}/{datetime.utcnow().strftime('%Y%m%d')}"
        }
        
        logger.info(f"Mock payment processed for user {user_id}: {amount} via {payment_method}")
        return payment_result
    except Exception as e:
        logger.error(f"Payment processing failed: {str(e)}")
        return {'success': False, 'error': str(e)}

@sessions_bp.route('/check-in', methods=['POST'])
@jwt_required()
def session_checkin():
    """
    Start a new parking session with vehicle
    ---
    tags:
      - Session Management
    security:
      - JWT: []
    parameters:
      - in: body
        name: session
        description: Session check-in information
        required: true
        schema:
          type: object
          required:
            - vehicle_id
            - parkinglot_id
          properties:
            vehicle_id:
              type: integer
              example: 1
            parkinglot_id:
              type: integer
              example: 1
    responses:
      201:
        description: Session created successfully
        schema:
          type: object
          properties:
            success:
              type: boolean
            data:
              type: object
              properties:
                ticket_id:
                  type: string
                session_id:
                  type: string
                parking_lot_name:
                  type: string
                slot_location:
                  type: object
                start_time:
                  type: string
                vehicle_info:
                  type: object
                status:
                  type: string
      400:
        description: Validation error
      404:
        description: Vehicle or parking lot not found
      409:
        description: Vehicle already has active session or no available slots
    """
    try:
        user_id = get_jwt_identity()
        
        # Handle malformed JSON or empty request
        try:
            data = request.get_json(force=True)
        except Exception as e:
            # Check if it's truly empty vs malformed
            if not request.data or request.data.strip() == b'':
                logger.warning(f"Empty request body in check-in")
                return jsonify({
                    "success": False,
                    "error": "No data provided"
                }), 400
            else:
                logger.warning(f"Malformed JSON in check-in request: {str(e)}")
                return jsonify({
                    "success": False,
                    "error": "Invalid JSON format"
                }), 400
        
        if not data:
            return jsonify({
                "success": False,
                "error": "No data provided"
            }), 400
        
        logger.info(f"Session check-in request for user {user_id}: {data}")
        
        # Validate input data
        try:
            validated_data = session_checkin_schema.load(data)
        except ValidationError as err:
            logger.warning(f"Validation error for user {user_id}: {err.messages}")
            return jsonify({
                "success": False,
                "error": "Validation failed",
                "details": err.messages
            }), 400
        
        # Verify vehicle ownership
        vehicle = UserVehicle.query.filter_by(
            vehicle_id=validated_data['vehicle_id'],
            user_id=user_id,
            is_active=True
        ).first()
        
        if not vehicle:
            logger.warning(f"Vehicle {validated_data['vehicle_id']} not found for user {user_id}")
            return jsonify({
                "success": False,
                "error": "Vehicle not found"
            }), 404
        
        # Verify parking lot exists
        parking_lot = ParkingLotDetails.query.get(validated_data['parkinglot_id'])
        if not parking_lot:
            logger.warning(f"Parking lot {validated_data['parkinglot_id']} not found")
            return jsonify({
                "success": False,
                "error": "Parking lot not found"
            }), 404
        
        # Check for existing active session for this vehicle
        active_session = ParkingSession.query.filter_by(
            user_id=user_id,
            vehicle_id=validated_data['vehicle_id'],
            session_status='active'
        ).first()
        
        if active_session:
            logger.warning(f"Vehicle {validated_data['vehicle_id']} already has active session {active_session.ticket_id}")
            return jsonify({
                "success": False,
                "error": "Vehicle already has an active parking session"
            }), 409
        
        # Allocate parking slot
        available_slot = allocate_parking_slot(validated_data['parkinglot_id'], vehicle.vehicle_type)
        if not available_slot:
            logger.warning(f"No available parking slots in lot {validated_data['parkinglot_id']}")
            return jsonify({
                "success": False,
                "error": "No available parking slots"
            }), 409
        
        # Generate ticket ID
        ticket_id = generate_ticket_id()
        
        # Create session
        session = ParkingSession(
            ticket_id=ticket_id,
            user_id=user_id,
            vehicle_id=validated_data['vehicle_id'],
            parkinglot_id=validated_data['parkinglot_id'],
            slot_id=available_slot.id,
            floor_id=available_slot.floor_id,
            row_id=available_slot.row_id,
            vehicle_reg_no=vehicle.registration_number,
            vehicle_type=vehicle.vehicle_type,
            start_time=datetime.utcnow(),
            session_status='active',
            payment_status='pending'
        )
        
        # Update slot with session info
        available_slot.vehicle_reg_no = vehicle.registration_number
        available_slot.ticket_id = ticket_id
        
        db.session.add(session)
        db.session.commit()
        
        # Get slot location info
        floor = Floor.query.get(available_slot.floor_id) if available_slot.floor_id else None
        row = Row.query.get(available_slot.row_id) if available_slot.row_id else None
        
        slot_location = {
            "slotId": available_slot.id,
            "floorId": available_slot.floor_id,
            "rowId": available_slot.row_id,
            "floorName": floor.name if floor else "Ground Floor",
            "rowName": row.name if row else "A",
            "slotName": available_slot.name
        }
        
        logger.info(f"Session {ticket_id} created successfully for user {user_id}, vehicle {vehicle.vehicle_id}")
        
        # Create response with snake_case for consistency
        session_data = {
            "ticket_id": session.ticket_id,
            "user_id": session.user_id,
            "vehicle_id": session.vehicle_id,
            "parkinglot_id": session.parkinglot_id,
            "parking_lot_name": parking_lot.name,
            "parking_lot_address": parking_lot.address,
            "vehicle_reg_no": session.vehicle_reg_no,
            "vehicle_type": session.vehicle_type,
            "start_time": session.start_time.isoformat(),
            "end_time": None,
            "duration_hrs": 0.0,
            "total_amount": 0.0,
            "payment_status": session.payment_status,
            "payment_method": None,
            "receipt_url": None,
            "status": session.session_status,
            "slot_location": slot_location,
            "vehicle_info": {
                "registration_number": vehicle.registration_number,
                "vehicle_name": vehicle.vehicle_name,
                "vehicle_type": vehicle.vehicle_type,
                "make": vehicle.make,
                "model": vehicle.model
            }
        }
        
        return jsonify({
            "success": True,
            "data": session_data,
            "message": "Parking session started successfully"
        }), 201
        
    except Exception as e:
        db.session.rollback()
        logger.error(f"Error creating session for user {user_id}: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to start parking session"
        }), 500

@sessions_bp.route('/checkout', methods=['POST'])
@jwt_required()
def session_checkout():
    """
    End parking session and process payment
    ---
    tags:
      - Session Management
    security:
      - JWT: []
    parameters:
      - in: body
        name: checkout
        description: Session checkout information
        required: true
        schema:
          type: object
          required:
            - ticket_id
          properties:
            ticket_id:
              type: string
              example: "TKT20251028045557YG7JSE"
            payment_method:
              type: string
              example: "card"
              enum: ["card", "upi", "cash", "wallet"]
    responses:
      200:
        description: Session ended successfully
        schema:
          type: object
          properties:
            success:
              type: boolean
            data:
              type: object
              properties:
                ticket_id:
                  type: string
                start_time:
                  type: string
                end_time:
                  type: string
                duration:
                  type: string
                total_amount:
                  type: number
                payment_status:
                  type: string
                receipt_url:
                  type: string
                slot_location:
                  type: object
      400:
        description: Validation error
      402:
        description: Payment processing failed
      404:
        description: Active session not found
    """
    try:
        user_id = get_jwt_identity()
        data = request.get_json()
        
        if not data:
            return jsonify({
                "success": False,
                "error": "No data provided"
            }), 400
        
        logger.info(f"Session checkout request for user {user_id}: {data}")
        
        # Validate input data
        try:
            validated_data = session_checkout_schema.load(data)
        except ValidationError as err:
            logger.warning(f"Validation error for user {user_id}: {err.messages}")
            return jsonify({
                "success": False,
                "error": "Validation failed",
                "details": err.messages
            }), 400
        
        # Find active session
        session = ParkingSession.query.filter_by(
            ticket_id=validated_data['ticket_id'],
            user_id=user_id,
            session_status='active'
        ).first()
        
        if not session:
            logger.warning(f"Active session {validated_data['ticket_id']} not found for user {user_id}")
            return jsonify({
                "success": False,
                "error": "Active session not found"
            }), 404
        
        # Calculate duration and cost
        end_time = datetime.utcnow()
        duration_seconds = (end_time - session.start_time).total_seconds()
        duration_hours = duration_seconds / 3600
        
        # Get parking lot for rate calculation
        parking_lot = ParkingLotDetails.query.get(session.parkinglot_id)
        if not parking_lot:
            logger.error(f"Parking lot {session.parkinglot_id} not found for session {session.ticket_id}")
            return jsonify({
                "success": False,
                "error": "Parking lot information not found"
            }), 404
        
        # Calculate cost
        hourly_rate = get_parking_lot_rates(parking_lot)
        total_amount = calculate_parking_cost(duration_hours, hourly_rate)
        
        # Process payment
        payment_result = process_payment(
            user_id=user_id,
            amount=total_amount,
            payment_method=validated_data.get('payment_method', 'card')
        )
        
        if not payment_result['success']:
            logger.error(f"Payment processing failed for session {session.ticket_id}: {payment_result.get('error')}")
            return jsonify({
                "success": False,
                "error": "Payment processing failed"
            }), 402
        
        # Update session
        session.end_time = end_time
        session.duration_hrs = duration_hours
        session.total_amount = total_amount
        session.payment_status = 'completed'
        session.payment_method = payment_result['method']
        session.receipt_url = payment_result['receipt_url']
        session.session_status = 'completed'
        
        # Free up parking slot
        if session.slot_id:
            free_parking_slot(session.slot_id)
        
        db.session.commit()
        
        # Get slot location info for response
        slot_location = {}
        if session.slot_id:
            slot = Slot.query.get(session.slot_id)
            if slot:
                floor = Floor.query.get(slot.floor_id) if slot.floor_id else None
                row = Row.query.get(slot.row_id) if slot.row_id else None
                slot_location = {
                    "floor_name": floor.name if floor else "Ground Floor",
                    "row_name": row.name if row else "A",
                    "slot_name": slot.name
                }
        
        # Format duration for display
        hours = int(duration_hours)
        minutes = int((duration_hours - hours) * 60)
        duration_display = f"{hours}h {minutes}m" if hours > 0 else f"{minutes}m"
        
        logger.info(f"Session {session.ticket_id} completed successfully for user {user_id}. Duration: {duration_display}, Cost: €{total_amount}")
        
        return jsonify({
            "success": True,
            "data": {
                "ticket_id": session.ticket_id,
                "start_time": session.start_time.isoformat(),
                "end_time": session.end_time.isoformat(),
                "duration": duration_display,
                "duration_hours": round(duration_hours, 2),
                "total_amount": float(total_amount),
                "payment_status": "completed",
                "payment_method": session.payment_method,
                "receipt_url": session.receipt_url,
                "slot_location": slot_location,
                "parking_lot_name": parking_lot.name if parking_lot else "Unknown"
            },
            "message": "Parking session completed successfully"
        }), 200
        
    except Exception as e:
        db.session.rollback()
        logger.error(f"Error completing session for user {user_id}: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to complete parking session"
        }), 500

def format_duration(duration_hours):
    """Format duration in hours to human-readable string"""
    hours = int(duration_hours)
    minutes = int((duration_hours - hours) * 60)
    if hours > 0:
        return f"{hours}h {minutes}m"
    else:
        return f"{minutes}m"

@sessions_bp.route('/active', methods=['GET'])
@jwt_required()
def get_active_sessions():
    """
    Get all active sessions for user
    ---
    tags:
      - Session Management
    security:
      - JWT: []
    responses:
      200:
        description: List of active sessions
        schema:
          type: object
          properties:
            success:
              type: boolean
            data:
              type: array
              items:
                type: object
                properties:
                  ticket_id:
                    type: string
                  parking_lot_name:
                    type: string
                  parking_lot_address:
                    type: string
                  vehicle_reg_no:
                    type: string
                  start_time:
                    type: string
                  current_duration:
                    type: string
                  estimated_cost:
                    type: number
                  slot_location:
                    type: object
    """
    try:
        user_id = get_jwt_identity()
        logger.info(f"Fetching active sessions for user {user_id}")
        
        sessions = ParkingSession.query.filter_by(
            user_id=user_id,
            session_status='active'
        ).order_by(ParkingSession.start_time.desc()).all()
        
        active_sessions = []
        for session in sessions:
            # Calculate current duration
            current_duration_seconds = (datetime.utcnow() - session.start_time).total_seconds()
            current_duration_hours = current_duration_seconds / 3600
            
            # Get parking lot info
            parking_lot = ParkingLotDetails.query.get(session.parkinglot_id)
            
            # Calculate estimated cost
            if parking_lot:
                hourly_rate = get_parking_lot_rates(parking_lot)
                estimated_cost = calculate_parking_cost(current_duration_hours, hourly_rate)
            else:
                estimated_cost = 0.0
            
            # Get slot location
            slot_location = {}
            if session.slot_id:
                slot = Slot.query.get(session.slot_id)
                if slot:
                    floor = Floor.query.get(slot.floor_id) if slot.floor_id else None
                    row = Row.query.get(slot.row_id) if slot.row_id else None
                    slot_location = {
                        "floor_name": floor.name if floor else "Ground Floor",
                        "row_name": row.name if row else "A",
                        "slot_name": slot.name
                    }
            
            active_sessions.append({
                "ticket_id": session.ticket_id,
                "user_id": session.user_id,
                "vehicle_id": session.vehicle_id,
                "parkinglot_id": session.parkinglot_id,
                "parking_lot_name": parking_lot.name if parking_lot else "Unknown",
                "parking_lot_address": parking_lot.address if parking_lot else "Unknown",
                "vehicle_reg_no": session.vehicle_reg_no,
                "vehicle_type": session.vehicle_type,
                "vehicle_info": {
                    "registration_number": session.vehicle_reg_no,
                    "vehicle_type": session.vehicle_type
                },
                "start_time": session.start_time.isoformat(),
                "end_time": None,
                "current_duration": format_duration(current_duration_hours),
                "duration_hours": round(current_duration_hours, 2),
                "total_amount": float(estimated_cost),
                "estimated_cost": float(estimated_cost),
                "payment_status": session.payment_status,
                "payment_method": session.payment_method,
                "receipt_url": session.receipt_url,
                "slot_location": slot_location,
                "status": "active"
            })
        
        logger.info(f"Found {len(active_sessions)} active sessions for user {user_id}")
        
        return jsonify({
            "success": True,
            "data": active_sessions
        }), 200
        
    except Exception as e:
        logger.error(f"Error fetching active sessions for user {user_id}: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to fetch active sessions"
        }), 500

@sessions_bp.route('/history', methods=['GET'])
@jwt_required()
def get_session_history():
    """
    Get past sessions for user with pagination
    ---
    tags:
      - Session Management
    security:
      - JWT: []
    parameters:
      - in: query
        name: page
        type: integer
        default: 1
        description: Page number for pagination
      - in: query
        name: per_page
        type: integer
        default: 20
        description: Number of sessions per page
      - in: query
        name: status
        type: string
        enum: ["completed", "cancelled"]
        description: Filter by session status
    responses:
      200:
        description: List of past sessions
        schema:
          type: object
          properties:
            success:
              type: boolean
            data:
              type: array
              items:
                type: object
                properties:
                  ticket_id:
                    type: string
                  parking_lot_name:
                    type: string
                  vehicle_reg_no:
                    type: string
                  start_time:
                    type: string
                  end_time:
                    type: string
                  duration:
                    type: string
                  total_amount:
                    type: number
                  payment_status:
                    type: string
                  session_status:
                    type: string
            pagination:
              type: object
              properties:
                page:
                  type: integer
                per_page:
                  type: integer
                total:
                  type: integer
                pages:
                  type: integer
    """
    try:
        user_id = get_jwt_identity()
        
        # Get pagination parameters
        page = request.args.get('page', 1, type=int)
        per_page = min(request.args.get('per_page', 20, type=int), 100)  # Max 100 per page
        status_filter = request.args.get('status', type=str)
        
        logger.info(f"Fetching session history for user {user_id}, page {page}, per_page {per_page}")
        
        # Build query
        query = ParkingSession.query.filter(
            ParkingSession.user_id == user_id,
            ParkingSession.session_status.in_(['completed', 'cancelled'])
        )
        
        # Apply status filter if provided
        if status_filter and status_filter in ['completed', 'cancelled']:
            query = query.filter(ParkingSession.session_status == status_filter)
        
        # Order by end time (most recent first)
        query = query.order_by(ParkingSession.end_time.desc())
        
        # Paginate
        pagination = query.paginate(
            page=page, 
            per_page=per_page, 
            error_out=False
        )
        
        sessions = pagination.items
        past_sessions = []
        
        for session in sessions:
            # Get parking lot info
            parking_lot = ParkingLotDetails.query.get(session.parkinglot_id)
            
            # Get vehicle info
            vehicle = UserVehicle.query.get(session.vehicle_id) if session.vehicle_id else None
            
            # Get slot location
            slot_location = {}
            if session.slot_id:
                slot = Slot.query.get(session.slot_id)
                if slot:
                    floor = Floor.query.get(slot.floor_id) if slot.floor_id else None
                    row = Row.query.get(slot.row_id) if slot.row_id else None
                    slot_location = {
                        "floor_name": floor.name if floor else "Ground Floor",
                        "row_name": row.name if row else "A",
                        "slot_name": slot.name
                    }
            
            # Format duration
            duration_display = "N/A"
            if session.duration_hrs:
                duration_display = format_duration(float(session.duration_hrs))
            elif session.start_time and session.end_time:
                duration_seconds = (session.end_time - session.start_time).total_seconds()
                duration_display = format_duration(duration_seconds / 3600)
            
            past_sessions.append({
                "ticket_id": session.ticket_id,
                "parking_lot_name": parking_lot.name if parking_lot else "Unknown",
                "parking_lot_address": parking_lot.address if parking_lot else "Unknown",
                "vehicle_reg_no": session.vehicle_reg_no,
                "vehicle_info": {
                    "registration_number": session.vehicle_reg_no,
                    "vehicle_name": vehicle.vehicle_name if vehicle else None,
                    "vehicle_type": session.vehicle_type
                },
                "start_time": session.start_time.isoformat() if session.start_time else None,
                "end_time": session.end_time.isoformat() if session.end_time else None,
                "duration": duration_display,
                "duration_hours": float(session.duration_hrs) if session.duration_hrs else None,
                "total_amount": float(session.total_amount) if session.total_amount else 0.0,
                "payment_status": session.payment_status,
                "payment_method": session.payment_method,
                "receipt_url": session.receipt_url,
                "status": session.session_status,  # Changed from session_status to status for consistency
                "slot_location": slot_location
            })
        
        logger.info(f"Found {len(past_sessions)} past sessions for user {user_id} on page {page}")
        
        return jsonify({
            "success": True,
            "data": past_sessions,
            "pagination": {
                "page": pagination.page,
                "per_page": pagination.per_page,
                "total": pagination.total,
                "pages": pagination.pages,
                "has_next": pagination.has_next,
                "has_prev": pagination.has_prev
            }
        }), 200
        
    except Exception as e:
        logger.error(f"Error fetching session history for user {user_id}: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to fetch session history"
        }), 500

@sessions_bp.route('/<ticket_id>', methods=['GET'])
@jwt_required()
def get_session_details(ticket_id):
    """
    Get detailed information about a specific session
    ---
    tags:
      - Session Management
    security:
      - JWT: []
    parameters:
      - in: path
        name: ticket_id
        type: string
        required: true
        description: Session ticket ID
    responses:
      200:
        description: Session details
        schema:
          type: object
          properties:
            success:
              type: boolean
            data:
              type: object
              properties:
                ticket_id:
                  type: string
                parking_lot_info:
                  type: object
                vehicle_info:
                  type: object
                session_info:
                  type: object
                payment_info:
                  type: object
                slot_location:
                  type: object
      404:
        description: Session not found
    """
    try:
        user_id = get_jwt_identity()
        logger.info(f"Fetching session details for ticket {ticket_id}, user {user_id}")
        
        # Find session
        session = ParkingSession.query.filter_by(
            ticket_id=ticket_id,
            user_id=user_id
        ).first()
        
        if not session:
            logger.warning(f"Session {ticket_id} not found for user {user_id}")
            return jsonify({
                "success": False,
                "error": "Session not found"
            }), 404
        
        # Get related information
        parking_lot = ParkingLotDetails.query.get(session.parkinglot_id)
        vehicle = UserVehicle.query.get(session.vehicle_id) if session.vehicle_id else None
        
        # Get slot location
        slot_location = {}
        if session.slot_id:
            slot = Slot.query.get(session.slot_id)
            if slot:
                floor = Floor.query.get(slot.floor_id) if slot.floor_id else None
                row = Row.query.get(slot.row_id) if slot.row_id else None
                slot_location = {
                    "floor_name": floor.name if floor else "Ground Floor",
                    "row_name": row.name if row else "A",
                    "slot_name": slot.name,
                    "floor_id": slot.floor_id,
                    "row_id": slot.row_id,
                    "slot_id": slot.id
                }
        
        # Calculate duration
        duration_display = "N/A"
        duration_hours = None
        if session.session_status == 'active':
            current_duration_seconds = (datetime.utcnow() - session.start_time).total_seconds()
            duration_hours = current_duration_seconds / 3600
            duration_display = format_duration(duration_hours)
        elif session.duration_hrs:
            duration_hours = float(session.duration_hrs)
            duration_display = format_duration(duration_hours)
        elif session.start_time and session.end_time:
            duration_seconds = (session.end_time - session.start_time).total_seconds()
            duration_hours = duration_seconds / 3600
            duration_display = format_duration(duration_hours)
        
        session_details = {
            "ticket_id": session.ticket_id,
            "parking_lot_info": {
                "id": parking_lot.id if parking_lot else None,
                "name": parking_lot.name if parking_lot else "Unknown",
                "address": parking_lot.address if parking_lot else "Unknown",
                "city": parking_lot.city if parking_lot else None,
                "parking_timing": parking_lot.parking_timing if parking_lot else None
            },
            "vehicle_info": {
                "vehicle_id": vehicle.vehicle_id if vehicle else None,
                "registration_number": session.vehicle_reg_no,
                "vehicle_name": vehicle.vehicle_name if vehicle else None,
                "make": vehicle.make if vehicle else None,
                "model": vehicle.model if vehicle else None,
                "vehicle_type": session.vehicle_type
            },
            "session_info": {
                "start_time": session.start_time.isoformat() if session.start_time else None,
                "end_time": session.end_time.isoformat() if session.end_time else None,
                "duration": duration_display,
                "duration_hours": duration_hours,
                "status": session.session_status
            },
            "payment_info": {
                "total_amount": float(session.total_amount) if session.total_amount else 0.0,
                "payment_status": session.payment_status,
                "payment_method": session.payment_method,
                "receipt_url": session.receipt_url
            },
            "slot_location": slot_location
        }
        
        logger.info(f"Session details retrieved for ticket {ticket_id}")
        
        return jsonify({
            "success": True,
            "data": session_details
        }), 200
        
    except Exception as e:
        logger.error(f"Error fetching session details for ticket {ticket_id}: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Failed to fetch session details"
        }), 500