package org.zj.atm.project.toolkit;

import java.security.SecureRandom;

/**
 * 六位唯一ID随机生成器
 */
public final class RandomGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成随机ID
     * @return ID
     */
    public static String generateRandomString() {
        return generateRandomString(6);
    }

    /**
     * 生成随机ID
     * @param length 生成多少位
     * @return ID
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i ++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }


}
