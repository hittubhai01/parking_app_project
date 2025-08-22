import api from './api';
import { API_ENDPOINTS, USER_ROLES } from '../utils/constants';
// import mockApiService from './mockApiService';

/**
 * Fetch session details based on user role
 * Super Admin: Gets all sessions across all lots
 * Admin: Gets sessions for their assigned lots only
 */
export const fetchSessionDetails = async (user) => {
  try {
    if (user.role === USER_ROLES.SUPER_ADMIN) {
      // Super Admin gets all session details
      const response = await api.get(API_ENDPOINTS.ADMIN.ALL_SESSION_DETAILS);
      return response.data;
    } else {
      // Regular Admin gets session details for their user ID
      const response = await api.get(`${API_ENDPOINTS.ADMIN.SESSION_DETAILS}/${user.user_id}`);
      return response.data;
    }
  } catch (error) {
    console.error('Error fetching session details:', error);
    throw error;
  }
};

/**
 * Fetch admin lots information
 */
export const fetchAdminLots = async (adminId) => {
  try {
    const response = await api.get(`${API_ENDPOINTS.ADMIN.ALL_ADMIN_LOTS}${adminId}/`);
    return response.data;
  } catch (error) {
    console.error('Error fetching admin lots:', error);
    throw error;
  }
};

/**
 * Generate sample revenue data for chart (mock data until real API is available)
 */
export const generateRevenueChartData = (sessions) => {
  // Group sessions by date and calculate daily revenue
  const revenueByDate = {};
  
  sessions.forEach(session => {
    if (session.end_time && session.duration_hrs) {
      const date = new Date(session.start_time).toLocaleDateString('en-IN', {
        month: 'short',
        day: 'numeric'
      });
      
      const revenue = session.duration_hrs * (session.vehicle_type === 'car' ? 50 : 30);
      
      if (revenueByDate[date]) {
        revenueByDate[date] += revenue;
      } else {
        revenueByDate[date] = revenue;
      }
    }
  });

  // Convert to chart format
  return Object.entries(revenueByDate).map(([period, revenue]) => ({
    period,
    revenue
  }));
};

/**
 * Get dashboard data with error handling and loading states
 */
export const getDashboardData = async (user) => {
  // // Use mock API service if enabled
  // if (mockApiService.isEnabled) {
  //   try {
  //     return await mockApiService.getDashboardData(user);
  //   } catch (error) {
  //     console.error('Mock API failed:', error);
  //     // Fall through to real API or fallback mock data
  //   }
  // }

  try {
    const [sessionData, adminLots] = await Promise.all([
      fetchSessionDetails(user),
      user.user_id ? fetchAdminLots(user.user_id) : Promise.resolve([])
    ]);

    const revenueData = generateRevenueChartData(sessionData.sessions || []);
    
    return {
      sessions: sessionData.sessions || [],
      adminLots: adminLots || [],
      revenueData,
      totalParkingSlots: adminLots?.reduce((total, lot) => total + (lot.total_slots || 0), 0) || 100
    };
  } catch (error) {
    // Return mock data if API fails (for development)
    console.warn('API failed, using mock data:', error.message);
    
    const mockSessions = [
      {
        ticket_id: "T001",
        parkinglot_id: 1,
        vehicle_reg_no: "KA01AB1234",
        user_id: 1,
        start_time: "2024-01-01T10:00:00Z",
        end_time: "2024-01-01T12:00:00Z",
        duration_hrs: 2,
        vehicle_type: "car"
      },
      {
        ticket_id: "T002",
        parkinglot_id: 1,
        vehicle_reg_no: "KA01CD5678",
        user_id: 2,
        start_time: "2024-01-01T11:00:00Z",
        end_time: null,
        duration_hrs: null,
        vehicle_type: "motorcycle"
      },
      {
        ticket_id: "T003",
        parkinglot_id: 2,
        vehicle_reg_no: "KA01EF9012",
        user_id: 3,
        start_time: "2024-01-01T09:00:00Z",
        end_time: "2024-01-01T11:30:00Z",
        duration_hrs: 2.5,
        vehicle_type: "car"
      }
    ];

    const mockRevenueData = [
      { period: 'Jan 1', revenue: 225 },
      { period: 'Jan 2', revenue: 180 },
      { period: 'Jan 3', revenue: 320 },
      { period: 'Jan 4', revenue: 290 },
      { period: 'Jan 5', revenue: 410 },
      { period: 'Jan 6', revenue: 380 },
      { period: 'Jan 7', revenue: 450 }
    ];

    return {
      sessions: mockSessions,
      adminLots: [],
      revenueData: mockRevenueData,
      totalParkingSlots: 20,
      isUsingMockData: true
    };
  }
};