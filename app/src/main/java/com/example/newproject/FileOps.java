package com.example.newproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileOps {
    Context context;
    public FileOps(Context context) {
        this.context = context;
    }

    public void writeToIntFile(String fileName, String text){
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            if (text != null) {
                writer.write(text);
                writer.close();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readIntStorage(String fileName){
        String content;

        try {
            File file = new File(context.getFilesDir(), fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
            return content;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String fileName){
        File file = new File(context.getFilesDir(), fileName);

        if (file.exists()) {
            file.delete();
        } else {
            throw new RuntimeException("File does not exist");
        }
    }

    public void writeNotify(String fileName, String text){
        try {
            File file = new File(context.getFilesDir(), fileName);
            if(!file.exists()){
                FileOutputStream outputStream = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(text);
                writer.close();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
