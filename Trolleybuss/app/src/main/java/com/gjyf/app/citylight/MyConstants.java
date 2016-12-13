package com.gjyf.app.citylight;

import android.content.Context;
import android.text.TextUtils;

import com.gjyf.trolleybus.trolleybuss.utils.SharedPreferencesUtils;

import java.util.UUID;

public class MyConstants {
    public static String TAGADDRESS = "98:D3:33:80:AA:12";
    public static String DEVICENAME = "RFID";
    public static UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static int isconncetion = 0;
    public static String CARADDRESS = "192.168.1.27";
    public static int SUCCESS = 400;
    public static int GETSUCCESS = 500;
    public static int FAILURE = 404;

    public static String IP = "";
    public static String HOST = "";
//    public static String IP1 = "192.168.1.105";
//    public static String HOST1 = "8080";
//    public static String IP2 = "192.168.1.125";
//    public static String HOST2 = "18080";

    public static String LOCATION = "http://" + IP + ":" + HOST + "/Electric_Auto/app/appPosition";   //位置信息上传
    public static String LOGINURL = "http://" + IP + ":" + HOST + "/Electric_Auto/app/appLogin";   //登陆
    public static String REPAIRE_GET_TASK = "http://" + IP + ":" + HOST + "/Electric_Auto/app/repairTask";    //抢修任务获取
    public static String REPAIRE_GET_ZHICAN = "http://" + IP + ":" + HOST + "/Electric_Auto/app/poleDetails";   //抢修资产位置获取
    public static String REPAIR_POST_RECOR = "http://" + IP + ":" + HOST + "/Electric_Auto/app/repairDetails";   //抢修完成信息反馈
    public static String STORAGE = "http://" + IP + ":" + HOST + "/Electric_Auto/app/materialDetails";  //获取库房信息
    public static String GET_STREET = "http://" + IP + ":" + HOST + "/Electric_Auto/app/getStreetNameAndBarCode";  //获取街道名灯杆号
    public static String OUTGOING_POST = "http://" + IP + ":" + HOST + "/Electric_Auto/app/toInOutTask";  //出库单信息提交
    public static String ZHiCAN = "http://" + IP + ":" + HOST + "/Electric_Auto/app/poleSearch";   //资产信息获取
    public static String JIAOJIE = "http://" + IP + ":" + HOST + "/Electric_Auto/app/shiftChg";    //交接
    public static String OUTGOING_GET = "http://" + IP + ":" + HOST + "/Electric_Auto/app/outTaskAudit";  //出库单信息获取
    public static String OUTGOING_APPROVAL = "http://" + IP + ":" + HOST + "/Electric_Auto/app/outTaskAuditSave";  //出库单审批


//    public static String LOCATION = "http://" + IP2 + ":" + HOST2 + "/Electric_Auto/app/appPosition";   //位置信息上传
//    public static String LOGINURL = "http://" + IP2 + ":" + HOST2 + "/Electric_Auto/app/appLogin";   //登陆
//    public static String REPAIRE_GET_TASK = "http://" + IP2 + ":" + HOST2 + "/Electric_Auto/app/repairTask";    //抢修任务获取
//    public static String REPAIRE_GET_ZHICAN = "http://" + IP2 + ":" + HOST2 + "/Electric_Auto/app/poleDetails";   //抢修资产位置获取
//    public static String REPAIR_POST_RECOR = "http://" + IP2 + ":" + HOST2 + "/Electric_Auto/app/repairDetails";   //抢修完成信息反馈
//
//    public static String JIAOJIE = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/shiftChg";                    //交接
//    public static String Storage = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/getStreetNameAndBarCode";  //获取街道名灯杆号
//    public static String GET_STREET = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/getStreetNameAndBarCode";  //获取街道名灯杆号
//    public static String OUTGOING_POST = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/toInOutTask";  //出库单信息提交
//    public static String ZHiCAN = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/poleSearch";   //资产信息获取
//
//    public static String OUTGOING_GET = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/outTaskAudit";  //出库单信息获取
//    public static String OUTGOING_APPROVAL = "http://" + IP1 + ":" + HOST1 + "/Electric_Auto/app/outTaskAuditSave";  //出库单审批

