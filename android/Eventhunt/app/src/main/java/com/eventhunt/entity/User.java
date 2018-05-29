package com.eventhunt.entity;

import android.accounts.Account;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class User {
    private static GoogleSignInAccount account;
    private static MapFilter mapFilter;
    private static Boolean isEmpty;

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
}
