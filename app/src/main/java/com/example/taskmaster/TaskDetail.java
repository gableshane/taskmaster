package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        String title = getIntent().getStringExtra("taskTitle");
        String description = getIntent().getStringExtra("taskDescription");

        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);

        taskTitle.setText(title);
        taskDescription.setText(description);
    }
}
