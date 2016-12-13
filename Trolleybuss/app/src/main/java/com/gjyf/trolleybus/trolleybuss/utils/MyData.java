package com.gjyf.trolleybus.trolleybuss.utils;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：Yang on 2016/7/8 10:26
 * 获取系统时间
 */
public class MyData {
    public static String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getDateEN() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    public static LatLng getBaiduLatLng(String lo, String la) {
        LatLng sourceLatLng = new LatLng(Double.parseDouble(la), Double.parseDouble(lo));
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        // sourceLatLng待转换坐标    
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public static LatLng getBaiduLatLngGPS(String lo, String la) {
        LatLng sourceLatLng = new LatLng(Double.parseDouble(la), Double.parseDouble(lo));
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标    
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public static int GetChinesenum(String txt) {
        int n = 0;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(txt);
        for (int i = 0; i < txt.length(); i++) {
            String a = txt.substring(i, i + 1);
            m = p.matcher(a);
            if (m.matches()) {
                n++;
            }
        }
        return n;
    }
}
