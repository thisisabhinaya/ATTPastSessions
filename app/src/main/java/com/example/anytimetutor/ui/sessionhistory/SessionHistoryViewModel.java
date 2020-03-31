package com.example.anytimetutor.ui.sessionhistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SessionHistoryViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public SessionHistoryViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
