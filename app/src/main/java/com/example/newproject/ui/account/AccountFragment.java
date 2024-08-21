package com.example.newproject.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.newproject.FileOps;
import com.example.newproject.FirebaseProcess;
import com.example.newproject.LoginActivity;
import com.example.newproject.Notification;
import com.example.newproject.R;
import com.example.newproject.TaskStorage;
import com.example.newproject.Tasks;
import com.example.newproject.databinding.FragmentAccountBinding;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AccountFragment extends Fragment {
    private String mTaskOwner;
    private ArrayList<Tasks> tasks;
    Intent intent;
    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FileOps fileOps = new FileOps(getContext());
        tasks = TaskStorage.loadTasks(getContext());

        binding.logoutBtn.setOnClickListener(v -> {
            try{
                mTaskOwner = fileOps.readIntStorage("userEmail.txt");
            } catch (RuntimeException e){
                mTaskOwner = "";
            }

            ArrayList<Tasks> totalTasks = (ArrayList<Tasks>) tasks.stream()
                    .filter(tasks -> mTaskOwner.equals(tasks.taskOwner))
                    .collect(Collectors.toList());

            int taskNum = totalTasks.size();
            
            if (taskNum == 0){
                fileOps.writeToIntFile("userName.txt", "");
                fileOps.writeToIntFile("userEmail.txt", "");
                fileOps.writeToIntFile("userLog.txt", "-1");

                intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                FirebaseProcess.uploadTask(getContext(), new FirebaseProcess.OnUploadTaskListener() {
                    @Override
                    public void OnUploadSuccess(String status) {
                        String taskOwner = fileOps.readIntStorage("userEmail.txt");
                        TaskStorage.deleteTask(getContext(), taskOwner);

                        fileOps.writeToIntFile("userName.txt", "");
                        fileOps.writeToIntFile("userEmail.txt", "");
                        fileOps.writeToIntFile("userLog.txt", "-1");

                        intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void OnUploadFailure(String message) {
                        Toast.makeText(getContext(), "Log out Failed, Please try again later", Toast.LENGTH_SHORT).show();
                        Log.d("AccountFragment", message);
                    }
                });
            }
        });

        binding.uploadTask.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Uploading Tasks", Toast.LENGTH_SHORT).show();
            FirebaseProcess.uploadTask(getContext(), new FirebaseProcess.OnUploadTaskListener() {
                @Override
                public void OnUploadSuccess(String status) {
                    if (status.equals("Success")) {
                        Toast.makeText(getContext(), "Task Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void OnUploadFailure(String message) {
                    Toast.makeText(getContext(), "Task Upload Failed, Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d("AccountFragment", message);
                }
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}