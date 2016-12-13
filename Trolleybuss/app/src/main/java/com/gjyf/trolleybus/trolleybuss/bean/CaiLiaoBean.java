package com.gjyf.trolleybus.trolleybuss.bean;

import java.io.Serializable;

/**
 * 作者：Yang on 2016/11/22 10:52
 */
public class CaiLiaoBean implements Serializable {
    private String name;
    private String type;
    private String num;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
