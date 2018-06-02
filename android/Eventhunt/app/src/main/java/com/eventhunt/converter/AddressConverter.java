package com.eventhunt.converter;

import android.arch.persistence.room.TypeConverter;
import android.location.Address;

import com.google.gson.Gson;

/**
 * Created by Shiplayer on 02.06.18.
 */

public class AddressConverter {
    @TypeConverter
    public static Address toAddress(String string){
        Gson gson = new Gson();
        return gson.fromJson(string, Address.class);
    }

    @TypeConverter
    public static String toString(Address address){
        Gson gson = new Gson();
        return gson.toJson(address);
    }
}
