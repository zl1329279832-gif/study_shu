-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_management;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 1:删除 0:未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 1:删除 0:未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    menu_type TINYINT DEFAULT 1 COMMENT '菜单类型 1:菜单 2:按钮',
    visible TINYINT DEFAULT 1 COMMENT '是否可见 1:是 0:否',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 1:删除 0:未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 插入默认角色数据
INSERT INTO sys_role (role_name, role_code, description, status) VALUES 
('超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', 1),
('管理员', 'MANAGER', '普通管理员，拥有图书管理权限', 1),
('普通用户', 'USER', '普通用户，只能查询和借阅图书', 1);

-- 插入默认菜单数据
INSERT INTO sys_menu (menu_name, parent_id, path, component, icon, sort_order, menu_type, visible) VALUES 
('系统管理', 0, '/system', '', 'setting', 1, 1, 1),
('用户管理', 1, '/system/user', 'system/user/index', 'user', 1, 1, 1),
('角色管理', 1, '/system/role', 'system/role/index', 'role', 2, 1, 1),
('菜单管理', 1, '/system/menu', 'system/menu/index', 'menu', 3, 1, 1),
('图书管理', 0, '/book', '', 'book', 2, 1, 1),
('图书列表', 5, '/book/list', 'book/list/index', 'list', 1, 1, 1),
('图书分类', 5, '/book/category', 'book/category/index', 'category', 2, 1, 1),
('借阅管理', 0, '/borrow', '', 'borrow', 3, 1, 1),
('借阅记录', 8, '/borrow/record', 'borrow/record/index', 'record', 1, 1, 1),
('我的借阅', 8, '/borrow/my', 'borrow/my/index', 'my', 2, 1, 1);

-- 角色菜单关联（超级管理员拥有所有菜单）
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 1, id FROM sys_menu;

-- 管理员拥有图书管理和借阅管理
INSERT INTO sys_role_menu (role_id, menu_id) VALUES 
(2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

-- 普通用户只能查看图书和我的借阅
INSERT INTO sys_role_menu (role_id, menu_id) VALUES 
(3, 5), (3, 6), (3, 7), (3, 8), (3, 10);

-- 插入默认用户（密码：123456，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, phone, email, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '超级管理员', '13800138000', 'admin@library.com', 1),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', '13800138001', 'manager@library.com', 1),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '普通用户', '13800138002', 'user@library.com', 1);

-- 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(255) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 1:删除 0:未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书表
CREATE TABLE IF NOT EXISTS book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    author VARCHAR(100) COMMENT '作者',
    isbn VARCHAR(20) COMMENT 'ISBN',
    publisher VARCHAR(100) COMMENT '出版社',
    category_id BIGINT COMMENT '分类ID',
    stock INT DEFAULT 0 COMMENT '库存数量',
    status TINYINT DEFAULT 1 COMMENT '状态 1:可借 0:不可借',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 1:删除 0:未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_book_name (book_name),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    expect_return_time DATETIME COMMENT '预计归还时间',
    actual_return_time DATETIME COMMENT '实际归还时间',
    status TINYINT DEFAULT 1 COMMENT '状态 1:借阅中 2:已归还 3:已逾期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 插入图书分类数据
INSERT INTO book_category (category_name, category_code, description, sort_order, status) VALUES 
('文学小说', 'LITERATURE', '文学类小说作品', 1, 1),
('科技教育', 'TECH', '科技、教育类书籍', 2, 1),
('历史人文', 'HISTORY', '历史、人文类书籍', 3, 1),
('经济管理', 'ECONOMY', '经济、管理类书籍', 4, 1);

-- 插入图书数据
INSERT INTO book (book_name, author, isbn, publisher, category_id, stock, status) VALUES 
('红楼梦', '曹雪芹', '9787020002207', '人民文学出版社', 1, 50, 1),
('西游记', '吴承恩', '9787020008735', '人民文学出版社', 1, 45, 1),
('JavaScript高级程序设计', 'Zakas', '9787115545381', '人民邮电出版社', 2, 30, 1),
('深入理解Java虚拟机', '周志明', '9787111547310', '机械工业出版社', 2, 20, 1),
('明朝那些事儿', '当年明月', '9787505722460', '中国友谊出版公司', 3, 35, 1);

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(1, 1),
(2, 2),
(3, 3);
