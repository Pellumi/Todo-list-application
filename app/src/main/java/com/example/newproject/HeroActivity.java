package com.example.newproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

public class HeroActivity extends AppCompatActivity {
    Intent intent;
    String userLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero);

        TaskScheduler.scheduleTaskUpload(this);

        FileOps fileOps = new FileOps(HeroActivity.this);

        try{
            userLog = fileOps.readIntStorage("userLog.txt");
        } catch (RuntimeException e){
            userLog = "-1";
        }

        if (Objects.equals(userLog, "-1")){
            intent = new Intent(HeroActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_hero);
        NavigationUI.setupWithNavController(navView, navController);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent){
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_hero);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void handleIntent(Intent intent){
        if(intent != null && intent.getData() != null){
            Uri data = intent.getData();
            if(data != null){
                String host = data.getHost();
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_hero);
                if("navigation_dashboard".equals(host)){
                    navController.navigate(R.id.navigation_dashboard);
                } else if ("navigation_notifications".equals(host)) {
                    navController.navigate(R.id.navigation_notifications);
                } else if ("navigation_account".equals(host)) {
                    navController.navigate(R.id.navigation_account);
                }
            }
        }
    }
}