package com.optimum.coolkeybord.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.optimum.coolkeybord.models.Historymodal;

import java.util.List;

public class Historyviewmodel extends AndroidViewModel {

    private WordRepository mRepository;

    private final LiveData<List<Historymodal>> mAllWords;

    public Historyviewmodel (Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public  LiveData<List<Historymodal>> getAllWords() { return mAllWords; }

    public void insert(Historymodal word) { mRepository.insert(word); }

    public  boolean delete(Historymodal word) {
      return   mRepository.delete(word);

    }
}