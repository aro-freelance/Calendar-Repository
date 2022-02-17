package com.aro.jcalendar.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.aro.jcalendar.model.Task;
import com.aro.jcalendar.util.CalendarRoomDatabase;
import java.util.List;

public class TaskRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        CalendarRoomDatabase database = CalendarRoomDatabase.getDatabase(application);
        this.taskDao = database.taskDao();
        this.allTasks = taskDao.getAllTasks();
    }

    //create
    public void insert(Task task){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.insertTask(task));
    }

    //read
    public LiveData<Task> get(long id){
        return taskDao.get(id);
    }

    //read all
    public LiveData<List<Task>> getTaskList(){
        return allTasks;
    }

    //update
    public void update(Task task){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.update(task));
    }

    //delete
    public void delete(Task task){
        CalendarRoomDatabase.databaseWriterExecutor.execute(()-> taskDao.delete(task));
    }

}
