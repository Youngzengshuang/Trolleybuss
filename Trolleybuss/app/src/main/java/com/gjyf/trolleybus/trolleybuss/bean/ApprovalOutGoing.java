package com.gjyf.trolleybus.trolleybuss.bean;

import java.io.Serializable;

/**
 * 作者：Yang on 2016/12/1 09:34
 */
public class ApprovalOutGoing implements Serializable {

    private String ioTaskId;  //任务标识

    private String ioTaskNo;  //任务编号/出入库任务编号，即出入库单号。登记单号

    private String ioFlag;  //出入标志/出入标志。0：入库、1：出库

    private String ioTWhReasonCode;  //出入库原因

    private String madeDepNo;  //制定部门

    private String madeName;  //制定人员

    private String relaId;  //关联标识/接收ID、传单编号、盘点任务编号

    private String relaNo;  //关联单号、即订单号

    private String taskStatus;  //任务状态/ 01-未发送，02-已发送，03-审批回退，04-审批通过，05-未执行，06-执行中，07-执行完毕，08-暂停，09-终止。

    private String orgNo;  //单位编码

    private String ioTaskDate;  //任务制定日期

    private String executeDate;  //任务执行日期

    private String taskCateg;  //任务类别

    private String storeAreaSort;  //存放分类。

    private String arriveBatchNo;  //到货批次号

    private String repBatchNo;  //ERP提供的·物料号

    private String manufacturer;  //制造单位、即供货单位

    private String typeCode;  //类型

    private String modelCode;  //资产型号

    private String qty;  //本任务的出入库总数量

    private String finishedIoQty;  //实际出入库数量

    private String countUnit;  //计数单位

    private String fromState;  //推送状态，用于移动端检索是否有新的任务通过审批   1 : 已推送   0 ：未推送
    private String materialJson;

    public String getMaterialJson() {
        return materialJson;
    }

    public void setMaterialJson(String materialJson) {
        this.materialJson = materialJson;
    }

    public String getIoTaskId() {
        return ioTaskId;
    }

    public void setIoTaskId(String ioTaskId) {
        this.ioTaskId = ioTaskId;
    }

    public String getIoTaskNo() {
        return ioTaskNo;
    }

    public void setIoTaskNo(String ioTaskNo) {
        this.ioTaskNo = ioTaskNo;
    }

    public String getIoFlag() {
        return ioFlag;
    }

    public void setIoFlag(String ioFlag) {
        this.ioFlag = ioFlag;
    }

    public String getIoTWhReasonCode() {
        return ioTWhReasonCode;
    }

    public void setIoTWhReasonCode(String ioTWhReasonCode) {
        this.ioTWhReasonCode = ioTWhReasonCode;
    }

    public String getMadeDepNo() {
        return madeDepNo;
    }

    public void setMadeDepNo(String madeDepNo) {
        this.madeDepNo = madeDepNo;
    }

    public String getMadeName() {
        return madeName;
    }

    public void setMadeName(String madeName) {
        this.madeName = madeName;
    }

    public String getRelaId() {
        return relaId;
    }

    public void setRelaId(String relaId) {
        this.relaId = relaId;
    }

    public String getRelaNo() {
        return relaNo;
    }

    public void setRelaNo(String relaNo) {
        this.relaNo = relaNo;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getIoTaskDate() {
        return ioTaskDate;
    }

    public void setIoTaskDate(String ioTaskDate) {
        this.ioTaskDate = ioTaskDate;
    }

    public String getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(String executeDate) {
        this.executeDate = executeDate;
    }

    public String getTaskCateg() {
        return taskCateg;
    }

    public void setTaskCateg(String taskCateg) {
        this.taskCateg = taskCateg;
    }

    public String getStoreAreaSort() {
        return storeAreaSort;
    }

    public void setStoreAreaSort(String storeAreaSort) {
        this.storeAreaSort = storeAreaSort;
    }

    public String getArriveBatchNo() {
        return arriveBatchNo;
    }

    public void setArriveBatchNo(String arriveBatchNo) {
        this.arriveBatchNo = arriveBatchNo;
    }

    public String getRepBatchNo() {
        return repBatchNo;
    }

    public void setRepBatchNo(String repBatchNo) {
        this.repBatchNo = repBatchNo;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getFinishedIoQty() {
        return finishedIoQty;
    }

    public void setFinishedIoQty(String finishedIoQty) {
        this.finishedIoQty = finishedIoQty;
    }

    public String getCountUnit() {
        return countUnit;
    }

    public void setCountUnit(String countUnit) {
        this.countUnit = countUnit;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }
}
