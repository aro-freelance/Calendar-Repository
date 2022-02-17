package com.aro.jcalendar.model;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.aro.jcalendar.data.CalendarRepository;
import java.util.List;

public class CalendarViewModel extends AndroidViewModel {

    public static CalendarRepository repository;
    public final LiveData<List<Calendar>> allCalendarDates;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        repository = new CalendarRepository(application);
        allCalendarDates = repository.getCalendarList();
    }

    //create
    public static void insert(Calendar calendar) {repository.insert(calendar);}

    //read
    public LiveData<Calendar> get(long id) {return repository.get(id); }

    //read all
    public LiveData<List<Calendar>> getAllCalendarDates() {return allCalendarDates;}

    //update
    public static void update(Calendar calendar) {repository.update(calendar);}

    //delete
    public static void delete(Calendar calendar) {repository.delete(calendar);}

    //delete all
    public static void deleteAll() {repository.deleteAll();}
}
