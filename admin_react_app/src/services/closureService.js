import api from './api';
import { API_ENDPOINTS } from '../utils/constants';

class ClosureService {
  // Get closure data for daily closure page
  async getClosureData(date) {
    try {
      const params = date ? { params: { date } } : undefined;
      const response = await api.get(API_ENDPOINTS.ADMIN.CLOSURE, params);
      console.log('Closure API Response:', response.data); // Debug log
      
      // Handle different response structures
      if (response.data && response.data.closure) {
        return response.data.closure; // { success: true, closure: {...} }
      } else if (response.data && response.data.outstanding_amount !== undefined) {
        return response.data; // Direct closure object
      } else {
        console.warn('Unexpected API response structure:', response.data);
        return this.getMockClosureData();
      }
    } catch (error) {
      console.warn('API call failed, using mock closure data:', error.message);
      return this.getMockClosureData();
    }
  }

  // Finalize daily closure with payment amount using unified /admin/closure endpoint
  async finalizeClosureData(paymentAmount, date) {
    try {
      const targetDate = date || new Date().toISOString().split('T')[0];
      const response = await api.post(API_ENDPOINTS.ADMIN.CLOSURE, {
        date: targetDate,
        amount_paid: paymentAmount
      });
      console.log('Finalize Closure API Response:', response.data); // Debug log
      
      // Handle different response structures
      if (response.data && response.data.closure) {
        return response.data.closure;
      } else if (response.data && response.data.amount_paid !== undefined) {
        return response.data;
      } else {
        console.warn('Unexpected finalize response structure:', response.data);
        return this.simulateClosureFinalization(paymentAmount);
      }
    } catch (error) {
      console.warn('API call failed, simulating closure finalization:', error.message);
      return this.simulateClosureFinalization(paymentAmount);
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

    console.log('Raw closure data for metrics:', closureData); // Debug log

    // Handle both field naming conventions
    const outstandingAmount = closureData.opening_balance || closureData.outstanding_amount || 0;
    const todayCollection = closureData.today_collection || closureData.today_collection || 0;
    const totalDue = closureData.total_due || (outstandingAmount + todayCollection);
    const amountPaid = closureData.amount_paid || closureData.payment_made || 0;
    const newOutstanding = closureData.new_outstanding || Math.max(0, totalDue - amountPaid);

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

  // Validate payment amount input (compatible with helper function)
  validatePaymentAmount(amount) {
    const numAmount = parseFloat(amount);
    
    if (isNaN(numAmount)) {
      return { isValid: false, error: 'Please enter a valid payment amount' };
    }
    
    if (numAmount < 0) {
      return { isValid: false, error: 'Payment amount cannot be negative' };
    }
    
    if (numAmount > 1000000) {
      return { isValid: false, error: 'Payment amount seems too large. Please verify.' };
    }
    
    return { isValid: true, value: numAmount };
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

  // Get mock closure data for fallback
  getMockClosureData() {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    
    // Randomize values for more realistic mock data
    const outstandingAmount = Math.floor(Math.random() * 5000) + 1000;
    const todayCollection = Math.floor(Math.random() * 3000) + 500;
    const totalDue = outstandingAmount + todayCollection;
    
    return {
      date: today.toISOString().split('T')[0],
      opening_balance: outstandingAmount,
      outstanding_amount: outstandingAmount, // Include both for compatibility
      today_collection: todayCollection,
      total_due: totalDue,
      amount_paid: 0,
      payment_made: 0, // Include both for compatibility
      new_outstanding: totalDue,
      status: 'pending',
      previous_date: yesterday.toISOString().split('T')[0],
      isMockData: true
    };
  }

  // Simulate closure finalization with mock data
  simulateClosureFinalization(paymentAmount) {
    // Use the service's validatePaymentAmount method
    const validation = this.validatePaymentAmount(paymentAmount);
    if (!validation.isValid) {
      throw new Error(validation.error);
    }
    
    const validatedAmount = validation.value;
    const mockData = this.getMockClosureData();
    
    // Calculate new outstanding amount
    const totalDue = mockData.total_due;
    const newOutstanding = Math.max(0, totalDue - validatedAmount);
    
    // Determine status based on payment
    let status = 'pending';
    if (validatedAmount > 0) {
      status = validatedAmount >= totalDue ? 'completed' : 'partial';
    }
    
    return {
      ...mockData,
      amount_paid: validatedAmount,
      payment_made: validatedAmount, // Include both for compatibility
      new_outstanding: newOutstanding,
      status: status,
      finalized_at: new Date().toISOString(),
      isMockData: true
    };
  }

  // Get closure history for reporting
  async getClosureHistory(startDate, endDate) {
    try {
      const response = await api.get(`${API_ENDPOINTS.ADMIN.CLOSURE}/history`, {
        params: { start_date: startDate, end_date: endDate }
      });
      return response.data;
    } catch (error) {
      console.warn('API call failed, using mock closure history:', error.message);
      return this.getMockClosureHistory(startDate, endDate);
    }
  }

  // Generate mock closure history
  getMockClosureHistory(startDate, endDate) {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const days = Math.floor((end - start) / (1000 * 60 * 60 * 24)) + 1;
    
    const history = [];
    
    for (let i = 0; i < days; i++) {
      const date = new Date(start);
      date.setDate(date.getDate() + i);
      
      const outstandingAmount = Math.floor(Math.random() * 5000) + 1000;
      const todayCollection = Math.floor(Math.random() * 3000) + 500;
      const totalDue = outstandingAmount + todayCollection;
      const paymentMade = Math.floor(Math.random() * totalDue);
      const newOutstanding = Math.max(0, totalDue - paymentMade);
      
      history.push({
        date: date.toISOString().split('T')[0],
        opening_balance: outstandingAmount,
        outstanding_amount: outstandingAmount,
        today_collection: todayCollection,
        total_due: totalDue,
        amount_paid: paymentMade,
        payment_made: paymentMade,
        new_outstanding: newOutstanding,
        status: paymentMade >= totalDue ? 
                'completed' : (paymentMade > 0 ? 'partial' : 'pending'),
        isMockData: true
      });
    }
    
    return history;
  }
}

// Create and export a singleton instance
const closureService = new ClosureService();
export default closureService;