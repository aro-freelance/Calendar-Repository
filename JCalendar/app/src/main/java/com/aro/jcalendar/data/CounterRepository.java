package com.aro.jcalendar.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.aro.jcalendar.model.Counter;
import com.aro.jcalendar.util.CalendarRoomDatabase;

import java.util.List;

public class CounterRepository {

    private final CounterDao counterDao;
    private final LiveData<List<Counter>> allCounters;

    public CounterRepository(Application application) {
        CalendarRoomDatabase database = CalendarRoomDatabase.getDatabase(application);
        this.counterDao = database.counterDao();
        this.allCounters = counterDao.getAllCounters();
    }

    //create
    public void insert(Counter counter){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> counterDao.insertCounter(counter));
    }

    //read
    public LiveData<Counter> get(long id){
        return counterDao.get(id);
    }

    //read all
    public LiveData<List<Counter>> getCounterList(){
        return allCounters;
    }

    //update
    public void update(Counter counter){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> counterDao.update(counter));
    }

    //delete
    public void delete(Counter counter){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> counterDao.delete(counter));
    }
}
