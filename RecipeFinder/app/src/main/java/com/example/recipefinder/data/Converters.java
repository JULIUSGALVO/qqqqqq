package com.example.recipefinder.data;

import androidx.room.TypeConverter;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromStringArray(String[] value) {
        if (value == null) return null;
        return String.join(",", value);
    }

    @TypeConverter
    public static String[] toStringArray(String value) {
        if (value == null) return null;
        return value.split(",");
    }
} 