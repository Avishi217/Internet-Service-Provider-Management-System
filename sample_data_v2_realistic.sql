-- Sample Data for ISP Management System V2
-- Indian Telecom Plans (Airtel-style) with Realistic Data
-- Database: isp_management

USE isp_management;

-- ============================================
-- REALISTIC INDIAN TELECOM PLANS
-- ============================================

-- VOICE PLANS (Tariffed)
INSERT INTO plans (plan_name, plan_type, price_inr, old_price_inr, validity_days, data_per_day_gb, total_data_gb, voice_benefits, sms_per_day, description, status, is_addon) VALUES
('Basic Voice Plan', 'voice', 99.00, 79.00, 28, NULL, 0.20, '50% more talktime of ₹99, 1p/sec voice tariff', 0, '50% talktime + 200MB data for 28 days', 'active', FALSE);

-- UNLIMITED VOICE BUNDLES (Most Popular!)
INSERT INTO plans (plan_name, plan_type, price_inr, old_price_inr, validity_days, data_per_day_gb, total_data_gb, voice_benefits, sms_per_day, description, status, is_addon) VALUES
-- 28 Days Plans (EXACT from Airtel image)
('Unlimited Basic 2GB', 'unlimited', 179.00, 149.00, 28, NULL, 2.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 2 GB data', 'active', FALSE),
('Unlimited 1GB/day', 'unlimited', 265.00, 219.00, 28, 1.00, 28.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 1 GB/day data', 'active', FALSE),
('Unlimited 1.5GB/day', 'unlimited', 299.00, 249.00, 28, 1.50, 42.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 1.5 GB/day data', 'active', FALSE),
('Unlimited 2GB/day', 'unlimited', 359.00, 298.00, 28, 2.00, 56.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 2 GB/day data', 'active', FALSE),

-- 56 Days Plans (EXACT from Airtel image)
('Unlimited 56D 1.5GB/day', 'unlimited', 479.00, 399.00, 56, 1.50, 84.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 1.5 GB/day data', 'active', FALSE),
('Unlimited 56D 2GB/day', 'unlimited', 549.00, 449.00, 56, 2.00, 112.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 2 GB/day data', 'active', FALSE),

-- 84 Days Plans (EXACT from Airtel image)
('Unlimited 84D 6GB', 'unlimited', 455.00, 379.00, 84, NULL, 6.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 6 GB data', 'active', FALSE),
('Unlimited 84D 1.5GB/day', 'unlimited', 719.00, 598.00, 84, 1.50, 126.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 1.5 GB/day data', 'active', FALSE),
('Unlimited 84D 2GB/day', 'unlimited', 839.00, 698.00, 84, 2.00, 168.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 2 GB/day data', 'active', FALSE),

-- 365 Days Plans (EXACT from Airtel image)
('Unlimited Annual 24GB', 'unlimited', 1799.00, 1498.00, 365, NULL, 24.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 24 GB data', 'active', FALSE),
('Unlimited Annual 2GB/day', 'unlimited', 2999.00, 2498.00, 365, 2.00, 730.00, 'Unlimited calling', 100, 'Unlimited calling, 100 SMS/day, 2 GB/day data - BEST VALUE!', 'active', FALSE);

-- DATA TOP-UP PACKS (EXACT from Airtel image)
INSERT INTO plans (plan_name, plan_type, price_inr, old_price_inr, validity_days, data_per_day_gb, total_data_gb, voice_benefits, sms_per_day, description, status, is_addon, addon_validity_hours) VALUES
-- Midnight expiry addons (your custom feature!)
('Data Addon 1GB Midnight', 'data_topup', 15.00, NULL, 1, NULL, 1.00, NULL, NULL, '1 GB data valid till midnight', 'active', TRUE, 24),
('Data Addon 2GB Midnight', 'data_topup', 25.00, NULL, 1, NULL, 2.00, NULL, NULL, '2 GB data valid till midnight', 'active', TRUE, 24),
-- Unlimited validity addons (from Airtel image)
('Data Addon 3GB', 'data_topup', 58.00, 48.00, 0, NULL, 3.00, NULL, NULL, '3 GB data - Unlimited validity', 'active', TRUE, NULL),
('Data Addon 12GB', 'data_topup', 118.00, 98.00, 0, NULL, 12.00, NULL, NULL, '12 GB data - Unlimited validity', 'active', TRUE, NULL),
('Data Addon 50GB', 'data_topup', 301.00, 251.00, 0, NULL, 50.00, NULL, NULL, '50 GB data - Unlimited validity', 'active', TRUE, NULL);

-- ============================================
-- SAMPLE ADMIN USER
-- ============================================
INSERT INTO users (user_id, phone, role, status, created_at) VALUES
(1, '5550000001', 'ADMIN', 'active', NOW());

-- ============================================
-- SAMPLE CUSTOMERS WITH REALISTIC DATA
-- ============================================

-- Customer 1: Active with Unlimited Plus plan (2GB/day)
INSERT INTO users (user_id, phone, role, status, created_at) VALUES
(2, '9876543210', 'CUSTOMER', 'active', DATE_SUB(NOW(), INTERVAL 45 DAY));

INSERT INTO customers (customer_id, user_id, first_name, last_name, email, phone, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
(1, 2, 'Rahul', 'Sharma', 'rahul.sharma@example.com', '9876543210', 5, DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_ADD(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 45 DAY), 'active');

-- Customer 2: Active with Annual plan
INSERT INTO users (user_id, phone, role, status, created_at) VALUES
(3, '9988776655', 'CUSTOMER', 'active', DATE_SUB(NOW(), INTERVAL 90 DAY));

INSERT INTO customers (customer_id, user_id, first_name, last_name, email, phone, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
(2, 3, 'Priya', 'Patel', 'priya.patel@example.com', '9988776655', 12, DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 305 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), 'active');

-- Customer 3: Plan expired - needs recharge!
INSERT INTO users (user_id, phone, role, status, created_at) VALUES
(4, '8877665544', 'CUSTOMER', 'active', DATE_SUB(NOW(), INTERVAL 120 DAY));

INSERT INTO customers (customer_id, user_id, first_name, last_name, email, phone, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
(3, 4, 'Amit', 'Kumar', 'amit.kumar@example.com', '8877665544', NULL, NULL, NULL, DATE_SUB(CURDATE(), INTERVAL 120 DAY), 'expired');

-- Customer 4: New user, no plan yet
INSERT INTO users (user_id, phone, role, status, created_at) VALUES
(5, '7766554433', 'CUSTOMER', 'active', NOW());

INSERT INTO customers (customer_id, user_id, first_name, last_name, email, phone, plan_id, plan_activated_date, plan_expiry_date, registration_date, connection_status) VALUES
(4, 5, 'Sneha', 'Reddy', 'sneha.reddy@example.com', '7766554433', NULL, NULL, NULL, CURDATE(), 'active');

-- ============================================
-- REALISTIC DAILY DATA USAGE (Last 30 days for Customer 1)
-- ============================================

-- Generate daily usage data for past 30 days (Customer 1 - Rahul)
INSERT INTO daily_data_usage (customer_id, usage_date, plan_id, data_used_gb, daily_limit_gb, limit_exceeded) VALUES
-- Last 30 days with varying usage
(1, DATE_SUB(CURDATE(), INTERVAL 29 DAY), 5, 1.85, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 5, 2.10, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 27 DAY), 5, 1.45, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 26 DAY), 5, 1.92, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 5, 2.35, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 24 DAY), 5, 1.67, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 23 DAY), 5, 1.23, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 22 DAY), 5, 1.98, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 21 DAY), 5, 2.15, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 5, 1.56, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 19 DAY), 5, 1.78, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 18 DAY), 5, 1.34, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 17 DAY), 5, 1.89, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 16 DAY), 5, 2.05, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 5, 1.67, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 14 DAY), 5, 1.45, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 5, 1.92, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 5, 1.78, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 5, 1.56, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 5, 2.23, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 9 DAY), 5, 1.89, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 8 DAY), 5, 1.67, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 5, 1.98, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 5, 1.45, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 5, 1.78, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 5, 2.12, 2.00, TRUE),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 5, 1.56, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 5, 1.89, 2.00, FALSE),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 5, 1.67, 2.00, FALSE),
(1, CURDATE(), 5, 1.34, 2.00, FALSE);

