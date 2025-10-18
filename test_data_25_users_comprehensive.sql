-- ==========================================
-- COMPREHENSIVE TEST DATA: 25 USERS
-- Different Plans, Usage Patterns & Complaints
-- ==========================================

USE isp_management;

-- Clean existing test data (optional)
-- DELETE FROM daily_data_usage WHERE customer_id > 5;
-- DELETE FROM complaints WHERE customer_id > 5;
-- DELETE FROM customers WHERE customer_id > 5;
-- DELETE FROM users WHERE user_id > 5;

-- ==========================================
-- 1. USERS (25 new users with realistic names)
-- ==========================================

INSERT INTO users (phone, password_hash, role, is_active, created_at) VALUES
-- Existing 5 users (1-5) already in database
-- New 20 users (6-25)
('9876543211', '$2a$10$dummyHashForPassword1', 'customer', TRUE, '2025-09-15 10:00:00'),
('9876543212', '$2a$10$dummyHashForPassword2', 'customer', TRUE, '2025-09-16 11:00:00'),
('9876543213', '$2a$10$dummyHashForPassword3', 'customer', TRUE, '2025-09-17 12:00:00'),
('9876543214', '$2a$10$dummyHashForPassword4', 'customer', TRUE, '2025-09-18 13:00:00'),
('9876543215', '$2a$10$dummyHashForPassword5', 'customer', TRUE, '2025-09-19 14:00:00'),
('9876543216', '$2a$10$dummyHashForPassword6', 'customer', TRUE, '2025-09-20 15:00:00'),
('9876543217', '$2a$10$dummyHashForPassword7', 'customer', TRUE, '2025-09-21 16:00:00'),
('9876543218', '$2a$10$dummyHashForPassword8', 'customer', TRUE, '2025-09-22 17:00:00'),
('9876543219', '$2a$10$dummyHashForPassword9', 'customer', TRUE, '2025-09-23 18:00:00'),
('9876543220', '$2a$10$dummyHashForPassword10', 'customer', TRUE, '2025-09-24 19:00:00'),
('9876543221', '$2a$10$dummyHashForPassword11', 'customer', TRUE, '2025-09-25 10:30:00'),
('9876543222', '$2a$10$dummyHashForPassword12', 'customer', TRUE, '2025-09-26 11:30:00'),
('9876543223', '$2a$10$dummyHashForPassword13', 'customer', TRUE, '2025-09-27 12:30:00'),
('9876543224', '$2a$10$dummyHashForPassword14', 'customer', TRUE, '2025-09-28 13:30:00'),
('9876543225', '$2a$10$dummyHashForPassword15', 'customer', TRUE, '2025-09-29 14:30:00'),
('9876543226', '$2a$10$dummyHashForPassword16', 'customer', TRUE, '2025-09-30 15:30:00'),
('9876543227', '$2a$10$dummyHashForPassword17', 'customer', TRUE, '2025-10-01 16:30:00'),
('9876543228', '$2a$10$dummyHashForPassword18', 'customer', TRUE, '2025-10-02 17:30:00'),
('9876543229', '$2a$10$dummyHashForPassword19', 'customer', TRUE, '2025-10-03 18:30:00'),
('9876543230', '$2a$10$dummyHashForPassword20', 'customer', TRUE, '2025-10-04 19:30:00');

-- ==========================================
-- 2. CUSTOMERS (25 users with different plans)
-- ==========================================

INSERT INTO customers (user_id, first_name, last_name, email, address, city, state, pincode, plan_id, connection_status, plan_activated_date, plan_expiry_date) VALUES
-- Users 6-10: Basic Plans (₹99-₹179)
(6, 'Rajesh', 'Kumar', 'rajesh.kumar@gmail.com', '12 MG Road', 'Bangalore', 'Karnataka', '560001', 1, 'active', '2025-10-01', '2025-10-29'),
(7, 'Sneha', 'Patel', 'sneha.patel@gmail.com', '45 FC Road', 'Pune', 'Maharashtra', '411016', 2, 'active', '2025-09-25', '2025-10-23'),
(8, 'Amit', 'Shah', 'amit.shah@yahoo.com', '78 Park Street', 'Kolkata', 'West Bengal', '700016', 1, 'active', '2025-10-05', '2025-11-02'),
(9, 'Priya', 'Reddy', 'priya.reddy@gmail.com', '23 Anna Salai', 'Chennai', 'Tamil Nadu', '600002', 2, 'expired', '2025-09-10', '2025-10-08'),
(10, 'Vikram', 'Desai', 'vikram.desai@hotmail.com', '56 Linking Road', 'Mumbai', 'Maharashtra', '400050', 1, 'active', '2025-10-08', '2025-11-05'),

