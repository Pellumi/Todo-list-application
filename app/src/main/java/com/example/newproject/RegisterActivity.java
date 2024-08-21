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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    ImageView backToMain;
    EditText lastName, firstName, userName, mEmail, mPassword;
    Button mRegister;
    TextView forgotPassword, logIn;
    Intent intent;
    CheckBox togglePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backToMain = findViewById(R.id.back);
        lastName = findViewById(R.id.last_name);
        firstName = findViewById(R.id.first_name);
//        userName = findViewById(R.id.user_name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register_btn);
        forgotPassword = findViewById(R.id.forgot_password);
        logIn = findViewById(R.id.log_in);
        togglePassword = findViewById(R.id.toggle_password);

        backToMain.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "This feature will be available in the next update", Toast.LENGTH_SHORT).show();
        });

        changeUI(lastName);
        changeUI(firstName);
//        changeUI(userName);
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

        mRegister.setOnClickListener(v -> {
            Log.d(TAG, "Attempting to Register User");

            String fName = firstName.getText().toString().trim();
            String lName = lastName.getText().toString().trim();
//            String uName = userName.getText().toString().trim();
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            
            String name = fName + " " + lName;

            if(isStringNull(fName) || isStringNull(lName) || isStringNull(email) || isStringNull(password)){
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be up to 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseProcess.registerUser(fName,  lName, name, email, password, new FirebaseProcess.OnUserRegistrationListener() {
                @Override
                public void OnRegistrationSuccess(String status) {
                    Toast.makeText(RegisterActivity.this, "Registration successful! Proceed to login", Toast.LENGTH_SHORT).show();
                    intent = new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    RegisterActivity.this.startActivity(intent);
                }

                @Override
                public void OnRegistrationFailure(String message) {
                    Toast.makeText(RegisterActivity.this, "Registration failed! Please try again later", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, message);
                }
            });
        });

        logIn.setOnClickListener(v -> {
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
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

    public boolean isStringNull(String text){
        Log.d(TAG, "isStringNull: checking if string is null");
        return text == null || text.isEmpty();
    }
}