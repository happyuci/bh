package com.sfwl.bh.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class RandomStrUtil {

    private static final String DEFAULT_SOURCE = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String STRING_SOURCE1 = DEFAULT_SOURCE;
    // 不包含大写字母D、I、S、O、B
    public static final String STRING_SOURCE2 = "0123456789ACEFGHJKLMNPQRTUVWXYZ";
    // 不包含大写字母D、I、S、O、B、Z
    public static final String STRING_SOURCE3 = "0123456789ACEFGHJKLMNPQRTUVWXY";

    public static String getRandomStringByLength(int length, String source) {
        String base = StringUtils.isBlank(source) ? DEFAULT_SOURCE : source;
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomStringByLength(int length) {
        return getRandomStringByLength(length, null);
    }

    public static void main(String[] arg) {
        for (int i = 0; i <= 20; i++) {
            System.out.println(getRandomStringByLength(12, STRING_SOURCE2));
        }
    }
}
