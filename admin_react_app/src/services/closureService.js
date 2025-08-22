import api from './api';
import { API_ENDPOINTS } from '../utils/constants';

class ClosureService {
  // Get closure data for daily closure page
  async getClosureData() {
    try {
      const response = await api.get(API_ENDPOINTS.ADMIN.CLOSURE);
      return response.data;
    } catch (error) {
      this.handleClosureError(error);
    }
  }

  // Finalize daily closure with payment amount
  async finalizeClosureData(paymentAmount) {
    try {
      const response = await api.post(API_ENDPOINTS.ADMIN.CLOSURE, {
        payment_made: paymentAmount
      });
      return response.data;
    } catch (error) {
      this.handleClosureError(error);
    }
  }

  // Calculate closure metrics from API data
  calculateClosureMetrics(closureData) {
    if (!closureData) {
      return {
        outstandingAmount: 0,
        todayCollection: 0,
        totalDue: 0,
        amountPaid: 0,
        newOutstanding: 0,
        date: new Date().toISOString().split('T')[0],
        status: 'pending'
      };
    }

    const outstandingAmount = closureData.outstanding_amount || 0;
    const todayCollection = closureData.today_collection || 0;
    const totalDue = outstandingAmount + todayCollection;
    const amountPaid = closureData.payment_made || 0;
    const newOutstanding = Math.max(0, totalDue - amountPaid);

    return {
      outstandingAmount,
      todayCollection,
      totalDue,
      amountPaid,
      newOutstanding,
      date: closureData.date || new Date().toISOString().split('T')[0],
      status: closureData.status || (amountPaid > 0 ? 'completed' : 'pending')
    };
  }

  // Validate payment amount input
  validatePaymentAmount(amount) {
    const numAmount = parseFloat(amount);
    
    if (isNaN(numAmount)) {
      throw new Error('Please enter a valid payment amount');
    }
    
    if (numAmount < 0) {
      throw new Error('Payment amount cannot be negative');
    }
    
    if (numAmount > 1000000) {
      throw new Error('Payment amount seems too large. Please verify.');
    }
    
    return numAmount;
  }

  // Handle closure service errors
  handleClosureError(error) {
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 403:
          throw new Error('You are not authorized to access closure data');
        case 404:
          throw new Error('Closure data not found');
        case 400:
          throw new Error(data?.message || 'Invalid closure request');
        default:
          throw new Error(data?.message || 'Failed to process closure operation');
      }
    } else if (error.request) {
      throw new Error('Network error. Please check your connection');
    } else {
      throw new Error(error.message || 'An unexpected error occurred');
    }
  }
}

// Create and export a singleton instance
const closureService = new ClosureService();
export default closureService;