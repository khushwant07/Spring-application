INSERT INTO product_schema.categories (name, description) VALUES
  ('Electronics', 'Devices and accessories'),
  ('Books', 'Physical and bestsellers'),
  ('Home', 'Everyday home items');

INSERT INTO product_schema.products (name, description, price, stock_quantity, image_url, category_id)
VALUES
  ('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 120, 'https://placehold.co/400x300?text=Mouse', 1),
  ('USB-C Hub', '7-in-1 USB-C hub', 49.50, 45, 'https://placehold.co/400x300?text=Hub', 1),
  ('Clean Code', 'Software craftsmanship', 42.00, 200, 'https://placehold.co/400x300?text=Book', 2),
  ('Desk Lamp', 'LED desk lamp', 35.00, 60, 'https://placehold.co/400x300?text=Lamp', 3);
