package com.aro.jcalendar.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aro.jcalendar.R;
import com.aro.jcalendar.model.Task;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    /*
    this is the note list adapter (to display when a day is clicked)
     */

    private final List<Task> taskList;
    private final OnItemClickedListener itemClickedListener;
    private final Context context;



    //constructor
    public RecyclerViewAdapter(List<Task> taskList, OnItemClickedListener onItemClickedListener, Context context) {
        this.taskList = taskList;
        this.itemClickedListener = onItemClickedListener;
        this.context = context;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get the task at the current position
        Task task = taskList.get(position);
        String formattedDate;

        LocalDateTime dueDateLDT = task.getDueDate();

        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        String rotationStringName = task.getTask() + "_rotation_saved_name_" + task.getImageUrl();

        int rotation = sharedPreferences.getInt(rotationStringName, 0);

        //account for AM PM
        int hour = dueDateLDT.getHour();
        String amPM = " AM";
        if(hour > 12){
           hour = hour - 12;
           amPM = " PM";
        }
        if(hour == 12){
            amPM = " PM";
        }
        if(hour == 0){
            hour = 12;
            amPM = " AM";
        }

        //fix minutes less than zero
        int minutes = dueDateLDT.getMinute();
        String minString = String.valueOf(minutes);

        if(minutes < 10){
            minString = "0" + minString;
        }

        formattedDate = "" + hour + ":" + minString + amPM +" on "
                + dueDateLDT.getMonth() + " " + dueDateLDT.getDayOfMonth() + ", " + dueDateLDT.getYear();

        int color = task.getColorInt();
        int textColor = task.getTextColorInt();

        //if for some reason the text color is not black or white, set it to black
        if(textColor != -1 && textColor != -16777216){
            textColor = -16777216;
        }

        if(task.getImageUrl() != null){

            holder.imageView.setVisibility(View.VISIBLE);

            String imageUrl = task.getImageUrl();

            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_baseline_image_grey_24)
                    .rotate(rotation)
                    .into(holder.imageView);

        }
        else{
            holder.imageView.setVisibility(View.GONE);
        }

        //set the task data to the views
        holder.taskText.setText(task.getTask());
        holder.dueDateChip.setText(formattedDate);
        holder.taskText.setTextColor(textColor);
        holder.parentLayout.setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //set up views from the row layout
        public AppCompatRadioButton radioButton;
        public AppCompatTextView taskText;
        public Chip dueDateChip;
        public ImageView imageView;
        public ConstraintLayout parentLayout;

        OnItemClickedListener onItemClickedListener;

        //constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.todo_radio_button);
            taskText = itemView.findViewById(R.id.todo_row_textview);
            dueDateChip = itemView.findViewById(R.id.todo_row_chip);
            imageView = itemView.findViewById(R.id.image_view_todo_row);
            parentLayout = itemView.findViewById(R.id.todo_row_layout);


            this.onItemClickedListener = itemClickedListener;

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);
            dueDateChip.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            Task currentTask = taskList.get(getAdapterPosition());

            //if the user clicks the whole row open it for edits
            if(id == R.id.todo_row_layout){
                onItemClickedListener.onTaskClicked(getAdapterPosition(), currentTask);
            }
            //if the user clicks the date chip
            else if (id == R.id.todo_row_chip){
                onItemClickedListener.onTaskClicked(getAdapterPosition(), currentTask);
            }
            //if the user clicks the radio button... this is handled in main activity. Move to completed. Or if in completed prompt delete.
            else if (id == R.id.todo_radio_button){
                onItemClickedListener.onTaskRadioButtonClicked(currentTask);
            }
        }
    }
}
