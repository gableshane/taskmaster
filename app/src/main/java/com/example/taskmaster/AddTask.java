package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        Button addTask = findViewById(R.id.addTaskButton);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleInput = findViewById(R.id.taskTitle);
                String title = titleInput.getText().toString();

                EditText descriptionInput = findViewById(R.id.taskDescription);
                String description = descriptionInput.getText().toString();

                Task newTask = new Task(title, description);

                TextView submitted = findViewById(R.id.submitted);
                submitted.setVisibility(View.VISIBLE);
            }
        });
    }


}
