package com.gjyf.trolleybus.trolleybuss.bean;

import java.io.Serializable;

/*
 * 任务信息
 */

public class TaskInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String appNo;         //申请编号
    private String districtTown;  //区县
    private String streetNo;      //街道
    private String defailedAdderss;    //详细地址
    private String excpDesc;   //异常现象描述
    private String poleId;  //电杆内码
    private String latItude;    //纬度
    private String longItude;    //经度
    private String reportName;   //报修人姓名
    private String reportTel;   //报修人电话
    private String handleEmpNo;  //安排处理人员
    private String deptNo;   //安排处理部门
    private String handleCode;   //处理标志
    private String remark;


    public TaskInfo() {
    }

    public TaskInfo(String appNo, String districtTown, String streetNo, String defailedAdderss, String excpDesc, String poleId, String dimensionLongitude, String dimensionLatitude, String reportName, String reportTel, String handleEmpNo, String deptNo, String handleCode) {
        this.appNo = appNo;
        this.districtTown = districtTown;
        this.streetNo = streetNo;
        this.defailedAdderss = defailedAdderss;
        this.excpDesc = excpDesc;
        this.poleId = poleId;
        latItude = dimensionLatitude;
        longItude = dimensionLongitude;
        this.reportName = reportName;
        this.reportTel = reportTel;
        this.handleEmpNo = handleEmpNo;
        this.deptNo = deptNo;
        this.handleCode = handleCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public String getDistrictTown() {
        return districtTown;
    }

    public void setDistrictTown(String districtTown) {
        this.districtTown = districtTown;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getDefailedAdderss() {
        return defailedAdderss;
    }

    public void setDefailedAdderss(String defailedAdderss) {
        this.defailedAdderss = defailedAdderss;
    }

    public String getExcpDesc() {
        return excpDesc;
    }

    public void setExcpDesc(String excpDesc) {
        this.excpDesc = excpDesc;
    }

    public String getPoleId() {
        return poleId;
    }

    public void setPoleId(String poleId) {
        this.poleId = poleId;
    }

    public String getLongItude() {
        return longItude;
    }

    public void setLongItude(String longItude) {
        this.longItude = longItude;
    }

    public String getLatItude() {
        return latItude;
    }

    public void setLatItude(String latItude) {
        this.latItude = latItude;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportTel() {
        return reportTel;
    }

    public void setReportTel(String reportTel) {
        this.reportTel = reportTel;
    }

    public String getHandleEmpNo() {
        return handleEmpNo;
    }

    public void setHandleEmpNo(String handleEmpNo) {
        this.handleEmpNo = handleEmpNo;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public String getHandleCode() {
        return handleCode;
    }

    public void setHandleCode(String handleCode) {
        this.handleCode = handleCode;
    }
}
