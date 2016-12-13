package com.gjyf.trolleybus.trolleybuss.bean;

import java.io.Serializable;


public class CityLightInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int id;
    private String strNumberCount;// 设备编号
    private String strPowerNum;// 电源位号
    private String strDistrictName;// 区县
    private String strIncode;// 内码
    private String strBuildDate;// 建成日期
    private String strLampWattage;// 光源瓦数
    private String strStreetName;// 街道
    private String strLight;// 灯杆厂家
    private String strLanternFactory;// 灯具厂家
    private String dianlan;// 电缆编号
    private String strCityPart;// 城市部件编号
    private String strDeviceType;// 设备类行
    private String saomiaoshijian; // 扫描时间
    private String isrepairs; // 是否已经报修
    private String carnum; // 车辆编码
    private String carcard; // 车牌号

    public CityLightInfo() {
        // TODO Auto-generated constructor stub
    }

    public CityLightInfo(String strNumberCount, String strPowerNum,
                         String strDistrictName, String strIncode, String strBuildDate,
                         String strLampWattage, String strStreetName, String strLight,
                         String strLanternFactory, String dianlan, String strCityPart,
                         String strDeviceType) {
        super();
        this.strNumberCount = strNumberCount;
        this.strPowerNum = strPowerNum;
        this.strDistrictName = strDistrictName;
        this.strIncode = strIncode;
        this.strBuildDate = strBuildDate;
        this.strLampWattage = strLampWattage;
        this.strStreetName = strStreetName;
        this.strLight = strLight;
        this.strLanternFactory = strLanternFactory;
        this.dianlan = dianlan;
        this.strCityPart = strCityPart;
        this.strDeviceType = strDeviceType;
    }

    public String getStrNumberCount() {
        return strNumberCount;
    }

    public void setStrNumberCount(String strNumberCount) {
        this.strNumberCount = strNumberCount;
    }

    public String getStrPowerNum() {
        return strPowerNum;
    }

    public void setStrPowerNum(String strPowerNum) {
        this.strPowerNum = strPowerNum;
    }

    public String getStrDistrictName() {
        return strDistrictName;
    }

    public void setStrDistrictName(String strDistrictName) {
        this.strDistrictName = strDistrictName;
    }

    public String getStrIncode() {
        return strIncode;
    }

    public void setStrIncode(String strIncode) {
        this.strIncode = strIncode;
    }

    public String getStrBuildDate() {
        return strBuildDate;
    }

    public void setStrBuildDate(String strBuildDate) {
        this.strBuildDate = strBuildDate;
    }

    public String getStrLampWattage() {
        return strLampWattage;
    }

    public void setStrLampWattage(String strLampWattage) {
        this.strLampWattage = strLampWattage;
    }

    public String getStrStreetName() {
        return strStreetName;
    }

    public void setStrStreetName(String strStreetName) {
        this.strStreetName = strStreetName;
    }

    public String getStrLight() {
        return strLight;
    }

    public void setStrLight(String strLight) {
        this.strLight = strLight;
    }

    public String getStrLanternFactory() {
        return strLanternFactory;
    }

    public void setStrLanternFactory(String strLanternFactory) {
        this.strLanternFactory = strLanternFactory;
    }

    public String getDianlan() {
        return dianlan;
    }

    public void setDianlan(String dianlan) {
        this.dianlan = dianlan;
    }

    public String getStrCityPart() {
        return strCityPart;
    }

    public void setStrCityPart(String strCityPart) {
        this.strCityPart = strCityPart;
    }

    public String getStrDeviceType() {
        return strDeviceType;
    }

    public void setStrDeviceType(String strDeviceType) {
        this.strDeviceType = strDeviceType;
    }

    public String getSaomiaoshijian() {
        return saomiaoshijian;
    }

    public void setSaomiaoshijian(String saomiaoshijian) {
        this.saomiaoshijian = saomiaoshijian;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsrepairs() {
        return isrepairs;
    }

    public void setIsrepairs(String isrepairs) {
        this.isrepairs = isrepairs;
    }

    public String getCarnum() {
        return carnum;
    }

    public void setCarnum(String carnum) {
        this.carnum = carnum;
    }

    public String getCarcard() {
        return carcard;
    }

    public void setCarcard(String carcard) {
        this.carcard = carcard;
    }

}
