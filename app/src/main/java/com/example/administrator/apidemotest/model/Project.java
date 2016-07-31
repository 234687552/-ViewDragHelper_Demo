package com.example.administrator.apidemotest.model;

/**
 * 临时data
 */
public class Project {
    //日期 e.g 2016122
    private int  day;

    //type类型
    private String type;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(int is_finish) {
        this.is_finish = is_finish;
    }

    public String getProjectText() {
        return projectText;
    }

    public void setProjectText(String projectText) {
        this.projectText = projectText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //计划内容
    private String projectText;
    //id
    private int id;
    //完成是已经完成，默认没完成，为0；
    private int is_finish=0;
}
