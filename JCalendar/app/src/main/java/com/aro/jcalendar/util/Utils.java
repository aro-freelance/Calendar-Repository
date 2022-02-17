package com.aro.jcalendar.util;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;

public class Utils {

 /*
     This is a class with helper functions that may be used in multiple activities and classes
     */


    public static String formatDate(LocalDateTime date){
        //simple date format patterns can be found here: https://developer.android.com/reference/java/text/SimpleDateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d"); //Sat, Sep 4

        return simpleDateFormat.format(date);
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static long[] getTimeDifference(LocalDateTime start, LocalDateTime end) {

        Duration duration = Duration.between(start, end);

        long days = duration.toDays();

        long hours = duration.toHours() - (duration.toDays() * 24);

        long minutes = duration.toMinutes() - (duration.toHours() * 60);

        long seconds = duration.getSeconds() - (duration.toMinutes() * 60);

        return new long[]{days, hours, minutes, seconds};
    }

}
