package com.eventhunt.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.eventhunt.dao.EventDao;
import com.eventhunt.entity.Event;

/**
 * Created by Shiplayer on 02.06.18.
 */

@Database(entities = {Event.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}
