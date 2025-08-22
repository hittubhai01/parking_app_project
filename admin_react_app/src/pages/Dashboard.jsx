import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { KPICard, Button, ErrorDisplay, KPICardSkeleton, ChartSkeleton } from '../components/common';
import { RevenueChart } from '../components/dashboard';
import { calculateDashboardKPIs } from '../utils/kpiCalculations';
import { getDashboardData } from '../services/dashboardService';
import { USER_ROLES } from '../utils/constants';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  // State management
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch dashboard data on component mount
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await getDashboardData(user);
        setDashboardData(data);
      } catch (err) {
        setError(err.message);
        console.error('Dashboard data fetch error:', err);
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchData();
    }
  }, [user]);

  // Memoize expensive KPI calculations
  const kpis = useMemo(() => {
    if (!dashboardData) return null;
    return calculateDashboardKPIs(dashboardData.sessions, [], dashboardData.totalParkingSlots);
  }, [dashboardData]);

  // Memoize quick actions based on user role
  const quickActions = useMemo(() => {
    const commonActions = [
      { label: 'Live Sessions', path: '/live-sessions', icon: '🚗' },
      { label: 'Payment Collection', path: '/payment-collection', icon: '💰' },
      { label: 'Daily Closure', path: '/daily-closure', icon: '📊' }
    ];

    if (user?.role === USER_ROLES.SUPER_ADMIN) {
      return [
        ...commonActions,
        { label: 'Admin Management', path: '/admin-management', icon: '👥' },
        { label: 'Settings', path: '/settings', icon: '⚙️' }
      ];
    }

    return commonActions;
  }, [user?.role]);

  // Retry function for error recovery
  const handleRetry = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getDashboardData(user);
      setDashboardData(data);
    } catch (err) {
      setError(err.message);
      console.error('Dashboard data fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  // Loading state with skeleton
  if (loading) {
    return (
      <div className="p-6 space-y-8">
        {/* Header Skeleton */}
        <div className="flex items-center justify-between">
          <div>
            <div className="h-8 bg-gray-200 rounded w-48 mb-2 animate-pulse"></div>
            <div className="h-4 bg-gray-200 rounded w-64 animate-pulse"></div>
          </div>
          <div className="h-4 bg-gray-200 rounded w-24 animate-pulse"></div>
        </div>

        {/* Quick Actions Skeleton */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="h-6 bg-gray-200 rounded w-32 mb-4 animate-pulse"></div>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
            {[...Array(5)].map((_, i) => (
              <div key={i} className="h-20 bg-gray-200 rounded animate-pulse"></div>
            ))}
          </div>
        </div>

        {/* KPI Cards Skeleton */}
        <div>
          <div className="h-6 bg-gray-200 rounded w-48 mb-4 animate-pulse"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, i) => (
              <KPICardSkeleton key={i} />
            ))}
          </div>
        </div>

        {/* Chart Skeleton */}
        <ChartSkeleton />
      </div>
    );
  }

  // Error state
  if (error && !dashboardData) {
    return (
      <div className="p-6">
        <ErrorDisplay
          error={error}
          type="network"
          onRetry={handleRetry}
          showRetry={true}
        />
      </div>
    );
  }

  return (
    <div className="p-6 space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">
            Welcome back, {user?.username || user?.user_email}!
            {dashboardData?.isUsingMockData && (
              <span className="ml-2 text-sm text-amber-600">(Using demo data)</span>
            )}
          </p>
        </div>
        <div className="text-sm text-gray-500">
          Role: <span className="font-medium">{user?.role}</span>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold mb-4">Quick Actions</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
          {quickActions.map((action) => (
            <Button
              key={action.path}
              variant="secondary"
              onClick={() => navigate(action.path)}
              className="flex flex-col items-center p-4 h-auto"
            >
              <span className="text-2xl mb-2">{action.icon}</span>
              <span className="text-sm text-center">{action.label}</span>
            </Button>
          ))}
        </div>
      </div>

      {/* KPI Cards */}
      {kpis && (
        <div>
          <h2 className="text-lg font-semibold mb-4">Key Performance Indicators</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <KPICard
              title="Total Income"
              value={kpis.totalIncome.value}
              subtitle={kpis.totalIncome.subtitle}
              trend={kpis.totalIncome.trend}
              valueType="currency"
            />
            <KPICard
              title="Total Sessions"
              value={kpis.totalSessions.value}
              subtitle={kpis.totalSessions.subtitle}
              trend={kpis.totalSessions.trend}
              valueType="number"
            />
            <KPICard
              title="Revenue per Slot"
              value={kpis.revenuePerSlot.value}
              subtitle={kpis.revenuePerSlot.subtitle}
              trend={kpis.revenuePerSlot.trend}
              valueType="currency"
            />
            <KPICard
              title="Active Participants"
              value={kpis.activeParticipants.value}
              subtitle={kpis.activeParticipants.subtitle}
              trend={kpis.activeParticipants.trend}
              valueType="number"
            />
            <KPICard
              title="Average Session Time"
              value={kpis.averageSessionTime.value}
              subtitle={kpis.averageSessionTime.subtitle}
              trend={kpis.averageSessionTime.trend}
              valueType="duration"
            />
            <KPICard
              title="Occupancy Rate"
              value={kpis.occupancyRate.value}
              subtitle={kpis.occupancyRate.subtitle}
              trend={kpis.occupancyRate.trend}
              valueType="percentage"
            />
          </div>
        </div>
      )}

      {/* Revenue Chart */}
      {dashboardData?.revenueData && (
        <RevenueChart 
          data={dashboardData.revenueData}
          title="Revenue Trends"
        />
      )}

      {/* Summary Information */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold mb-4">Dashboard Summary</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h3 className="font-medium text-gray-900 mb-2">Session Overview</h3>
            <ul className="text-sm text-gray-600 space-y-1">
              <li>• Total Sessions: {dashboardData?.sessions?.length || 0}</li>
              <li>• Active Sessions: {dashboardData?.sessions?.filter(s => !s.end_time).length || 0}</li>
              <li>• Completed Sessions: {dashboardData?.sessions?.filter(s => s.end_time).length || 0}</li>
            </ul>
          </div>
          <div>
            <h3 className="font-medium text-gray-900 mb-2">System Information</h3>
            <ul className="text-sm text-gray-600 space-y-1">
              <li>• Total Parking Slots: {dashboardData?.totalParkingSlots || 0}</li>
              <li>• Admin Lots: {dashboardData?.adminLots?.length || 0}</li>
              <li>• Data Source: {dashboardData?.isUsingMockData ? 'Demo Data' : 'Live API'}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;