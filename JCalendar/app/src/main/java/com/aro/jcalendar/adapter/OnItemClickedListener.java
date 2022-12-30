package com.aro.jcalendar.adapter;


import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.model.Counter;
import com.aro.jcalendar.model.Task;

public interface OnItemClickedListener {

    //if the calendar grid box is clicked
    void OnCalendarClickedListener(int position, Calendar calendar);

    //if task layout is clicked
    void onTaskClicked(int position, Task task);

    //if radio button is clicked
    void onTaskRadioButtonClicked(Task task);

//    void OnCounterClickedListener(int position, Counter currentCounter);

}
