package com.gjyf.trolleybus.trolleybuss.bean;

/**
 * 作者：Yang on 2016/11/18 14:54
 * 库房信息
 */
public class StorageInfo {
    private int id;
    private String name;  //材料名称
    private String namenum;//材料编号
    private String type;//规格
    private String num; //现有数量
    private String sumnum;  //库存上限

    public StorageInfo() {
    }

    public StorageInfo(String name, String namenum, String num, String sumnum) {
        this.name = name;
        this.namenum = namenum;
        this.num = num;
        this.sumnum = sumnum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getNamenum() {
        return namenum;
    }

    public void setNamenum(String namenum) {
        this.namenum = namenum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSumnum() {
        return sumnum;
    }

    public void setSumnum(String sumnum) {
        this.sumnum = sumnum;
    }
}
