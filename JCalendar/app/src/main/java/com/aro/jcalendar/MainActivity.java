package com.aro.jcalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.aro.jcalendar.adapter.CalendarAdapter;
import com.aro.jcalendar.adapter.OnItemClickedListener;
import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.model.CalendarViewModel;
import com.aro.jcalendar.model.Counter;
import com.aro.jcalendar.model.CounterViewModel;
import com.aro.jcalendar.model.Task;
import com.aro.jcalendar.model.TaskViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnItemClickedListener, AdapterView.OnItemSelectedListener {

    /*
    This app is a calendar app that will display a calendar grid.
    Notes can be added to days and displayed on the grid.
    This uses Room Database with 3 entities. 1. Calendar 2. Notes 3. Categories



        //this is not urgent because it is not breaking anything (just not in use)
        todo: remove unused columns from the task database (boolean isDone and String color)
        //this is not urgent because it is working as is
        todo : add rotation value to the room table and remove the sharedprefs stuff that is saving it


                   ////////////////////////////
                    ///////////////////////////
                    ///////////////////////////
                    todo possible update ideas
                   ///////////////////////////
                   ///////////////////////////



        todo : read me in the menu explaining the icons and basic workings of the app

        todo : Notifications
         Add android notification functionality

        todo: add a loading screen
         make the background image load before the user is shown the activity

        todo : Stickers?
         add the ability to put stickers on days maybe if i can

         todo : Gallery?
          Possibly add a fragment tab which is a gallery

          todo : View Modes
           Implement different types of display mode. Monthly, weekly, daily.

           todo: consider login system?



    /*

    This activity is for displaying the calendar data in a calendar grid view..

     */


    private static final int GALLERY_CODE = 1;

    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private GridLayoutManager layout;
    private Spinner monthTitleSpinner;
    private ProgressBar progressBar;
    private ImageView backgroundImage;

    private final LocalDateTime now = LocalDateTime.now();
    private LocalDateTime movingLDT = LocalDateTime.of(now.getYear(), now.getMonthValue(),
            1, 0, 0);

    private List<Calendar> calendarList;
    private List<Calendar> monthList;
    private List<Task> taskList;
    private List<Counter> counterList;
    private List<Counter> allCountersList;
    private List<LocalDateTime> ldtsWithNotificationList;

    private Uri imageUri;
    private String uriAsString;

    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;
    private int backgroundImageRotateValue = 0;
    private boolean isInitialLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.calendar_grid_recyclerview);
        monthTitleSpinner = findViewById(R.id.month_title_spinner);
        ImageButton prevMonthButton = findViewById(R.id.prev_month_button);
        ImageButton nextMonthButton = findViewById(R.id.next_month_button);
        ConstraintLayout backgroundLayout = findViewById(R.id.main_constraint_background);
        progressBar = findViewById(R.id.progressBar_main);
        backgroundImage = findViewById(R.id.background_imageview_main);

        calendarList = new ArrayList<>();
        monthList = new ArrayList<>();
        taskList = new ArrayList<>();
        counterList = new ArrayList<>();
        ldtsWithNotificationList = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        CalendarViewModel calendarViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CalendarViewModel.class);

        //get the counter values for this month and make a list of them
        CounterViewModel counterViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CounterViewModel.class);
        counterViewModel.getAllCounters().observe(this, allCounters ->{

            //use the full list of counters, allCounters to make a list for the current month list
            allCountersList = allCounters;

        });


        //get the tasks from the database
        TaskViewModel taskViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {

            taskList = tasks;

            //set up the list of dates for the month
            getMonthList();

            //use the full list of counters, allCountersList to make a list for the current month list
            getCounterList();

            //set recyclerview
            adapter = new CalendarAdapter(monthList, ldtsWithNotificationList, taskList, counterList, this);
            layout = new GridLayoutManager(this, 7);
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(adapter);

        });

        prevMonthButton.setOnClickListener(this::prevMonthButtonMethod);
        nextMonthButton.setOnClickListener(this::nextMonthButtonMethod);
        monthTitleSpinner.setOnItemSelectedListener(this);

        //set monthlistspinner
        monthListSpinnerSetup();

        //get rotation from sharedprefs
        backgroundImageRotateValue = sharedPreferences.getInt("background_rotation", 0);
        //set the background image using the rotation
        setBackgroundImage(backgroundImageRotateValue);

    }


    public void monthListSpinnerSetup(){

        int year = movingLDT.getYear();
        int monthNowInt = movingLDT.getMonthValue();

        //if this is the first load get the month and year that the user was on when they were on the calendar last time
        if(isInitialLoad){
            monthNowInt = sharedPreferences.getInt("user_nav_month", monthNowInt);
            year = sharedPreferences.getInt("user_nav_year", year);

            movingLDT = LocalDateTime.of(year, monthNowInt, 1, 0, 0);

            isInitialLoad = false;
        }

        //make a list of strings for the months of the year the user is currently on/navigated to
        ArrayList<String> monthArrayList = new ArrayList<>();

        monthArrayList.add(Month.JANUARY.toString() + " " + year);
        monthArrayList.add(Month.FEBRUARY.toString() + " " + year);
        monthArrayList.add(Month.MARCH.toString() + " " + year);
        monthArrayList.add(Month.APRIL.toString() + " " + year);
        monthArrayList.add(Month.MAY.toString() + " " + year);
        monthArrayList.add(Month.JUNE.toString() + " " + year);
        monthArrayList.add(Month.JULY.toString() + " " + year);
        monthArrayList.add(Month.AUGUST.toString() + " " + year);
        monthArrayList.add(Month.SEPTEMBER.toString() + " " + year);
        monthArrayList.add(Month.OCTOBER.toString() + " " + year);
        monthArrayList.add(Month.NOVEMBER.toString() + " " + year);
        monthArrayList.add(Month.DECEMBER.toString() + " " + year);

        //use the array to populate the month list spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthTitleSpinner.setAdapter(adapter);

        //set the spinner selection
        monthTitleSpinner.setSelection(monthNowInt - 1);
    }


    //create the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle the menu options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.action_calendar:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.action_today:
                intent = new Intent(this, AddNewNote.class);
                startActivity(intent);

                break;

            case R.id.action_change_bg_image:

                //open background photo select
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);


                //onActivityResults happens here

                break;

            case R.id.action_rotate_bg_image:

                //rotate image
                backgroundImageRotateValue = backgroundImageRotateValue + 90;

                //save rotate value to sharedprefs
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("background_rotation", backgroundImageRotateValue);
                editor.apply();


                //set background
                setBackgroundImage(backgroundImageRotateValue);

                break;

        }

        return super.onOptionsItemSelected(item);
    }



    private void setBackgroundImage(int rotateValue){

        //get image from sharedprefs
        uriAsString = sharedPreferences.getString("background_image", "");

        if(!uriAsString.equals("")){

            Picasso.get().load(uriAsString)
                    .rotate(rotateValue)
                    .into(backgroundImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Random theDragon = new Random();

        //make a filename where we will save the image
        String imageName = "" + theDragon.nextInt(9999) + Timestamp.now().getSeconds();


        //if user is returning from photo intent
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

            //if there is selected image data
            if(data != null){

                imageUri = data.getData(); // this is the path where the image is
                //this is the url
                uriAsString = imageUri.toString();

                //if there is an image
                if(imageUri != null){

                    progressBar.setVisibility(View.VISIBLE);

                    //make a filepath to save the image in firebase
                    StorageReference filepath = storageReference
                            .child("background_images")
                            .child(imageName);


                    //save the image to the filepath
                    filepath.putFile(imageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                progressBar.setVisibility(View.INVISIBLE);

                                filepath.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {

                                            imageUri = uri;
                                            uriAsString = imageUri.toString();

                                            //save image url to sharedprefs
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("background_image", uriAsString);
                                            editor.apply();

                                            setBackgroundImage(backgroundImageRotateValue);
                                        })
                                        //if we can't get the image url
                                        .addOnFailureListener(e -> {

                                            //toast?
                                            //Log.d("imgstore", "Failed to get image url. Error: " + e  );

                                        });
                            })
                            //if the image doesn't save to firebase
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                //toast?
                                //Log.d("imgstore", "Failed to upload image to firebase. Error: " + e);
                            });
                }
            }
        }
    }


    //make a list of the ldts on the current month list that have a task
    // (this will be used to create the UI display that shows the task "notifications" on the calendar grid)
    private void setNotifications(List<Task> taskList) {

        //loop through the full task list
        for (int i = 0; i < taskList.size(); i++) {

            Task thisTask = taskList.get(i);

                //loop through the month list
                for (int m = 0; m < monthList.size(); m++) {
                    LocalDateTime currentMonthListLDT = monthList.get(m).getDate();

                    //if the task is on the month list (based on date info)
                    if(thisTask.getDueDate().getMonthValue() == currentMonthListLDT.getMonthValue() &&
                       thisTask.getDueDate().getDayOfMonth() == currentMonthListLDT.getDayOfMonth() &&
                       thisTask.getDueDate().getYear() == currentMonthListLDT.getYear()){

                        //if it is not completed
                        if(!thisTask.category.equals("Completed")){

                            //add to the notification list of ldts
                            ldtsWithNotificationList.add(thisTask.getDueDate());

                        }
                    }
                }
        }

    }

    //add dates to the start of the month to display from last month
    // (based on the day of the week when the first of the month is)
    private void setStartOfMonth(){

        int month = movingLDT.getMonthValue();
        int year = movingLDT.getYear();

        //time doesn't matter for this since it is just being used to determine
        // day of the week of the first of the month
        LocalDateTime dayOne = LocalDateTime.of(year, month, 1, 0, 0);

        String dayOfWeek = String.valueOf(dayOne.getDayOfWeek());

        int startOfMonthCorrectionNumber = 0;

        //set the correction number based on the first of the month
        // this int is the number of days from last month to display on the calendar grid
        switch (dayOfWeek){

            case "MONDAY":
               startOfMonthCorrectionNumber = 1;
                break;
            case "TUESDAY":
                startOfMonthCorrectionNumber = 2;
                break;
            case "WEDNESDAY":
                startOfMonthCorrectionNumber = 3;
                break;
            case "THURSDAY":
                startOfMonthCorrectionNumber = 4;
                break;
            case "FRIDAY":
                startOfMonthCorrectionNumber = 5;
                break;
            case "SATURDAY":
                startOfMonthCorrectionNumber = 6;
                break;
            case "SUNDAY":
                startOfMonthCorrectionNumber = 0;
                break;
            default:
                break;
        }


        //add to the month list
        for (int i = 0; i < startOfMonthCorrectionNumber; i++) {

            Calendar calendarToSave = new Calendar();
            LocalDateTime ldt;

            //make an ldt date to add to the monthList
            //if display month is not January
            if(movingLDT.getMonthValue() > 1){
                ldt = LocalDateTime.of(movingLDT.getYear(), movingLDT.getMonthValue() - 1,
                        dayOne.minusDays(startOfMonthCorrectionNumber - i).getDayOfMonth(), 0 , 0);
            }
            //if it is January
            else {
                ldt = LocalDateTime.of(movingLDT.getYear() -1, 12 ,
                        dayOne.minusDays(startOfMonthCorrectionNumber - i).getDayOfMonth(), 0 , 0);
            }


            //// handle calendar database stuff////////////////////////////////////
            ///// todo: double check that this is needed ///////////////////////
            //// note that this database is fully deleted each time the month list method in this activity runs ///////
            boolean isNewCalendarEntry = true;
            //look for entry matching the date
            for(Calendar calendar : calendarList){
                //if there is an entry matching the date, get the calendar data from the database
                if(calendar.getDate() == ldt){
                    isNewCalendarEntry = false;
                    //set the calendar parameters
                    calendarToSave = calendar;
                }
            }
            //if there is not a matching entry in the database, make the other parameters blank
            if(isNewCalendarEntry){
                //set the calendar parameters to blanks
                calendarToSave.setDate(ldt);
                calendarToSave.setHasNotes(false);
            }

            /////////////////////////////////////////////////////////////////

            //save calendar object to the monthList (the list that is displayed to the calendar adapter grid)
            monthList.add(calendarToSave);
        }
    }


    //get current month, add it one date at a time to the calendar list, and set it to the recyclerview
    private void getMonthList(){

        int year = movingLDT.getYear();

        boolean checkLeapYear = false;
        if( year % 4 == 0) { checkLeapYear = true; }

        int monthLength = movingLDT.getMonth().length(checkLeapYear);

        CalendarViewModel.deleteAll();
        calendarList.clear();
        monthList.clear();
        ldtsWithNotificationList.clear();


        //this adds the last days of the previous month to the calendar start
        // to offset for the day of the week of the first of the month
        setStartOfMonth();

        //make a calendar object and save it to the list
        // (note that these dates for the currently selected month are added to the month list
        // after the start of month correction dates from the previous month)
        for (int i = 0; i < monthLength; i++) {

            Calendar calendarToSave = new Calendar();

            LocalDateTime ldt = LocalDateTime.of(movingLDT.getYear(), movingLDT.getMonth(),
                    i +1, 0 , 0);

            boolean isNewCalendarEntry = true;


            ////// calendar database stuff/////////////////////////////////
            ///////// todo: check if this is needed///////////////////////
            //look for entry matching the date
            for(Calendar calendar : calendarList){

                //if there is an entry matching the date, get the calendar data from the database
                if(calendar.getDate() == ldt){

                    isNewCalendarEntry = false;

                    //set the calendar parameters
                    calendarToSave = calendar;
                    

                }
            }
            //if there is not a matching entry in the database, make the other parameters blank
            if(isNewCalendarEntry){

                //set the calendar parameters to blanks

                calendarToSave.setDate(ldt);
                calendarToSave.setHasNotes(false);

            }
            ////////////////////////////////////////////////////////////////////

            //save calendar object to the month list
            monthList.add(calendarToSave);
        }

        //populate the list that holds the ldts with a note (in the current monthList)
        setNotifications(taskList);

    }

    //use the monthList and allCounterList to set a list of Counters that will display on the current calendar grid
    private void getCounterList(){

        //for each of the Counters
        for (int i = 0; i < allCountersList.size(); i++) {

            Counter currentCounter = allCountersList.get(i);

            //compare it to each of the days in the monthlist
            for (int j = 0; j < monthList.size(); j++) {

                Calendar currentDay = monthList.get(j);

                //if they are the same date
                if(currentDay.date.isEqual(currentCounter.date)){

                    //add the counter to the counterList
                    counterList.add(currentCounter);

                }
            }
        }

    }



    //Button for user to navigate to previous month
    private void prevMonthButtonMethod(View view) {

        monthList.clear();

        //update the ldt that is being used to display the month
        if(movingLDT.getMonthValue() != 1){
            movingLDT = LocalDateTime.of(movingLDT.getYear(), movingLDT.getMonthValue() - 1,
                    1, 0, 0);
        }
        else{
            movingLDT = LocalDateTime.of(movingLDT.getYear() - 1, 12,
                    1, 0, 0);
        }

        //save the month the user navigated to in shared prefs
        int month = movingLDT.getMonthValue();
        int year = movingLDT.getYear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_nav_month", month);
        editor.putInt("user_nav_year", year);
        editor.apply();

        //populate the month spinner using the (recently updated) movingLDT
        monthListSpinnerSetup();

        //set the month list using the (recently updated) movingLDT
        getMonthList();

        //use the full list of counters, allCountersList to make a list for the current month list
        getCounterList();

        //set recyclerview (calendar grid)
        adapter = new CalendarAdapter(monthList, ldtsWithNotificationList, taskList, counterList, this);
        layout = new GridLayoutManager(this, 7);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);


    }

    private void nextMonthButtonMethod(View view) {

        monthList.clear();

        //update the ldt that is being used to display the month
        if(movingLDT.getMonthValue() == 12){
            movingLDT = LocalDateTime.of(movingLDT.getYear() + 1, 1,
                    1, 0, 0);
        }
        else{
            movingLDT = LocalDateTime.of(movingLDT.getYear(), movingLDT.getMonthValue() + 1,
                    1, 0, 0);
        }

        //save the month the user navigated to in shared prefs
        int month = movingLDT.getMonthValue();
        int year = movingLDT.getYear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_nav_month", month);
        editor.putInt("user_nav_year", year);
        editor.apply();

        //populate the month spinner using the (recently updated) movingLDT
        monthListSpinnerSetup();

        //set the month list using the (recently updated) movingLDT
        getMonthList();

        //use the full list of counters, allCountersList to make a list for the current month list
        getCounterList();

        //set recyclerview (calendar grid)
        adapter = new CalendarAdapter(monthList, ldtsWithNotificationList, taskList, counterList,this);
        layout = new GridLayoutManager(this, 7);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

    }


    //user clicked on a calendar grid box
    @Override
    public void OnCalendarClickedListener(int position, Calendar calendar) {


        Intent intent = new Intent(this, AddNewNote.class);

        //send date info about the grid box the user clicked using intent
        int monthInt = calendar.getDate().getMonthValue();
        int dayInt = calendar.getDate().getDayOfMonth();
        int yearInt = calendar.getDate().getYear();
        LocalDateTime calendarLDT = calendar.getDate();
        String calendarLDTString = calendarLDT.toString();
        intent.putExtra("calendarDateString", calendarLDTString);
        intent.putExtra("calendarMonth", monthInt);
        intent.putExtra("calendarDay", dayInt);
        intent.putExtra("calendarYear", yearInt);

        startActivity(intent);

    }

    //must be here (because we are using the same onclicklistener). but don't need to use here
    @Override
    public void onTaskClicked(int position, Task task) {
    }

    //must be here (because we are using the same onclicklistener). but don't need to use here
    @Override
    public void onTaskRadioButtonClicked(Task task) {
    }

    @Override
    public void OnCounterClickedListener(int position) {
        //TODO: fill this out with what will happen when the counter number on the calendar is clicked
        //open the counter interface...
        //check if a counter is going
        //if a counter is going then ask for confirmation to reset it
        //else a counter is not going so start it
    }

    //month spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        monthList.clear();

        //set the movingLDT (month the user has selected) to the month the user clicked in the spinner drop down
        movingLDT = LocalDateTime.of(movingLDT.getYear(), pos +1, 1, 0, 0);

        //save the month the user navigated to in shared prefs
        int month = movingLDT.getMonthValue();
        int year = movingLDT.getYear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_nav_month", month);
        editor.putInt("user_nav_year", year);
        editor.apply();

        //use the month the user selected to set the monthList
        getMonthList();

        //use the full list of counters, allCountersList to make a list for the current month list
        getCounterList();

        //set recyclerview
        adapter = new CalendarAdapter(monthList, ldtsWithNotificationList, taskList, counterList, this);
        layout = new GridLayoutManager(this, 7);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

    }

    //month spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}