package com.aro.jcalendar.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.aro.jcalendar.data.CategoryRepository;
import java.util.List;

public class CategoryViewModel extends AndroidViewModel {


    public static CategoryRepository repository;
    public final LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
        allCategories = repository.getCategoryList();
    }




    //create
    public static void insert(Category category) {repository.insert(category);}

    //read
    public LiveData<Category> get(long id) {return repository.get(id); }

    //read all
    public LiveData<List<Category>> getAllCategories() {return allCategories;}

    //update
    public static void update(Category category) {repository.update(category);}

    //delete
    public static void delete(Category category) {repository.delete(category);}

}
