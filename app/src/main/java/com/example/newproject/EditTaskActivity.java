package com.example.newproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Objects;

public class EditTaskActivity extends AppCompatActivity {
    Intent intent;
    EditText mTaskTopic, mTaskText;
    CheckBox toggleStarred;
    ImageView backToMain;
    Button mUpdateTask;
    private ArrayList<Tasks> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        mTaskTopic = findViewById(R.id.task_topic);
        mTaskText = findViewById(R.id.task_text);
        toggleStarred = findViewById(R.id.toggle_starred);
        backToMain = findViewById(R.id.back_to_main);
        mUpdateTask = findViewById(R.id.update_task);

        tasks = TaskStorage.loadTasks(this);
        Tasks task = findTaskById(taskId);

        backToMain.setOnClickListener(v -> {
            intent = new Intent(this, TaskActivity.class);
            assert task != null;
            intent.putExtra("TASK_ID", task.id);
            startActivity(intent);
        });

        if(task != null){
            mTaskTopic.setText(task.topic);
            if (Objects.equals(task.taskMessage, "")){
                mTaskText.setText("No task description given");
            } else {
                mTaskText.setText(task.taskMessage);
            }

            toggleStarred.setChecked(task.starred);

            toggleStarred.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.starred = isChecked;
                TaskStorage.saveTasks(EditTaskActivity.this, tasks);
            });


            mUpdateTask.setOnClickListener(v -> {
                String taskTopic = mTaskTopic.getText().toString().trim();
                String taskText = mTaskText.getText().toString().trim();

                String taskOwner = task.taskOwner;

                if(!taskTopic.isEmpty() && taskText.equals("No task description given")){
                    TaskStorage.updateTask(EditTaskActivity.this, task.id, taskOwner, taskTopic, "");

                    intent = new Intent(this, HeroActivity.class);
                    intent.setData(Uri.parse("myapp://navigation_dashboard"));
                    startActivity(intent);

                    Toast.makeText(this, "Your Task has been updated", Toast.LENGTH_SHORT).show();
                } else if (!taskTopic.isEmpty()) {
                    TaskStorage.updateTask(EditTaskActivity.this, task.id, taskOwner, taskTopic, taskText);

                    intent = new Intent(this, HeroActivity.class);
                    intent.setData(Uri.parse("myapp://navigation_dashboard"));
                    startActivity(intent);

                    Toast.makeText(this, "Your Task has been updated", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(this, "Task Topic cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Tasks findTaskById(int taskId){
        for (Tasks task : tasks){
            if(task.id == taskId){
                return task;
            }
        }
        return null;
    }
}