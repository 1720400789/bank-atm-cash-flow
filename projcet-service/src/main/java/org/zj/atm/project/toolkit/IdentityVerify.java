package org.zj.atm.project.toolkit;

import org.zj.atm.framework.starter.convention.exception.ClientException;
import org.zj.atm.project.common.enums.DebitCardErrorCodeEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
     */
    public static boolean checkIDNo(String IDNo18) {
        if (Objects.isNull(IDNo18)) {
            throw new ClientException(DebitCardErrorCodeEnum.ID_CARD_NOTNULL);
        }
        // 校验身份证号码的长度
        if (!checkStrLength(IDNo18, 18)) {
            throw new ClientException(DebitCardErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        }
        // 匹配身份证号码的正则表达式
        if (!regexMatch(IDNo18, REGEX_ID_NO_18)) {
            throw new ClientException(DebitCardErrorCodeEnum.ID_CARD_VERIFY_ERROR);
        }
        // 校验身份证号码的验证码
        return validateCheckNumber(IDNo18);
    }

    /**
     * 判断是否大于等于 18 岁
     */
    public static boolean checkAge(String IDNo18) {
        String ageStr = IDNo18.substring(6, 14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date givenDate = null; // 将字符串解析为日期类型
        try {
            givenDate = sdf.parse(ageStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 创建 Calendar 实例并设置日期
        Calendar givenCalendar = Calendar.getInstance();
        givenCalendar.setTime(givenDate);

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date());

        int givenYear = givenCalendar.get(Calendar.YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int yearDifference = currentYear - givenYear;
        if (yearDifference < 18) {
            return false;
        }

        if (yearDifference == 18 && givenCalendar.get(Calendar.MONTH) > currentCalendar.get(Calendar.MONTH)) {
            return false;
        }

        if (yearDifference == 18 && givenCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) && givenCalendar.get(Calendar.DAY_OF_MONTH) > currentCalendar.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

    /**
     * 校验字符串长度
     *
     * @param inputString 字符串
     * @param len 预期长度
     * @return true - 校验通过<br>
     *         false - 校验不通过
     */
    public static boolean checkStrLength(String inputString, int len) {
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
