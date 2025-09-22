import api from './api';
import { API_ENDPOINTS, USER_ROLES } from '../utils/constants';

/**
 * Fetch session details based on user role from mock data store
 */
export const fetchSessionDetails = async (user) => {
  try {
    if (!user) return { sessions: [] };

    if (user.role === USER_ROLES.SUPER_ADMIN) {
      const { data } = await api.get(API_ENDPOINTS.ADMIN.ALL_SESSION_DETAILS);
      return { sessions: Array.isArray(data) ? data : [] };
    }

    const { data } = await api.get(`${API_ENDPOINTS.ADMIN.SESSION_DETAILS}/${user.user_id}`);
    return { sessions: Array.isArray(data) ? data : [] };
  
  
  } catch (error) {
    console.error('Error fetching session details from API:', error);
    throw error;
  }
};

/**
 * Fetch admin lots information from mock data store
 */
export const fetchAdminLots = async (adminId, role) => {
  try {
    if (!adminId) return [];

    if (role === USER_ROLES.SUPER_ADMIN) {
      // Get all admin assignments for super admin
      const { data } = await api.get(API_ENDPOINTS.ADMIN.ALL_ADMIN_LOTS);
      // Flatten to a simple list of lot ids mapping is not needed for dashboard counts
      // The API returns {meta: {total}, data: [{admin_id, admin_name, assigned_lots: [{parkinglot_id, ...}]}]}
      const assignments = Array.isArray(data?.data) ? data.data : [];
      const lotIds = new Set();
      assignments.forEach(a => (a.assigned_lots || []).forEach(lot => lotIds.add(lot.parkinglot_id)));
      return Array.from(lotIds).map(id => ({ parkinglot_id: id }));
    }

    // Regular admin
    const { data } = await api.get(`/admin/admin_lots/${adminId}`);
    // API returns object with assigned_lots: [{ parkinglot_id, parking_name, location, parking_type, assigned_date }]
    return Array.isArray(data?.assigned_lots) ? data.assigned_lots : [];
  } catch (error) {
    console.error('Error fetching admin lots from API:', error);
    return [];
  }
};

/**
 * Fetch detailed admin information with assigned lots
 */
export const fetchAdminDetails = async (adminId) => {
  try {
    if (!adminId) return null;

    const { data } = await api.get(`/admin/admin_lots/${adminId}`);
    
    // Return the admin data with the new structure
    return {
      admin_id: data.admin_id,
      admin_name: data.admin_name,
      admin_email: data.admin_email,
      admin_phone_no: data.admin_phone_no,
      admin_address: data.admin_address,
      joining_date: data.joining_date,
      status: data.status,
      assigned_lots: Array.isArray(data.assigned_lots) ? data.assigned_lots : [],
      // Future fields
      permissions: data.permissions,
      shift_timings: data.shift_timings
    };
  } catch (error) {
    console.error('Error fetching admin details from API:', error);
    return null;
  }
};

/**
 * Calculate available slots for a parking lot
 */
const calculateAvailableSlots = () => 0; // Not available via API; not used in dashboard

/**
 * Generate revenue chart data from mock sessions
 */
export const generateRevenueChartData = (sessions) => {
  // Group sessions by date and calculate daily revenue (with fallback estimation)
  const revenueByDate = {};
  
  const estimateAmount = (s) => {
    if (typeof s.total_amount === 'number' && s.total_amount > 0) return s.total_amount;
    const duration = typeof s.duration_hrs === 'number' ? s.duration_hrs : 0;
    if (duration <= 0) return 0;
    const rate = s.vehicle_type === 'car' ? 50 : 30; // fallback base rates
    return Math.round(duration * rate * 100) / 100;
  };
  
  sessions.forEach(session => {
    if (session.end_time) {
      const date = new Date(session.start_time).toLocaleDateString('en-IN', {
        month: 'short',
        day: 'numeric'
      });
      const amount = estimateAmount(session);
      if (!amount) return;
      
      if (revenueByDate[date]) {
        revenueByDate[date] += amount;
      } else {
        revenueByDate[date] = amount;
      }
    }
  });

  return Object.entries(revenueByDate).map(([period, revenue]) => ({
    period,
    revenue: Math.round(revenue * 100) / 100
  }));
};

/**
 * Get dashboard data with mock data fallback
 */
export const getDashboardData = async (user) => {
  try {
    const [sessionData, adminLots] = await Promise.all([
      fetchSessionDetails(user),
      user?.user_id ? fetchAdminLots(user.user_id, user.role) : Promise.resolve([])
    ]);

    const revenueData = generateRevenueChartData(sessionData.sessions || []);
    
    // Calculate total parking slots without using debug endpoints
    // Heuristic: Admin → 50 slots per assigned lot; Super Admin → 100 default
    let totalParkingSlots = 0;
    if (user?.role === USER_ROLES.ADMIN) {
      totalParkingSlots = (adminLots?.length || 0) * 50;
    }
    if (!totalParkingSlots) totalParkingSlots = 100;
    
    return {
      sessions: sessionData.sessions || [],
      adminLots: adminLots || [],
      revenueData,
      totalParkingSlots: totalParkingSlots || 100,
      isUsingMockData: true
    };
  } catch (error) {
    console.warn('Mock data failed, using fallback data:', error.message);
    return getFallbackDashboardData();
  }
};

/**
 * Fallback data in case mock data store is empty
 */
const getFallbackDashboardData = () => {
  const mockSessions = [
    {
      ticket_id: "T001",
      parkinglot_id: 1,
      vehicle_reg_no: "KA01AB1234",
      user_id: 1,
      start_time: "2024-01-01T10:00:00Z",
      end_time: "2024-01-01T12:00:00Z",
      duration_hrs: 2,
      vehicle_type: "car",
      total_amount: 100
    },
    {
      ticket_id: "T002",
      parkinglot_id: 1,
      vehicle_reg_no: "KA01CD5678",
      user_id: 2,
      start_time: "2024-01-01T11:00:00Z",
      end_time: null,
      duration_hrs: null,
      vehicle_type: "motorcycle",
      total_amount: 0
    }
  ];

  const mockRevenueData = [
    { period: 'Jan 1', revenue: 225 },
    { period: 'Jan 2', revenue: 180 },
    { period: 'Jan 3', revenue: 320 }
  ];

  const mockAdminLots = [
    { parkinglot_id: 1, parkinglot_name: "Main Parking Lot", total_slots: 50, available_slots: 25 }
  ];

  return {
    sessions: mockSessions,
    adminLots: mockAdminLots,
    revenueData: mockRevenueData,
    totalParkingSlots: 50,
    isUsingMockData: true
  };
};