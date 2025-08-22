import api from './api';
import { API_ENDPOINTS } from '../utils/constants';

/**
 * Check out a vehicle from parking session
 * @param {string} ticketId - The ticket ID to check out
 * @returns {Promise} API response
 */
export const checkOutVehicle = async (ticketId) => {
  try {
    const response = await api.post(API_ENDPOINTS.ADMIN.SESSION_CHECKOUT, {
      ticket_id: ticketId
    });
    return response.data;
  } catch (error) {
    console.error('Error checking out vehicle:', error);
    throw error;
  }
};

/**
 * Get active sessions for live monitoring
 * @param {Object} user - Current user object
 * @returns {Promise} Active sessions data
 */
export const getActiveSessions = async (user) => {
  try {
    // This would typically call the session details API and filter for active sessions
    // For now, we'll use mock data that matches the UI design
    
    // Simulate API delay
    await new Promise(resolve => setTimeout(resolve, 500));
    
    return {
      activeSessions: [
        {
          ticket_id: "T001",
          participant_name: "John Doe",
          vehicle_reg_no: "ABC-123",
          parkinglot_id: "P1",
          start_time: "2024-01-08T10:25:00Z",
          duration: "1h 25m",
          vehicle_type: "car",
          avatar_color: "#8B5CF6" // Purple
        },
        {
          ticket_id: "T002", 
          participant_name: "Sarah Miller",
          vehicle_reg_no: "XYZ-456",
          parkinglot_id: "P2",
          start_time: "2024-01-08T09:45:00Z",
          duration: "2h 5m",
          vehicle_type: "motorcycle",
          avatar_color: "#EC4899" // Pink
        },
        {
          ticket_id: "T003",
          participant_name: "Robert Wilson",
          vehicle_reg_no: "DEF-789",
          parkinglot_id: "P3",
          start_time: "2024-01-08T11:10:00Z",
          duration: "1h 0m",
          vehicle_type: "car",
          avatar_color: "#EF4444" // Red
        },
        {
          ticket_id: "T004",
          participant_name: "Lisa Brown",
          vehicle_reg_no: "GHI-012",
          parkinglot_id: "P1",
          start_time: "2024-01-08T08:30:00Z",
          duration: "3h 40m",
          vehicle_type: "car",
          avatar_color: "#10B981" // Green
        },
        {
          ticket_id: "T005",
          participant_name: "Mike Johnson",
          vehicle_reg_no: "JKL-345",
          parkinglot_id: "P2",
          start_time: "2024-01-08T10:00:00Z",
          duration: "2h 10m",
          vehicle_type: "car",
          avatar_color: "#6366F1" // Indigo
        }
      ],
      stats: {
        activeParticipants: 24,
        totalRevenue: 186.50,
        avgSessionTime: "2h 15m",
        occupancyRate: 78
      },
      recentActivity: [
        {
          type: "join",
          message: "New participant joined (Vehicle: MNO-678)",
          time: "2 minutes ago"
        },
        {
          type: "payment",
          message: "Payment received from ABC-123 (₹7.50)",
          time: "5 minutes ago"
        },
        {
          type: "leave",
          message: "Participant left (Vehicle: PQR-901)",
          time: "8 minutes ago"
        },
        {
          type: "join",
          message: "New participant joined (Vehicle: STU-234)",
          time: "12 minutes ago"
        },
        {
          type: "payment",
          message: "Payment received from XYZ-456 (₹5.00)",
          time: "15 minutes ago"
        }
      ]
    };
  } catch (error) {
    console.error('Error fetching active sessions:', error);
    throw error;
  }
};