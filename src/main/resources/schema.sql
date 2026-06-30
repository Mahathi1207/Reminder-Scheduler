CREATE TABLE categories (
                            category_id INT AUTO_INCREMENT PRIMARY KEY,
                            username varchar(50) NOT NULL,
                            category_name VARCHAR(100) NOT NULL,
                            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE,
                            UNIQUE (user_id, category_name)  -- Ensures categories are unique per user
);
CREATE TABLE reminders (
                           reminder_id INT AUTO_INCREMENT PRIMARY KEY,
                           category_id INT NOT NULL,
                           reminder_datetime DATETIME NOT NULL,
                           reminder_medium ENUM('SMS', 'Email', 'Both') NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);

