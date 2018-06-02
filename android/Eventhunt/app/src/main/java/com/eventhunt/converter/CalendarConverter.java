package com.eventhunt.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shiplayer on 02.06.18.
 */

public class CalendarConverter {
    @TypeConverter
    public static Calendar toCalendar(Long timestamp){
        if(timestamp != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(timestamp));
            return calendar;
        } else
            return null;
    }
    @TypeConverter
    public static Long toTimestamp(Calendar calendar){
        if(calendar!= null)
            return calendar.getTime().getTime();
        else
            return null;
    }
}
