package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DeletedTaskActivity extends AppCompatActivity implements DeletedTaskAdapter.OnTaskDeletedListener{
    private ArrayList<Tasks> tasks;
    private TextView mDeletedTaskNum;
    private RecyclerView deletedTaskList;
    private LinearLayout emptyArray;
    private ImageView backToMain;
    Intent intent;
    private FileOps fileOps;
    private String mTaskOwner;
    private DeletedTaskAdapter deletedTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deleted_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDeletedTaskNum = findViewById(R.id.deleted_task_num);
        deletedTaskList = findViewById(R.id.deleted_task_list);
        emptyArray = findViewById(R.id.empty_array);
        backToMain = findViewById(R.id.back_to_main);

        fileOps = new FileOps(DeletedTaskActivity.this);
        deletedTaskList.setLayoutManager(new LinearLayoutManager(DeletedTaskActivity.this));
        tasks = TaskStorage.loadTasks(DeletedTaskActivity.this);

        updateTaskList();

        backToMain.setOnClickListener(v -> {
            intent = new Intent(DeletedTaskActivity.this, HeroActivity.class);
            startActivity(intent);
        });
    }

    private void updateTaskList(){
        try{
            mTaskOwner = fileOps.readIntStorage("userEmail.txt");
        } catch (RuntimeException e){
            mTaskOwner = "";
        }

        ArrayList<Tasks> deletedTasks = (ArrayList<Tasks>) tasks.stream()
                .filter(tasks -> mTaskOwner.equals(tasks.taskOwner) && tasks.deleted)
                .collect(Collectors.toList());

        int taskNum = deletedTasks.size();
        String mTask = String.valueOf(taskNum);
        mDeletedTaskNum.setText("You have " + mTask + " task(s)");

        if(deletedTasks.isEmpty()){
            deletedTaskList.setVisibility(View.GONE);
            emptyArray.setVisibility(View.VISIBLE);
        } else {
            deletedTaskList.setVisibility(View.VISIBLE);
            emptyArray.setVisibility(View.GONE);

            deletedTaskAdapter = new DeletedTaskAdapter(deletedTasks, DeletedTaskActivity.this, this);
            deletedTaskList.setAdapter(deletedTaskAdapter);
        }
    }

    @Override
    public void onTaskDeleted(int taskId, boolean isDeleted) {
        restoreTask(taskId, isDeleted);
    }

    private void restoreTask(int taskId, boolean isDeleted){
        for(Tasks task: tasks){
            if(task.id == taskId){
                task.deleted = isDeleted;
            }
        }
        TaskStorage.saveTasks(DeletedTaskActivity.this, tasks);
        updateTaskList();
    }

}