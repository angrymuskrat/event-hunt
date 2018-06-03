package com.eventhunt.database;


import android.app.Application;
import android.arch.persistence.room.Room;

/**
 * Created by Shiplayer on 02.06.18.
 */

public class AppDB extends Application {
    public static AppDB instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static AppDB getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
