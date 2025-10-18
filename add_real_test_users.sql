-- Add REAL test users with realistic data
-- Fresh phone numbers that don't exist yet

USE isp_management;

-- Add 5 new test users
INSERT INTO users (phone, role, created_at) VALUES 
('6789012345', 'CUSTOMER', NOW()),
('6789123456', 'CUSTOMER', NOW()),
('6789234567', 'CUSTOMER', NOW()),
('6789345678', 'CUSTOMER', NOW()),
('6789456789', 'CUSTOMER', NOW());

-- Customer 1: Ananya Verma - Heavy Instagram & YouTube user, 80% data used (WARNING)
INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='6789012345'), 'Ananya', 'Verma', 'ananya.verma@gmail.com', '6789012345', 'Koramangala, Bangalore 560034', 3, '2025-10-01', '2025-10-29', '2025-09-28', 'active');

-- Customer 2: Rohan Kapoor - Gamer, exceeded daily limit (125% - SHOW ADDON BUTTON)
INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='6789123456'), 'Rohan', 'Kapoor', 'rohan.kapoor@yahoo.com', '6789123456', 'Dwarka, New Delhi 110075', 5, '2025-09-20', '2025-11-15', '2025-09-18', 'active');

-- Customer 3: Kavya Reddy - Netflix binge watcher, 60% used (NORMAL)
INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='6789234567'), 'Kavya', 'Reddy', 'kavya.reddy@outlook.com', '6789234567', 'Banjara Hills, Hyderabad 500034', 6, '2025-09-25', '2025-11-20', '2025-09-23', 'active');

-- Customer 4: Arjun Mehta - New user, low usage (15%)
INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='6789345678'), 'Arjun', 'Mehta', 'arjun.mehta@hotmail.com', '6789345678', 'Andheri West, Mumbai 400053', 4, '2025-10-10', '2025-11-07', '2025-10-08', 'active');

-- Customer 5: Sneha Gupta - No active plan (NEEDS RECHARGE)
INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='6789456789'), 'Sneha', 'Gupta', 'sneha.gupta@gmail.com', '6789456789', 'Park Street, Kolkata 700016', NULL, NULL, NULL, '2025-10-05', 'expired');

-- ============================================
-- TODAY'S DATA USAGE (CURDATE = Oct 13, 2025)
-- ============================================

-- Ananya: 1.2 GB / 1.5 GB = 80% (WARNING ALERT)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='6789012345'), CURDATE(), 3, 1.20, 1.50, 0.00, FALSE, FALSE);

-- Rohan: 2.5 GB / 2.0 GB = 125% (EXCEEDED - SHOW ADDON)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='6789123456'), CURDATE(), 5, 2.50, 2.00, 0.00, TRUE, FALSE);

-- Kavya: 0.9 GB / 1.5 GB = 60% (NORMAL)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='6789234567'), CURDATE(), 6, 0.90, 1.50, 0.00, FALSE, FALSE);

-- Arjun: 0.15 GB / 1.0 GB = 15% (LOW USAGE)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='6789345678'), CURDATE(), 4, 0.15, 1.00, 0.00, FALSE, FALSE);

-- ============================================
-- PLATFORM USAGE - TODAY (Realistic patterns)
-- ============================================

-- Ananya's usage: Instagram + YouTube heavy
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='6789012345'), CURDATE(), 9, 'social_media', 'Instagram', 420, 60),
((SELECT customer_id FROM customers WHERE phone='6789012345'), CURDATE(), 14, 'streaming', 'YouTube', 680, 120),
((SELECT customer_id FROM customers WHERE phone='6789012345'), CURDATE(), 19, 'messaging', 'WhatsApp', 80, 180),
((SELECT customer_id FROM customers WHERE phone='6789012345'), CURDATE(), 21, 'social_media', 'Instagram', 220, 45);

-- Rohan's usage: Gaming + Streaming (HIGH DATA)
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='6789123456'), CURDATE(), 10, 'gaming', 'PUBG Mobile', 950, 180),
((SELECT customer_id FROM customers WHERE phone='6789123456'), CURDATE(), 16, 'streaming', 'Netflix', 1200, 150),
((SELECT customer_id FROM customers WHERE phone='6789123456'), CURDATE(), 20, 'gaming', 'COD Mobile', 350, 90);

