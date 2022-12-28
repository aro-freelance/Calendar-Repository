package com.aro.jcalendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.aro.jcalendar.model.Category;
import com.aro.jcalendar.model.CategoryViewModel;
import com.aro.jcalendar.model.Priority;
import com.aro.jcalendar.model.SharedViewModel;
import com.aro.jcalendar.model.Task;
import com.aro.jcalendar.model.TaskViewModel;
import com.aro.jcalendar.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BottomSheetFragment extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener{

    //this fragment lets the user make a new task or edit an existing task


    private static final int GALLERY_CODE = 1;
    private EditText taskEditText;
    private ImageButton calendarButton;
    private ImageButton saveButton;
    private Group calendarGroup;
    private Button rotateButton;
    private TimePicker timePicker;
    private ImageButton categoryButton;
    private Group categoryGroup;
    private Spinner categorySpinner;
    private EditText categoryEditText;
    private Button categoryEnterButton;
    private ImageButton colorButton;
    private ImageButton photoButton;
    private Group colorGroup;
    private Group photoGroup;
    private Button intentPhotoButton;
    private Priority priority;

    private LocalDateTime dueDate;
    private SharedViewModel sharedViewModel;
    private boolean isEdit = false;
    private String category = null;
    private ArrayList<String> categories;
    private LocalDateTime now = LocalDateTime.now();
    private CategoryViewModel categoryViewModel;
    private LocalDateTime clickedLDT;

    private Uri imageUri;
    private String imageUriString;
    private String colorString;
    private ImageView photo;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private SeekBar redBar;
    private SeekBar blueBar;
    private SeekBar greenBar;
    private TextView redNumberBox;
    private TextView blueNumberBox;
    private TextView greenNumberBox;
    private int redValue;
    private int blueValue;
    private int greenValue;
    private int colorARGB;
    private ImageView colorPreviewBox;
    private TextView previewText;
    private CheckBox textColorCheckBox;
    private int textColorARGB;
    private int rotationValue = 0;
    private String rotationSavedNameString = null;
    private SharedPreferences sharedPreferences;
    private Task task;
    private Task taskToUpdate;
    private Task tempTask;
    public BottomSheetFragment() { }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        taskEditText = view.findViewById(R.id.enter_todo_edit_text);
        calendarButton = view.findViewById(R.id.calendar_button);
        saveButton = view.findViewById(R.id.save_todo_button);
        calendarGroup = view.findViewById(R.id.calendar_group);
        categoryButton = view.findViewById(R.id.category_button);
        categoryGroup = view.findViewById(R.id.category_group);
        categorySpinner = view.findViewById(R.id.category_spinner_bottom_sheet);
        categoryEditText = view.findViewById(R.id.category_edit_text_bottom_sheet);
        categoryEnterButton = view.findViewById(R.id.new_category_button_bottom_sheet);
        timePicker = view.findViewById(R.id.time_picker);
        colorButton = view.findViewById(R.id.color_select_button);
        photoButton = view.findViewById(R.id.photo_add_button);
        colorGroup = view.findViewById(R.id.color_group);
        photoGroup = view.findViewById(R.id.photo_group);
        intentPhotoButton = view.findViewById(R.id.intent_photo_button);
        photo = view.findViewById(R.id.image_view_bottomsheet);
        progressBar = view.findViewById(R.id.progressBar_bottomsheet);
        redBar = view.findViewById(R.id.red_bar);
        blueBar = view.findViewById(R.id.blue_bar);
        greenBar = view.findViewById(R.id.green_bar);
        redNumberBox = view.findViewById(R.id.red_text_box);
        blueNumberBox = view.findViewById(R.id.blue_text_box);
        greenNumberBox = view.findViewById(R.id.green_text_box);
        colorPreviewBox = view.findViewById(R.id.preview_color_imageview);
        previewText = view.findViewById(R.id.preview_text_colors);
        textColorCheckBox = view.findViewById(R.id.checkbox_invert_text_color);
        rotateButton = view.findViewById(R.id.rotate_photo_button_bottomsheet);

        sharedPreferences = getActivity().getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

       categoryViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(CategoryViewModel.class);

       categories = new ArrayList<>();

       //populate the list of stored categories
       categoryViewModel.getAllCategories().observe(this, this::getCategories);

       storageReference = FirebaseStorage.getInstance().getReference();

       //if the user enters text for a new category make the category enter button visible
       categoryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!TextUtils.isEmpty(categoryEditText.getText().toString().trim())){
                    categoryEnterButton.setVisibility(View.VISIBLE);
                }
            }
        });

        redBar.setMax(255);
        greenBar.setMax(255);
        blueBar.setMax(255);

        redValue = 255;
        greenValue = 255;
        blueValue = 255;

        redNumberBox.setText(String.valueOf(redValue));
        greenNumberBox.setText(String.valueOf(greenValue));
        blueNumberBox.setText(String.valueOf(blueValue));

        redBar.setProgress(redValue);
        greenBar.setProgress(greenValue);
        blueBar.setProgress(blueValue);

        colorARGB = Color.argb(255 , redValue , greenValue, blueValue);
        colorPreviewBox.setBackgroundColor(colorARGB);

        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                redValue = i;
                redNumberBox.setText(String.valueOf(redValue));

                colorARGB = Color.argb(255 , redValue , greenValue, blueValue);
                colorPreviewBox.setBackgroundColor(colorARGB);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blueValue = i;
                blueNumberBox.setText(String.valueOf(blueValue));

                colorARGB = Color.argb(255 , redValue , greenValue, blueValue);
                colorPreviewBox.setBackgroundColor(colorARGB);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                greenValue = i;
                greenNumberBox.setText(String.valueOf(greenValue));

                colorARGB = Color.argb(255 , redValue , greenValue, blueValue);
                colorPreviewBox.setBackgroundColor(colorARGB);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        categorySpinner.setOnItemSelectedListener(this);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        saveButton.setOnClickListener(this::saveButtonMethod);
        calendarButton.setOnClickListener(this::calendarButtonMethod);
        categoryButton.setOnClickListener(this::categoryButtonMethod);
        categoryEnterButton.setOnClickListener(this::categoryEnterButtonMethod);
        photoButton.setOnClickListener(this::photoButtonMethod);
        colorButton.setOnClickListener(this::colorButtonMethod);
        intentPhotoButton.setOnClickListener(this::addPhotoButtonMethod);
        textColorCheckBox.setOnClickListener(this::onCheckBoxClicked);
        rotateButton.setOnClickListener(this::rotateButtonMethod);
        photo.setOnClickListener(this::addPhotoButtonMethod);

        categoryEnterButton.setVisibility(View.GONE);

        categoryEditText.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                switch(keyCode){
                    //if the enter key is pressed when the title text is being edited close the soft keyboard
                    case KeyEvent.KEYCODE_ENTER:
                        Utils.hideKeyboard(categoryEditText);

                        //act as if the user has pushed the enter button on the UI and get their input
                        getUserInputCategory();
                }
            }

            return false;
        });


        setUIOnLoad();
    }

    private void clearData(){
        isEdit = false;
        imageUri = null;
        imageUriString = null;
        rotationSavedNameString = null;
        rotationValue = 0;
        greenBar.setProgress(255);
        blueBar.setProgress(255);
        redBar.setProgress(255);
        textColorCheckBox.setChecked(false);
        task = null;
        taskToUpdate = null;
        tempTask = null;
    }

    //set the UI if this is an edit or if we are returning after photo intent was opened
    private void setUIOnLoad(){

        Task taskToLoad = sharedViewModel.getSelectedItem().getValue();

        if(isEdit || tempTask != null && taskToLoad != null){

            //title text
            String taskTitle = taskToLoad.getTask();
            taskEditText.setText(taskTitle);

            //photo
            if(imageUriString == null){
                imageUriString = taskToLoad.getImageUrl();
            }

            if(rotationSavedNameString == null){
                rotationSavedNameString = taskTitle + "_rotation_saved_name_" + taskToLoad.getImageUrl();
                rotationValue = sharedPreferences.getInt(rotationSavedNameString, 0);
            }


            //time
            int hour =  taskToLoad.getDueDate().getHour();
            int minute = taskToLoad.getDueDate().getMinute();
            timePicker.setHour(hour);
            timePicker.setMinute(minute);


            //color
            int textColorInt = taskToLoad.getTextColorInt();
            int colorInt = taskToLoad.getColorInt();

            int b = (colorInt >> 0 ) & 255;
            int g = (colorInt >> 8) & 255;
            int r = (colorInt >> 16) & 255;


            greenBar.setProgress(g);
            blueBar.setProgress(b);
            redBar.setProgress(r);

            //if the text color stored is white, check the invert text color box
            if( textColorInt == -1){
                textColorCheckBox.setChecked(true);
                //white
                textColorARGB = Color.argb(255, 255, 255, 255);
                previewText.setTextColor(textColorARGB);
            }

            //category
            if(category == null){

                category =  taskToLoad.getCategory();

                //if the category already exists (it should) set the spinner to that category
                if(categories.contains(category)){
                    categorySpinner.setSelection(categories.indexOf(category));
                }
                //or add that category and set spinner
                else{
                    //add the user input to the array
                    categories.add(category);

                    //use the array to populate the spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);

                    //set the spinner to have the newest item selected
                    categorySpinner.setSelection(categories.size() - 1);
                }
            }
        }
    }

    private void rotateButtonMethod(View view) {
        rotatePhoto();
    }

    private void rotatePhoto() {

        rotationValue = rotationValue + 90;

        if(imageUriString != null){
            Picasso.get().load(imageUriString)
                    .placeholder(R.drawable.ic_baseline_image_grey_24)
                    .rotate(rotationValue)
                    .into(photo);

        }
    }

    private void onCheckBoxClicked(View view) {

        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()){

            case R.id.checkbox_invert_text_color:
                if(checked){
                    //white
                    textColorARGB = Color.argb(255, 255, 255, 255);
                }
                else{
                    //black
                    textColorARGB = Color.argb(255, 0, 0, 0);
                }

                previewText.setTextColor(textColorARGB);

                break;
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        //if we have something in sharedViewModel (where we pass data between activities)
        // and we are not returning from a temp save
        if(sharedViewModel.getSelectedItem().getValue() != null && tempTask == null){
            isEdit = sharedViewModel.getIsEdit();
            if(isEdit){
                //get the task the user clicked
                taskToUpdate = sharedViewModel.getSelectedItem().getValue();
                //set the UI using that task
                setUIOnLoad();
                //set the ldt to the one stored in the task
                clickedLDT = taskToUpdate.getDueDate();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //clear the data entered for this task when we leave so that it is not there when we create/edit another task
        reset();
    }

    //set the list of categories for the day to the spinner
    public void getCategories(List<Category> categoryList){

        for (int i = 0; i < categoryList.size(); i++) {
            Category mCategory = categoryList.get(i);
            String categoryString = mCategory.getCategoryName();

            if(!categories.contains(categoryString)){
                categories.add(categoryString);
            }

        }

        if(!categories.contains("To Do List")){
            categories.add("To Do List");
        }

        //we don't want the user to add a task directly to completed
        categories.remove("Completed");

        //use the array to populate the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }





    private void addPhotoButtonMethod(View view) {

        //save any data that the user has already entered so that when we return from photo intent
        // we can repopulated the UI fields with that data
        tempSave();


        //select photo implicit intent
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //after user returns from photo intent
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

            //if we have data for a selected photo
            if(data != null){

                imageUri = data.getData(); // this is the path where the image is
                // this is the url
                imageUriString = imageUri.toString();

                //show the photo and rotate button now that we have a photo to work with
                photo.setVisibility(View.VISIBLE);
                rotateButton.setVisibility(View.VISIBLE);

                //set the photo to the preview image view
                Picasso.get().load(imageUriString)
                        .placeholder(R.drawable.ic_baseline_image_grey_24)
                        .rotate(rotationValue)
                        .into(photo);

                progressBar.setVisibility(View.INVISIBLE);

                //set the other UI fields again using the temp saved task info
                setUIOnLoad();

                //set the sharedViewModel task back to taskToUpdate
                // (temp save makes it the temp info)
                sharedViewModel.selectItem(taskToUpdate);
            }
        }
    }


    //get the user input from the category text view and add it to the spinner + the category data for the activity
    private void getUserInputCategory(){
        String categoryText = categoryEditText.getText().toString().trim();
        if(!TextUtils.isEmpty(categoryText)){

            //add the user input to the array
            categories.add(categoryText);

            //use the array to populate the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);

            //set the spinner to have the newest item selected
            categorySpinner.setSelection(categories.size() - 1);

            //and just to be safe set the category to the user input now
            category = categoryText;

            //reset the edit text
            categoryEditText.setText("");
        }
        else{
            Toast.makeText(getContext(), "Category field empty. Enter a new category.", Toast.LENGTH_SHORT).show();
        }
        categoryEnterButton.setVisibility(View.GONE);
    }

    //open color tab
    private void colorButtonMethod(View view) {

        calendarGroup.setVisibility(View.GONE);
        categoryGroup.setVisibility(View.GONE);
        photoGroup.setVisibility(View.GONE);

        colorGroup.setVisibility(
                colorGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );

    }

    //open photo tab
    private void photoButtonMethod(View view) {

        calendarGroup.setVisibility(View.GONE);
        categoryGroup.setVisibility(View.GONE);
        colorGroup.setVisibility(View.GONE);

        photoGroup.setVisibility(
                photoGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );

        //don't need these until the url is available
        rotateButton.setVisibility(View.GONE);
        photo.setVisibility(View.GONE);


        //if there is a photo already, show it in the preview
        if(imageUriString != null && !imageUriString.equals("")){
            photo.setVisibility(View.VISIBLE);
            rotateButton.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUriString)
                    .placeholder(R.drawable.ic_baseline_image_grey_24)
                    .rotate(rotationValue)
                    .into(photo);
        }
    }

    private void categoryEnterButtonMethod(View view) {

        getUserInputCategory();
        categoryEnterButton.setVisibility(View.GONE);
    }

    //open category tab
    private void categoryButtonMethod(View view) {

        Utils.hideKeyboard(view);
        calendarGroup.setVisibility(View.GONE);
        photoGroup.setVisibility(View.GONE);
        colorGroup.setVisibility(View.GONE);

        categoryGroup.setVisibility(
                categoryGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );

    }



    //open time select tab
    private void calendarButtonMethod(View view) {

        Utils.hideKeyboard(view);
        categoryGroup.setVisibility(View.GONE);
        photoGroup.setVisibility(View.GONE);
        colorGroup.setVisibility(View.GONE);

        calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
        );
    }

    //save the task to the database and close the task editor (bottom sheet)
    private void saveButtonMethod(View view) {
        saveMethod();
    }

    private void saveMethod(){

        progressBar.setVisibility(View.VISIBLE);

        String taskText = taskEditText.getText().toString().trim();

        //////////////////////////////////////////////
        /////// Set default values //////
        //////////////////////////////////////////////

        //if for some reason we don't have a date, make the date now
        if(clickedLDT != null){now = clickedLDT;}
        if (dueDate == null) {
            dueDate = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getDueDate();
        }
        if (priority == null) {
            if (Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getPriority() != null) {
                priority = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getPriority();
            } else {
                priority = Priority.LOW;
            }

        }
        if(category == null){
            if (Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getCategory() != null) {
                category = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getCategory();
            } else {
                category = "To Do List";
            }
        }
        if (imageUriString == null){
            if(Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getImageUrl() != null){
                imageUriString = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getImageUrl();
            } else {
                imageUriString = "";
            }
        }
        if (colorString == null){
            if(Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getColor() != null){
                colorString = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getColor();
            } else {
                colorString = "";
            }
        }
        ///////////////////////////////////////////////////////
        //////////////////////////////////////////////////////


        //add the time from the UI to the dueDate from the day the user clicked on to enter this activity
        dueDate = LocalDateTime.of(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute() );

        final String finalTaskText = taskText;

        colorARGB = Color.argb(255, redValue, greenValue, blueValue);

        //save category to sharedprefs
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latest_category", category);
        editor.apply();

        //if there is a new image
        if(imageUri != null){

            Random theDragon = new Random();

            //make a filepath where we will save the image
            String imageName = taskText + theDragon.nextInt(9999) + Timestamp.now().getSeconds();
            StorageReference filepath = storageReference
                    .child("journal_images")
                    .child(imageName);

            //save the image to the filepath
            filepath.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressBar.setVisibility(View.INVISIBLE);

                        filepath.getDownloadUrl()
                                .addOnSuccessListener(uri -> {

                                    String imageUrl = uri.toString();

                                    rotationSavedNameString = finalTaskText + "_rotation_saved_name_" + imageUrl;

                                    //save the rotation value to shared prefs
                                    editor.putInt(rotationSavedNameString, rotationValue);
                                    editor.apply();

                                    //make task object
                                    task = new Task(finalTaskText, priority, dueDate,
                                            now, false, category, imageUrl, colorString, colorARGB, textColorARGB);

                                    Category newCategory = new Category(category);

                                    //if the category doesn't exist in the category database yet, add it
                                    if(!Objects.requireNonNull(categoryViewModel.getAllCategories().getValue()).contains(newCategory)){
                                        CategoryViewModel.insert(newCategory);
                                    }

                                        //update a previous save
                                        if(isEdit){

                                            taskToUpdate.setTask(finalTaskText);
                                            taskToUpdate.setDateCreated(now);
                                            taskToUpdate.setPriority(priority);
                                            taskToUpdate.setDueDate(dueDate);
                                            taskToUpdate.setDone(false);
                                            taskToUpdate.setCategory(category);
                                            taskToUpdate.setImageUrl(imageUrl);
                                            taskToUpdate.setColor(colorString);
                                            taskToUpdate.setColorInt(colorARGB);
                                            taskToUpdate.setTextColorInt(textColorARGB);
                                            TaskViewModel.update(taskToUpdate);
                                            isEdit = false;

                                        }
                                        //new save
                                        else{
                                            TaskViewModel.insert(task);
                                        }

                                        reset();

                                        if(this.isVisible()){
                                            dismiss();
                                        }
                                })
                                //if we can't get the image url
                                .addOnFailureListener(e -> {

                                    //make a toast?
                                    //Log.d("imgstore", "Failed to get image url. Error: " + e  );

                                });
                    })
                    //if the image doesn't save to firebase
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);

                        //make a toast
                        //Log.d("imgstore", "Failed to upload image to firebase. Error: " + e);

                    });
        }

        //there is no image
        else{

            task = new Task(taskText, priority, dueDate,
                    now, false, category, null, colorString , colorARGB, textColorARGB);

            Category newCategory = new Category(category);

            //if the category doesn't exist in the category database yet, add it
            if(!Objects.requireNonNull(categoryViewModel.getAllCategories().getValue()).contains(newCategory)){
                CategoryViewModel.insert(newCategory);
            }

            //update a task
                if(isEdit){
                    taskToUpdate.setTask(taskText);
                    taskToUpdate.setDateCreated(now);
                    taskToUpdate.setPriority(priority);
                    taskToUpdate.setDueDate(dueDate);
                    taskToUpdate.setDone(false);
                    taskToUpdate.setCategory(category);
                    taskToUpdate.setColor(colorString);
                    taskToUpdate.setColorInt(colorARGB);
                    taskToUpdate.setTextColorInt(textColorARGB);
                    TaskViewModel.update(taskToUpdate);
                    isEdit = false;

                }
                //add a new task to the database
                else{
                    TaskViewModel.insert(task);
                }

                reset();

                if(this.isVisible()){
                    dismiss();
                }
            }
    }

    private void tempSave(){

        String taskText = taskEditText.getText().toString().trim();

        //////////////////////////////////////////////
        /////// Set default values //////
        //////////////////////////////////////////////

        //if for some reason we don't have a date, make the date now
        if(clickedLDT != null){now = clickedLDT;}
        if (dueDate == null) {
            dueDate = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getDueDate();
        }
        if (priority == null) {
            if (Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getPriority() != null) {
                priority = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getPriority();
            } else {
                priority = Priority.LOW;
            }

        }
        if(category == null){
            if (Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getCategory() != null) {
                category = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getCategory();
            } else {
                category = "To Do List";
            }
        }
        if (imageUriString == null){
            if(Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getImageUrl() != null){
                imageUriString = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getImageUrl();
            } else {
                imageUriString = "";
            }
        }
        if (colorString == null){
            if(Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getColor() != null){
                colorString = Objects.requireNonNull(sharedViewModel.getSelectedItem().getValue()).getColor();
            } else {
                colorString = "";
            }
        }
        ///////////////////////////////////////////////////////
        /////////////////////////////////////////////////////


        //add the time from the UI to the dueDate from the day the user clicked on to enter this activity
        dueDate = LocalDateTime.of(dueDate.getYear(), dueDate.getMonth(), dueDate.getDayOfMonth(),
                timePicker.getHour(), timePicker.getMinute() );

        final String finalTaskText = taskText;

        colorARGB = Color.argb(255, redValue, greenValue, blueValue);

        String imageUrl = null;
        if(taskToUpdate != null){
            imageUrl = taskToUpdate.getImageUrl();
        }

        //make task object
        tempTask = new Task(finalTaskText, priority, dueDate,
                now, false, category, imageUrl, colorString, colorARGB, textColorARGB);

        sharedViewModel.selectItem(tempTask);
    }

    private void reset(){
        taskEditText.setText("");

        clearData();

    }


    //user selects something in the category spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        category = parent.getItemAtPosition(pos).toString();

    }

    //nothing selected in category spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}