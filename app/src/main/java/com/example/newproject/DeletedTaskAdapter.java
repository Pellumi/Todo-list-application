package com.example.newproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeletedTaskAdapter extends RecyclerView.Adapter<DeletedTaskAdapter.DeletedTaskViewHolder> {
    private final ArrayList<Tasks> tasks;
    Context context;
    private OnTaskDeletedListener onTaskDeletedListener;

    public DeletedTaskAdapter(ArrayList<Tasks> tasks, Context context, OnTaskDeletedListener onTaskDeletedListener) {
        this.tasks = tasks;
        this.context = context;
        this.onTaskDeletedListener = onTaskDeletedListener;
    }

    @NonNull
    @Override
    public DeletedTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_deleted_component, parent, false);
        return new DeletedTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeletedTaskViewHolder holder, int position) {
        Tasks task = tasks.get(position);
        holder.restore.setChecked(task.deleted);
        holder.restore.setOnClickListener(v -> {
            boolean isDeleted = holder.restore.isChecked();
            onTaskDeletedListener.onTaskDeleted(task.id, isDeleted);
        });

        holder.taskTopic.setText(task.topic);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class DeletedTaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox restore;
        TextView taskTopic;
        public DeletedTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            restore = itemView.findViewById(R.id.restore);
            taskTopic = itemView.findViewById(R.id.task_delete_topic);
        }
    }

    public interface OnTaskDeletedListener{
        void onTaskDeleted(int taskId, boolean isDeleted);
    }
}
