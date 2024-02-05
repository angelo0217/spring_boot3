CREATE TABLE books
(
    book_id          INT NOT NULL AUTO_INCREMENT,
    title            VARCHAR(255),
    author           VARCHAR(255),
    publication_date DATE,
    isbn             VARCHAR(20),
    genre            VARCHAR(100),
    price            DECIMAL(10, 2),
    quantity         INT,
    PRIMARY KEY (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE borrowing_records
(
    record_id     INT NOT NULL AUTO_INCREMENT,
    book_id       INT,
    borrower_name VARCHAR(255),
    borrow_date   DATE,
    return_date   DATE,
    status        VARCHAR(50),
    PRIMARY KEY (record_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE stock_data
(
    id INT NOT NULL AUTO_INCREMENT,
    stockCode                    VARCHAR(255) NOT NULL,
    tradeDate             BIGINT       ,
    time                  BIGINT       ,
    flat                  DOUBLE       ,
    floor                 DOUBLE       ,
    ceil                  DOUBLE       ,
    open                  DOUBLE       ,
    high                  DOUBLE       ,
    low                   DOUBLE       ,
    close                 DOUBLE       ,
    volume                INT          ,
    millionAmount         DOUBLE       ,
    previousClose         DOUBLE       ,
    previousVolume        INT          ,
    previousMillionAmount DOUBLE       ,
    PRIMARY KEY (id)
);
CREATE INDEX index_stock_code ON stock_data(stockCode);
CREATE INDEX index_date ON stock_data(tradeDate);