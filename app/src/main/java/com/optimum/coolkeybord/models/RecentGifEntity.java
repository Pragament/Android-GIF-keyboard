package com.optimum.coolkeybord.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gIFtable")
public class RecentGifEntity {
    @PrimaryKey(autoGenerate = true)
//    @PrimaryKey()
//    @ColumnInfo(name = "id")
   public int id;
    @ColumnInfo(name = "gifjson")
    public String gifjson;

    public RecentGifEntity( String gifjson) {

        this.gifjson = gifjson;
    }
}
