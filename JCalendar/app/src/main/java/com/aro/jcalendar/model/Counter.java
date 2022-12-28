package com.aro.jcalendar.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;


@Entity(tableName = "counter_table")
public class Counter {

    /*
    This is the database table for the counters on the calendar grid.
    These will be an auto-incrementing counter that displays on the grid.
    After they are added they will continue up until the current day until the user stops them/resets them.
     */


    @ColumnInfo(name="counter_id")
    @PrimaryKey(autoGenerate = true)
    public long counterId;

    @ColumnInfo(name="date_started")
    public LocalDateTime dateStarted;

    @ColumnInfo(name="counter_title")
    public String counterTitle;

    @ColumnInfo(name="counter_additional_info")
    public String counterAdditionalInfo;

    @ColumnInfo(name="is_active")
    public Boolean isActive;

    //this is the "current" date at the point in the counter we are looking at
    public LocalDateTime date;

    //this is the "current" value at the point in the counter we are looking at
    public int value;

    public Counter(){};

    public Counter(LocalDateTime dateStarted, String counterTitle, String counterAdditionalInfo, Boolean isActive, int value) {
        this.dateStarted = dateStarted;
        this.counterTitle = counterTitle;
        this.counterAdditionalInfo = counterAdditionalInfo;
        this.isActive = isActive;
        this.value = value;
    }

    public LocalDateTime getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(LocalDateTime dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getCounterTitle() {
        return counterTitle;
    }

    public void setCounterTitle(String counterTitle) {
        this.counterTitle = counterTitle;
    }

    public String getCounterAdditionalInfo() {
        return counterAdditionalInfo;
    }

    public void setCounterAdditionalInfo(String counterAdditionalInfo) {
        this.counterAdditionalInfo = counterAdditionalInfo;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
