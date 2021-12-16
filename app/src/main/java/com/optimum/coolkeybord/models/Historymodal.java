package com.optimum.coolkeybord.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

// below line is for setting table name.
@Entity(tableName = "history_table")
public class Historymodal {
    // below line is to auto increment
    // id for each course.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;

    @ColumnInfo(name = "title")
    String title;
    @ColumnInfo(name = "type")
    String type;
    @ColumnInfo(name = "girurl")
    String girurl;
    @ColumnInfo(name = "occurrence")
    int occurrence =0;

//    public Historymodal(String id, String title, String type, int occurrence) {
//        this.id = id;
//        this.title = title;
//        this.type = type;
//        this.occurrence = occurrence;
//    }

    public Historymodal(int id, String title, String type, String girurl, int occurrence) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.girurl = girurl;
        this.occurrence = occurrence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGirurl() {
        return girurl;
    }

    public void setGirurl(String girurl) {
        this.girurl = girurl;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

}
