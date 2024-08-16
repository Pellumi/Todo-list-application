package com.example.newproject.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.CompletedTaskAdapter;
import com.example.newproject.FileOps;
import com.example.newproject.R;
import com.example.newproject.TaskAdapter;
import com.example.newproject.TaskStorage;
import com.example.newproject.Tasks;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class NotificationsFragment extends Fragment implements CompletedTaskAdapter.OnTaskCompletedListener{
    private RecyclerView completedTaskList;
    private LinearLayout emptyLayout;
    private FileOps fileOps;
    private String mTaskOwner;
    private ArrayList<Tasks> tasks;
    private CompletedTaskAdapter completedTaskAdapter;
    private TextView mCompletedTaskNum;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        fileOps = new FileOps(getContext());

        completedTaskList = view.findViewById(R.id.completed_task_list);
        mCompletedTaskNum = view.findViewById(R.id.completed_task_num_text);
        emptyLayout = view.findViewById(R.id.empty_array);

        completedTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        tasks = TaskStorage.loadTasks(getContext());

        updateTaskList();
        return view;
    }

    private void updateTaskList(){
        try{
            mTaskOwner = fileOps.readIntStorage("userEmail.txt");
        } catch (RuntimeException e){
            mTaskOwner = "";
        }

        ArrayList<Tasks> totalTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && tasks.completed && !tasks.deleted)
                .collect(Collectors.toList());

        int taskNum = totalTasks.size();
        String mTask = String.valueOf(taskNum);
        mCompletedTaskNum.setText(mTask + " Completed task(s)");

        if(totalTasks.isEmpty()){
            completedTaskList.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            completedTaskList.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            completedTaskAdapter = new CompletedTaskAdapter(totalTasks, getContext(), this);
            completedTaskList.setAdapter(completedTaskAdapter);
        }
    }

    @Override
    public void onTaskCompleted(int taskId, boolean isComplete){
        updateTaskComplete(taskId, isComplete);
    }

    private void updateTaskComplete(int taskId, boolean isComplete){
        for(Tasks task: tasks){
            if(task.id == taskId){
                task.completed = isComplete;
            }
        }
        TaskStorage.saveTasks(getContext(), tasks);
        updateTaskList();
    }
}