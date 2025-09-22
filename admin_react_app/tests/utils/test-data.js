import users from '../fixtures/users.json' assert { type: 'json' };

export const apiEndpoints = {
  login: '/auth/login',
  dashboard: '/admin/sessions/details/all',
  adminManagement: '/admins/admin_lots/all',
  liveSessions: '/admin/sessions/details/all',
  paymentCollection: '/admin/total_due',
  dailyClosure: '/admin/closure',
};

export const testUrls = {
  login: '/login',
  dashboard: '/dashboard',
  adminManagement: '/admin-management',
  liveSessions: '/live-sessions',
  paymentCollection: '/payment-collection',
  dailyClosure: '/daily-closure',
  settings: '/settings',
};

export const testCredentials = {
  superAdmin: {
    email: 'superadmin@parking.com',
    password: 'password123',
    role: 'super_admin'
  },
  admin: {
    email: 'admin@parking.com',
    password: 'admin123',
    role: 'admin'
  },
  invalid: {
    email: 'invalid@email.com',
    password: 'wrongpassword'
  }
};

export { users };
