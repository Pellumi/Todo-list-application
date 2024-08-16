package com.example.newproject;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseProcess {
    public interface OnUserRegistrationListener{
        void OnRegistrationSuccess(String status);
        void OnRegistrationFailure(String message);
    }

    public static void registerUser(String fName, String lName, String name, String email, String password, OnUserRegistrationListener listener){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DataUser dataUser = new DataUser();
        dataUser.setName(name);
        dataUser.setFirstName(fName);
        dataUser.setLastName(lName);
        dataUser.setEmail(email);

        Task<AuthResult> authResultTask = firebaseAuth.createUserWithEmailAndPassword(email, password);
        authResultTask.addOnSuccessListener(authResult -> {
            String key = firebaseDatabase.getReference("Users").push().getKey();
            assert key != null;
            firebaseDatabase.getReference("Users").child(key).setValue(dataUser).addOnCompleteListener(task -> {
                if(listener != null){
                    if(task.isSuccessful()){
                        listener.OnRegistrationSuccess("Success");
                    } else {
                        listener.OnRegistrationFailure(Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });
        });

        authResultTask.addOnFailureListener(e -> {
            if(listener != null){
                listener.OnRegistrationFailure(e.getMessage());
            }
        });
    }

//    private static ValueEventListener valueEventListener;
    public interface UserCallback {
        void onUserFound(String name, String fName);
        void onUserNotFound();
    }

    public static void getUser (String email, UserCallback callback) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");

        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot userSnapshot: snapshot.getChildren()){
                        String name = userSnapshot.child("name").getValue(String.class);
                        String fName = userSnapshot.child("firstName").getValue(String.class);
                        callback.onUserFound(name, fName);
                        return;
                    }
                } else {
                    callback.onUserNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnUploadTaskListener {
        void OnUploadSuccess(String status);
        void OnUploadFailure(String message);
    }

    public static void uploadTask(Context context, OnUploadTaskListener onUploadTaskListener){

        ArrayList<Tasks> tasks = TaskStorage.loadTasks(context);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        for(Tasks task : tasks){
            Query query = databaseReference.orderByChild("email").equalTo(task.taskOwner);

            query.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    for (DataSnapshot userSnapshot : task1.getResult().getChildren()) {
                        DatabaseReference taskRef = userSnapshot.getRef().child("Tasks");

                        taskRef.removeValue().addOnCompleteListener(removeTask -> {
                            if (removeTask.isSuccessful()) {

                                taskRef.push().setValue(task.toMap())
                                        .addOnSuccessListener(aVoid -> onUploadTaskListener.OnUploadSuccess("Success"))
                                        .addOnFailureListener(e -> onUploadTaskListener.OnUploadFailure(e.getMessage()));
                            } else {
                                onUploadTaskListener.OnUploadFailure(removeTask.getException().getMessage());
                            }
                        });
                    }
                } else {
                    onUploadTaskListener.OnUploadFailure(task1.getException().getMessage());
                }
            });
        }
    }

    public interface OnTaskFetchedListener{
        void OnTaskFetchedSuccess(ArrayList<Tasks> tasks);
        void OnTaskFetchedFailure(String message);
    }

    public static void fetchTasks(String userEmail, OnTaskFetchedListener onTaskFetchedListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);

        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DataSnapshot userSnapshot : task.getResult().getChildren()){
                    DatabaseReference taskRef = userSnapshot.getRef().child("Tasks");

                    taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                ArrayList<Tasks> tasks = new ArrayList<>();
                                for (DataSnapshot taskSnapshot : snapshot.getChildren()){
                                    
                                    Tasks task = taskSnapshot.getValue(Tasks.class);
                                    if (task != null) {
                                        tasks.add(task);
                                    }
                                }

                                onTaskFetchedListener.OnTaskFetchedSuccess(tasks);
                            } else {
                                onTaskFetchedListener.OnTaskFetchedFailure("No tasks found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            onTaskFetchedListener.OnTaskFetchedFailure(error.getMessage());
                        }
                    });
                }
            } else {
                onTaskFetchedListener.OnTaskFetchedFailure(task.getException().getMessage());
            }
        });
    }

    public static void fetchTask(String userEmail, OnTaskFetchedListener onTaskFetchedListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = databaseReference.orderByChild("email").equalTo(userEmail);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Tasks> allTasks = new ArrayList<>();
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    DatabaseReference taskRef = userSnapshot.getRef().child("Tasks");
                    taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                                    Tasks task = taskSnapshot.getValue(Tasks.class);
                                    if (task != null) {
                                        allTasks.add(task);
                                    }
                                }
                            }

                            // Notify success after processing all user data
                            if (allTasks.size() > 0) {
                                onTaskFetchedListener.OnTaskFetchedSuccess(allTasks);
                            } else {
                                onTaskFetchedListener.OnTaskFetchedFailure("No tasks found");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            onTaskFetchedListener.OnTaskFetchedFailure(error.getMessage());
                        }
                    });
                }
            } else {
                onTaskFetchedListener.OnTaskFetchedFailure(task.getException().getMessage());
            }
        });
    }
}
