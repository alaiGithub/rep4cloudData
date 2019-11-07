package com.yuezhu.util;

/**
 * @program: crawler
 * @description: 自定义的字符串格式化工具
 * @author: Mr.Chen
 * @create: 2019-09-02 16:08
 **/

import java.nio.ByteBuffer;


public class StringUtil {
    public static void main(String[] args) {
        String str = "\uD83C\uDFE0为深化庆祝改革开放40年群众性主题宣传教育活动\uD83C\uDFE0";
        System.out.println(filterOffUtf8Mb4(str));

    }
    //java过滤非汉字的utf8的字符
    public static String filterOffUtf8Mb4(String text) {
        byte[] bytes= "".getBytes();
        try{
            bytes = text.getBytes("utf-8");

        }catch (Exception e){

        }
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }

            b += 256; // 去掉符号位

            if (((b >> 5) ^ 0x6) == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            } else if (((b >> 4) ^ 0xE) == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            } else if (((b >> 3) ^ 0x1E) == 0) {
                i += 4;
            } else if (((b >> 2) ^ 0x3E) == 0) {
                i += 5;
            } else if (((b >> 1) ^ 0x7E) == 0) {
                i += 6;
            } else {
                buffer.put(bytes[i++]);
            }
        }
        buffer.flip();
        String str="";
        try{
            str = new String(buffer.array(), "utf-8");

        }catch (Exception e){

        }
        return str;
    }



}
