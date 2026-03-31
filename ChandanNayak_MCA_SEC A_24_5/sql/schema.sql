CREATE DATABASE voting_system;

USE voting_system;

CREATE TABLE candidates (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    votes INT DEFAULT 0
);

INSERT INTO candidates (name) VALUES 
('Alice'),
('Bob'),
('Charlie');