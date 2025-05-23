package com.optimum.coolkeybord.database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.optimum.coolkeybord.models.Historymodal;
import com.optimum.coolkeybord.models.RecentGifEntity;

import java.util.List;

@androidx.room.Dao
public interface WordDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    // below method is use to
    // add data to database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Historymodal... model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecentGif(RecentGifEntity... model);

    @Query("SELECT * FROM giftable WHERE id=:idp")
    List<RecentGifEntity> isGifexists(String idp);
    // below method is use to update
    // the data in our database.
    @Update
    void update(Historymodal model);

    // below line is use to delete a
    // specific course in our database.
    @Delete
    void delete(Historymodal model);

    // on below line we are making query to
    // delete all courses from our database.
    @Query("DELETE FROM history_table")
    void deleteAllCourses();

    // below line is to read all the courses from our database.
    // in this we are ordering our courses in ascending order
    // with our course name.
    @Query("SELECT * FROM history_table ORDER BY title ASC")
    List<Historymodal> getAllHistories();


    @Query("SELECT * FROM history_table where title LIKE '%' || :titl || '%' AND occurrence > 10 ORDER BY title DESC LIMIT 10")
    List<Historymodal> getAllHistbyOccurace(String titl);


    @Query("SELECT occurrence FROM history_table where title LIKE '%' || :lastWord || '%'")
    Integer getWordFrequency(String lastWord);

    @Query("SELECT * FROM history_table ORDER BY title ASC")
    LiveData<List<Historymodal>> getAllHistoriesfordelete();

    @Query("SELECT * FROM gIFtable ORDER BY id DESC")
    LiveData<List<RecentGifEntity>> getAllRecentGifviewmodels();
}
