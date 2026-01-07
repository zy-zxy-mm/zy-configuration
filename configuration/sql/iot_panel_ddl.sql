-- IoT控制面板相关表DDL
-- 创建时间: 2026-01-05

-- 面板表
CREATE TABLE IF NOT EXISTS `iot_panel` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `panel_name` varchar(100) NOT NULL COMMENT '面板名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(32) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` int(1) DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_panel_name` (`panel_name`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT控制面板';

-- 页面表
CREATE TABLE IF NOT EXISTS `iot_panel_page` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `panel_id` varchar(36) NOT NULL COMMENT '面板ID',
  `page_name` varchar(100) NOT NULL COMMENT '页面名称',
  `canvas_width` int(11) DEFAULT 390 COMMENT '画布宽度',
  `canvas_height` int(11) DEFAULT 844 COMMENT '画布高度',
  `status` int(1) DEFAULT 0 COMMENT '状态 0草稿 1已发布',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` int(1) DEFAULT 0 COMMENT '是否删除 0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_panel_id` (`panel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT面板页面';

-- 版本表
CREATE TABLE IF NOT EXISTS `iot_panel_page_version` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `page_id` varchar(36) NOT NULL COMMENT '页面ID',
  `version_no` int(11) NOT NULL COMMENT '版本号',
  `config_json` longtext COMMENT '配置JSON',
  `change_log` varchar(255) DEFAULT NULL COMMENT '变更说明',
  `is_published` int(1) DEFAULT 0 COMMENT '是否已发布 0否 1是',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`page_id`),
  KEY `idx_version_no` (`page_id`, `version_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT面板页面版本';

-- 发布表
CREATE TABLE IF NOT EXISTS `iot_panel_page_publish` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `page_id` varchar(36) NOT NULL COMMENT '页面ID',
  `version_id` varchar(36) NOT NULL COMMENT '版本ID',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `publish_by` varchar(50) DEFAULT NULL COMMENT '发布人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_page_id` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT面板页面发布记录';

-- 命令审计表
CREATE TABLE IF NOT EXISTS `iot_command_audit` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `trace_id` varchar(64) DEFAULT NULL COMMENT '追踪ID',
  `operator_id` varchar(64) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人名称',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `command_key` varchar(64) DEFAULT NULL COMMENT '命令键',
  `params_json` text COMMENT '参数JSON',
  `result_code` int(11) DEFAULT NULL COMMENT '结果码',
  `result_msg` varchar(255) DEFAULT NULL COMMENT '结果消息',
  `cost_ms` int(11) DEFAULT NULL COMMENT '耗时(毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_trace_id` (`trace_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IoT命令审计记录';
