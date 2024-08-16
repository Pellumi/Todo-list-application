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

public class TaskActivity extends AppCompatActivity {
    Intent intent;
    TextView mTaskTopic, mTaskText;
    CheckBox toggleStarred;
    ImageView backToMain;
    LinearLayout mComplete, mEdit, mDelete;
    private ArrayList<Tasks> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);
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
        mComplete = findViewById(R.id.complete_task);
        mEdit = findViewById(R.id.edit_task);
        mDelete = findViewById(R.id.delete_task);

        tasks = TaskStorage.loadTasks(this);
        Tasks task = findTaskById(taskId);

        backToMain.setOnClickListener(v -> {
            intent = new Intent(this, HeroActivity.class);
            intent.setData(Uri.parse("myapp://navigation_dashboard"));
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
                TaskStorage.saveTasks(TaskActivity.this, tasks);
            });

            mComplete.setOnClickListener(v -> {
                completeTask(task.id);
                Toast.makeText(TaskActivity.this, "Your Task has been set to completed", Toast.LENGTH_SHORT).show();
            });

            mEdit.setOnClickListener(v -> {
                intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("TASK_ID", task.id);
                startActivity(intent);
            });

            mDelete.setOnClickListener(v -> {
                deleteTask(task.id);
                intent = new Intent(this, HeroActivity.class);
                intent.setData(Uri.parse("myapp://navigation_dashboard"));
                startActivity(intent);
                Toast.makeText(TaskActivity.this, "Your Task has been deleted successfully", Toast.LENGTH_SHORT).show();
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

    private void completeTask(int taskId){
        for(Tasks task: tasks){
            if(task.id == taskId){
                task.completed = true;
            }
        }
        TaskStorage.saveTasks(TaskActivity.this, tasks);
    }

    private void deleteTask(int taskId){
        for(Tasks task: tasks){
            if(task.id == taskId){
                task.deleted = true;
            }
        }
        TaskStorage.saveTasks(TaskActivity.this, tasks);
    }
}