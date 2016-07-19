package com.example.administrator.apidemotest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.model.ProjectList;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 一个DB包含两个个表，一个保存project，一个保存project下面的清单；
 * project_id 是每一个project的标识 ，每一个id是project_list的标识；
 */
public class ProjectDb {
    //数据库名称
    private static final String DB_NAME = "my_project";
    //数据库版本
    private static final int VERSION = 1;
    public boolean isFirstTime=false;
    private SQLiteDatabase db;

    public ProjectDb(Context context) {
        //获取openHelper实例开始创建表
        ProjectDbOpener dbOpener = new ProjectDbOpener(context, DB_NAME, null, VERSION);
        db = dbOpener.getWritableDatabase();
    }
    //--------------------------------------project-----------------------------------------------------------//
    /**
     * 新增一个project
     */
    public void saveProject(Project project){
        ContentValues values = new ContentValues();
        values.put("day",project.getDay());
        values.put("is_finish",project.getIs_finish());
        values.put("project_text",project.getProjectText());
        values.put("type",project.getType());
        db.insert("Project", null, values);
        values.clear();
    }
    /**
     * 删除一个project
     */
    public void deleteProject(int  id){
        db.delete("Project", "id = ?", new String[]{String.valueOf(id)});
    }
    /**
     * 更新一个project
     */
    public void updateProject(int id,Project newProject){
        ContentValues values = new ContentValues();
        values.put("day",newProject.getDay());
        values.put("is_finish",newProject.getIs_finish());
        values.put("project_text",newProject.getProjectText());
        values.put("type",newProject.getType());
        db.update("Project",values,"id=?",new String[]{String.valueOf(id)});
    }
    /**
     * 根据类型读取所有的project；
     */
    public List<Project> getProjects(String type){
        List<Project> projects=new ArrayList<Project>();
        Cursor cursor = type.equals("所有")?
                db.query("Project", null, null, null, null, null, "is_finish,day"):
                db.query("Project", null, "type=?", new String[]{type}, null, null, "is_finish,day");
        if (cursor.moveToFirst()){
            do {
                Project project=new Project();
                project.setIs_finish(cursor.getInt(cursor.getColumnIndex("is_finish")));
                project.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                project.setId(cursor.getInt(cursor.getColumnIndex("id")));
                project.setProjectText(cursor.getString(cursor.getColumnIndex("project_text")));
                project.setType(cursor.getString(cursor.getColumnIndex("type")));
                projects.add(project);
            } while (cursor.moveToNext());
        }
        return projects;
    }
    /**
     * 根据id读取某一个project
     */
    public Project getProject(int id){
        Project project=new Project();
        Cursor cursor = db.query("Project", null, "id=?", new String[]{String.valueOf(id)}, null, null, "is_finish,day");
        if (cursor.moveToFirst()){
            do {
                project.setIs_finish(cursor.getInt(cursor.getColumnIndex("is_finish")));
                project.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                project.setId(cursor.getInt(cursor.getColumnIndex("id")));
                project.setProjectText(cursor.getString(cursor.getColumnIndex("project_text")));
                project.setType(cursor.getString(cursor.getColumnIndex("type")));
            }while (cursor.moveToNext());
        }
        return project;
    }
    //--------------------------------------project_list------------------------------------------------------//
    /**
     * 新增一个project_list
     */
    public void saveProjectList(ProjectList projectList){
        ContentValues values = new ContentValues();
        values.put("day",projectList.getDay());
        values.put("time",projectList.getTime());
        values.put("project_id",projectList.getProject_id());
        values.put("is_finish",projectList.getIs_finish());
        values.put("list_text",projectList.getListText());
        db.insert("List", null, values);
        values.clear();
    }
    /**
     * 删除一个project_list
     */
    public void deleteProjectList(int  id){
        db.delete("List", "id = ?", new String[]{String.valueOf(id)});
    }
    /**
     * 更新一个project_list
     */
    public void updateProjectList(int id,ProjectList newProjectList){
        ContentValues values = new ContentValues();
        values.put("day",newProjectList.getDay());
        values.put("time",newProjectList.getTime());
        values.put("project_id",newProjectList.getProject_id());
        values.put("is_finish",newProjectList.getIs_finish());
        values.put("list_text",newProjectList.getListText());
        db.update("List",values,"id=?",new String[]{String.valueOf(id)});
    }

    /**
     * 根据project_id读取所有的list
     */
    public List<ProjectList> getProjectList(int project_id){
        List<ProjectList> projectLists=new ArrayList<ProjectList>();

        Cursor cursor = db.query("List", null, "project_id=?", new String[]{String.valueOf(project_id)}, null, null, "is_finish,day,time");
        if (cursor.moveToFirst()){
            do {
                ProjectList list=new ProjectList();
                list.setIs_finish(cursor.getInt(cursor.getColumnIndex("is_finish")));
                list.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                list.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list.setProject_id(cursor.getInt(cursor.getColumnIndex("project_id")));
                list.setListText(cursor.getString(cursor.getColumnIndex("list_text")));
                list.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                projectLists.add(list);
            } while (cursor.moveToNext());
        }
        return projectLists;
    }

    private class ProjectDbOpener extends SQLiteOpenHelper {
        //创建PROJECT表
        String CREATE_PROJECT = "create table Project (" +
                "id integer primary key autoincrement," +
                "day integer,"+
                "is_finish integer,"+
                "project_text text," +
                "type text)";
        //创建PROJECT_LIST表
        String CREATE_LIST = "create table List (" +
                "id integer primary key autoincrement," +
                "project_id integer,"+
                "day integer,"+
                "time integer,"+
                "is_finish integer,"+
                "list_text text)";

        public ProjectDbOpener(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PROJECT);
            db.execSQL(CREATE_LIST);
            isFirstTime=true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop if table exists Project");
            db.execSQL("drop if table exists List");
            onCreate(db);
        }
    }

}
