package org.zj.atm.user;

//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.zj.atm.framework.starter.convention.exception.ClientException;
//import org.zj.atm.framework.starter.convention.exception.ServiceException;
//import org.zj.atm.user.common.enums.DebitCardErrorCodeEnum;
//
//import java.util.Arrays;
//import java.util.Objects;
//
//import static org.zj.atm.user.common.constant.RedisCacheConstant.DEBIT_CARD_CREATE_LASTEST;

public class debitCardTest {
//    public static void main(String[] args) {
//        System.out.println(createCheckDigit("625965087177209".toCharArray()));
//    }
//
//    // 加权因子
//    private static final int[] W = { 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1 };
//
//    /**
//     * 16位特定银行号码的正则表达式
//     */
//    private static final String REGEX_DEBIT_NO_15 = "^%s\\d{10}$";
//
//    /**
//     * 校验银行卡号码
//     *
//     * 适用于16位的特定配置银行卡号号码
//     */
//    public static boolean checkIDNo(String IDNo16, String cardPrefix) {
//        if (Objects.isNull(IDNo16)) {
//            throw new ClientException(DebitCardErrorCodeEnum.DEBIT_CARD_NULL);
//        }
//        // 校验前缀长度
//        if (!checkStrLength(cardPrefix, 6)) {
//            throw new ServiceException(DebitCardErrorCodeEnum.DEBIT_PREFIX_ERROR);
//        }
//        // 校验号码的长度
//        if (!checkStrLength(IDNo16, 16)) {
//            throw new ClientException(DebitCardErrorCodeEnum.DEBIT_CARD_REGIX_ERROR);
//        }
//        // 匹配身份证号码的正则表达式
//        if (!regexMatch(IDNo16, String.format(REGEX_DEBIT_NO_15, cardPrefix))) {
//            throw new ClientException(DebitCardErrorCodeEnum.DEBIT_CARD_REGIX_ERROR);
//        }
//        // 校验身份证号码的验证码
//        return validateCheckNumber(IDNo16);
//    }
//
//    /**
//     * 校验字符串长度
//     *
//     * @param inputString 字符串
//     * @param len 预期长度
//     * @return true - 校验通过<br>
//     *         false - 校验不通过
//     */
//    private static boolean checkStrLength(String inputString, int len) {
//        if (inputString == null || inputString.length() != len) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 匹配正则表达式
//     *
//     * @param inputString 字符串
//     * @param regex 正则表达式
//     * @return true - 校验通过<br>
//     *         false - 校验不通过
//     */
//    private static boolean regexMatch(String inputString, String regex) {
//        return inputString.matches(regex);
//    }
//
//    /**
//     * 校验码校验
//     */
//    private static boolean validateCheckNumber(String IDNo16) {
//        char[] IDNoArray = IDNo16.toCharArray();
//
//        return createCheckDigit(Arrays.copyOfRange(IDNoArray, 0, 15)) == IDNoArray[15];
//    }
//
//    /**
//     * 根据前 15 位生成第 16 位校验位
//     */
//    private static char createCheckDigit(char[] IDNo16Pre15) {
//        int sum = 0;
//        for (int i = 0; i < IDNo16Pre15.length; i ++) {
//            sum += processNum(Integer.parseInt(String.valueOf(IDNo16Pre15[i])) * W[i]);
//        }
//        return (char) ((10 - (sum % 10)) + '0');
//    }
//
//    /**
//     * 生成本行银行卡号
//     */
//    public static String createDebitCardId(String cardPrefix, StringRedisTemplate stringRedisTemplate) {
//        String lastCardId = stringRedisTemplate.opsForValue().get(DEBIT_CARD_CREATE_LASTEST);
//        if (Objects.isNull(lastCardId)) {
//            // 如果 lastCardId 为空，则说明还没初始化
//            String firstId = "000000001";
//            stringRedisTemplate.opsForValue().set(DEBIT_CARD_CREATE_LASTEST, firstId);
//            char checkDigit = createCheckDigit((cardPrefix + firstId).toCharArray());
//            return cardPrefix + firstId + checkDigit;
//        }
//        // 如果不为空
//        // 将字符串转换为整数并自增1
//        String incrementedId = String.valueOf(Integer.parseInt(lastCardId) + 1);
//        char checkDigit = createCheckDigit((cardPrefix + incrementedId).toCharArray());
//        // 将自增后的整数值转换回字符串并设置回 Redis
//        stringRedisTemplate.opsForValue().set(DEBIT_CARD_CREATE_LASTEST, incrementedId);
//        return cardPrefix + incrementedId + checkDigit;
//    }
//
//    /**
//     * 返回整数 num 每一位之和
//     */
//    private static int processNum(int num) {
//        int sum = 0;
//        while (num != 0) {
//            sum += num % 10;
//            num /= 10;
//        }
//        return sum;
//    }
}
