package com.aro.jcalendar.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    /*
    This will hold data so that we can move it between activities and fragments.
     */

    private final MutableLiveData<Task> selectedItem = new MutableLiveData<>();
    private boolean isEdit;

    //get edit status
    public boolean getIsEdit() {
        return isEdit;
    }

    //set edit status
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    //set saved Task
    public void selectItem(Task task){
        selectedItem.setValue(task);
    }

    //get saved Task
    public LiveData<Task> getSelectedItem(){
        return selectedItem;
    }



}
