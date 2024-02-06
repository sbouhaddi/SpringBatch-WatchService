CREATE TABLE IF NOT EXISTS VISITORS
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    visitorId INT,
    firstName varchar(255),
    lastName varchar(255),
    emailAddress varchar(255),
    phoneNumber varchar(255),
    address varchar(255),
    visitDate varchar(255)
);