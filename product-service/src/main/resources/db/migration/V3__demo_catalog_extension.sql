-- Extra demo categories and products for browsing, cart, and checkout flows.
INSERT INTO product_schema.categories (name, description) VALUES
  ('Sports & Outdoors', 'Fitness and outdoor essentials'),
  ('Snacks', 'Pantry picks for quick demos');

INSERT INTO product_schema.products (name, description, price, stock_quantity, image_url, category_id) VALUES
  -- Electronics (1)
  ('Mechanical Keyboard', 'Compact 75% layout, hot-swappable switches', 89.99, 40, 'https://placehold.co/400x300?text=Keyboard', 1),
  ('Noise-Cancelling Headphones', 'Over-ear ANC, 30h battery', 199.00, 25, 'https://placehold.co/400x300?text=Headphones', 1),
  ('Portable SSD 1TB', 'USB-C, up to 1050 MB/s read', 129.99, 55, 'https://placehold.co/400x300?text=SSD', 1),
  ('1080p Webcam', 'Autofocus, dual mics, privacy shutter', 79.50, 70, 'https://placehold.co/400x300?text=Webcam', 1),
  -- Books (2)
  ('The Pragmatic Programmer', 'Classic career and craft advice', 49.99, 85, 'https://placehold.co/400x300?text=Pragmatic', 2),
  ('Designing Data-Intensive Applications', 'Foundations for reliable systems', 56.00, 30, 'https://placehold.co/400x300?text=DDIA', 2),
  -- Home (3)
  ('Ceramic Mug Set (4)', 'Microwave-safe, matte glaze', 24.99, 100, 'https://placehold.co/400x300?text=Mugs', 3),
  ('Non-Slip Yoga Mat', '6mm extra thick, carrying strap', 32.50, 45, 'https://placehold.co/400x300?text=Yoga', 3),
  ('Compact Air Fryer', '2.1 L basket, 8 presets', 89.00, 20, 'https://placehold.co/400x300?text=AirFryer', 3),
  -- Sports (4 — new category from this migration)
  ('Insulated Water Bottle', '24 oz stainless, leak-proof lid', 28.00, 90, 'https://placehold.co/400x300?text=Bottle', 4),
  ('Adjustable Dumbbells Pair', '5–25 lb each, quick dial', 249.99, 12, 'https://placehold.co/400x300?text=Dumbbells', 4),
  -- Snacks (5 — new category from this migration)
  ('Trail Mix Variety Pack', '12 single-serve pouches', 18.99, 150, 'https://placehold.co/400x300?text=TrailMix', 5),
  ('Dark Chocolate Bar 70%', '10 bars, fair trade', 22.50, 200, 'https://placehold.co/400x300?text=Chocolate', 5);
