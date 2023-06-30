CREATE TABLE books (
   book_id INT PRIMARY KEY,
   title VARCHAR(255),
   author VARCHAR(255),
   publication_date DATE,
   isbn VARCHAR(20),
   genre VARCHAR(100),
   price DECIMAL(10, 2),
   quantity INT
);

CREATE TABLE borrowing_records (
   record_id INT PRIMARY KEY,
   book_id INT,
   borrower_name VARCHAR(255),
   borrow_date DATE,
   return_date DATE,
   status VARCHAR(50)
);