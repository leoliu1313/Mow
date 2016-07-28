package com.example.chinyao.mow.mow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.chinyao.mow.R;

public class EditItemActivity extends AppCompatActivity {

    EditText editText;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mow_activity_edit_item);

        position = getIntent().getIntExtra("position", -1);
        String content = getIntent().getStringExtra("content");

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(content);
    }

    public void saveItem(View view) {
        Intent data = new Intent();
        data.putExtra("position", position);
        data.putExtra("content", editText.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }
}
