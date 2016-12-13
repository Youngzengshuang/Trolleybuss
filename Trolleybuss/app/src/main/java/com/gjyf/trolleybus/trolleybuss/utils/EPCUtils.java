package com.gjyf.trolleybus.trolleybuss.utils;

import android.text.TextUtils;
import android.util.Log;

import com.gjyf.trolleybus.trolleybuss.bean.EPCBean;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by 杨曾爽 on 2016/5/9.
 */
public class EPCUtils {
    /**
     * * 字符串转换成十六进制字符串
     * * @param String str 待转换的ASCII字符串
     * *  @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * * 十六进制转换字符串
     * * @param String str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    //汉字解析，字符串大写
    public static String decode(String bytes) {
        bytes = bytes.toUpperCase();
        String hexString = "0123456789ABCDEF";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        String bb = "";
        try {
            bb = new String(baos.toByteArray(), "gb2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bb;
    }

    //EPC信息获取封装
    public static EPCBean GetEpcBean(String epc) {

//        String epc = "2CEF89FE98FDA8F0B520A1D1F6ADABCDABCDABCDABCDABCD";
        // 28:26.339 23643-23643/com.gjyf.trolleybus.trolleybuss V/yzs: 原始c2cd3dabcdcda2a12411a53606f180bdabcdabcdabcdabcd
        //   12-10 11:28:26.340 23643-23643/com.gjyf.trolleybus.trolleybuss V/yzs: 解码690096666600096c8fdc0efbad3c2b700000000000000000
        //  12-10 11:28:26.343 23643-23643/com.gjyf.trolleybus.trolleybuss V/yzs: 街道号E0096
        //  12-10 11:28:26.343 23643-23643/com.gjyf.trolleybus.trolleybuss V/yzs: 资产码666600096
        // 12-10 11:28:26.343 23643-23643/com.gjyf.trolleybus.trolleybuss V/yzs: 道路名三里河路
        // Log.v("yzs", "原始" + epc);
        //  Log.v("yzs", "解码" + epc);
        String streetid = "";
        String num = "";
        String street = "";


        try {
            epc = EPCUtils.GetEpc(epc);
            String w = EPCUtils.hexStr2Str(Integer.toHexString(Integer.parseInt(epc.substring(0, 2))));
            streetid = w + epc.substring(2, 6);
            num = epc.substring(6, 15);
            street = epc.substring(15, epc.length()).replaceAll("0*$", "");
            street = EPCUtils.decode(street);
        } catch (Exception e) {

        }


        EPCBean bean = new EPCBean();
        bean.setNum(num);
        bean.setSteetname(street);
        bean.setStreetid(streetid);
        try {
            if (bean.getStreetid().substring(0, 1).equals("W") || bean.getStreetid().substring(0, 1).equals("E") || bean.getStreetid().substring(0, 1).equals("S") || bean.getStreetid().substring(0, 1).equals("N")) {
                return bean;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }


    //EPC解密
    public static String GetEpc(String epc) {
        String newepc = "";
        for (int i = 0; i < epc.length(); i += 4) {
            String epcSub = epc.substring(i, i + 4);
            String string = HexEnCrypt(epcSub, "ABCD");
            newepc += string;
        }
        return newepc;
    }

    //两字符串异或
    private static String HexEnCrypt(String str1, String str2) {
        StringBuffer sb = new StringBuffer();
        int len1 = str1.length(), len2 = str2.length();
        int i = 0, index = 0;
        if (len2 > len1) {
            index = len2 - len1;
            while (i++ < len2 - len1) {
                sb.append(str2.charAt(i - 1));
                str1 = "0" + str1;
            }
        } else if (len1 > len2) {
            index = len1 - len2;
            while (i++ < len1 - len2) {
                sb.append(str1.charAt(i - 1));
                str2 = "0" + str2;
            }
        }
        int len = str1.length();
        while (index < len) {
            int j = (Integer.parseInt(str1.charAt(index) + "", 16) ^ Integer.parseInt(str2.charAt(index) + "", 16)) & 0xf;
            sb.append(Integer.toHexString(j));
            index++;
        }
        return sb.toString();
    }


    private static String hexString = "0123456789ABCDEF";

    public static String encodeCN(String data) {
        byte[] bytes;
        try {
            bytes = data.getBytes("gbk");
            StringBuilder sb = new StringBuilder(bytes.length * 2);

            for (int i = 0; i < bytes.length; i++) {
                sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
                sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encodeStr(String data) {
        String result = "";
        byte[] bytes;
        try {
            bytes = data.getBytes("gbk");
            for (int i = 0; i < bytes.length; i++) {
                result += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判定是否为中文汉字
     *
     * @param data
     * @return
     */
    public static boolean isCN(String data) {
        boolean flag = false;
        String regex = "^[\u4e00-\u9fa5]*$";
        if (data.matches(regex)) {
            flag = true;
        }
        return flag;
    }

    public static String getHexResult(String targetStr) {
        StringBuilder hexStr = new StringBuilder();
        int len = targetStr.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                char tempStr = targetStr.charAt(i);
                String data = String.valueOf(tempStr);
                if (isCN(data)) {
                    hexStr.append(encodeCN(data));
                } else {
                    hexStr.append(encodeStr(data));
                }
            }
        }
        return hexStr.toString();
    }


}


