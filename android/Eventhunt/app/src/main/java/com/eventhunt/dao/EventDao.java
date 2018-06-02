package com.eventhunt.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.eventhunt.entity.Event;

import java.util.List;

/**
 * Created by Shiplayer on 02.06.18.
 */

@Dao
public interface EventDao {
    @Query("SELECT * FROM event")
    List<Event> getAllEvent();

    @Query("SELECT * FROM event ORDER BY id DESC LIMIT 1")
    Event getLastEvent();

    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);
}
