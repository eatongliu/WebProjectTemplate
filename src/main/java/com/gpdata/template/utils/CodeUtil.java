package com.gpdata.template.utils;

import java.util.Random;

/**
 * Created by acer_liuyutong on 2017/3/20.
 */
public class CodeUtil {

    /*
    *  把中文字符串转换为十六进制Unicode编码字符串
    */
    public static String stringToUnicode(String s) {
        StringBuilder builder = new StringBuilder();
//        s.chars().forEach();
        return "";
    }

    /**
     * 把十六进制Unicode编码字符串转换为中文字符串
     */
    public static String unicodeToString(String str) {
        return "";
    }

    /**
     * 生成随机字符
     * @param length 随机字符长度
     * @return
     */
    public static String getCode(int length) { // length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // 直接以Unicode字符串的方式初始化字符串时，会自动

        String s1 = "\\配\\置\\成\\功\\，\\重\\启\\后\\生\\效";
        System.out.println("s1: " + s1);
        //转换汉字为Unicode码
        String s2 = "配置成功，重启后生效";
        s2 = CodeUtil.stringToUnicode(s2);
        System.out.println("s2: " + s2);
        //转换Unicode码为汉字

        String aaa = "\u4ec0\u4e48\u662f\u5b89\u5168\u63a7\u4ef6\uff1f###\u5b89\u5168\u63a7\u4ef6\u53ef\u4ee5\u4fdd\u8bc1\u7528\u6237\u7684\u5bc6\u7801\u4e0d\u88ab\u7a83\u53d6\uff0c\u4ece\u800c\u4fdd\u8bc1\u8d44\u91d1\u5b89\u5168";
        String s3 = CodeUtil.unicodeToString(aaa);
        System.out.println("s3: " + s3);
    }

}