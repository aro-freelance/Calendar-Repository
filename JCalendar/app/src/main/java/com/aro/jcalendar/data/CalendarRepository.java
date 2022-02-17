package com.aro.jcalendar.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.util.CalendarRoomDatabase;

import java.util.List;

public class CalendarRepository {

    private final CalendarDao calendarDao;
    private final LiveData<List<Calendar>> allCalendarDates;


    public CalendarRepository(Application application) {
        CalendarRoomDatabase database = CalendarRoomDatabase.getDatabase(application);
        this.calendarDao = database.calendarDao();
        this.allCalendarDates = calendarDao.getAllCalendarDates();
    }

    //create
    public void insert(Calendar calendar){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> calendarDao.insertCalendar(calendar));
    }

    //read
    public LiveData<Calendar> get(long id){
        return calendarDao.get(id);
    }

    //read all
    public LiveData<List<Calendar>> getCalendarList(){
        return allCalendarDates;
    }

    //update
    public void update(Calendar calendar){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> calendarDao.update(calendar));
    }

    //delete
    public void delete(Calendar calendar){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> calendarDao.delete(calendar));
    }

    //delete all
    public void deleteAll(){
        CalendarRoomDatabase.databaseWriterExecutor.execute(calendarDao::deleteAll);
    }

}