    public static String key = "asdfghjklzxcvbnm";


    // 查询全部信息

    public static String LammportRepairSql = "SELECT CL.NUMBERCOUNT, CPS.POWERNUMBER, CD.NAME AS "
            + "DISTRICTNAME, CS.NAME AS STREETNAME, CL.INCODE, date(CL.BUILDDATETIME) as "
            + "BUILDDATETIME, CL.LAMPWATTAGE, CLIF .NAME AS LIGTHFACTORYNAME, CLAF.NAME AS "
            + "LANTERNFACTORYNAME, CCT.NAME AS CABLETYPENAME, CL.CITYPARTNUMBER, (SELECT "
            + "NAME FROM C_DEVICETYPE WHERE ID=? ) AS DeviceTypeName,(SELECT NAME FROM "
            + "C_LAMPPOSTCNPOWERNUMBER WHERE CODE =CPS.POWERNUMBER) AS POWERNUMBERCN FROM "
            + "C_LAMPPOST AS CL LEFT JOIN C_POWERSUPPLY AS CPS ON CPS.POWERSUPPLYID = "
            + "CL.POWERSUPPLYID LEFT JOIN C_STREET AS CS ON CS.STREETID = CL.STREETID LEFT "
            + "JOIN C_DISTRICT AS CD ON CD.DISTRICTID = CL.DISTRICTID LEFT JOIN "
            + "C_LIGTHFACTORY AS CLIF ON CLIF .ID = CL.LIGTHFACTORY LEFT JOIN C_LANTERNFACTORY AS CLAF ON CLAF.ID ="
            + " CL.LANTERNFACTORY LEFT JOIN C_CABLETYPE AS CCT ON CCT.ID = CL.CABLETYPE "
            + "WHERE CL.INCODE = ?;";

    public static void SetServiceInfo(Context context) {
        String ip = SharedPreferencesUtils.getString(context, "ip");
        String host = SharedPreferencesUtils.getString(context, "host");

        if (!TextUtils.isEmpty(ip)) {
            IP = ip;
        }
        if (!TextUtils.isEmpty(host)) {
            HOST = host;
        }
        LOCATION = "http://" + ip + ":" + host + "/Electric_Auto/app/appPosition";   //位置信息上传
        LOGINURL = "http://" + ip + ":" + host + "/Electric_Auto/app/appLogin";   //登陆
        REPAIRE_GET_TASK = "http://" + ip + ":" + host + "/Electric_Auto/app/repairTask";    //抢修任务获取
        REPAIRE_GET_ZHICAN = "http://" + ip + ":" + host + "/Electric_Auto/app/poleDetails";   //抢修资产位置获取
        REPAIR_POST_RECOR = "http://" + ip + ":" + host + "/Electric_Auto/app/repairDetails";   //抢修完成信息反馈
        STORAGE = "http://" + ip + ":" + host + "/Electric_Auto/app/materialDetails";  //获取库房信息
        GET_STREET = "http://" + ip + ":" + host + "/Electric_Auto/app/getStreetNameAndBarCode";  //获取街道名灯杆号
        OUTGOING_POST = "http://" + ip + ":" + host + "/Electric_Auto/app/toInOutTask";  //出库单信息提交
        ZHiCAN = "http://" + ip + ":" + host + "/Electric_Auto/app/poleSearch";   //资产信息获取
        JIAOJIE = "http://" + ip + ":" + host + "/Electric_Auto/app/shiftChg";    //交接
        OUTGOING_GET = "http://" + ip + ":" + host + "/Electric_Auto/app/outTaskAudit";  //出库单信息获取
        OUTGOING_APPROVAL = "http://" + ip + ":" + host + "/Electric_Auto/app/outTaskAuditSave";  //出库单审批


    }

}