-- Users 11-15: Mid-tier Plans (₹265-₹359)
(11, 'Neha', 'Gupta', 'neha.gupta@gmail.com', '89 Connaught Place', 'Delhi', 'Delhi', '110001', 3, 'active', '2025-09-20', '2025-10-18'),
(12, 'Rahul', 'Sharma', 'rahul.sharma@gmail.com', '34 Hazratganj', 'Lucknow', 'Uttar Pradesh', '226001', 4, 'active', '2025-10-01', '2025-10-29'),
(13, 'Divya', 'Nair', 'divya.nair@yahoo.com', '67 Marine Drive', 'Kochi', 'Kerala', '682031', 5, 'active', '2025-10-10', '2025-11-07'),
(14, 'Arjun', 'Mehta', 'arjun.mehta@gmail.com', '12 Civil Lines', 'Jaipur', 'Rajasthan', '302006', 3, 'suspended', '2025-09-15', '2025-10-13'),
(15, 'Pooja', 'Singh', 'pooja.singh@hotmail.com', '45 Residency Road', 'Indore', 'Madhya Pradesh', '452001', 4, 'active', '2025-10-12', '2025-11-09'),

-- Users 16-20: Premium Plans (₹479-₹719)
(16, 'Karan', 'Malhotra', 'karan.malhotra@gmail.com', '78 SG Highway', 'Ahmedabad', 'Gujarat', '380015', 6, 'active', '2025-09-18', '2025-11-13'),
(17, 'Anjali', 'Iyer', 'anjali.iyer@gmail.com', '23 Residency Road', 'Bangalore', 'Karnataka', '560025', 7, 'active', '2025-09-22', '2025-11-17'),
(18, 'Sanjay', 'Rao', 'sanjay.rao@yahoo.com', '56 Banjara Hills', 'Hyderabad', 'Telangana', '500034', 6, 'active', '2025-10-05', '2025-11-30'),
(19, 'Meera', 'Joshi', 'meera.joshi@gmail.com', '89 Law Garden', 'Ahmedabad', 'Gujarat', '380009', 9, 'active', '2025-09-25', '2025-12-18'),
(20, 'Rohan', 'Verma', 'rohan.verma@hotmail.com', '34 Koramangala', 'Bangalore', 'Karnataka', '560034', 7, 'active', '2025-10-08', '2025-12-02'),

-- Users 21-25: Mix of plans + some expired/no plan
(21, 'Ananya', 'Chatterjee', 'ananya.chat@gmail.com', '67 Salt Lake', 'Kolkata', 'West Bengal', '700064', 8, 'active', '2025-09-28', '2025-12-21'),
(22, 'Varun', 'Kapoor', 'varun.kapoor@gmail.com', '12 Greater Kailash', 'Delhi', 'Delhi', '110048', 10, 'active', '2025-10-02', '2025-12-25'),
(23, 'Ishita', 'Bansal', 'ishita.bansal@yahoo.com', '45 Janpath', 'Delhi', 'Delhi', '110001', 5, 'expired', '2025-08-20', '2025-09-17'),
(24, 'Aditya', 'Pandey', 'aditya.pandey@gmail.com', '78 Boat Club Road', 'Pune', 'Maharashtra', '411001', NULL, 'expired', NULL, NULL),
(25, 'Ritu', 'Saxena', 'ritu.saxena@hotmail.com', '23 Saket', 'Delhi', 'Delhi', '110017', NULL, 'expired', NULL, NULL);

-- ==========================================
-- 3. DAILY DATA USAGE (Realistic patterns)
-- ==========================================

INSERT INTO daily_data_usage (customer_id, usage_date, daily_limit_gb, data_used_gb, limit_exceeded, created_at) VALUES
-- User 6 (Rajesh - Basic Voice): Minimal data
(6, '2025-10-13', 0.50, 0.12, FALSE, '2025-10-13 08:00:00'),
(6, '2025-10-12', 0.50, 0.25, FALSE, '2025-10-12 20:00:00'),

-- User 7 (Sneha - 2GB): Normal usage
(7, '2025-10-13', 2.00, 1.45, FALSE, '2025-10-13 18:00:00'),
(7, '2025-10-12', 2.00, 1.80, FALSE, '2025-10-12 22:00:00'),

