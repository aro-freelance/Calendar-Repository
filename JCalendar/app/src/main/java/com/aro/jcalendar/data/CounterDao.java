package com.aro.jcalendar.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aro.jcalendar.model.Counter;

import java.util.List;

@Dao
public interface CounterDao {
    //create
    @Insert
    void insertCounter(Counter counter);

    //read
    @Query("SELECT * FROM counter_table WHERE counter_table.counter_id == :id")
    LiveData<Counter> get(long id);

    //read all
    @Query("SELECT * FROM counter_table")
    LiveData<List<Counter>> getAllCounters();

    //update
    @Update
    void update(Counter counter);

    //delete
    @Delete
    void delete(Counter counter);

    //delete all
    @Query("DELETE FROM counter_table")
    void deleteAll();
}
