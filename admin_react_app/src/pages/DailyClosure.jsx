import React, { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import KPICard from '../components/common/KPICard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import Input from '../components/forms/Input';
import closureService from '../services/closureService';
import { formatCurrency, formatClosureStatus, validatePaymentAmount } from '../utils/helpers';

const DailyClosure = () => {
  const { user } = useAuth();
  
  // State for closure data and metrics
  const [closureData, setClosureData] = useState(null);
  const [metrics, setMetrics] = useState({
    outstandingAmount: 0,
    todayCollection: 0,
    totalDue: 0,
    amountPaid: 0,
    newOutstanding: 0,
    status: 'pending'
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // State for finalization modal
  const [showModal, setShowModal] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [finalizing, setFinalizing] = useState(false);
  const [validationError, setValidationError] = useState('');

  // Fetch closure data on component mount
  useEffect(() => {
    fetchClosureData();
  }, []);

  const fetchClosureData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const data = await closureService.getClosureData();
      setClosureData(data);
      
      // Calculate metrics using service method
      const calculatedMetrics = closureService.calculateClosureMetrics(data);
      setMetrics(calculatedMetrics);
      
      // Pre-fill payment amount with total due
      setPaymentAmount(calculatedMetrics.totalDue.toString());
    } catch (err) {
      setError(err.message);
      console.error('Failed to fetch closure data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFinalizeClick = () => {
    if (metrics.status === 'completed') {
      return; // Already completed
    }
    setShowModal(true);
    setValidationError('');
  };

  const handleModalClose = () => {
    setShowModal(false);
    setValidationError('');
  };

  const handlePaymentAmountChange = (e) => {
    setPaymentAmount(e.target.value);
    setValidationError('');
  };

  const handleFinalizeClosure = async () => {
    try {
      // Validate payment amount
      const validation = validatePaymentAmount(paymentAmount);
      if (!validation.isValid) {
        setValidationError(validation.error);
        return;
      }

      setFinalizing(true);
      setValidationError('');

      // Call API to finalize closure
      const result = await closureService.finalizeClosureData(validation.value);
      
      // Update metrics with new data
      const updatedMetrics = {
        ...metrics,
        amountPaid: validation.value,
        newOutstanding: result.new_outstanding || Math.max(0, metrics.totalDue - validation.value),
        status: 'completed'
      };
      setMetrics(updatedMetrics);

      // Close modal and show success
      setShowModal(false);
      
      // Show success message (you could add a toast notification here)
      console.log('Closure finalized successfully');
      
      // Refresh data to get latest state
      await fetchClosureData();
      
    } catch (err) {
      setValidationError(err.message);
      console.error('Failed to finalize closure:', err);
    } finally {
      setFinalizing(false);
    }
  };

  const statusInfo = formatClosureStatus(metrics.status);

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Daily Closure</h1>
          
          {/* Status Indicator */}
          <div className="flex justify-center">
            <span className={`inline-flex px-4 py-2 rounded-full text-sm font-medium ${statusInfo.className}`}>
              {statusInfo.label}
            </span>
          </div>
        </div>

        {/* Loading State */}
        {loading ? (
          <div className="flex items-center justify-center h-64">
            <LoadingSpinner size="lg" />
          </div>
        ) : error ? (
          <div className="text-center py-8">
            <div className="text-red-600 mb-2">
              <svg className="w-12 h-12 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <p className="text-red-600 mb-4">{error}</p>
            <Button onClick={fetchClosureData} variant="outline">
              Retry
            </Button>
          </div>
        ) : (
          <>
            {/* Financial Metrics Cards */}
            <div className="space-y-6 mb-8">
              {/* Top Row - 3 Cards */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <KPICard
                  title="Outstanding Amount"
                  value={formatCurrency(metrics.outstandingAmount)}
                  loading={loading}
                  className="text-center"
                />
                <KPICard
                  title="Today's Collection"
                  value={formatCurrency(metrics.todayCollection)}
                  loading={loading}
                  className="text-center"
                />
                <KPICard
                  title="Total Due"
                  value={formatCurrency(metrics.totalDue)}
                  loading={loading}
                  className="text-center border-2 border-blue-200"
                />
              </div>

              {/* Bottom Row - 2 Cards (Centered) */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-2xl mx-auto">
                <KPICard
                  title="Amount Paid"
                  value={formatCurrency(metrics.amountPaid)}
                  loading={loading}
                  className="text-center"
                />
                <KPICard
                  title="New Outstanding"
                  value={formatCurrency(metrics.newOutstanding)}
                  loading={loading}
                  className="text-center"
                />
              </div>
            </div>

            {/* Action Button */}
            <div className="text-center">
              <Button
                variant="primary"
                size="lg"
                onClick={handleFinalizeClick}
                disabled={metrics.status === 'completed'}
                className="bg-purple-600 hover:bg-purple-700 text-white px-8 py-3 text-lg font-semibold"
              >
                {metrics.status === 'completed' ? 'Closure Completed' : 'Finalize Closure'}
              </Button>
            </div>
          </>
        )}

        {/* Finalization Modal */}
        <Modal
          isOpen={showModal}
          onClose={handleModalClose}
          title="Finalize Daily Closure"
        >
          <div className="space-y-4">
            <div className="bg-gray-50 p-4 rounded-lg">
              <h4 className="font-semibold text-gray-900 mb-2">Closure Summary</h4>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Outstanding Amount:</span>
                  <span className="font-medium">{formatCurrency(metrics.outstandingAmount)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Today's Collection:</span>
                  <span className="font-medium">{formatCurrency(metrics.todayCollection)}</span>
                </div>
                <div className="flex justify-between border-t pt-2">
                  <span className="font-semibold">Total Due:</span>
                  <span className="font-semibold">{formatCurrency(metrics.totalDue)}</span>
                </div>
              </div>
            </div>

            <div>
              <Input
                label="Payment Amount"
                type="number"
                value={paymentAmount}
                onChange={handlePaymentAmountChange}
                placeholder="Enter payment amount"
                error={validationError}
                step="0.01"
                min="0"
              />
              <p className="text-sm text-gray-500 mt-1">
                Enter the amount you are settling today
              </p>
            </div>

            <div className="flex space-x-3 pt-4">
              <Button
                variant="outline"
                onClick={handleModalClose}
                disabled={finalizing}
                className="flex-1"
              >
                Cancel
              </Button>
              <Button
                variant="primary"
                onClick={handleFinalizeClosure}
                loading={finalizing}
                className="flex-1 bg-purple-600 hover:bg-purple-700"
              >
                {finalizing ? 'Finalizing...' : 'Confirm Closure'}
              </Button>
            </div>
          </div>
        </Modal>
      </div>
    </div>
  );
};

export default DailyClosure;