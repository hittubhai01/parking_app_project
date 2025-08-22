// API Configuration
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:3001' 


// User Roles
export const USER_ROLES = {
  ADMIN: "admin",
  SUPER_ADMIN: "super_admin",
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: "auth_token",
  AUTH_USER: "auth_user",
};

// Navigation Sections
export const NAV_SECTIONS = {
  MAIN: "MAIN",
  ADMINISTRATION: "ADMINISTRATION",
  OPERATIONS: "OPERATIONS",
  ACCOUNT: "ACCOUNT",
};

// API Endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: "/auth/login",
  },
  ADMIN: {
    ALL_SESSION_DETAILS: "/admin/all_session/details/", // super_admin -> GET
    SESSION_DETAILS: "/admin/session/details",        // admin -> GET /admin/session/details/:user_id
    ASSIGN_LOT: "/admin/assign_lot",
    REMOVE_ASSIGNMENT: "/admin/remove_assignment",
    ALL_ADMIN_LOTS: "/all_admin/admin_lots/",
    CLOSURE: "/admin/closure",
    SESSION_CHECKIN: "/admin/session/checkin",
    SESSION_CHECKOUT: "/admin/session/checkout",
  },
};

// Demo Credentials (matching RBAC document)
export const DEMO_CREDENTIALS = {
  SUPER_ADMIN: {
    user_email: "superadmin@parking.com",
    user_password: "password123",
    role: USER_ROLES.SUPER_ADMIN,
    label: "Super Admin",
  },
  ADMIN: {
    user_email: "john@example.com",
    user_password: "password123",
    role: USER_ROLES.ADMIN,
    label: "Admin",
  },
};
