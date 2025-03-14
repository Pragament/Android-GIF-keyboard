package com.optimum.coolkeybord.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.optimum.coolkeybord.models.Historymodal;
import com.optimum.coolkeybord.models.RecentGifEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// adding annotation for our database entities and db version.
@Database(entities = {Historymodal.class, RecentGifEntity.class}, version = 1 ,exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {

    public abstract WordDao wordDao();

    private static volatile HistoryDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HistoryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HistoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryDatabase.class, "history_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
//    private static final int NUMBER_OF_THREADS = 4;
//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//    // below line is to create instance
//    // for our database class.
//    private static HistoryDatabase instance;
//
//    // below line is to create
//    // abstract variable for dao.
//    public abstract Dao Dao();
//
//    // on below line we are getting instance for our database.
//    public static synchronized HistoryDatabase getInstance(Context context) {
//        // below line is to check if
//        // the instance is null or not.
//        if (instance == null) {
//            // if the instance is null we
//            // are creating a new instance
//            instance =
//                    // for creating a instance for our database
//                    // we are creating a database builder and passing
//                    // our database class with our database name.
//                    Room.databaseBuilder(context.getApplicationContext(),
//                            HistoryDatabase.class, "history_database")
//                            // below line is use to add fall back to
//                            // destructive migration to our database.
//                            .fallbackToDestructiveMigration()
//                            // below line is to add callback
//                            // to our database.
//                            .addCallback(roomCallback)
//                            // below line is to
//                            // build our database.
//                            .build();
//        }
//        // after creating an instance
//        // we are returning our instance
//        return instance;
//    }
//
//    // below line is to create a callback for our room database.
//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//            // this method is called when database is created
//            // and below line is to populate our data.
//            new PopulateDbAsyncTask(instance).execute();
//        }
//    };
//
//    // we are creating an async task class to perform task in background.
//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//        PopulateDbAsyncTask(HistoryDatabase instance) {
//            Dao dao = instance.Dao();
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//            return null;
//        }
//    }
}