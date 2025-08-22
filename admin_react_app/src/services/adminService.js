import api from './api';
import { API_ENDPOINTS } from '../utils/constants';

class AdminService {
  // Create new admin with lot assignments
  async createAdmin(adminData) {
    try {
      const response = await api.post(API_ENDPOINTS.ADMIN.ASSIGN_LOT, {
        name: adminData.name,
        email: adminData.email,
        password: adminData.password,
        role: 'admin', // Hardcoded as 'admin' - not user selectable
        assigned_lots: adminData.assigned_lots,
      });

      return response.data;
    } catch (error) {
      // Handle specific admin creation errors
      if (error.response) {
        const { status, data } = error.response;
        
        switch (status) {
          case 400:
            throw new Error(data?.message || 'Invalid admin data provided');
          case 403:
            throw new Error('You are not authorized to create admin accounts');
          case 409:
            throw new Error('An admin with this email already exists');
          default:
            throw new Error(data?.message || 'Failed to create admin account');
        }
      } else if (error.request) {
        throw new Error('Network error. Please check your connection');
      } else {
        throw new Error(error.message || 'An unexpected error occurred');
      }
    }
  }

  // Get all admin data with assigned lots
  async getAllAdmins() {
    try {
      const response = await api.get(API_ENDPOINTS.ADMIN.ALL_ADMIN_LOTS);
      return response.data;
    } catch (error) {
      if (error.response) {
        const { status, data } = error.response;
        
        switch (status) {
          case 403:
            throw new Error('You are not authorized to view admin data');
          case 404:
            throw new Error('Admin data not found');
          default:
            throw new Error(data?.message || 'Failed to fetch admin data');
        }
      } else if (error.request) {
        throw new Error('Network error. Please check your connection');
      } else {
        throw new Error(error.message || 'An unexpected error occurred');
      }
    }
  }

  // Delete admin assignment
  async deleteAdmin(userId) {
    try {
      const response = await api.delete(API_ENDPOINTS.ADMIN.REMOVE_ASSIGNMENT, {
        data: { user_id: userId }
      });
      
      return response.data;
    } catch (error) {
      if (error.response) {
        const { status, data } = error.response;
        
        switch (status) {
          case 403:
            throw new Error('You are not authorized to delete admin accounts');
          case 404:
            throw new Error('Admin account not found');
          default:
            throw new Error(data?.message || 'Failed to delete admin account');
        }
      } else if (error.request) {
        throw new Error('Network error. Please check your connection');
      } else {
        throw new Error(error.message || 'An unexpected error occurred');
      }
    }
  }

  // Get all session details (for Super Admin)
  async getAllSessionDetails() {
    try {
      const response = await api.get(API_ENDPOINTS.ADMIN.ALL_SESSION_DETAILS);
      return response.data;
    } catch (error) {
      if (error.response) {
        const { status, data } = error.response;
        
        switch (status) {
          case 403:
            throw new Error('You are not authorized to view session data');
          default:
            throw new Error(data?.message || 'Failed to fetch session data');
        }
      } else if (error.request) {
        throw new Error('Network error. Please check your connection');
      } else {
        throw new Error(error.message || 'An unexpected error occurred');
      }
    }
  }
}

// Create and export a singleton instance
const adminService = new AdminService();
export default adminService;