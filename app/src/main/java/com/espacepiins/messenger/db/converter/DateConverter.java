package com.espacepiins.messenger.db.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by guillaume on 18-03-20.
 */
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
