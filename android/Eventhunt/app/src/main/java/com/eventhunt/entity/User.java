package com.eventhunt.entity;

import android.util.SparseArray;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class User {
    private static GoogleSignInAccount account;
    private static MapFilter mapFilter;
    private static Boolean isEmpty;
    private static SparseArray<Event> events;

    public static Boolean isEmpty() {
        return isEmpty == null ? true : isEmpty;
    }

    public static GoogleSignInAccount getAccount() {
        return account;
    }

    public static void setAccount(GoogleSignInAccount account) {
        if(account == null){
            isEmpty = null;
            mapFilter = null;
            User.account = null;
        } else {
            isEmpty = false;
            User.account = account;
            mapFilter = MapFilter.getDefault();
        }
    }

    public static MapFilter getMapFilter() {
        return mapFilter;
    }

    public static void setMapFilter(MapFilter mapFilter) {
        User.mapFilter = mapFilter;
    }

    public static void addEvent(int id, Event event){
        if(events == null)
            events = new SparseArray<>();
        events.put(id, event);
    }

    public static int getSizeEvents(){
        return events.size();
    }

    public static Event getEvent(int index){
        if(events.get(index, null) != null)
            return events.get(index);
        else
            return null;
    }
}
