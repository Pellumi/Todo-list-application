package com.example.newproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskStorage {
    private static final String TAG = "TaskStorage";
    private static final String PREF_NAME = "task_storage";
    private static final String KEY_TASKS = "tasks";
    
    public static void saveTasks(Context context, ArrayList<Tasks> tasks){
        Log.d(TAG, "saving Task");

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray jsonArray = new JSONArray();
        for(Tasks task: tasks){
            jsonArray.put(task.toJson());
        }

        editor.putString(KEY_TASKS, jsonArray.toString());
        editor.apply();
    }

    public static ArrayList<Tasks> loadTasks(Context context){
        Log.d(TAG, "Loading Tasks");

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String taskString = sharedPreferences.getString(KEY_TASKS, null);

        ArrayList<Tasks> tasks = new ArrayList<>();
        if(taskString != null){
            try{
                JSONArray jsonArray = new JSONArray(taskString);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Tasks task = Tasks.fromJson(jsonObject);
                    tasks.add(task);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return tasks;
    }

    public static void updateTask(Context context, int taskId, String taskOwner, String taskTopic, String taskMessage) {
        Log.d(TAG, "updating Tasks");

        ArrayList<Tasks> tasks = loadTasks(context);

        for (Tasks task : tasks) {
            if (task.id == taskId && task.taskOwner.equals(taskOwner)) {
                task.topic = taskTopic;
                task.taskMessage = taskMessage;
                break;
            }
        }

        saveTasks(context, tasks);
    }

    public static void deleteTask (Context context, String taskOwner){
        Log.d(TAG, "deleting Tasks");

        ArrayList<Tasks> tasks = loadTasks(context);

        ArrayList<Tasks> updatedTasks = new ArrayList<>();
        for(Tasks task: tasks) {
            if (!task.taskOwner.equals(taskOwner)) {
                updatedTasks.add(task);
            }
        }

        saveTasks(context, updatedTasks);
    }
}
