package com.hayes.bash.hayesredis.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneNumberInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phoneNumber;
    private String baseType;
    private String provinceNumber;
    private String cityNumber;
    private String state;
    private String generationType;
    private String createDate;
    private String updateDate;
    private String segment;
    private String gradeId;
    private String remark;
    private String rule;
    private String storeId;
    private String employeeId;
    private String iccid;
    private String price;
    private String minConsume;
    private String prepare;
    private int holdTime;
    private String dependId;
    private String dependName;
    private String name;// 仓库名称

    private String dependParId;//父机构id
    private String dependParName;//父机构name
    private String stateName;

    private String deptname;//机构名称
    private String provincename;
    private String cityname;
    private String prepayflag;
    private String prepay;
    private String endDuration;
    private String usedDuration;
    private String preDuration;

}