-- User 8 (Amit - Basic Voice): Low usage
(8, '2025-10-13', 0.50, 0.08, FALSE, '2025-10-13 10:00:00'),

-- User 9 (Priya - Expired): No usage
-- User 10 (Vikram - Basic Voice): Moderate
(10, '2025-10-13', 0.50, 0.38, FALSE, '2025-10-13 16:00:00'),

-- User 11 (Neha - 1GB/day): High usage (80%)
(11, '2025-10-13', 1.00, 0.85, FALSE, '2025-10-13 19:00:00'),
(11, '2025-10-12', 1.00, 0.92, FALSE, '2025-10-12 23:00:00'),

-- User 12 (Rahul - 1.5GB/day): Normal
(12, '2025-10-13', 1.50, 0.95, FALSE, '2025-10-13 14:00:00'),

-- User 13 (Divya - 2GB/day): EXCEEDED!
(13, '2025-10-13', 2.00, 2.45, TRUE, '2025-10-13 20:00:00'),
(13, '2025-10-12', 2.00, 2.20, TRUE, '2025-10-12 23:30:00'),

-- User 14 (Arjun - Suspended): No usage
-- User 15 (Pooja - 1.5GB/day): Low usage
(15, '2025-10-13', 1.50, 0.45, FALSE, '2025-10-13 12:00:00'),

-- User 16 (Karan - 1.5GB/day 56D): High usage
(16, '2025-10-13', 1.50, 1.35, FALSE, '2025-10-13 21:00:00'),

-- User 17 (Anjali - 2GB/day 56D): EXCEEDED!
(17, '2025-10-13', 2.00, 2.75, TRUE, '2025-10-13 22:00:00'),
(17, '2025-10-12', 2.00, 2.10, TRUE, '2025-10-12 23:00:00'),

-- User 18 (Sanjay - 1.5GB/day 56D): Normal
(18, '2025-10-13', 1.50, 1.05, FALSE, '2025-10-13 17:00:00'),

-- User 19 (Meera - 1.5GB/day 84D): Very high (approaching limit)
(19, '2025-10-13', 1.50, 1.42, FALSE, '2025-10-13 20:30:00'),

-- User 20 (Rohan - 2GB/day 56D): EXCEEDED!
(20, '2025-10-13', 2.00, 2.90, TRUE, '2025-10-13 23:00:00'),

-- User 21 (Ananya - 84D 6GB): Batch usage
(21, '2025-10-13', 0.50, 0.45, FALSE, '2025-10-13 15:00:00'),

-- User 22 (Varun - 2GB/day 84D): Normal
(22, '2025-10-13', 2.00, 1.60, FALSE, '2025-10-13 19:00:00'),

-- User 23 (Ishita - Expired): No usage
-- User 24 (Aditya - No plan): No usage
-- User 25 (Ritu - No plan): No usage
(25, '2025-10-13', 0.00, 0.00, FALSE, '2025-10-13 10:00:00');

-- ==========================================
-- 4. COMPLAINTS (15 realistic complaints)
-- ==========================================

INSERT INTO complaints (customer_id, subject, description, priority, status, submitted_date, resolved_date, assigned_employee_id, created_at) VALUES
-- High Priority - Network Issues
(7, 'No internet connection since morning', 'I am unable to connect to the internet since 8 AM today. Shows connected but no data. Please help urgently.', 'high', 'open', '2025-10-13 09:30:00', NULL, NULL, '2025-10-13 09:30:00'),

(13, 'Very slow internet speed', 'Internet speed is extremely slow. Getting only 1-2 Mbps instead of promised 10 Mbps. Cannot work from home like this.', 'high', 'in_progress', '2025-10-12 14:00:00', NULL, 2, '2025-10-12 14:00:00'),

(17, 'Daily data limit exceeding automatically', 'My daily data shows 2.75GB used but I have only used around 1.5GB. Something is wrong with the tracking system.', 'high', 'open', '2025-10-13 22:30:00', NULL, NULL, '2025-10-13 22:30:00'),

-- Medium Priority - Billing Issues
(11, 'Charged twice for same plan', 'I was charged ₹265 twice on Oct 1st for the same plan activation. Please refund one payment.', 'medium', 'in_progress', '2025-10-02 10:00:00', NULL, 2, '2025-10-02 10:00:00'),

(16, 'Auto-renewal not working', 'My plan expired but auto-renewal did not work. I have sufficient balance but plan did not renew automatically.', 'medium', 'open', '2025-10-11 08:00:00', NULL, NULL, '2025-10-11 08:00:00'),

