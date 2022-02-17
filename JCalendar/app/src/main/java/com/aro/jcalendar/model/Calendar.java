package com.aro.jcalendar.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDateTime;
import java.util.List;


@Entity(tableName = "calendar_table")
public class Calendar {

    @ColumnInfo(name="calendar_id")
    @PrimaryKey(autoGenerate = true)
    public long calendarId;

    public LocalDateTime date;

    @ColumnInfo(name="list_of_notes")
    public List<String> listOfNotes;

    @ColumnInfo(name="is_today")
    public Boolean isToday;

    @ColumnInfo(name="has_notes")
    public Boolean hasNotes;

    @ColumnInfo(name="color_int")
    public int colorInt;


    public Calendar(){}


    public Calendar(LocalDateTime date, List<String> listOfNotes, Boolean isToday, Boolean hasNotes, int colorInt) {
        this.date = date;
        this.listOfNotes = listOfNotes;
        this.isToday = isToday;
        this.hasNotes = hasNotes;
        this.colorInt = colorInt;
    }


    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<String> getListOfNotes() {
        return listOfNotes;
    }

    public void setListOfNotes(List<String> listOfNotes) {
        this.listOfNotes = listOfNotes;
    }

    public Boolean getToday() {
        return isToday;
    }

    public void setToday(Boolean today) {
        isToday = today;
    }

    public Boolean getHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(Boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public int getColorInt() {
        return colorInt;
    }

    public void setColorInt(int colorInt) {
        this.colorInt = colorInt;
    }


    @Override
    public String toString() {
        return "Calendar{" +
                "calendarId=" + calendarId +
                ", date=" + date +
                ", listOfNotes=" + listOfNotes +
                ", isToday=" + isToday +
                ", hasNotes=" + hasNotes +
                ", colorInt=" + colorInt +
                '}';
    }
}