-- ============================================
-- PLATFORM-WISE USAGE DATA (Today + Yesterday)
-- ============================================

-- Yesterday's usage by platform for Customer 1
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
-- Morning (6 AM - 12 PM)
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 7, 'messaging', 'WhatsApp', 45.50, 30),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 8, 'social_media', 'Instagram', 120.75, 45),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 9, 'browsing', 'Chrome', 85.20, 25),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 10, 'streaming', 'YouTube', 450.00, 60),

-- Afternoon (12 PM - 6 PM)
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 13, 'social_media', 'Facebook', 95.30, 35),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 14, 'messaging', 'WhatsApp', 32.10, 20),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 15, 'streaming', 'YouTube', 380.50, 50),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 16, 'social_media', 'Instagram', 145.80, 40),

-- Evening (6 PM - 12 AM) - PEAK HOURS!
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 19, 'streaming', 'Netflix', 850.00, 90),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 20, 'streaming', 'YouTube', 520.30, 65),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 21, 'social_media', 'Instagram', 180.40, 50),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 22, 'gaming', 'PUBG Mobile', 210.50, 45),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 23, 'messaging', 'WhatsApp', 28.90, 15);

-- Today's usage (so far)
INSERT INTO platform_usage (customer_id, usage_date, hour_of_day, platform_category, platform_name, data_used_mb, session_duration_minutes) VALUES
(1, CURDATE(), 8, 'messaging', 'WhatsApp', 38.20, 25),
(1, CURDATE(), 9, 'social_media', 'Instagram', 125.60, 40),
(1, CURDATE(), 10, 'browsing', 'Chrome', 75.40, 30),
(1, CURDATE(), 11, 'streaming', 'YouTube', 420.80, 55),
(1, CURDATE(), 12, 'social_media', 'Facebook', 88.50, 30),
(1, CURDATE(), 13, 'streaming', 'Hotstar', 380.20, 50);

