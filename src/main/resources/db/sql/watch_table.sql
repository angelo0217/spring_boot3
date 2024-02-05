CREATE TABLE watch_stock
(
    id INT NOT NULL AUTO_INCREMENT,
    stockCode VARCHAR(255) NOT NULL,
    happenDate DATETIME DEFAULT NOW(),
    lastDateMoney DOUBLE NOT NULL,
    detectMoney DOUBLE NOT NULL,
    lastDayVolumes INT NOT NULL,
    detectVolumes INT NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX index_id_code ON watch_stock(id, stockCode);