-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    role TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),
    sort_order INT DEFAULT 0,
    parent_id BIGINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 图书信息表
CREATE TABLE IF NOT EXISTS book_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50),
    publisher VARCHAR(100),
    publish_date DATE,
    category_id BIGINT,
    description TEXT,
    cover_image VARCHAR(255),
    price DECIMAL(10,2),
    total_quantity INT DEFAULT 0,
    available_quantity INT DEFAULT 0,
    location VARCHAR(50),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES book_category(id)
);

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status TINYINT DEFAULT 0,
    remark VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (book_id) REFERENCES book_info(id)
);

-- 插入图书分类数据
INSERT INTO book_category (name, code, description, sort_order, parent_id) VALUES
('文学', 'LITERATURE', '文学类图书', 1, 0),
('小说', 'NOVEL', '小说类图书', 2, 0),
('科技', 'TECHNOLOGY', '科技类图书', 3, 0),
('历史', 'HISTORY', '历史类图书', 4, 0),
('艺术', 'ART', '艺术类图书', 5, 0),
('教育', 'EDUCATION', '教育类图书', 6, 0),
('经济', 'ECONOMICS', '经济类图书', 7, 0),
('医学', 'MEDICINE', '医学类图书', 8, 0);

-- 插入示例图书数据
INSERT INTO book_info (isbn, title, author, publisher, publish_date, category_id, description, price, total_quantity, available_quantity, location, status) VALUES
('978-7-111-1', '红楼梦', '曹雪芹', '人民文学出版社', '2020-01-15', 1, '中国古典文学四大名著之一', 45.00, 10, 8, 'A区-01-01', 1),
('978-7-111-2', '西游记', '吴承恩', '人民文学出版社', '2020-03-20', 1, '中国古典文学四大名著之一', 42.00, 8, 6, 'A区-01-02', 1),
('978-7-111-3', '三体', '刘慈欣', '重庆出版社', '2019-06-01', 2, '科幻小说巅峰之作', 58.00, 15, 12, 'B区-02-01', 1),
('978-7-111-4', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '2021-08-10', 3, 'Java程序员必读经典', 108.00, 5, 3, 'C区-03-01', 1),
('978-7-111-5', 'Spring实战', 'Craig Walls', '人民邮电出版社', '2022-02-28', 3, 'Spring框架实战指南', 89.00, 6, 4, 'C区-03-02', 1),
('978-7-111-6', '明朝那些事儿', '当年明月', '中国友谊出版公司', '2018-11-01', 4, '历史通俗读物', 168.00, 7, 5, 'D区-04-01', 1),
('978-7-111-7', '艺术的故事', '贡布里希', '广西美术出版社', '2017-05-15', 5, '艺术史经典著作', 280.00, 3, 2, 'E区-05-01', 1),
('978-7-111-8', '深度学习', 'Ian Goodfellow', '人民邮电出版社', '2021-09-20', 3, '人工智能领域经典教材', 128.00, 4, 2, 'C区-03-03', 1);
