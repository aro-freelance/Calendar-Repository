package com.aro.jcalendar.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime;

@Entity(tableName = "task_table")
public class Task {
    /*
    This is our task table (notes)

    note that all of these parameters will be columns.
    The column info tag is used when we want to change the name
    that the column will have from the default name which we gave it here to something else
    */

    @ColumnInfo(name="task_id")
    @PrimaryKey(autoGenerate = true)
    public long taskId;

    //the text of the note
    public String task;

    //todo this is not in use
    public Priority priority;

    @ColumnInfo(name = "due_date")
    public LocalDateTime dueDate;

    @ColumnInfo(name="created_at")
    public LocalDateTime dateCreated;

    //todo this is not in use
    @ColumnInfo(name="is_done")
    public Boolean isDone;

    public String category;

    @ColumnInfo(name="image_url")
    public String imageUrl;

    //todo this is not in use
    public String color;

    @ColumnInfo(name="color_int")
    public Integer colorInt;

    @ColumnInfo(name="text_color_int")
    public Integer textColorInt;



    public Task(){}
//
    public Task(String task, Priority priority, LocalDateTime dueDate, LocalDateTime cateCreated,
                Boolean isDone, String category, String imageUrl, String color, Integer colorInt,
                Integer textColorInt) {
        this.task = task;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dateCreated = cateCreated;
        this.isDone = isDone;
        this.category = category;
        this.imageUrl = imageUrl;
        this.color = color;
        this.colorInt = colorInt;
        this.textColorInt = textColorInt;
    }


    public Integer getTextColorInt() {
        return textColorInt;
    }

    public void setTextColorInt(Integer textColorInt) {
        this.textColorInt = textColorInt;
    }

    public int getColorInt() {
        return colorInt;
    }

    public void setColorInt(Integer colorInt) {
        this.colorInt = colorInt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", task='" + task + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", dateCreated=" + dateCreated +
                ", isDone=" + isDone +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", color='" + color + '\'' +
                ", colorInt=" + colorInt +
                ", textColorInt=" + textColorInt +
                '}';
    }
}
