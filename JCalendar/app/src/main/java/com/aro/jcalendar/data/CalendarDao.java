package com.aro.jcalendar.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aro.jcalendar.model.Calendar;

import java.util.List;

@Dao
public interface CalendarDao {


    //create
    @Insert
    void insertCalendar(Calendar calendar);

    //read
    @Query("SELECT * FROM calendar_table WHERE calendar_table.calendar_id == :id")
    LiveData<Calendar> get(long id);

    //read all
    @Query("SELECT * FROM calendar_table")
    LiveData<List<Calendar>> getAllCalendarDates();

    //update
    @Update
    void update(Calendar calendar);

    //delete
    @Delete
    void delete(Calendar calendar);

    //delete all
    @Query("DELETE FROM calendar_table")
    void deleteAll();



}