(12, 'Invoice not received for last month', 'I did not receive my September invoice on email. Need it for reimbursement from office.', 'medium', 'resolved', '2025-10-01 11:00:00', '2025-10-03 16:00:00', 2, '2025-10-01 11:00:00'),

-- Low Priority - General Queries
(8, 'How to check data usage breakdown', 'I want to see which apps are consuming my data. Is there a way to check detailed data usage breakdown?', 'low', 'resolved', '2025-10-05 12:00:00', '2025-10-06 10:00:00', 2, '2025-10-05 12:00:00'),

(15, 'Request for plan upgrade options', 'I am currently on ₹299 plan but need more data. What are my upgrade options and charges?', 'low', 'resolved', '2025-10-10 09:00:00', '2025-10-11 14:00:00', 2, '2025-10-10 09:00:00'),

(19, 'SMS not working on roaming', 'I am in Mumbai and SMS is not working. Calls and data working fine but cannot send/receive SMS.', 'medium', 'in_progress', '2025-10-12 16:00:00', NULL, 2, '2025-10-12 16:00:00'),

-- Critical - Service Disruption
(20, 'Complete service outage in my area', 'No service for past 3 hours. Multiple neighbors also facing same issue. Area: Koramangala, Bangalore.', 'high', 'open', '2025-10-13 18:00:00', NULL, NULL, '2025-10-13 18:00:00'),

(22, 'Cannot activate new plan', 'Recharged with ₹839 plan but plan is not getting activated. Money deducted but service not working.', 'high', 'in_progress', '2025-10-02 11:30:00', NULL, 2, '2025-10-02 11:30:00'),

-- Account Issues
(14, 'Account suspended without notice', 'My account was suspended today morning without any prior notice or outstanding payment. Please reactivate.', 'high', 'in_progress', '2025-10-13 07:00:00', NULL, 2, '2025-10-13 07:00:00'),

(9, 'Unable to recharge online', 'Getting payment failed error when trying to recharge online. Tried 3 different cards. Please fix.', 'medium', 'open', '2025-10-08 15:00:00', NULL, NULL, '2025-10-08 15:00:00'),

(21, 'Port-in request status', 'I initiated port-in request 5 days ago but no update yet. Request ID: PIN2025100800123. Please update.', 'low', 'in_progress', '2025-10-08 10:00:00', NULL, 2, '2025-10-08 10:00:00'),

(6, 'Voice call quality poor', 'Voice call quality has been poor for last 2 days. Frequent call drops and echoing sound during calls.', 'medium', 'open', '2025-10-11 17:00:00', NULL, NULL, '2025-10-11 17:00:00');

-- ==========================================
-- VERIFICATION QUERIES
-- ==========================================

-- Check total users
SELECT '=== TOTAL USERS ===' AS info;
SELECT COUNT(*) AS total_users FROM users;

-- Check active customers with plans
SELECT '=== CUSTOMERS BY STATUS ===' AS info;
SELECT connection_status, COUNT(*) AS count FROM customers GROUP BY connection_status;

-- Check customers by plan
SELECT '=== CUSTOMERS BY PLAN ===' AS info;
SELECT 
    p.plan_name,
    COUNT(c.customer_id) AS customer_count
FROM plans p
LEFT JOIN customers c ON p.plan_id = c.plan_id
WHERE p.is_addon = FALSE
GROUP BY p.plan_id, p.plan_name
ORDER BY customer_count DESC;

-- Check complaints summary
SELECT '=== COMPLAINTS SUMMARY ===' AS info;
SELECT 
    status,
    priority,
    COUNT(*) AS count
FROM complaints
GROUP BY status, priority
ORDER BY 
    CASE priority 
        WHEN 'high' THEN 1 
        WHEN 'medium' THEN 2 
        WHEN 'low' THEN 3 
    END,
    status;

-- Check users with exceeded limits
SELECT '=== USERS WHO EXCEEDED LIMITS ===' AS info;
SELECT 
    c.customer_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    c.phone,
    d.daily_limit_gb,
    d.data_used_gb,
    d.usage_date
FROM customers c
JOIN daily_data_usage d ON c.user_id = d.customer_id
WHERE d.limit_exceeded = TRUE
    AND d.usage_date = '2025-10-13'
ORDER BY d.data_used_gb DESC;

SELECT '=== TEST DATA LOADED SUCCESSFULLY! ===' AS result;
