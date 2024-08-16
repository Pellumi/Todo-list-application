package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    ImageView backToMain;
    EditText mEmail, mPassword;
    Button mLogin;
    TextView forgotPassword, signUp;
    Intent intent;
    Notification loginNotify = new Notification(LoginActivity.this);
    CheckBox togglePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backToMain = findViewById(R.id.back);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login_btn);
        forgotPassword = findViewById(R.id.forgot_password);
        signUp = findViewById(R.id.sign_up);
        togglePassword = findViewById(R.id.toggle_password);

        changeUI(mEmail);
        changeUI(mPassword);

        togglePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setText("Hide Password");
            } else {
                mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setText("Show Password");
            }
            mPassword.setSelection(mPassword.length());
        });

        mLogin.setOnClickListener(v -> {
            Log.d(TAG, "onClick: attempting to log in");

            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (isStringNull(email) || isStringNull(password)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Invalid Credentials")
                        .setMessage("Please fill in all fields")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
//                check if database values match
                mEmail.setEnabled(false);
                mEmail.setAlpha(0.5F);
                mPassword.setEnabled(false);
                mEmail.setAlpha(0.5F);
                Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();

                try {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            handleSuccessfulLogin();
                        } else if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).contains("There is no user record corresponding to this identifier")) {
                            mEmail.setEnabled(true);
                            mEmail.setAlpha(1F);
                            mPassword.setEnabled(true);
                            mEmail.setAlpha(1F);

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Invalid Credentials")
                                    .setMessage("The email or password you entered does not exist.")
                                    .setPositiveButton("Sign Up", (dialog, id) -> {
                                        dialog.cancel();
                                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    })
                                    .setNegativeButton("Cancel", (dialog, id) -> {
                                        dialog.cancel();
                                        mEmail.setEnabled(true);
                                        mEmail.setAlpha(1F);
                                        mPassword.setEnabled(true);
                                        mEmail.setAlpha(1F);
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (task.getException().getMessage().contains("auth credential is incorrect")) {
                            mEmail.setEnabled(true);
                            mEmail.setAlpha(1F);
                            mPassword.setEnabled(true);
                            mEmail.setAlpha(1F);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Invalid Username or Password")
                                    .setMessage("Username or Password is incorrect")
                                    .setPositiveButton("OK", (dialog, id) -> dialog.cancel())
                                    .setNegativeButton("Reset Password", (dialog, id) -> {
                                        dialog.cancel();
                                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                        // TODO: create a forgot password activity
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else if (task.getException().getMessage().contains("network error")) {
                            mEmail.setEnabled(true);
                            mEmail.setAlpha(1F);
                            mPassword.setEnabled(true);
                            mEmail.setAlpha(1F);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Network Error")
                                    .setMessage("Please check your internet connection")
                                    .setPositiveButton("OK", (dialog, id) -> dialog.cancel());
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            mEmail.setEnabled(true);
                            mEmail.setAlpha(1F);
                            mPassword.setEnabled(true);
                            mEmail.setAlpha(1F);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Error")
                                    .setMessage(task.getException().getMessage())
                                    .setPositiveButton("OK", (dialog, id) -> dialog.cancel());
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(v -> {
            Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(mLogin).show();
        });

        signUp.setOnClickListener(v -> {
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleSuccessfulLogin() {
        FileOps fileOps = new FileOps(LoginActivity.this);
        String userEmail = mEmail.getText().toString().trim();

        FirebaseProcess.getUser(userEmail, new FirebaseProcess.UserCallback() {
            @Override
            public void onUserFound(String name, String fName) {
                fileOps.writeToIntFile("userName.txt", name);
                fileOps.writeToIntFile("firstName.txt", fName);
                Toast.makeText(LoginActivity.this, name, Toast.LENGTH_SHORT).show();
                loginNotify.showNotification("Welcome to Tasked, " + fName, "Let's get you started on a journey to co-ordination");

                fileOps.writeToIntFile("userEmail.txt", userEmail);
                fileOps.writeToIntFile("userLog.txt", "1");

                FirebaseProcess.fetchTasks(userEmail, new FirebaseProcess.OnTaskFetchedListener(){
                    @Override
                    public void OnTaskFetchedSuccess(ArrayList<Tasks> tasks) {
                        TaskStorage.saveTasks(LoginActivity.this, tasks);

                        Log.d(TAG, "handleSuccessfulLogin: navigating to main activity");
                        intent = new Intent(LoginActivity.this, HeroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("UserName", name);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void OnTaskFetchedFailure(String message) {
                        Log.d(TAG, "handleSuccessfulLogin: navigating to main activity");
                        intent = new Intent(LoginActivity.this, HeroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("UserName", name);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            @Override
            public void onUserNotFound() {
                fileOps.writeToIntFile("userName.txt", "User name not retrieved");
//                Toast.makeText(LoginActivity.this, "User name not retrieved", Toast.LENGTH_SHORT).show();
                loginNotify.showNotification("Welcome to Tasked", "Let's get you started on a journey to completion");

                fileOps.writeToIntFile("userEmail.txt", userEmail);
                fileOps.writeToIntFile("userLog.txt", "1");

                Log.d(TAG, "handleSuccessfulLogin: navigating to main activity");
                intent = new Intent(LoginActivity.this, HeroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("UserName", "User");
                startActivity(intent);
                finish();
            }
        });
    }

    public void changeUI(EditText text){
        text.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            } else if(!hasFocus && text.getText().toString().isEmpty()){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            }
        });
    }

    private boolean isStringNull(String str) {
        Log.d(TAG, "isStringNull: checking if string is null");
        return str == null || str.isEmpty();
    }
}