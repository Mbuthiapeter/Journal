
/**
 * Created by Mbuthia Peter on 25/06/2018. As part of completion of 7 days of code challenge In
 * pursuit of the Google ALC nano degree program. ALC With Google 3.0
 */

package com.friki.mbuthia.journalapp.utils;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