-- ============================================
-- HOURLY USAGE STATS (Last 7 days - for peak hours graph)
-- ============================================

-- Sample hourly data for yesterday (Customer 1)
INSERT INTO hourly_usage_stats (customer_id, usage_date, hour_of_day, total_data_mb, upload_mb, download_mb) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 0, 45.20, 5.20, 40.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, 12.50, 2.50, 10.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 6, 35.80, 8.80, 27.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 7, 78.40, 12.40, 66.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 8, 165.95, 25.95, 140.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 9, 535.20, 85.20, 450.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 10, 450.00, 50.00, 400.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 11, 235.70, 35.70, 200.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 12, 158.30, 28.30, 130.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 13, 127.40, 22.40, 105.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 14, 412.60, 32.60, 380.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 15, 380.50, 30.50, 350.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 16, 326.20, 36.20, 290.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 17, 285.60, 45.60, 240.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 18, 445.80, 65.80, 380.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 19, 900.00, 50.00, 850.00),  -- PEAK!
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 20, 700.70, 80.70, 620.00),  -- PEAK!
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 21, 390.90, 60.90, 330.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 22, 239.40, 29.40, 210.00),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 23, 72.90, 14.90, 58.00);

-- ============================================
-- RECHARGE HISTORY & PAYMENTS
-- ============================================

-- Payment for Customer 1's current plan
INSERT INTO payment_transactions (transaction_id, customer_id, amount, payment_type, payment_method, gateway_transaction_id, gateway_name, upi_id, status, initiated_at, completed_at) VALUES
(1, 1, 359.00, 'recharge', 'upi', 'RAZP_TX_20250912_001', 'Razorpay', 'rahul@paytm', 'success', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY));

INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, validity_days, recharge_date, validity_start_date, validity_end_date, is_active) VALUES
(1, 1, 5, 359.00, 28, DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), DATE_ADD(CURDATE(), INTERVAL 16 DAY), TRUE);

-- Customer 2's annual plan purchase
INSERT INTO payment_transactions (transaction_id, customer_id, amount, payment_type, payment_method, gateway_transaction_id, gateway_name, card_last_4_digits, status, initiated_at, completed_at) VALUES
(2, 2, 2999.00, 'recharge', 'debit_card', 'RAZP_TX_20250814_002', 'Razorpay', '4532', 'success', DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY));

INSERT INTO recharge_history (customer_id, transaction_id, plan_id, amount_paid, validity_days, recharge_date, validity_start_date, validity_end_date, is_active) VALUES
(2, 2, 12, 2999.00, 365, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 305 DAY), TRUE);

-- ============================================
-- SAMPLE DATA ADDONS (Customer 1 bought addon yesterday)
-- ============================================

INSERT INTO payment_transactions (transaction_id, customer_id, amount, payment_type, payment_method, gateway_transaction_id, gateway_name, upi_id, status, initiated_at, completed_at) VALUES
(3, 1, 25.00, 'addon_purchase', 'upi', 'RAZP_TX_20251011_003', 'Razorpay', 'rahul@paytm', 'success', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO data_addons (customer_id, plan_id, data_amount_gb, price_inr, purchased_at, expires_at, is_expired, data_used_gb) VALUES
(1, 14, 2.00, 25.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(DATE_ADD(CURDATE(), INTERVAL 1 DAY), INTERVAL 1 SECOND), TRUE, 2.00);

-- ============================================
-- NOTIFICATIONS (Recent alerts for Customer 1)
-- ============================================

INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
(1, 'data_limit_alert', 'Daily Data Limit Alert', 'You have used 1.6GB of your 2GB daily limit. Only 400MB remaining!', TRUE, 'medium', DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(1, 'payment_success', 'Payment Successful', 'Your recharge of ₹359 for Unlimited Plus plan was successful. Validity: 28 days', TRUE, 'low', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(1, 'plan_expiry', 'Plan Expiring Soon', 'Your plan will expire in 16 days. Recharge now to continue services!', FALSE, 'high', NOW());

INSERT INTO notifications (customer_id, type, title, message, is_read, priority, created_at) VALUES
(3, 'plan_expiry', 'Plan Expired - Recharge Now!', 'Your plan has expired. Recharge now to activate your connection!', FALSE, 'high', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ============================================
-- SAMPLE COMPLAINT (Customer 1)
-- ============================================

INSERT INTO complaints (customer_id, ticket_number, subject, description, category, status, priority, created_at) VALUES
(1, 'TICKET_20251010_001', 'Slow internet speed in evening', 'Internet speed drops significantly between 8 PM to 11 PM. Expected 100 Mbps but getting only 20 Mbps.', 'technical', 'in_progress', 'medium', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================================
-- END OF SAMPLE DATA
-- ============================================
