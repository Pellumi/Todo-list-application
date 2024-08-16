package com.example.newproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private final ArrayList<Tasks> tasks;
    Context context;
    private OnTaskStarredListener onTaskStarredListener;
    private OnTaskCompletedListener onTaskCompletedListener;

    public TaskAdapter(ArrayList<Tasks> tasks, Context context, OnTaskStarredListener onTaskStarredListener, OnTaskCompletedListener onTaskCompletedListener){
        this.tasks = tasks;
        this.context = context;
        this.onTaskStarredListener = onTaskStarredListener;
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_main_component, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Tasks task = tasks.get(position);

        holder.toggleDone.setChecked(task.completed);
        holder.toggleDone.setOnClickListener(v -> {
            boolean isComplete = holder.toggleDone.isChecked();
            onTaskCompletedListener.onTaskCompleted(task.id, isComplete);
        });

        holder.taskTopic.setText(task.topic);
        if(!Objects.equals(task.taskMessage, "")){
            holder.taskMessage.setText(task.taskMessage);
        } else {
            holder.taskMessage.setVisibility(View.GONE);
        }

        holder.toggleStarred.setChecked(task.starred);
        holder.toggleStarred.setOnClickListener(v -> {
            boolean isChecked = holder.toggleStarred.isChecked();
            onTaskStarredListener.onTaskStarred(task.id, isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TaskActivity.class);
            intent.putExtra("TASK_ID", task.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTopic, taskMessage;
        CheckBox toggleStarred, toggleDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTopic = itemView.findViewById(R.id.task_main_topic);
            taskMessage = itemView.findViewById(R.id.task_main_text);
            toggleStarred = itemView.findViewById(R.id.toggle_starred);
            toggleDone = itemView.findViewById(R.id.toggle_done);
        }
    }

    public interface OnTaskStarredListener{
        void onTaskStarred(int taskId, boolean isStarred);
    }

    public interface OnTaskCompletedListener{
        void onTaskCompleted(int taskId, boolean isComplete);
    }
}
