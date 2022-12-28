package com.aro.jcalendar.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aro.jcalendar.data.CounterRepository;

import java.util.List;

public class CounterViewModel extends AndroidViewModel {

    public static CounterRepository repository;
    public final LiveData<List<Counter>> allCounters;

    public CounterViewModel(@NonNull Application application) {
        super(application);
        repository = new CounterRepository(application);
        allCounters = repository.getCounterList();
    }

    //create
    public static void insert(Counter counter) {repository.insert(counter);}

    //read
    public LiveData<Counter> get(long id) {return repository.get(id); }

    //read all
    public LiveData<List<Counter>> getAllCounters() {return allCounters;}

    //update
    public static void update(Counter counter) {repository.update(counter);}

    //delete
    public static void delete(Counter counter) {repository.delete(counter);}
}
