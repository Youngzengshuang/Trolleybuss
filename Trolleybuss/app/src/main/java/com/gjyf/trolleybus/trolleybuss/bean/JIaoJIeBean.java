package com.gjyf.trolleybus.trolleybuss.bean;

/**
 * 作者：Yang on 2016/12/7 16:37
 * 交接记录
 */
public class JIaoJIeBean {
    private String shiftChgId;      //交接标识

    private String shiftDate;      //交班时间

    private String shiftUserId;      //交接人

    private String successionDate;      //接班时间

    private String successionUserId;      //接班人

    private String memo;      //交接备注

    private String shiftStatus;      //状态:0是未接班，1已接班。


    public String getShiftChgId() {
        return shiftChgId;
    }

    public void setShiftChgId(String shiftChgId) {
        this.shiftChgId = shiftChgId;
    }

    public String getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getShiftUserId() {
        return shiftUserId;
    }

    public void setShiftUserId(String shiftUserId) {
        this.shiftUserId = shiftUserId;
    }

    public String getSuccessionDate() {
        return successionDate;
    }

    public void setSuccessionDate(String successionDate) {
        this.successionDate = successionDate;
    }

    public String getSuccessionUserId() {
        return successionUserId;
    }

    public void setSuccessionUserId(String successionUserId) {
        this.successionUserId = successionUserId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(String shiftStatus) {
        this.shiftStatus = shiftStatus;
    }
}
