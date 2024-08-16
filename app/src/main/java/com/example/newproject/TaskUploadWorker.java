package com.example.newproject;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TaskUploadWorker extends Worker{

    public TaskUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseProcess.uploadTask(getApplicationContext(), new FirebaseProcess.OnUploadTaskListener() {
            @Override
            public void OnUploadSuccess(String status) {

            }

            @Override
            public void OnUploadFailure(String message) {

            }
        });

        return Result.success();
    }
}
