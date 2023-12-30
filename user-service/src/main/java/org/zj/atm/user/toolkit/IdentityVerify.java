package org.zj.atm.user.toolkit;

import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.user.common.enums.UserRegisterErrorCodeEnum;

import java.util.Objects;

/**
 * 身份证校验类
 */
public final class IdentityVerify {
    // 加权因子
    private static final int[] W = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

    /**
     * 18位二代身份证号码的正则表达式
     */
    private static final String REGEX_ID_NO_18 = "^"
            + "\\d{6}" // 6位地区码
            + "(18|19|([23]\\d))\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}" // 3位顺序码
            + "[0-9Xx]" // 校验码
            + "$";

    /**
     * 校验身份证号码
     *
     * <p>
     * 适用于18位的二代身份证号码
     * </p>
     *
     * @param IDNo18 身份证号码
     * @return true - 校验通过<br>
     *         false - 校验不通过
     * @throws IllegalArgumentException
     *             如果身份证号码为空或长度不为18位或不满足身份证号码组成规则
     *             <i>6位地址码+
     *             出生年月日YYYYMMDD+3位顺序码
     *             +0~9或X(x)校验码</i>
     */
    public static boolean checkIDNo(String IDNo18) {
        if (Objects.isNull(IDNo18)) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_NOTNULL);
        }
        // 校验身份证号码的长度
        if (!checkStrLength(IDNo18, 18)) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        }
        // 匹配身份证号码的正则表达式
        if (!regexMatch(IDNo18, REGEX_ID_NO_18)) {
            throw new ClientException(UserRegisterErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        }
        // 校验身份证号码的验证码
        return validateCheckNumber(IDNo18);
    }

    /**
     * 校验字符串长度
     *
     * @param inputString 字符串
     * @param len 预期长度
     * @return true - 校验通过<br>
     *         false - 校验不通过
     */
    private static boolean checkStrLength(String inputString, int len) {
        if (inputString == null || inputString.length() != len) {
            return false;
        }
        return true;
    }

    /**
     * 匹配正则表达式
     *
     * @param inputString 字符串
     * @param regex 正则表达式
     * @return true - 校验通过<br>
     *         false - 校验不通过
     */
    private static boolean regexMatch(String inputString, String regex) {
        return inputString.matches(regex);
    }

    /**
     * 校验码校验
     * <p>
     * 适用于18位的二代身份证号码
     * </p>
     *
     * @param IDNo18 身份证号码
     * @return true - 校验通过<br>
     *         false - 校验不通过
     */
    private static boolean validateCheckNumber(String IDNo18) {
        char[] IDNoArray = IDNo18.toCharArray();
        int sum = 0;
        for (int i = 0; i < W.length; i++) {
            sum += Integer.parseInt(String.valueOf(IDNoArray[i])) * W[i];
        }
        // 校验位是X，则表示10
        if (IDNoArray[17] == 'X' || IDNoArray[17] == 'x') {
            sum += 10;
        } else {
            sum += Integer.parseInt(String.valueOf(IDNoArray[17]));
        }
        // 如果除11模1，则校验通过
        return sum % 11 == 1;
    }
}
