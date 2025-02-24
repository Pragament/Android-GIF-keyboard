package com.optimum.coolkeybord.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.optimum.coolkeybord.models.Historymodal;
import com.optimum.coolkeybord.models.RecentGifEntity;

import java.util.List;

public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Historymodal>> mAllWords;
    private  LiveData<List<RecentGifEntity>> listLiveDatarecentgif;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    WordRepository(Application application) {
        HistoryDatabase db = HistoryDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAllHistoriesfordelete();
        listLiveDatarecentgif = mWordDao.getAllRecentGifviewmodels();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Historymodal>> getAllWords() {
        return mAllWords;
    }
  LiveData<List<RecentGifEntity>> getallRecnetGifs() {
        return listLiveDatarecentgif;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Historymodal word) {
        HistoryDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }

    void insertRecentGif(RecentGifEntity recentGifEntity) {
        HistoryDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insertRecentGif(recentGifEntity);
        });
    }
    List<RecentGifEntity> isGifexists(String  idp) {
//        HistoryDatabase.databaseWriteExecutor.execute(() -> {
       return      mWordDao.isGifexists(idp);
//        });
    }
    boolean delete(Historymodal word) {
        HistoryDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.delete(word);
        });
        return  true;
    }
}
