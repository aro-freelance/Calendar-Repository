package com.aro.jcalendar.util;


import androidx.room.TypeConverter;
import com.aro.jcalendar.model.Priority;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Converter {


    //convert from list to string
    @TypeConverter
    public String get_string(List<String> str) {
        if (str == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : str) stringBuilder.append(s).append(",");
        return stringBuilder.toString();
    }

    @TypeConverter
    public List<String> set_string(String str) {
        if (str == null)
            return null;
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }


    //convert timestamp stored in db back into LocalDateTime
    @TypeConverter
    public static LocalDateTime timestampToLDT (Long value){

        return value == null ? null : Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }


    //convert LocalDateTime to Timestamp to store in db
    @TypeConverter
    public static Long ldtToTimestamp(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    //convert timestamp stored in Database back into a Date
    @TypeConverter
    public static Date timestampToDate (Long value){
        //if the value we received is null return null, else create a new Date with the value we are receiving
        return value == null ? null : new Date(value);

    }

    //convert Date to Timestamp to store in the db
    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Priority stringToPriority(String string){
        return string == null ? null : Priority.valueOf(string);
    }

    @TypeConverter
    public static String priorityToString(Priority priority) {
        return priority == null ? null : priority.toString();
    }


}
