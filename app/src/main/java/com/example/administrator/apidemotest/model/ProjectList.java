package com.example.administrator.apidemotest.model;

/**
 * 临时data
 */
public class ProjectList {

    //日期 e.g 2016122
    private int  day;
    //清单备注

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    private String remark;

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

    public String getListText() {
        return listText;
    }

    public void setListText(String listText) {
        this.listText = listText;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    //时间  e.g 1022
    private int  time;

    //计划内容
    private String listText;
    //id
    private int id;
    //project_id
    private int project_id;
    //完成是已经完成，默认没完成，为0；
    private int is_finish=0;

}
