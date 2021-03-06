package com.hetai.ble.ble_hetai_lib.enmu;

/**
 * 作者: 单位 on 2016/11/24.
 * 作用: 单位枚举
 */

public enum Units1 {
    UNIT_KG("00","kg"),
    UNIT_LB("01","lb"),
    UNIT_ST("02","st");

    private String  code;
    private String  desc;


    Units1(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
