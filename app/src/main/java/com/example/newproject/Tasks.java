package com.example.newproject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tasks {

    public int id;
    public String taskOwner;
    public String topic;
    public String taskMessage;
    public boolean starred;
    public boolean completed;
    public boolean deleted;
    public long dateCreated;

    public Tasks(){}

    public Tasks (int id, String taskOwner, String topic, String taskMessage){
        this.id = id;
        this.taskOwner = taskOwner;
        this.topic = topic;
        this.taskMessage = taskMessage;
        this.starred = false;
        this.completed = false;
        this.deleted = false;
        this.dateCreated = new Date().getTime();
    }

    public Tasks(int id, String taskOwner, String topic, String taskMessage, boolean starred, boolean completed, boolean deleted, Date dateCreated){
        this.id = id;
        this.taskOwner = taskOwner;
        this.topic = topic;
        this.taskMessage = taskMessage;
        this.starred = starred;
        this.completed = completed;
        this.deleted = deleted;
        this.dateCreated = dateCreated.getTime();
    }
    
    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("id", id);
            jsonObject.put("taskOwner", taskOwner);
            jsonObject.put("topic", topic);
            jsonObject.put("taskMessage", taskMessage);
            jsonObject.put("starred", starred);
            jsonObject.put("completed", completed);
            jsonObject.put("deleted", deleted);
            jsonObject.put("dateCreated", dateCreated);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Tasks fromJson(JSONObject jsonObject){
        try{
            return new Tasks(
                    jsonObject.getInt("id"),
                    jsonObject.getString("taskOwner"),
                    jsonObject.getString("topic"),
                    jsonObject.getString("taskMessage"),
                    jsonObject.getBoolean("starred"),
                    jsonObject.getBoolean("completed"),
                    jsonObject.getBoolean("deleted"),
                    new Date(jsonObject.getLong("dateCreated"))
            );
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("taskOwner", taskOwner);
        map.put("topic", topic);
        map.put("taskMessage", taskMessage);
        map.put("starred", starred);
        map.put("completed", completed);
        map.put("deleted", deleted);
        map.put("dateCreated", dateCreated);
        return map;
    }
}
