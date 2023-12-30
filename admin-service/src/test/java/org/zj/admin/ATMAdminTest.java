package org.zj.admin;

public class ATMAdminTest {

    public static final String SQL ="CREATE TABLE `t_user_%d` (\n" +
            "                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',\n" +
            "                            `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',\n" +
            "                            `identity_id` varchar(64) DEFAULT NULL COMMENT '身份证号',\n" +
            "                            `gender` tinyint(1) DEFAULT 0 COMMENT '用户性别 0：男 1：女',\n" +
            "                            `phone` varchar(64) DEFAULT NULL COMMENT '用户联系电话',\n" +
            "                            `address` varchar(256) DEFAULT NULL COMMENT '常住地址',\n" +
            "                            `freeze_flag` tinyint(1) DEFAULT 0 COMMENT '冻结标识 0：未冻结 1：已冻结',\n" +
            "                            `freeze_time` bigint(20) DEFAULT NULL COMMENT '上次冻结时间',\n" +
            "                            `deletion_time` bigint(20) DEFAULT NULL COMMENT '注销时间戳',\n" +
            "                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "                            `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "                            `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "                            PRIMARY KEY (`id`),\n" +
            "                            UNIQUE KEY idx_identity (`identity_id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";

    public static void main(String[] args) {
        for (int i = 0; i < 10; i ++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
