package com.aro.jcalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.aro.jcalendar.adapter.OnItemClickedListener;
import com.aro.jcalendar.adapter.RecyclerViewAdapter;
import com.aro.jcalendar.model.Calendar;
import com.aro.jcalendar.model.CalendarViewModel;
import com.aro.jcalendar.model.Category;
import com.aro.jcalendar.model.CategoryViewModel;
import com.aro.jcalendar.model.Counter;
import com.aro.jcalendar.model.CounterViewModel;
import com.aro.jcalendar.model.SharedViewModel;
import com.aro.jcalendar.model.Task;
import com.aro.jcalendar.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class AddNewNote extends AppCompatActivity implements OnItemClickedListener, AdapterView.OnItemSelectedListener {

    /*

    Activity for displaying the list of notes for the day.
    Also hosts the bottom sheet fragment which lets the user add a new note


     */

    private static final int GALLERY_CODE = 1;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private BottomSheetFragment bottomSheetFragment;
    private Button deleteCatButton;
    private TextView emptyCatTextView;
    private String categoryString;
    private Category category;
    private ProgressBar progressBar;
    private ImageView backgroundImage;
    private TextView counterTextView;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton floatingActionButtonCounters;

    private List<Task> currentTaskList;
    private List<Task> fullTaskList;
    private List<Counter> currentCounterList;
    private List<Counter> fullCounterList;
    private List<String> categories;

    private boolean loadSpinnerFromDelete = false;

    private CategoryViewModel categoryViewModel;
    private SharedViewModel sharedViewModel;
    private LocalDateTime ldtClicked;

    //the counter displayed for the ldtclicked
    private Counter displayedCounter = null;
    private Counter previousCounter = null;


    private SharedPreferences sharedPreferences;

    private Uri imageUri;
    private String uriAsString;
    private StorageReference storageReference;
    private int backgroundImageRotateValue = 0;
    private boolean isInitialLoad = true;
    private boolean isReadyToCheckIncrement = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("yelsa", "create add note");
//        isReadyToCheckIncrement = true;
//        Setup();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("yelsa", "resume add note");
        isReadyToCheckIncrement = true;
        Setup();
    }



    private void Setup(){

        Log.d("yelsa", "add note setup");

        setContentView(R.layout.activity_add_new_calendar);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButtonCounters = findViewById(R.id.fab_counter);
        spinner = findViewById(R.id.spinner_main_categories);
        deleteCatButton = findViewById(R.id.delete_empty_cat_buton);
        emptyCatTextView = findViewById(R.id.empty_cat_textview);
        TextView headerTextView = findViewById(R.id.add_new_header_date);
        CoordinatorLayout backgroundLayout = findViewById(R.id.coordinator_layout_add_new);
        progressBar = findViewById(R.id.progressBar_addnewnote);
        backgroundImage = findViewById(R.id.background_imageview_add_new);
        counterTextView = findViewById(R.id.counterTextView_dayview);


        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentTaskList = new ArrayList<>();
        fullTaskList = new ArrayList<>();
        categories = new ArrayList<>();
        currentCounterList = new ArrayList<>();
        fullCounterList = new ArrayList<>();

        storageReference = FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        //get the stored data about the date that the user clicked and use it to construct a LocalDateTime variable
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            int month = bundle.getInt("calendarMonth");
            int day = bundle.getInt("calendarDay");
            int year = bundle.getInt("calendarYear");
            if(month == 0){
                month = LocalDateTime.now().getMonthValue();
            }
            if(day == 0){
                day = LocalDateTime.now().getDayOfMonth();
            }
            if(year == 0){
                year = LocalDateTime.now().getYear();
            }
            ldtClicked = LocalDateTime.of(year, month, day, 0, 0);
        }
        //if there is no stored date, set the ldtClicked to now
        else {
            ldtClicked = LocalDateTime.of(LocalDate.now().getYear(), LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(), 0 , 0);
        }

        //set the header using the ldt (either the stored ldt or "now")
        if(ldtClicked != null){
            String ldtString = ldtClicked.getMonth() + " " + ldtClicked.getDayOfMonth() + " " + ldtClicked.getYear();
            headerTextView.setText(ldtString);
        }

        //set the saved calendar grid nav data to the date clicked
        // (this is used to store the month/year of the date the user is viewing so that when they return to the
        // calendar grid they are in the correct month/year)
        if (ldtClicked != null) {
            int month = ldtClicked.getMonthValue();
            int year = ldtClicked.getYear();
            //save the month the user navigated to in shared prefs
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("user_nav_month", month);
            editor.putInt("user_nav_year", year);
            editor.apply();
        }


        //this is used to save/load Task and bool isEdit across activities
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        //get the counter values for this month and make a list of them
        CounterViewModel counterViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CounterViewModel.class);
        counterViewModel.getAllCounters().observe(this, allCounters ->{

            //use the full list of counters, allCounters to make a list for the current month list
            fullCounterList = allCounters;
            Log.d("yelsa", "add note cvm. ready bool = " + isReadyToCheckIncrement);

            if(isReadyToCheckIncrement){
                isReadyToCheckIncrement = false;
                IncrementCounters();
            }

            Log.d("yelsa", "list size = " + fullCounterList.size());

        });

        //this is used to access the task ROOM database and fill the list of tasks using it
        TaskViewModel taskViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks ->{
            fullTaskList = tasks;

            //populate the currentTaskList with the tasks of the chosen category
            setCurrentTaskList(fullTaskList);
            sortByDate(currentTaskList);

            //show the user the tasks for the day in the current category
            recyclerViewAdapter = new RecyclerViewAdapter(currentTaskList, this, this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

        //this is used to access the categories stored in the database
        categoryViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(this, this::getCategories);

        //todo: check if this can be safely removed
        CalendarViewModel calendarViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CalendarViewModel.class);


        //SETUP UI
        //get rotation from sharedprefs and use it to set the rotation of the background
        backgroundImageRotateValue = sharedPreferences.getInt("background_rotation", 0);
        setBackgroundImage(backgroundImageRotateValue);

        floatingActionButton.setTooltipText("Add New Note");
        floatingActionButtonCounters.setTooltipText("Add New Counter");

        counterTextView.setOnClickListener(this::CounterTextClickMethod);
        spinner.setOnItemSelectedListener(this);
        floatingActionButton.setOnClickListener(this::floatingActionButtonMethod);
        floatingActionButtonCounters.setOnClickListener(this::floatingActionButtonMethodCounters);
        deleteCatButton.setOnClickListener(this::deleteCatButtonMethod);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //this controls the menu
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

                //onActivityResults runs here

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        //if we are returning from add image intent
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            //and we have image data
            if(data != null){

                Random theDragon = new Random();

                //make a filename for the image
                String imageName = "" + theDragon.nextInt(9999) + Timestamp.now().getSeconds();

                imageUri = data.getData(); // this is the path where the image is
                //this is the url
                uriAsString = imageUri.toString();

                //if we have a usable image path
                if(imageUri != null){

                    progressBar.setVisibility(View.VISIBLE);

                    //make a firebase filepath (using the imagename we made above)
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

                                            //save image url to shared prefs
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("background_image", uriAsString);
                                            editor.apply();

                                            //apply the background image to the UI
                                            setBackgroundImage(backgroundImageRotateValue);


                                        })
                                        //if we can't get the image url
                                        .addOnFailureListener(e -> {

                                            //could Toast the user here with the error
                                            //Log.d("imgstore", "Failed to get image url. Error: " + e  );

                                        });
                            })
                            //if the image doesn't save to firebase
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);

                                //could Toast the user here with the error
                                //Log.d("imgstore", "Failed to upload image to firebase. Error: " + e);

                            });
                }
            }
        }
    }

    private void setBackgroundImage(int rotateValue){
        uriAsString = sharedPreferences.getString("background_image", "");
        if(!uriAsString.equals("")){

            Picasso.get().load(uriAsString)
                    .rotate(rotateValue)
                    .into(backgroundImage);

        }
    }

    //remove category from the database
    private void deleteCategory(){

        Category catToDelete = category;

        LiveData<List<Category>> listOfAllCategories = categoryViewModel.getAllCategories();

        if(listOfAllCategories.getValue() != null){
            for (int i = 0; i < listOfAllCategories.getValue().size(); i++) {

                Category currentCategory = listOfAllCategories.getValue().get(i);

                //if the category that we want to delete (by name) is the same name as
                // the category we are looking at currently in the list
                // then remove it
                if(currentCategory.categoryName.equals(catToDelete.categoryName)){

                    //remove from the list populating the spinner
                    categories.remove(categoryString);

                    //remove from the database
                    CategoryViewModel.delete(currentCategory);

                }
            }
        }

    }

    //display the UI asking the user if they want to delete a category. And handle the methods that UI contains.
    private void deleteCatButtonMethod(View view) {

        BottomSheetDialog deleteCatDialog = new BottomSheetDialog(this);

        deleteCatDialog.setContentView(R.layout.delete_cat_dialog);

        Button deleteCatButton = deleteCatDialog.findViewById(R.id.delete_cat_button);
        Button backCatButton = deleteCatDialog.findViewById(R.id.delete_cat_dialog_back_button);

        deleteCatDialog.show();

        assert deleteCatButton != null;
        deleteCatButton.setOnClickListener(view1 -> {
            deleteCategory();
            deleteCatDialog.dismiss();
        });

        assert backCatButton != null;
        backCatButton.setOnClickListener(view12 -> deleteCatDialog.dismiss());

        deleteCatDialog.setOnCancelListener(dialogInterface -> deleteCatDialog.dismiss());

        deleteCatDialog.setOnDismissListener(dialogInterface -> deleteCatDialog.dismiss());
    }

    //make a list of the stored categories and set it to the spinner. (also set the default category on load)
    public void getCategories(List<Category> categoryList){

        //use the list of category objects stored in the database to make a list of category strings
        for (int i = 0; i < categoryList.size(); i++) {

            Category mCategory = categoryList.get(i);
            String mCategoryString = mCategory.getCategoryName();

            if(!categories.contains(mCategoryString)){
                categories.add(mCategoryString);
            }
        }

        //always include To Do List
        if(!categories.contains("To Do List")){
            categories.add("To Do List");
            Category toDoCat = new Category("To Do List");

            if(categoryViewModel.getAllCategories().getValue() != null){
                if(!(categoryViewModel.getAllCategories().getValue()).contains(toDoCat)){
                    CategoryViewModel.insert(toDoCat);
                }
            }
        }

        //always include Completed
        if(!categories.contains("Completed")){
            categories.add("Completed");
            Category completedCat = new Category("Completed");
            if(categoryViewModel.getAllCategories().getValue() != null){
                if(!(categoryViewModel.getAllCategories().getValue()).contains(completedCat)){
                    CategoryViewModel.insert(completedCat);
                }
            }
        }

        //use the array to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_spinner_layout, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //get the last saved category
        String lastSavedCategory = sharedPreferences.getString("latest_category", null);

        //clear the last saved category
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latest_category", null);
        editor.apply();

        //set the spinner to default
        //if a task was just deleted, return to the completed category
        if(loadSpinnerFromDelete){
            categoryString = "Completed";
        }
        else if(lastSavedCategory != null){
            categoryString = lastSavedCategory;
        }
        else{
            //populate the currentTaskList with the tasks of the chosen category
            setCurrentTaskList(fullTaskList);
            //set the spinner to the category of the first task in the currenttasklist.
            // (if anything prevents this from working set the spinner to "To Do List"
            if(currentTaskList != null){
                if(currentTaskList.size() > 0){
                    if(currentTaskList.get(0).getCategory() != null){
                        String firstCatString = currentTaskList.get(0).getCategory();
                        if(firstCatString.equals("Completed")){firstCatString = "To Do List";}
                        int firstCat = categories.indexOf(firstCatString);
                         spinner.setSelection(firstCat);
                         categoryString = firstCatString;

                    }
                    else{
                        spinner.setSelection(categories.indexOf("To Do List"));
                        categoryString = "To Do List";
                    }
                }
                else{
                    spinner.setSelection(categories.indexOf("To Do List"));
                    categoryString = "To Do List";
                }
            }
            else{
                spinner.setSelection(categories.indexOf("To Do List"));
                categoryString = "To Do List";
            }
        }


        if(categoryString != null){
            if(categories.contains(categoryString)){
                spinner.setSelection(categories.indexOf(categoryString));
            }

        }

        loadSpinnerFromDelete = false;
    }

    //make a list of tasks which have the current date and the current category.
    //this is what will be displayed to the UI in the recyclerview
    private void setCurrentTaskList(List<Task> tasks){

        currentTaskList.clear();


        //set up a list of tasks for the currently selected category
        for (int i = 0; i < tasks.size(); i++) {

            Task task = tasks.get(i);

            //this is used to handle old versions that do not have a category
            if(task.getCategory() == null){
                task.setCategory("To Do List");
                TaskViewModel.update(task);
            }

            //check if the due date of each task is the same as the date we are displaying (ignoring hour/min)
            if(task.getDueDate().getMonth() == ldtClicked.getMonth()){
                if(task.getDueDate().getYear() == ldtClicked.getYear()){
                    if(task.getDueDate().getDayOfMonth() == ldtClicked.getDayOfMonth()){
                        //if the category of the task is the one we are currently displaying
                        if(task.getCategory().equals(categoryString)){

                            //if we have a match add it to the current list
                            currentTaskList.add(task);
                        }
                    }
                }
            }
        }

        if(currentTaskList.size() > 0){

            deleteCatButton.setVisibility(View.GONE);
            emptyCatTextView.setVisibility(View.GONE);
        }
        //if there are no tasks for the current day and category
        else{

            if(categoryString != null){
                //if the empty category is not one of the default cats show empty cat UI
                if(!categoryString.equals("To Do List") && !categoryString.equals("Completed")){
                    deleteCatButton.setVisibility(View.VISIBLE);
                    emptyCatTextView.setVisibility(View.VISIBLE);
                }

                // if the empty cat is To Do List
                else if (categoryString.equals("To Do List")){

                    String firstCat = null;
                    //check if this is the first load
                    if(isInitialLoad){
                        //check if any of the other categories are not empty.
                        for (int i = 0; i < tasks.size(); i++) {
                            if(firstCat == null || firstCat.equals("Completed")){
                                Task task = tasks.get(i);
                                //check if the due date is the same as the date clicked (ignoring hour/min)
                                if(task.getDueDate().getMonth() == ldtClicked.getMonth()){
                                    if(task.getDueDate().getYear() == ldtClicked.getYear()){
                                        if(task.getDueDate().getDayOfMonth() == ldtClicked.getDayOfMonth()){

                                            firstCat = task.getCategory();

                                        }
                                    }
                                }
                            }
                        }

                        //if there is a category for the day that isn't empty
                        if(firstCat != null && !firstCat.equals("Completed")){
                            //set the spinner to first category that isn't empty
                            spinner.setSelection(categories.indexOf(firstCat));
                            categoryString = firstCat;
                        }
                        //if all categories are empty
                        else{
                            //keep the spinner on To Do List and show UI text that it is empty
                            spinner.setSelection(categories.indexOf("To Do List"));
                            categoryString = "To Do List";
                            deleteCatButton.setVisibility(View.GONE);
                            emptyCatTextView.setVisibility(View.VISIBLE);
                        }

                        isInitialLoad = false;
                    }
                    else{
                        //if this isn't first load keep the spinner on To Do List and show UI text that it is empty
                        spinner.setSelection(categories.indexOf("To Do List"));
                        categoryString = "To Do List";
                        deleteCatButton.setVisibility(View.GONE);
                        emptyCatTextView.setVisibility(View.VISIBLE);
                    }
                }
                //empty category is the completed list... set text to something?
                else{
                    spinner.setSelection(categories.indexOf("Completed"));
                    categoryString = "Completed";
                    deleteCatButton.setVisibility(View.GONE);
                    emptyCatTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void sortByDate(List<Task> tasks){
        tasks.sort(Comparator.comparing(task -> task.dueDate));
    }

    //open the bottom sheet to make a NEW note/task using the date we are viewing as the due date.
    private void floatingActionButtonMethod(View view) {
        Task ldtTask = new Task();
        ldtTask.setDueDate(ldtClicked);
        sharedViewModel.selectItem(ldtTask);
        sharedViewModel.setEdit(false);
        showBottomSheetDialog();
    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void floatingActionButtonMethodCounters(View view) {

        //call this with null because we are not editing an existing counter
        ShowCounterDialog(null);

    }

    private void ShowCounterDialog(Counter counter){

        BottomSheetDialog counterDialog = new BottomSheetDialog(this);

        counterDialog.setContentView(R.layout.counter_dialog);

        boolean isEdit = false;

        if(counter != null){
            isEdit = true;
        }

        //views
        TextView header = counterDialog.findViewById(R.id.counter_header);
        EditText titleEditText = counterDialog.findViewById(R.id.counter_title_edittext);
        EditText detailsEditText = counterDialog.findViewById(R.id.counter_details_edittext);
        Button startButton = counterDialog.findViewById(R.id.startcounter_button);
        Button resetButton = counterDialog.findViewById(R.id.resetcounter_button);
        Button stopButton = counterDialog.findViewById(R.id.stopcounter_button);
        Button deleteButton = counterDialog.findViewById(R.id.delete_counterbutton);
        Button deleteAllButton = counterDialog.findViewById(R.id.deleteall_counterbutton);

        if(isEdit){
            header.setText("Update a Counter");
            titleEditText.setText(counter.getCounterTitle());
            detailsEditText.setText(counter.getCounterAdditionalInfo());
            startButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            deleteAllButton.setVisibility(View.VISIBLE);
            startButton.setText("Edit");

            deleteButton.setTooltipText("Delete Counter for Today");
            deleteAllButton.setTooltipText("Delete Whole Counter");

            LocalDateTime now = LocalDateTime.now();

            if(counter.getDate().getMonthValue() == now.getMonthValue()
                    && counter.getDate().getDayOfMonth() == now.getDayOfMonth()
                    && counter.getDate().getYear() == now.getYear()){
                resetButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.VISIBLE);
            }
            else{
                resetButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
            }
        }
        else{
            header.setText("Add a Counter");
            startButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            deleteAllButton.setVisibility(View.GONE);
        }

        counterDialog.show();

        boolean finalIsEdit = isEdit;
        startButton.setOnClickListener(view -> {
            String titleString = titleEditText.getText().toString().trim();
            String detailsString = detailsEditText.getText().toString().trim();
            if(finalIsEdit){
                Log.d("yelsa", "edit button");
                editCounter(titleString, detailsString, counter);
            }
            else{
                Log.d("yelsa", "start button");
                startNewCounter(titleString, detailsString);
            }
            counterDialog.dismiss();
            isReadyToCheckIncrement = true;
        });

        resetButton.setOnClickListener(view2 -> {
            Log.d("yelsa", "reset button");
            String titleString = titleEditText.getText().toString().trim();
            String detailsString = detailsEditText.getText().toString().trim();
            resetCounter(titleString, detailsString, counter);
            counterDialog.dismiss();
            isReadyToCheckIncrement = true;
        });

        stopButton.setOnClickListener(view3 -> {
            Log.d("yelsa", "stop button");
            String titleString = titleEditText.getText().toString().trim();
            String detailsString = detailsEditText.getText().toString().trim();
            stopCounter(titleString, detailsString, counter);
            counterDialog.dismiss();
            isReadyToCheckIncrement = true;
        });

        deleteButton.setOnClickListener(view4 ->{
            Log.d("yelsa", "delete button");
            //TODO: add a confirmation prompt.
            CounterViewModel.delete(counter);
            counterDialog.dismiss();
            isReadyToCheckIncrement = true;
            counterTextView.setText("");
        });

        deleteAllButton.setOnClickListener(view5 ->{
            Log.d("yelsa", "delete all button");
            //TODO: add a confirmation prompt.

            Log.d("yelsa", "full counter list size " + fullCounterList.size());
            //delete all counters from DB if they have the same sequence title as the one selected
            for (int i = 0; i < fullCounterList.size(); i++) {
                isReadyToCheckIncrement = false;
                Counter c = fullCounterList.get(i);
                Log.d("yelsa", "c seq title = " + c.getSequenceTitle()
                        + ". counter clicked seq title = " + counter.getSequenceTitle());
                if(c.getSequenceTitle().equals(counter.getSequenceTitle())){
                    Log.d("yelsa", "deleting value " + c.getValue());
                    CounterViewModel.delete(c);
                }
            }

            counterDialog.dismiss();
            isReadyToCheckIncrement = true;
            counterTextView.setText("");

        });


        counterDialog.setOnCancelListener(dialogInterface -> counterDialog.dismiss());
        counterDialog.setOnDismissListener(dialogInterface -> counterDialog.dismiss());
    }

    private void startNewCounter(String titleString, String detailsString){

        String uniqueTag = String.valueOf(UUID.randomUUID());

        //make a counter with the strings given and today as the creation date.
        Counter counter = new Counter(ldtClicked, titleString, detailsString, true, ldtClicked, 1, uniqueTag);
        //Save it to the DB
        CounterViewModel.insert(counter);

    }

    private void editCounter(String titleString, String detailsString, Counter counter){

        //update the counter with the new information
        counter.setCounterTitle(titleString);
        counter.setCounterAdditionalInfo(detailsString);
        CounterViewModel.update(counter);

    }

    private void resetCounter(String titleString, String detailsString, Counter counter){

        counter.setActive(true);
        counter.setDateStarted(ldtClicked);
        counter.setCounterTitle(titleString);
        counter.setCounterAdditionalInfo(detailsString);
        counter.setValue(1);


        //save both counters
        CounterViewModel.update(counter);


    }

    private void stopCounter(String titleString, String detailsString, Counter counter){

        //stop the counter and save changes
        counter.setActive(false);
        counter.setCounterTitle(titleString);
        counter.setCounterAdditionalInfo(detailsString);
        CounterViewModel.update(counter);
    }



    //when this screen loads, increment all the counters in the list that are active
    private void IncrementCounters(){
        Log.d("yelsa", "AddNewNote: increment counters. list size = "
                + fullCounterList.size());

        //loop through all counters
        for (int i = 0; i < fullCounterList.size(); i++) {

            //if the counter is active
            if(fullCounterList.get(i).getActive()){

                //date of the active counter
                Date counterDate = Date.from(fullCounterList.get(i).getDate().atZone(ZoneId.systemDefault()).toInstant());
                Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

                int daysDifference = getDaysDifference(counterDate, now);

                Log.d("yelsa", "days difference: " + daysDifference);

                //if there is at least one day between the active counter and today
                if(daysDifference >= 1){

                    LocalDateTime dateStarted = fullCounterList.get(i).getDateStarted();
                    String title = fullCounterList.get(i).getCounterTitle();
                    String details = fullCounterList.get(i).getCounterAdditionalInfo();
                    int value = fullCounterList.get(i).getValue();
                    previousCounter = fullCounterList.get(i);
                    boolean isConflict = false;


                    //make a counter for each day between the dates
                    for (int j = 0; j < daysDifference; j++) {

                        //if this loop has not been turned off by running into another counter
                        if(!isConflict){
                            Counter thisCounter = new Counter(dateStarted, title, details, true,
                                    fullCounterList.get(i).getDate().plusDays(j + 1),  value + j + 1,
                                    fullCounterList.get(i).getSequenceTitle());

                            //check if there is another counter on that date
                            boolean dateIsEmpty = true;
                            //loop through all counters
                            for (int k = 0; k < fullCounterList.size(); k++) {

                                Counter conflictCheckCounter = fullCounterList.get(k);

                                if(conflictCheckCounter.getDate().getMonthValue() == thisCounter.getDate().getMonthValue()
                                        && conflictCheckCounter.getDate().getDayOfMonth() == thisCounter.getDate().getDayOfMonth()
                                        && conflictCheckCounter.getDate().getYear() == thisCounter.getDate().getYear()){
                                    dateIsEmpty = false;
                                }

                            }

                            if(dateIsEmpty){
                                CounterViewModel.insert(thisCounter);

                                previousCounter.setActive(false);
                                CounterViewModel.update(previousCounter);
                                previousCounter = thisCounter;
                            }
                            else{
                                isConflict = true;
                            }
                        }

                    }

                }

            }
        }


        setCurrentCounter(fullCounterList);

    }


    private void setCurrentCounter(List<Counter> counters){

        Log.d("yelsa", "set current counter");

        currentCounterList.clear();

        //set up a list of tasks for the currently selected category
        for (int i = 0; i < counters.size(); i++) {

            Counter counter = counters.get(i);

            //check if the due date of each task is the same as the date we are displaying (ignoring hour/min)
            if(counter.getDate().getMonth() == ldtClicked.getMonth()){
                if(counter.getDate().getYear() == ldtClicked.getYear()){
                    if(counter.getDate().getDayOfMonth() == ldtClicked.getDayOfMonth()){
                        //if we have a match add it to the current list
                        currentCounterList.add(counter);
                    }
                }
            }
        }

        Log.d("yelsa", "currentcounterlist = " + currentCounterList.size());

        if(currentCounterList.size() > 0){

            Log.d("yelsa", "setCurrentCounter: we have a counter. make visible");
            Counter firstDailyCounter = currentCounterList.get(0);

            String title = firstDailyCounter.getCounterTitle().toUpperCase(Locale.ROOT);
            String valueString = String.valueOf(firstDailyCounter.getValue());

            displayedCounter = firstDailyCounter;

            //TODO: format this so it looks better
            counterTextView.setText(title + System.lineSeparator() + valueString);
            counterTextView.setVisibility(View.VISIBLE);

            floatingActionButtonCounters.setVisibility(View.INVISIBLE);

        }
        //if there are no counters for the current day
        else{
            Log.d("yelsa", "setCurrentCounter: no counters. ready to add a new one");

            counterTextView.setVisibility(View.INVISIBLE);
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }


    private void CounterTextClickMethod(View view){

        ShowCounterDialog(displayedCounter);

    }

    public static int getDaysDifference(Date fromDate, Date toDate) {
        if(fromDate==null||toDate==null)
            return 0;

        return (int)( (toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }


    //this is because we are using the same onclick listener for the calendar grid. we do not need to use this here.
    @Override
    public void OnCalendarClickedListener(int position, Calendar calendar) {

    }

    //if the user clicks a task, open the bottom sheet to edit that task
    @Override
    public void onTaskClicked(int position, Task task) {
        sharedViewModel.selectItem(task);
        sharedViewModel.setEdit(true);

        showBottomSheetDialog();
    }

    //if the user clicks a radio button move that task to the completed category
    //if the task is ALREADY in the completed category when the radio button is clicked open the delete task UI
    @Override
    public void onTaskRadioButtonClicked(Task task) {
        //move the task to completed if it isn't there already
        if(!task.getCategory().equals("Completed")){
            task.setCategory("Completed");
            TaskViewModel.update(task);
        }

        //if the task is already marked completed
        else{

            //show a dialog asking if the user wants to delete the task
            BottomSheetDialog deleteDialog = new BottomSheetDialog(this);
            deleteDialog.setContentView(R.layout.delete_confirmation_dialog);

            Button deleteButton = deleteDialog.findViewById(R.id.delete_button);
            Button backButton = deleteDialog.findViewById(R.id.delete_dialog_back_button);

            deleteDialog.show();

            assert deleteButton != null;
            deleteButton.setOnClickListener(view -> {
                TaskViewModel.delete(task);
                loadSpinnerFromDelete = true;

                deleteDialog.dismiss();

            });

            assert backButton != null;
            backButton.setOnClickListener(view -> deleteDialog.dismiss());
            deleteDialog.setOnDismissListener(dialogInterface -> deleteDialog.dismiss());
            deleteDialog.setOnCancelListener(dialogInterface -> deleteDialog.dismiss());

        }
    }

//    @Override
//    public void OnCounterClickedListener(int position, Counter currentCounter) {
//        //shouldn't need to use this here, but do need to implement for interface
//    }

    //category spinner item selected
    // (if user selected a category in the dropdown)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        categoryString = parent.getItemAtPosition(pos).toString();

        category = new Category(categoryString);

        setCurrentTaskList(fullTaskList);

        sortByDate(currentTaskList);
        recyclerViewAdapter = new RecyclerViewAdapter(currentTaskList, this, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    //category spinner nothing selected
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}