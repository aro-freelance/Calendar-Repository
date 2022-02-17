package com.aro.jcalendar.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.aro.jcalendar.model.Category;
import com.aro.jcalendar.util.CalendarRoomDatabase;
import java.util.List;

public class CategoryRepository {

    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;

    public CategoryRepository(Application application) {
        CalendarRoomDatabase database = CalendarRoomDatabase.getDatabase(application);
        this.categoryDao = database.categoryDao();
        this.allCategories = categoryDao.getAllCategories();
    }

    //create
    public void insert(Category category){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.insertCategory(category));
    }

    //read
    public LiveData<Category> get(long id){
        return categoryDao.get(id);
    }

    //read all
    public LiveData<List<Category>> getCategoryList(){
        return allCategories;
    }

    //update
    public void update(Category category){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.update(category));
    }

    //delete
    public void delete(Category category){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> categoryDao.delete(category));
    }
}
