package com.aro.jcalendar.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.aro.jcalendar.model.Category;
import java.util.List;


@Dao
public interface CategoryDao {

    //create
    @Insert
    void insertCategory(Category category);

    //read
    @Query("SELECT * FROM category_table WHERE category_table.categoryId == :id")
    LiveData<Category> get(long id);

    //read all
    @Query("SELECT * FROM category_table")
    LiveData<List<Category>> getAllCategories();

    //update
    @Update
    void update(Category category);

    //delete
    @Delete
    void delete(Category category);

    //delete all
    @Query("DELETE FROM category_table")
    void deleteAll();


}
