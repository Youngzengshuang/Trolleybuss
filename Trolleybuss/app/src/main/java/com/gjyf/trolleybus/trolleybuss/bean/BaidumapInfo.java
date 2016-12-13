package com.gjyf.trolleybus.trolleybuss.bean;

import java.io.Serializable;


/**
 * 标记数据类
 * 经纬度信息等
 */

/**
 * @author YangZS
 */
public class BaidumapInfo implements Serializable {

    private String DeviceID;  // 编号
    private String DeviceName; // 设备名称
    private String Type; // 规格类型
    private String NowNo;  // 现有编号
    private String usertime; // 使用年限
    private String Streetname;  //位置
    private String createtime;   //创建时间
    private String DimensionLatitude; //纬度
    private String DimensionLongitude;  //经度
    private String OtherDevice; // 其他设备类型
    private String Date;  //备注信息

    public BaidumapInfo() {
    }

    public BaidumapInfo(String dimensionLatitude, String dimensionLongitude) {
        DimensionLatitude = dimensionLatitude;
        DimensionLongitude = dimensionLongitude;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getNowNo() {
        return NowNo;
    }

    public void setNowNo(String nowNo) {
        NowNo = nowNo;
    }

    public String getUsertime() {
        return usertime;
    }

    public void setUsertime(String usertime) {
        this.usertime = usertime;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getOtherDevice() {
        return OtherDevice;
    }

    public void setOtherDevice(String otherDevice) {
        OtherDevice = otherDevice;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getStreetname() {
        return Streetname;
    }

    public void setStreetname(String streetname) {
        Streetname = streetname;
    }

    public String getDimensionLatitude() {
        return DimensionLatitude;
    }

    public void setDimensionLatitude(String dimensionLatitude) {
        DimensionLatitude = dimensionLatitude;
    }

    public String getDimensionLongitude() {
        return DimensionLongitude;
    }

    public void setDimensionLongitude(String dimensionLongitude) {
        DimensionLongitude = dimensionLongitude;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


}
