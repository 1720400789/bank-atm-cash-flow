CREATE TABLE `t_user` (
                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
                            `identity_id` varchar(64) DEFAULT NULL COMMENT '身份证号',
                            `gender` tinyint(1) DEFAULT 0 COMMENT '用户性别 0：男 1：女',
                            `phone` varchar(64) DEFAULT NULL COMMENT '用户联系电话',
                            `address` varchar(256) DEFAULT NULL COMMENT '常住地址',
                            `former_card_num` int DEFAULT 0 COMMENT '历史持卡数量',
                            `freeze_flag` tinyint(1) DEFAULT 0 COMMENT '冻结标识 0：未冻结 1：已冻结 如果已经冻结该用户,则说明该用户异常,禁用该用户所有操作,提示人工解封',
                            `freeze_time` bigint(20) DEFAULT NULL COMMENT '上次冻结时间',
                            `deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0：未删除 1：已删除',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY idx_identity (`identity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `t_debit_card` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `debit_card_id` varchar(64) DEFAULT NULL COMMENT '银行卡号',
                            `pwd` varchar(64) DEFAULT NULL COMMENT '银行卡号对应密码，本行必须为 6 位数字',
                            `identity_id` varchar(64) DEFAULT NULL COMMENT '用户身份证号',
                            `phone` varchar(64) DEFAULT NULL COMMENT '联系电话',
                            `initial_balance` DECIMAL(25, 6) DEFAULT NULL COMMENT '开户时的账户金额',
                            `account_balance` DECIMAL(25, 6) DEFAULT NULL COMMENT '当前的账户余额',
                            `card_type` tinyint(1) DEFAULT 0 COMMENT '银行卡类型',
                            `rate` DECIMAL(10, 8) DEFAULT NULL COMMENT '存储利率',
                            `card_status` tinyint(1) DEFAULT 0 COMMENT '银行卡状态 0：正常 1：冻结',
                            `freeze_time` bigint(20) DEFAULT NULL COMMENT '上次冻结时间',
                            `deletion_time` bigint(20) DEFAULT 0 COMMENT '注销时间戳',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                            `del_flag` tinyint(1) DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY idx_uid_cid (`debit_card_id`, `identity_id`, `deletion_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# 路由表,因为 debit_card 表是根据 identity_id 分片的,考虑到之后会有根据银行卡号查询的情况,为了防止读扩散,使用路由表
CREATE TABLE `t_debit_card_goto` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `debit_card_id` varchar(64) DEFAULT NULL COMMENT '银行卡号',
                                `identity_id` varchar(64) DEFAULT NULL COMMENT '用户身份证号',
                                `deletion_time` bigint(20) DEFAULT 0 COMMENT '注销时间戳',
                                `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0：未删除 1：已删除',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY idx_uid_cid (`debit_card_id`, `identity_id`, `deletion_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;