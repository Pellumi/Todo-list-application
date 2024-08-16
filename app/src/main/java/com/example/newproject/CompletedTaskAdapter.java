package com.example.newproject;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.CompletedTaskViewHolder> {
    private final ArrayList<Tasks> tasks;
    Context context;
    private OnTaskCompletedListener onTaskCompletedListener;

    public CompletedTaskAdapter(ArrayList<Tasks> tasks, Context context, OnTaskCompletedListener onTaskCompletedListener) {
        this.tasks = tasks;
        this.context = context;
        this.onTaskCompletedListener = onTaskCompletedListener;
    }

    @NonNull
    @Override
    public CompletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_complete_component, parent, false);
        return new CompletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedTaskViewHolder holder, int position) {
        Tasks task = tasks.get(position);

        holder.toggleDone.setChecked(task.completed);
        holder.toggleDone.setOnClickListener(v -> {
            boolean isComplete = holder.toggleDone.isChecked();
            onTaskCompletedListener.onTaskCompleted(task.id, isComplete);
        });

        holder.taskTopic.setText(task.topic);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class CompletedTaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTopic;
        CheckBox toggleDone;

        public CompletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTopic = itemView.findViewById(R.id.task_complete_topic);
            taskTopic.setPaintFlags(taskTopic.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            toggleDone = itemView.findViewById(R.id.toggle_done);
        }
    }

    public interface OnTaskCompletedListener{
        void onTaskCompleted(int taskId, boolean isComplete);
    }
}
