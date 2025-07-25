-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.4.4 - MySQL Community Server - GPL
-- --------------------------------------------------------

-- -----------------------------------------------------
-- Section 1: 表结构创建 (Table Schema)
-- -----------------------------------------------------

--
-- 表 `sys_user`
--
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

--
-- 表 `sys_operation_log`
--
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作用户名',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作内容',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方法名',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'IP地址',
  `duration` bigint DEFAULT '0' COMMENT '执行时长(毫秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统操作日志表';

--
-- 表 `sys_user_token`
--
CREATE TABLE IF NOT EXISTS `sys_user_token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `access_token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问令牌',
  `access_token_expire_time` datetime NOT NULL COMMENT '访问令牌过期时间',
  `refresh_token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '刷新令牌',
  `refresh_token_expire_time` datetime NOT NULL COMMENT '刷新令牌过期时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效 (1:是, 0:否)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_access_token` (`access_token`) USING BTREE,
  UNIQUE KEY `uk_refresh_token` (`refresh_token`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证Token表';

--
-- 表 `sys_role`
--
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

--
-- 表 `sys_permission`
--
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID (0表示顶级)',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称 (例如: 用户管理)',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识符 (例如: user:create)',
  `type` int NOT NULL COMMENT '权限类型 (1:菜单, 2:按钮/API)',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

--
-- 表 `sys_user_role`
--
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`),
  KEY `fk_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与角色关联表';

--
-- 表 `sys_role_permission`
--
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `fk_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色与权限关联表';


-- -----------------------------------------------------
-- Section 2: 初始化数据 (Initial Data)
-- -----------------------------------------------------

--
-- 1. 初始化角色数据
--
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统最高权限拥有者'),
(2, '普通用户', 'USER', '拥有基本的查看权限');

--
-- 2. 初始化用户数据 (密码: 123456)
--
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`) VALUES
(1, 'admin', '$2a$10$Zt22V.Ipl/Eo2g1.QotgP.06PYSXYDSJEoR1ChJYziTxlQtV7dK/C', '超级管理员');

--
-- 3. 初始化权限数据 (菜单和API)
--
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (1, 0, '系统管理', 'system', 1, 0);
-- 用户管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (10, 1, '用户管理', 'system:user', 1, 10);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (11, 10, '查询用户', 'users:list', 2, 11);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (12, 10, '新增用户', 'users:create', 2, 12);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (13, 10, '修改用户', 'users:update', 2, 13);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (14, 10, '删除用户', 'users:delete', 2, 14);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (15, 10, '用户详情', 'users:detail', 2, 15);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (16, 10, '分配角色', 'users:assign_roles', 2, 16);
-- 角色管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (20, 1, '角色管理', 'system:role', 1, 20);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (21, 20, '查询角色', 'roles:list', 2, 21);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (22, 20, '新增角色', 'roles:create', 2, 22);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (23, 20, '修改角色', 'roles:update', 2, 23);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (24, 20, '删除角色', 'roles:delete', 2, 24);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (25, 20, '角色详情', 'roles:detail', 2, 25);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (26, 20, '分配权限', 'roles:assign_permissions', 2, 26);
-- 权限管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (30, 1, '权限管理', 'system:permission', 1, 30);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (31, 30, '查询权限', 'permissions:list', 2, 31);
-- 日志管理
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (40, 1, '日志管理', 'system:log', 1, 40);
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`) VALUES (41, 40, '查询日志', 'logs:list', 2, 41);
-- 文件管理菜单 (父菜单ID=0，作为一个顶级菜单)
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`)
VALUES (50, 0, '文件管理', 'system:file', 1, 50);
-- 文件上传权限 (父菜单ID=50, 即 '文件管理')
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `sort_order`)
VALUES (51, 50, '文件上传', 'files:upload', 2, 51);

--
-- 4. 初始化用户与角色关联数据
--
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

--
-- 5. 初始化角色与权限关联数据
--
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1),
(1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
(1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26),
(1, 30), (1, 31),
(1, 40), (1, 41),
(1, 50), (1, 51);