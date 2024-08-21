package com.example.newproject.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.DeletedTaskActivity;
import com.example.newproject.FileOps;
import com.example.newproject.HeroActivity;
import com.example.newproject.R;
import com.example.newproject.TaskAdapter;
import com.example.newproject.TaskStorage;
import com.example.newproject.Tasks;
import com.example.newproject.UIEffects;
import com.example.newproject.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment implements TaskAdapter.OnTaskStarredListener, TaskAdapter.OnTaskCompletedListener{
    private RecyclerView starredTaskList;
    private TaskAdapter taskAdapter;
    private LinearLayout emptyArray, completedTab, remainedTab, deletedTab, moveToTask;
    private ArrayList<Tasks> tasks;
    private FileOps fileOps;
    private UIEffects uiEffects;
    private TextView mUserNameView, mTotalTaskNum, mCompletedTaskNum, mRemainedTaskNum, mDeletedTaskNum;
    private String mNames, mTaskOwner;
    private EditText mTaskTopic;
    private Button createTask;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mUserNameView = view.findViewById(R.id.user_name);
        mTaskTopic = view.findViewById(R.id.task_topic);
        createTask = view.findViewById(R.id.create_task);
        starredTaskList = view.findViewById(R.id.starred_tasks);
        emptyArray = view.findViewById(R.id.empty_array);
        completedTab = view.findViewById(R.id.complete_task_tab);
        remainedTab = view.findViewById(R.id.remained_task_tab);
        deletedTab = view.findViewById(R.id.delete_task_tab);
        mTotalTaskNum = view.findViewById(R.id.total_task_num);
        mCompletedTaskNum = view.findViewById(R.id.complete_task_num);
        mRemainedTaskNum = view.findViewById(R.id.remain_task_num);
        mDeletedTaskNum = view.findViewById(R.id.delete_task_num);
        moveToTask = view.findViewById(R.id.move_to_task);

        fileOps = new FileOps(getContext());
        uiEffects = new UIEffects();
        tasks = TaskStorage.loadTasks(getContext());

        try{
            mNames = fileOps.readIntStorage("userName.txt");
            mTaskOwner = fileOps.readIntStorage("userEmail.txt");
        } catch (RuntimeException e){
            mNames = "";
            mTaskOwner = "";
        }

        updateTaskList();

        moveToTask.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Were still working on this", Toast.LENGTH_SHORT).show();
        });

        completedTab.setOnClickListener(v -> {
//            Navigation.findNavController(v).navigate(R.id.navigation_home_to_navigation_notifications);
            intent = new Intent(getContext(), HeroActivity.class);
            intent.setData(Uri.parse("myapp://navigation_notifications"));
            startActivity(intent);
//            Toast.makeText(getContext(), "Completed Tasks", Toast.LENGTH_SHORT).show();
        });

        remainedTab.setOnClickListener(v -> {
//            Navigation.findNavController(v).navigate(R.id.navigation_home_to_navigation_dashboard);
            intent = new Intent(getContext(), HeroActivity.class);
            intent.setData(Uri.parse("myapp://navigation_dashboard"));
            startActivity(intent);
//            Toast.makeText(getContext(), "Remained Tasks", Toast.LENGTH_SHORT).show();
        });

        deletedTab.setOnClickListener(v -> {
            intent = new Intent(getContext(), DeletedTaskActivity.class);
            startActivity(intent);
//            Toast.makeText(getContext(), "Deleted Tasks", Toast.LENGTH_SHORT).show();
        });

        intent = requireActivity().getIntent();
        String mName = intent.getStringExtra("UserName");

        if(mName != null){
            mUserNameView.setText("Hello, " + mName);
        } else {
            mUserNameView.setText("Hello, " + mNames);
        }

        starredTaskList.setLayoutManager(new LinearLayoutManager(getContext()));
        tasks = TaskStorage.loadTasks(getContext());


        uiEffects.changeUI(mTaskTopic);

        createTask.setOnClickListener(v -> {
            addTask();
            Toast.makeText(getContext(), "Task has been created", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
    
    private void addTask(){
        String title = mTaskTopic.getText().toString().trim();

        try{
            mTaskOwner = fileOps.readIntStorage("userEmail.txt");
        } catch (RuntimeException e){
            mTaskOwner = "";
        }

        if(!title.isEmpty()){
            int id = tasks.size() + 1;
            Tasks newTask = new Tasks(id, mTaskOwner, title, "");
            tasks.add(newTask);
            TaskStorage.saveTasks(getContext(), tasks);
            mTaskTopic.setText("");
        }

        updateTaskList();
    }

    @SuppressLint("SetTextI18n")
    private void updateTaskList(){
        try{
            mTaskOwner = fileOps.readIntStorage("userEmail.txt");
        } catch (RuntimeException e){
            mTaskOwner = "";
        }

        ArrayList<Tasks> totalTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner))
                .collect(Collectors.toList());

        ArrayList<Tasks> starredTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && !tasks.completed && !tasks.deleted && tasks.starred)
                .collect(Collectors.toList());

        ArrayList<Tasks> completedTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && tasks.completed && !tasks.deleted)
                .collect(Collectors.toList());

        ArrayList<Tasks> deletedTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && tasks.deleted)
                .collect(Collectors.toList());

        int totalTaskNum = totalTasks.size();
        int completedTaskNum = completedTasks.size();
        int remainingTaskNum = totalTaskNum - completedTaskNum;
        int deletedTaskNum = deletedTasks.size();
        String totalTaskText = String.valueOf(totalTaskNum);

        mTotalTaskNum.setText(totalTaskText + " Total Tasks");
        mCompletedTaskNum.setText(String.valueOf(completedTaskNum));
        mRemainedTaskNum.setText(String.valueOf(remainingTaskNum));
        mDeletedTaskNum.setText(String.valueOf(deletedTaskNum));

        if(starredTasks.isEmpty()){
            starredTaskList.setVisibility(View.GONE);
            emptyArray.setVisibility(View.VISIBLE);
        } else {
            starredTaskList.setVisibility(View.VISIBLE);
            emptyArray.setVisibility(View.GONE);

            taskAdapter = new TaskAdapter(starredTasks, getContext(), this, this);
            starredTaskList.setAdapter(taskAdapter);
        }
    }

    @Override
    public void onTaskStarred(int taskId, boolean isStarred){
        updateTask(taskId, isStarred);
    }

    @Override
    public void onTaskCompleted(int taskId, boolean isComplete){
        updateTaskComplete(taskId, isComplete);
    }

    private void updateTask(int taskId, boolean isStarred){
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