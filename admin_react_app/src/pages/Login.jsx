import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import Input from '../components/forms/Input';
import Button from '../components/common/Button';
import { validateLoginForm } from '../utils/validators';
import { DEMO_CREDENTIALS } from '../utils/constants';
import { determineRoleFromEmail } from '../utils/helpers';

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated, isLoading } = useAuth();
  
  const [formData, setFormData] = useState({
    user_email: '',
    user_password: '',
    // Remove role from UI state - will be auto-determined
  });
  
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loginError, setLoginError] = useState('');

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      // Redirect to intended destination or dashboard
      const from = location.state?.from || '/dashboard';
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, location.state]);

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    
    // Clear field error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: '',
      }));
    }
    
    // Clear login error
    if (loginError) {
      setLoginError('');
    }
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    const validation = validateLoginForm(formData);
    if (!validation.isValid) {
      setErrors(validation.errors);
      return;
    }
    
    setIsSubmitting(true);
    setLoginError('');
    
    try {
      // Auto-determine role based on email
      const role = determineRoleFromEmail(formData.user_email);
      
      // Create login payload with auto-determined role
      const loginPayload = {
        user_email: formData.user_email,
        user_password: formData.user_password,
        role: role, // Include role for API (required by backend)
      };
      
      await login(loginPayload);
      // Navigation will be handled by the useEffect above
    } catch (error) {
      setLoginError(error.message || 'Login failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle demo credential selection
  const handleDemoLogin = (demoType) => {
    const credentials = DEMO_CREDENTIALS[demoType];
    setFormData({
      user_email: credentials.user_email,
      user_password: credentials.user_password,
      // Don't include role in form data - it will be auto-determined
    });
    setErrors({});
    setLoginError('');
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* Header */}
        <div className="text-center">
          <h2 className="mt-6 text-2xl font-bold text-gray-900">
            Login Screen for Super Admin / Admin
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Login to your account to continue
          </p>
        </div>

        {/* Login Form */}
        <div className="bg-white py-8 px-6 shadow-lg rounded-lg">
          <form className="space-y-6" onSubmit={handleSubmit}>
            {/* Email Input */}
            <Input
              name="user_email"
              type="email"
              label="Email Address"
              value={formData.user_email}
              onChange={handleInputChange}
              placeholder="Enter your email"
              required
              error={errors.user_email}
              autoComplete="email"
            />

            {/* Password Input */}
            <Input
              name="user_password"
              type="password"
              label="Password"
              value={formData.user_password}
              onChange={handleInputChange}
              placeholder="Enter your password"
              required
              error={errors.user_password}
              autoComplete="current-password"
            />

            {/* Login Error */}
            
            {loginError && (
              <div className="bg-red-50 border border-red-200 rounded-md p-3">
                <p className="text-sm text-red-600">{loginError}</p>
              </div>
            )}

            {/* Submit Button */}
            <Button
              type="submit"
              variant="primary"
              size="lg"
              className="w-full"
              loading={isSubmitting}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Logging in...' : 'Login'}
            </Button>
          </form>

          {/* Demo Credentials Section */}
          <div className="mt-8 pt-6 border-t border-gray-200">
            <h3 className="text-sm font-medium text-gray-700 mb-4">
              Demo Credentials:
            </h3>
            
            <div className="space-y-2 text-sm">
              {/* Super Admin Demo */}
              <div>
                <span className="font-medium text-gray-900">Super Admin:</span>{' '}
                <button
                  type="button"
                  onClick={() => handleDemoLogin('SUPER_ADMIN')}
                  disabled={isSubmitting}
                  className="text-blue-600 hover:text-blue-800 underline"
                >
                  {DEMO_CREDENTIALS.SUPER_ADMIN.user_email} / {DEMO_CREDENTIALS.SUPER_ADMIN.user_password}
                </button>
              </div>

              {/* Admin Demo */}
              <div>
                <span className="font-medium text-gray-900">Admin:</span>{' '}
                <button
                  type="button"
                  onClick={() => handleDemoLogin('ADMIN')}
                  disabled={isSubmitting}
                  className="text-blue-600 hover:text-blue-800 underline"
                >
                  {DEMO_CREDENTIALS.ADMIN.user_email} / {DEMO_CREDENTIALS.ADMIN.user_password}
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="text-center">
          <p className="text-xs text-gray-500">
            © 2025 Parking Admin Dashboard. All rights reserved.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;