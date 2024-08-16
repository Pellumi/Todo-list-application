package com.example.newproject;

import android.widget.EditText;

public class UIEffects {
    public void changeUI(EditText text){
        text.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            } else if(!hasFocus && text.getText().toString().isEmpty()){
                text.setBackgroundResource(R.drawable.edit_text_clear);
            }
        });
    }
}
