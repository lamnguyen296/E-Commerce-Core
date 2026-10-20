-- ==========================================================
-- STOREFRONT SAMPLE SEED DATA
-- Database: ecommerce (MySQL)
-- ==========================================================

USE `ecommerce`;

-- 1. Optional Cleanup
-- DELETE FROM inventories WHERE id LIKE 'inv-%';
-- DELETE FROM cart_items WHERE id LIKE 'ci-%';
-- DELETE FROM product_variants WHERE id LIKE 'var-%';
-- DELETE FROM products WHERE id LIKE 'prod-%';
-- DELETE FROM categories WHERE id LIKE 'cat-%';

-- 2. Insert / Update 4 Featured Categories
INSERT INTO `categories` (`id`, `name`, `slug`, `description`, `image_url`, `featured`, `item_count_label`, `created_at`, `deleted`)
VALUES
('cat-shoes', 'Fashion Footwear', 'fashion-footwear', 'Curated premium collection of athletic and lifestyle footwear', 'https://images.unsplash.com/photo-1549298916-b41d501d3772?q=80&w=800&auto=format&fit=crop', 1, 'Over 500+ latest models', NOW(), 0),
('cat-watches', 'Luxury Watches', 'luxury-watches', 'Timeless mechanical craftsmanship and sophisticated luxury timepieces', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=800&auto=format&fit=crop', 1, 'Authentic Swiss & Japanese timepieces', NOW(), 0),
('cat-electronics', 'Electronics', 'electronics', 'Cutting-edge audio devices, computing, and high-performance tech', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=800&auto=format&fit=crop', 1, '12-24 months official warranty', NOW(), 0),
('cat-accessories', 'Accessories', 'accessories', 'Designer sunglasses, premium leather goods, and modern lifestyle essentials', 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=800&auto=format&fit=crop', 1, '2024 Fashion Trend', NOW(), 0)
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `slug` = VALUES(`slug`),
    `description` = VALUES(`description`),
    `image_url` = VALUES(`image_url`),
    `featured` = VALUES(`featured`),
    `item_count_label` = VALUES(`item_count_label`);

-- 3. Insert / Update 4 Featured Products
INSERT INTO `products` (`id`, `category_id`, `name`, `slug`, `description`, `brand`, `image_url`, `featured`, `created_at`, `deleted`)
VALUES
('prod-air-max-270', 'cat-shoes', 'Air Max 270 React Blue', 'air-max-270-react-blue', 'Modern lifestyle running shoe featuring responsive Max Air cushioning for all-day comfort.', 'NIKE', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop', 1, NOW(), 0),
('prod-sony-wh1000xm4', 'cat-electronics', 'WH-1000XM4 Noise Canceling', 'wh-1000xm4-noise-canceling', 'Industry-leading wireless noise canceling headphones with premium Hi-Res audio experience.', 'SONY', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop', 1, NOW(), 0),
('prod-seiko-presage', 'cat-watches', 'Presage Cocktail Time', 'presage-cocktail-time', 'Exquisite automatic mechanical watch with a sunburst textured dial inspired by classic cocktail bars.', 'SEIKO', 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?q=80&w=800&auto=format&fit=crop', 1, NOW(), 0),
('prod-rayban-aviator', 'cat-accessories', 'Classic Aviator Gold', 'classic-aviator-gold', 'Timeless aviator sunglasses featuring a gold-toned frame and premium polarized crystal lenses.', 'RAY-BAN', 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=800&auto=format&fit=crop', 1, NOW(), 0)
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `slug` = VALUES(`slug`),
    `description` = VALUES(`description`),
    `brand` = VALUES(`brand`),
    `image_url` = VALUES(`image_url`),
    `featured` = VALUES(`featured`);

-- 4. Insert / Update Product Variants
INSERT INTO `product_variants` (`id`, `product_id`, `sku`, `price`, `original_price`, `image_url`, `created_at`, `deleted`)
VALUES
('var-air-max-270-default', 'prod-air-max-270', 'NK-AM270-BLU-42', 2450000.0000, 3100000.0000, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=800&auto=format&fit=crop', NOW(), 0),
('var-sony-wh1000xm4-default', 'prod-sony-wh1000xm4', 'SN-WH1000XM4-SLV', 8990000.0000, NULL, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=800&auto=format&fit=crop', NOW(), 0),
('var-seiko-presage-default', 'prod-seiko-presage', 'SK-SRPB43J1-SLV', 12500000.0000, NULL, 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?q=80&w=800&auto=format&fit=crop', NOW(), 0),
('var-rayban-aviator-default', 'prod-rayban-aviator', 'RB-3025-001-GLD', 4200000.0000, 4900000.0000, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=800&auto=format&fit=crop', NOW(), 0)
ON DUPLICATE KEY UPDATE 
    `price` = VALUES(`price`),
    `original_price` = VALUES(`original_price`),
    `image_url` = VALUES(`image_url`);

-- 5. Insert / Update Inventory:
-- Nike: 35 (IN STOCK)
-- Sony: 0 (OUT OF STOCK - matches mockup)
-- Seiko: 10 (IN STOCK)
-- Ray-Ban: 20 (IN STOCK)
INSERT INTO `inventories` (`id`, `product_variant_id`, `quantity_in_stock`, `version`, `created_at`, `deleted`)
VALUES
('inv-air-max-270', 'var-air-max-270-default', 35, 0, NOW(), 0),
('inv-sony-wh1000xm4', 'var-sony-wh1000xm4-default', 0, 0, NOW(), 0),
('inv-seiko-presage', 'var-seiko-presage-default', 10, 0, NOW(), 0),
('inv-rayban-aviator', 'var-rayban-aviator-default', 20, 0, NOW(), 0)
ON DUPLICATE KEY UPDATE 
    `quantity_in_stock` = VALUES(`quantity_in_stock`);

-- 6. Insert / Update Sample Discounts
INSERT INTO `discounts` (
    `id`, `code`, `name`, `description`, `discount_type`, 
    `discount_value`, `max_discount_amount`, `min_order_value`, 
    `start_date`, `end_date`, `usage_limit`, `usage_count`, 
    `is_active`, `user_id`, `created_at`, `deleted`
) VALUES
('disc-welcome-10', 'WELCOME10', 'Giảm 10% Thành Viên Mới', 'Giảm 10% tối đa 500.000 ₫ cho đơn từ 1.000.000 ₫', 'PERCENTAGE', 10.0000, 500000.0000, 1000000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 1000, 0, 1, NULL, NOW(), 0),
('disc-sale-200k', 'SALE200K', 'Giảm ngay 200.000 ₫', 'Ưu đãi giảm 200.000 ₫ cho đơn từ 2.000.000 ₫', 'FIXED_AMOUNT', 200000.0000, NULL, 2000000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 500, 0, 1, NULL, NOW(), 0),
('disc-vip-500k', 'VIP500K', 'Ưu Đãi Đơn Lớn 500.000 ₫', 'Giảm 500.000 ₫ cho đơn hàng từ 5.000.000 ₫', 'FIXED_AMOUNT', 500000.0000, NULL, 5000000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 100, 0, 1, NULL, NOW(), 0),
('disc-min-50m', 'MIN50M', 'Giảm 1.000.000 ₫ Đơn 50Tr', 'Mã dành riêng cho đơn hàng trên 50.000.000 ₫', 'FIXED_AMOUNT', 1000000.0000, NULL, 50000000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 50, 0, 1, NULL, NOW(), 0),
('disc-expired', 'EXPIRED50', 'Khuyến Mãi Hết Hạn', 'Mã đã hết hạn sử dụng', 'PERCENTAGE', 50.0000, 2000000.0000, 500000.0000, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 100, 0, 1, NULL, NOW(), 0),
('disc-maxout', 'MAXOUT', 'Mã Đã Hết Lượt', 'Mã đã đạt tối đa số lượt sử dụng', 'FIXED_AMOUNT', 100000.0000, NULL, 500000.0000, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 10, 10, 1, NULL, NOW(), 0)
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `description` = VALUES(`description`),
    `discount_type` = VALUES(`discount_type`),
    `discount_value` = VALUES(`discount_value`),
    `max_discount_amount` = VALUES(`max_discount_amount`),
    `min_order_value` = VALUES(`min_order_value`),
    `start_date` = VALUES(`start_date`),
    `end_date` = VALUES(`end_date`),
    `usage_limit` = VALUES(`usage_limit`),
    `usage_count` = VALUES(`usage_count`),
    `is_active` = VALUES(`is_active`);

-- 7. Insert / Update Exclusive Discount for Admin
INSERT INTO `discounts` (
    `id`, `code`, `name`, `description`, `discount_type`, 
    `discount_value`, `max_discount_amount`, `min_order_value`, 
    `start_date`, `end_date`, `usage_limit`, `usage_count`, 
    `is_active`, `user_id`, `created_at`, `deleted`
)
SELECT 
    'disc-exclusive-admin', 'EXCLUSIVE20', 'Voucher Tri Ân Admin 20%', 'Voucher giảm 20% độc quyền dành riêng cho tài khoản Admin', 
    'PERCENTAGE', 20.0000, 1000000.0000, 2000000.0000, 
    NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 5, 0, 1, u.id, NOW(), 0
FROM `users` u 
WHERE u.username = 'admin'
LIMIT 1
ON DUPLICATE KEY UPDATE 
    `name` = VALUES(`name`),
    `discount_value` = VALUES(`discount_value`);

SELECT 'Storefront & Discounts Data Seeded Successfully!' AS Result;
