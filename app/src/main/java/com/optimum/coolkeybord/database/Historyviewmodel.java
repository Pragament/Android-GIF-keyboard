package com.optimum.coolkeybord.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.optimum.coolkeybord.models.Historymodal;
import com.optimum.coolkeybord.models.RecentGifEntity;

import java.util.List;

public class Historyviewmodel extends AndroidViewModel {

    private WordRepository mRepository;

    private final LiveData<List<Historymodal>> mAllWords;
    private final LiveData<List<RecentGifEntity>> listLiveDatarecentgif;

    public Historyviewmodel (Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
        listLiveDatarecentgif = mRepository.getallRecnetGifs();
    }

    public  LiveData<List<Historymodal>> getAllWords() { return mAllWords; }

    public  LiveData<List<RecentGifEntity>> getmAllRecentGifs() { return listLiveDatarecentgif; }

    public void insert(Historymodal word) { mRepository.insert(word); }


    public void insertgif(RecentGifEntity recentGifEntity) { mRepository.insertRecentGif(recentGifEntity); }

    public  boolean delete(Historymodal word) {
      return   mRepository.delete(word);

    }
}