-- Kavya's usage: Netflix binge
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='6789234567'), CURDATE(), 15, 'streaming', 'Netflix', 650, 180),
((SELECT customer_id FROM customers WHERE phone='6789234567'), CURDATE(), 21, 'social_media', 'Facebook', 180, 45),
((SELECT customer_id FROM customers WHERE phone='6789234567'), CURDATE(), 22, 'messaging', 'WhatsApp', 70, 60);

-- Arjun's usage: Light browsing
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='6789345678'), CURDATE(), 11, 'browsing', 'Google Chrome', 85, 30),
((SELECT customer_id FROM customers WHERE phone='6789345678'), CURDATE(), 18, 'messaging', 'WhatsApp', 65, 90);

-- ============================================
-- NOTIFICATIONS
-- ============================================

-- Ananya: 80% warning
INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
((SELECT customer_id FROM customers WHERE phone='6789012345'), 'data_limit_alert', '⚠️ 80% Data Used', 'You have used 1.2 GB of 1.5 GB today. You are near your daily limit!', FALSE, 'medium', NOW());

-- Rohan: Limit exceeded
INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
((SELECT customer_id FROM customers WHERE phone='6789123456'), 'data_limit_alert', '🔴 Daily Limit Exceeded', 'You have exceeded your daily data limit (2.5 GB used). Buy a data addon to continue browsing at high speed.', FALSE, 'high', NOW());

-- Kavya: Plan expiring soon
INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
((SELECT customer_id FROM customers WHERE phone='6789234567'), 'plan_expiry', '⏰ Plan Expiring Soon', 'Your plan will expire in 8 days. Recharge now to avoid service interruption.', FALSE, 'medium', NOW());

-- Sneha: No active plan
INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
((SELECT customer_id FROM customers WHERE phone='6789456789'), 'plan_expiry', '❌ No Active Plan', 'You don\'t have an active plan. Recharge now to start using services.', FALSE, 'high', NOW());

-- ============================================
-- RECHARGE HISTORY
-- ============================================

INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, validity_days, recharge_date, validity_start_date, validity_end_date, is_active) VALUES
((SELECT customer_id FROM customers WHERE phone='6789012345'), 'TXN202510010001', 3, 265.00, 28, '2025-10-01', '2025-10-01', '2025-10-29', TRUE),
((SELECT customer_id FROM customers WHERE phone='6789123456'), 'TXN202509200002', 5, 479.00, 56, '2025-09-20', '2025-09-20', '2025-11-15', TRUE),
((SELECT customer_id FROM customers WHERE phone='6789234567'), 'TXN202509250003', 6, 549.00, 56, '2025-09-25', '2025-09-25', '2025-11-20', TRUE),
((SELECT customer_id FROM customers WHERE phone='6789345678'), 'TXN202510100004', 4, 299.00, 28, '2025-10-10', '2025-10-10', '2025-11-07', TRUE);

-- ============================================
-- VERIFICATION
-- ============================================

SELECT '✅ New Users Added:' as Status;
SELECT customer_id, CONCAT(first_name, ' ', last_name) as Name, phone, 
       COALESCE(plan_id, 0) as plan_id, connection_status 
FROM customers 
WHERE phone IN ('6789012345', '6789123456', '6789234567', '6789345678', '6789456789');

SELECT '\n📊 Today\'s Data Usage:' as Status;
SELECT c.first_name, d.data_used_gb, d.daily_limit_gb, 
       CONCAT(ROUND((d.data_used_gb/d.daily_limit_gb)*100), '%') as percentage,
       CASE 
         WHEN d.limit_exceeded THEN '🔴 EXCEEDED'
         WHEN (d.data_used_gb/d.daily_limit_gb) >= 0.8 THEN '🟡 WARNING'
         ELSE '🟢 NORMAL'
       END as status
FROM daily_data_usage d 
JOIN customers c ON d.customer_id = c.customer_id 
WHERE d.usage_date = CURDATE() 
  AND c.phone IN ('6789012345', '6789123456', '6789234567', '6789345678');

SELECT '\n🔔 Notifications:' as Status;
SELECT c.first_name, n.type, n.title, n.priority 
FROM notifications n 
JOIN customers c ON n.customer_id = c.customer_id 
WHERE c.phone IN ('6789012345', '6789123456', '6789234567', '6789456789')
ORDER BY n.priority DESC, n.created_at DESC;

COMMIT;
