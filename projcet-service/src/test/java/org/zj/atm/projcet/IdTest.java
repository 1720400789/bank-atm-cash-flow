package org.zj.atm.projcet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IdTest {
    public static void main(String[] args) {
        System.out.println(checkAge("430426200512300073"));
    }

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
}
