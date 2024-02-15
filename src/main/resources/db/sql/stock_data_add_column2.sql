ALTER TABLE watch_stock DROP COLUMN stock_data;
ALTER TABLE stock_data ADD COLUMN dataDate DATETIME default now();