-- H2 数据库兼容版本的 schema.sql

CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
);

CREATE TABLE IF NOT EXISTS `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT '' COMMENT '操作用户名',
  `operation` varchar(50) DEFAULT '' COMMENT '操作内容',
  `method` varchar(200) DEFAULT '' COMMENT '请求方法名',
  `params` text COMMENT '请求参数',
  `ip_address` varchar(64) DEFAULT '' COMMENT 'IP地址',
  `duration` bigint DEFAULT '0' COMMENT '执行时长(毫秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sys_user_token` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `access_token` varchar(255) NOT NULL COMMENT '访问令牌',
  `access_token_expire_time` datetime NOT NULL COMMENT '访问令牌过期时间',
  `refresh_token` varchar(255) NOT NULL COMMENT '刷新令牌',
  `refresh_token_expire_time` datetime NOT NULL COMMENT '刷新令牌过期时间',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效 (1:是, 0:否)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token_access` (`access_token`),
  UNIQUE KEY `uk_token_refresh` (`refresh_token`)
);

CREATE TABLE IF NOT EXISTS `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(100) NOT NULL COMMENT '角色名称',
  `code` varchar(100) NOT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`)
);

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID (0表示顶级)',
  `name` varchar(100) NOT NULL COMMENT '权限名称 (例如: 用户管理)',
  `code` varchar(100) DEFAULT NULL COMMENT '权限标识符 (例如: user:create)',
  `type` int NOT NULL COMMENT '权限类型 (1:菜单, 2:按钮/API)',
  `sort_order` int DEFAULT '0' COMMENT '排序值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`code`)
);

CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
);

CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`)
);