-- 为已有数据库添加 role 字段的升级脚本
-- role: 0=普通用户, 1=管理员
-- 管理员账号由应用启动时自动创建（账号: admin 密码: admin123）

ALTER TABLE `user` ADD COLUMN `role` INT NOT NULL DEFAULT 0 AFTER `bio`;
