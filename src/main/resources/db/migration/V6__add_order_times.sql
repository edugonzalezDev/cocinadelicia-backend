-- V6__add_order_times.sql
ALTER TABLE customer_order
  ADD COLUMN requested_at TIMESTAMP NULL AFTER notes;

ALTER TABLE customer_order
  ADD COLUMN delivered_at TIMESTAMP NULL AFTER requested_at;

CREATE INDEX idx_order_requested_at ON customer_order (requested_at);
