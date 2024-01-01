package org.zj.atm.project.common.enums;

import lombok.AllArgsConstructor;
import org.zj.atm.framework.starter.convention.errocode.IErrorCode;

/**
 * 用户申请银行卡错误码枚举
 */
@AllArgsConstructor
public enum DebitCardErrorCodeEnum implements IErrorCode {

    USER_REGISTER_FAIL("A006000", "用户注册失败"),

    PASSWORD_MUST_SIX("A006001", "密码必须为6位"),

    PASSWORD_NOTNULL("A006002", "密码不能为空"),

    PHONE_NOTNULL("A006003", "手机号不能为空"),

    ID_TYPE_NOTNULL("A006004", "证件类型不能为空"),

    ID_CARD_NOTNULL("A006005", "证件号不能为空"),

    ID_CARD_VERIFY_ERROR("A006006", "证件号不符合第二代身份证"),

    ID_CARD_REGISTERED("A006007", "证件号已被占用"),

    MAIL_REGISTERED("A006008", "邮箱已被占用"),

    MAIL_NOTNULL("A006009", "邮箱不能为空"),

    USER_TYPE_NOTNULL("A006010", "旅客类型不能为空"),

    POST_CODE_NOTNULL("A006011", "邮编不能为空"),

    ADDRESS_NOTNULL("A006012", "地址不能为空"),

    REGION_NOTNULL("A006012", "国家/地区不能为空"),

    TELEPHONE_NOTNULL("A006013", "固定电话不能为空"),

    VERIFY_STATE_NOTNULL("A006014", "审核状态不能为空"),

    REAL_NAME_NOTNULL("A006015", "真实姓名不能为空"),

    AGE_TOO_YOUNG("A006016", "年龄不能小于 18 岁"),

    DEBIT_CARD_NULL("A006100", "银行卡号不能为空"),

    DEBIT_CARD_REGIX_ERROR("A006101", "非本行银行卡号"),

    DEBIT_CARD_NUM_BEYOND_UPPER_LIMIT("A006102", "持卡数量以达上限"),

    DEBIT_CARD_FREEZED("A006103", "该卡已被冻结，请联系前台柜员"),

    DEBIT_LOGIN_FAIL_UPPER("A006104", "24小时内输入密码错误超过3次，该银行卡以被冻结，请等待24小时或联系管理员"),

    DEBIT_CARD_LOGIN_DUPLICATE("A006105", "该银行卡重复登录"),

    DEBIT_CARD_LOGIN_MATCH_PWD_FAIL("A006106", "密码错误"),

    DEBIT_PREFIX_ERROR("B006102", "请管理员检测本行银行卡前缀"),

    DEBIT_APPLY_BLOCK("B006100", "申请卡号拥塞，请稍后再试...");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示消息
     */
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
