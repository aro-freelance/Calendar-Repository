package com.aro.jcalendar.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.aro.jcalendar.model.Task;
import java.util.List;


@Dao
public interface TaskDao {

    //create
    @Insert
    void insertTask(Task task);

    //read
    @Query("SELECT * FROM task_table WHERE task_table.task_id == :id")
    LiveData<Task> get(long id);

    //read all
    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAllTasks();

    //update
    @Update
    void update(Task task);

    //delete
    @Delete
    void delete(Task task);

    //delete all
    @Query("DELETE FROM task_table")
    void deleteAll();

}
