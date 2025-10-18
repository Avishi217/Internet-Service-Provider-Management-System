-- Quick Sample Data: Add 2-5 entries for testing
-- Run this after loading schema_v2_enhanced.sql and sample_data_v2_realistic.sql

USE isp_management;

-- Add 2 more customers with active plans
INSERT INTO users (phone, role, created_at) VALUES 
('8800112233', 'CUSTOMER', NOW()),
('8800112244', 'CUSTOMER', NOW());

INSERT INTO customers (user_id, first_name, last_name, email, phone, address, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
((SELECT user_id FROM users WHERE phone='8800112233'), 'Priya', 'Sharma', 'priya.sharma@email.com', '8800112233', 'MG Road, Bangalore, Karnataka', 3, '2025-10-01', '2025-10-29', NOW(), 'ACTIVE'),
((SELECT user_id FROM users WHERE phone='8800112244'), 'Amit', 'Kumar', 'amit.kumar@email.com', '8800112244', 'Connaught Place, Delhi', 5, '2025-09-15', '2025-11-10', NOW(), 'ACTIVE');

-- Add daily data usage for these customers (TODAY's usage)
-- Priya: Used 1.2 GB of 1.5 GB (80% - should show warning)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112233'), CURDATE(), 3, 1.20, 1.50, 0.00, FALSE, FALSE);

-- Amit: Used 2.5 GB of 2.0 GB (125% - limit exceeded, should show addon button)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, addon_data_used_gb, limit_exceeded, addon_purchased) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112244'), CURDATE(), 5, 2.50, 2.00, 0.00, TRUE, FALSE);

-- Add platform usage for today (for Priya)
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112233'), CURDATE(), 10, 'social_media', 'Instagram', 320, 45),
((SELECT customer_id FROM customers WHERE phone='8800112233'), CURDATE(), 14, 'streaming', 'YouTube', 580, 90),
((SELECT customer_id FROM customers WHERE phone='8800112233'), CURDATE(), 19, 'messaging', 'WhatsApp', 45, 120);

-- Add platform usage for today (for Amit)
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112244'), CURDATE(), 9, 'streaming', 'Netflix', 1200, 180),
((SELECT customer_id FROM customers WHERE phone='8800112244'), CURDATE(), 15, 'gaming', 'PUBG', 850, 120),
((SELECT customer_id FROM customers WHERE phone='8800112244'), CURDATE(), 20, 'social_media', 'Facebook', 450, 75);

-- Add notifications for these customers
INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112233'), 'data_limit_alert', 'Data Usage Warning', 'You have used 80% of your daily data limit (1.2 GB of 1.5 GB)', FALSE, 'medium', NOW()),
((SELECT customer_id FROM customers WHERE phone='8800112244'), 'data_limit_alert', 'Daily Data Limit Exceeded', 'You have exceeded your daily data limit. Purchase a data addon to continue browsing.', FALSE, 'high', NOW());

-- Add a recharge history entry for Priya
INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, validity_days, recharge_date, validity_start_date, validity_end_date, is_active) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112233'), 'TXN001PRIYA', 3, 265.00, 28, '2025-10-01', '2025-10-01', '2025-10-29', TRUE);

-- Add a recharge history entry for Amit  
INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, validity_days, recharge_date, validity_start_date, validity_end_date, is_active) VALUES
((SELECT customer_id FROM customers WHERE phone='8800112244'), 'TXN002AMIT', 5, 479.00, 56, '2025-09-15', '2025-09-15', '2025-11-10', TRUE);

-- Verify the data
SELECT 'Customers Added:' as Info;
SELECT customer_id, first_name, last_name, phone, plan_id, connection_status FROM customers WHERE phone IN ('8800112233', '8800112244');

SELECT 'Today\'s Data Usage:' as Info;
SELECT d.customer_id, c.first_name, d.data_used_gb, d.daily_limit_gb, d.limit_exceeded 
FROM daily_data_usage d 
JOIN customers c ON d.customer_id = c.customer_id 
WHERE d.usage_date = CURDATE();

SELECT 'Notifications:' as Info;
SELECT n.customer_id, c.first_name, n.type, n.title, n.is_read 
FROM notifications n 
JOIN customers c ON n.customer_id = c.customer_id 
WHERE c.phone IN ('8800112233', '8800112244');

COMMIT;
