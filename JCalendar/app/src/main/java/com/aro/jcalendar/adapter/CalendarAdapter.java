package com.aro.jcalendar.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aro.jcalendar.R;
import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.model.Task;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//an adapter to display the data as a calendar grid
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<Calendar> calendarList;
    private final List<LocalDateTime> notificationList;
    private final List<Task> taskList;
    private final OnItemClickedListener itemClickedListener;

    public CalendarAdapter(List<Calendar> calendarList, List<LocalDateTime> notificationList,
                           List<Task> taskList, OnItemClickedListener itemClickedListener) {
        this.calendarList = calendarList;
        this.notificationList = notificationList;
        this.taskList = taskList;
        this.itemClickedListener = itemClickedListener;
    }

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_single_box, parent, false);

        Context context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, int position) {

        holder.previewNoteOneTextView.setVisibility(View.INVISIBLE);
        holder.noteNumberTextView.setVisibility(View.INVISIBLE);

        holder.colorCircleOne.setVisibility(View.GONE);
        holder.colorCircleTwo.setVisibility(View.GONE);
        holder.colorCircleThree.setVisibility(View.GONE);

        //get the calendar at the current position
        Calendar calendar = calendarList.get(position);

        String dateNumber = String.valueOf(calendar.getDate().getDayOfMonth());

        for (int i = 0; i < notificationList.size(); i++) {

            LocalDateTime localDateTime = notificationList.get(i);
            List<Task> localTaskList = new ArrayList<>();
            int numberOfTasks = 0;

            int colorCircleCounter = 0;

            if(calendar.getDate().getMonthValue() == localDateTime.getMonthValue()
            && calendar.getDate().getDayOfMonth() == localDateTime.getDayOfMonth()
            && calendar.getDate().getYear() == localDateTime.getYear()){

                for (int t = 0; t < taskList.size(); t++) {

                    Task thisTask = taskList.get(t);

                    if( thisTask != null){

                        int color = thisTask.getColorInt();

                        if(thisTask.getDueDate().getMonthValue() == localDateTime.getMonthValue()
                                && thisTask.getDueDate().getDayOfMonth() == localDateTime.getDayOfMonth()
                                && thisTask.getDueDate().getYear() == localDateTime.getYear()){

                            if(!thisTask.category.equals("Completed")){

                                localTaskList.add(thisTask);
                                numberOfTasks += 1;

                                colorCircleCounter = colorCircleCounter + 1;

                                //if the color is white, to make it not invisible, make it display black (outline)
                                if(color == -1 ){
                                    color = -16777216;
                                }

                                if(colorCircleCounter == 1){
                                    holder.colorCircleOne.setVisibility(View.VISIBLE);
                                    holder.colorCircleOne.setColorFilter(color);
                                }

                                if(colorCircleCounter == 2){
                                    holder.colorCircleTwo.setVisibility(View.VISIBLE);
                                    holder.colorCircleTwo.setColorFilter(color);
                                }

                                if(colorCircleCounter == 3){
                                    holder.colorCircleThree.setVisibility(View.VISIBLE);
                                    holder.colorCircleThree.setColorFilter(color);

                                }

                            }

                        }
                    }

                    holder.previewNoteOneTextView.setVisibility(View.VISIBLE);
                    if(localTaskList.size() > 0){
                        if(localTaskList.get(0).getTask() != null){
                            holder.previewNoteOneTextView.setText(localTaskList.get(0).getTask());
                        }
                    }

                    holder.noteNumberTextView.setVisibility(View.VISIBLE);
                    holder.noteNumberTextView.setText(String.valueOf(numberOfTasks));
                    }
            }
        }

        holder.dateTextView.setText(dateNumber);

    }

    @Override
    public int getItemCount() {
        return calendarList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //declare views from the layout we are inflating
        private final TextView dateTextView;
        private final TextView noteNumberTextView;
        private final TextView previewNoteOneTextView;

        private final ImageView colorCircleOne;
        private final ImageView colorCircleTwo;
        private final ImageView colorCircleThree;


        OnItemClickedListener onItemClickedListener;

        //constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //implement views
            dateTextView = itemView.findViewById(R.id.date_number_textview);
            noteNumberTextView = itemView.findViewById(R.id.note_Quantity_TextView);
            previewNoteOneTextView = itemView.findViewById(R.id.note_preview_textview_1);

            colorCircleOne = itemView.findViewById(R.id.color_circle_1);
            colorCircleTwo = itemView.findViewById(R.id.color_circle_2);
            colorCircleThree = itemView.findViewById(R.id.color_circle_3);


            //set up on click

            this.onItemClickedListener = itemClickedListener;
            itemView.setOnClickListener(this);




        }

        @Override
        public void onClick(View view) {

            int id = view.getId();

            if(getAdapterPosition() >= 0 && getAdapterPosition() <= calendarList.size()){

                Calendar currentCalendar = calendarList.get(getAdapterPosition());

                if (id == R.id.single_box_parent){
                    onItemClickedListener.OnCalendarClickedListener(getAdapterPosition(), currentCalendar);
                }

            }
            //if other id (other view is clicked)
            //do other things
        }
    }
}
