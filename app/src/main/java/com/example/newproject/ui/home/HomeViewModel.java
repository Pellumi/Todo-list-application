package com.example.newproject.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newproject.FileOps;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> uName = new MutableLiveData<>();

    public LiveData<String> getUserName(Context context) {
        new Thread(() -> {
            FileOps fileOps = new FileOps(context);
            String userName = fileOps.readIntStorage("userName.txt");
            // Update the LiveData on the main thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> uName.setValue(userName));
        }).start();

        return uName;
    }
}
