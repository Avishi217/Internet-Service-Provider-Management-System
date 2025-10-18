package com.isp.service;

import com.isp.dao.UserDAO;
import com.isp.model.User;

public class AuthService {
    private UserDAO userDAO;
    private static User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Login user with username and password (legacy method)
     * @param username Username
     * @param password Password
     * @return User object if login successful, null otherwise
     */
    public User login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            currentUser = user;
        }
        return user;
    }
    
    /**
     * Login user with phone number and OTP (modern method)
     * @param phone Phone number
     * @param otpCode OTP code
     * @return User object if login successful, null otherwise
     */
    public User loginWithOTP(String phone, String otpCode) {
        // Validate OTP first
        if (!OTPService.validateOTP(phone, otpCode)) {
            System.out.println("❌ OTP validation failed for phone: " + phone);
            return null;
        }
        
        // Get user by phone number
        User user = userDAO.getUserByPhone(phone);
        if (user != null) {
            currentUser = user;
            System.out.println("✅ User logged in successfully: " + user.getUsername());
        } else {
            System.out.println("❌ User not found for phone: " + phone);
        }
        return user;
    }
    
    /**
     * Send OTP to phone number (for login).
     * Returns the generated OTP string for demo purposes (null on failure).
     * @param phone Phone number
     * @return generated OTP string or null
     */
    public String sendOTP(String phone) {
        // Check if phone is registered
        if (!OTPService.isPhoneRegistered(phone)) {
            System.out.println("❌ Phone number not registered: " + phone);
            return null;
        }

        // Generate and send OTP
        return OTPService.generateOTP(phone);
    }
    
    /**
     * Generate OTP for signup (no phone check needed)
     * @param phone Phone number
     * @return OTP code if generated successfully, null otherwise
     */
    public String generateSignupOTP(String phone) {
        // For signup, we don't check if phone exists
        return OTPService.generateOTP(phone);
    }
    
    /**
     * Sign up a new user with OTP verification
     * @param name Full name
     * @param email Email address
     * @param phone Phone number
     * @param otpCode OTP code
     * @return User object if signup successful, null otherwise
     */
    public User signupWithOTP(String name, String email, String phone, String otpCode) {
        // Validate OTP first
        if (!OTPService.validateOTP(phone, otpCode)) {
            System.out.println("❌ OTP validation failed for signup: " + phone);
            return null;
        }
        
        // Check if phone already registered
        if (OTPService.isPhoneRegistered(phone)) {
            System.out.println("❌ Phone number already registered: " + phone);
            return null;
        }
        
        // Split name into first and last
        String[] nameParts = name.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        // Create user account
        User newUser = new User();
        newUser.setUsername(phone); // Use phone as username
        newUser.setPassword("OTP_AUTH"); // No password for OTP-based auth
        newUser.setPhone(phone);
        newUser.setRole("CUSTOMER");
        newUser.setStatus("active");
        
        // Save to database
        int userId = userDAO.createUser(newUser);
        
        if (userId > 0) {
            // Also create customer record
            com.isp.model.Customer customer = new com.isp.model.Customer();
            customer.setUserId(userId);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setConnectionStatus("ACTIVE");
            
            com.isp.dao.CustomerDAO customerDAO = new com.isp.dao.CustomerDAO();
            int customerId = customerDAO.createCustomer(customer);
            
            if (customerId > 0) {
                newUser.setUserId(userId);
                currentUser = newUser;
                System.out.println("✅ User registered successfully: " + phone);
                return newUser;
            }
        }
        
        System.out.println("❌ Failed to create user account");
        return null;
    }

    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Get current logged in user
     * @return Current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Check if current user is admin
     * @return true if current user is admin, false otherwise
     */
    public static boolean isAdmin() {
        return currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user is customer
     * @return true if current user is customer, false otherwise
     */
    public static boolean isCustomer() {
        return currentUser != null && "customer".equalsIgnoreCase(currentUser.getRole());
    }

    /**
     * Check if current user is employee
     * @return true if current user is employee, false otherwise
     */
    public static boolean isEmployee() {
        return currentUser != null && "employee".equalsIgnoreCase(currentUser.getRole());
    }
}
