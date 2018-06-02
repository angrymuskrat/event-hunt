package com.eventhunt.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.eventhunt.database.AppDB;
import com.eventhunt.database.AppDatabase;
import com.eventhunt.entity.Event;
import com.eventhunt.util.ExecutorUtil;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Shiplayer on 02.06.18.
 */

public class EventModel extends ViewModel {
    private final Executor executor = ExecutorUtil.THREAD_POOL_EXECUTOR;
    private final AppDatabase database = AppDB.getInstance().getDatabase();
    private final MutableLiveData<List<Event>> allEventLiveData = new MutableLiveData<>();

    public void insertToDB(@NonNull final Event event) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.eventDao().insert(event);
            }
        });
    }

    public LiveData<Event> getLastEvent(){
        final MutableLiveData<Event> liveData = new MutableLiveData<>();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                liveData.postValue(database.eventDao().getLastEvent());
            }
        });
        return liveData;
    }

    public LiveData<List<Event>> getAllEvent(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                allEventLiveData.postValue(database.eventDao().getAllEvent());
            }
        });
        return allEventLiveData;
    }

    public Event getEventById(int id) {
        if (getAllEvent().getValue() != null) {
            for (Event event :
                    getAllEvent().getValue()) {
                if (event.getIdMarker() == id) {
                    return event;
                }
            }
        }
        return null;
    }
}
