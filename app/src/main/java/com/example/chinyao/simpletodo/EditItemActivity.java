package com.example.chinyao.simpletodo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        int position = getIntent().getIntExtra("position", -1);
        String content = getIntent().getStringExtra("content");

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(content);
    }
}
