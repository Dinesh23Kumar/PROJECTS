CREATE DATABASE IF NOT EXISTS EyeCheckupDB;

-- Use the database
USE EyeCheckupDB;

-- Create the eye_checkup table
CREATE TABLE IF NOT EXISTS eye_checkup (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender CHAR(1) NOT NULL, 
    result VARCHAR(255) DEFAULT 'Eyes are healthy.',
    UNIQUE(name) -- Ensure that the user name is unique for registering and login
);

-- Insert sample data (optional)
INSERT INTO eye_checkup (name, age, gender) VALUES 
('John Doe', 30, 'M'),
('Jane Smith', 25, 'F');

-- You can add more tables if necessary for detailed test results (optional)
-- Example table to store results of individual tests, if needed
CREATE TABLE IF NOT EXISTS eye_test_results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eye_checkup_id INT,
    test_name VARCHAR(100),
    result VARCHAR(50),
    FOREIGN KEY (eye_checkup_id) REFERENCES eye_checkup(id)
);




