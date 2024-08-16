package com.example.newproject.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.FileOps;
import com.example.newproject.R;
import com.example.newproject.TaskAdapter;
import com.example.newproject.TaskStorage;
import com.example.newproject.Tasks;
import com.example.newproject.databinding.FragmentDashboardBinding;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment implements TaskAdapter.OnTaskStarredListener, TaskAdapter.OnTaskCompletedListener{
    private RecyclerView taskList;
    private LinearLayout emptyLayout;
    private FileOps fileOps;
    private String mTaskOwner;
    private ArrayList<Tasks> tasks;
    private TaskAdapter taskAdapter;
    private TextView mTaskNum;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        fileOps = new FileOps(getContext());

        taskList = view.findViewById(R.id.task_list);
        mTaskNum = view.findViewById(R.id.task_num_text);
        emptyLayout = view.findViewById(R.id.empty_array);

        taskList.setLayoutManager(new LinearLayoutManager(getContext()));
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
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && !tasks.completed && !tasks.deleted)
                .collect(Collectors.toList());

        int taskNum = totalTasks.size();
        String mTask = String.valueOf(taskNum);
        mTaskNum.setText("You have " + mTask + " task(s)");

        if(totalTasks.isEmpty()){
            taskList.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            taskList.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            taskAdapter = new TaskAdapter(totalTasks, getContext(), this, this);
            taskList.setAdapter(taskAdapter);
        }
    }

    @Override
    public void onTaskStarred(int taskId, boolean isStarred){
        updateTaskStarred(taskId, isStarred);
    }

    @Override
    public void onTaskCompleted(int taskId, boolean isComplete){
        updateTaskComplete(taskId, isComplete);
    }

    private void updateTaskStarred(int taskId, boolean isStarred){
        for(Tasks task: tasks){
            if(task.id == taskId){
                task.starred = isStarred;
            }
        }
        TaskStorage.saveTasks(getContext(), tasks);
        updateTaskList();
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