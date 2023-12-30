package org.zj.atm.project.common.constant;

/**
 * atm redis 前缀常量类
 */
public class RedisCacheConstant {

    /**
     * 用来根据用户身份证号加锁
     */
    public static final String LOCK_USER_REGISTER_KEY = "atm:user:lock_user-register:%s";

    /**
     * 本行 16 位银行卡号的生成记录
     * 该键下记录的是上一个银行卡号的 XXXXXX_________X 中间这 9 位
     */
    public static final String DEBIT_CARD_CREATE_LASTEST = "atm:debit-card:last-card-id";

    /**
     * 写银行表时的锁
     */
    public static final String LOCK_DEBIT_CARD_WRITE_KEY = "atm:debit-card:write";
}
