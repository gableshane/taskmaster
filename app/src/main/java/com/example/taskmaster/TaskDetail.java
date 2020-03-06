package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        String title = getIntent().getStringExtra("taskTitle");
        String description = getIntent().getStringExtra("taskDescription");
        String UUID = getIntent().getStringExtra("imageUUID");
        Log.i("stg", "Image UUID "+ UUID);

        TextView taskTitle = findViewById(R.id.taskTitle);
        TextView taskDescription = findViewById(R.id.taskDescription);

        taskTitle.setText(title);
        taskDescription.setText(description);

        // ADD IF TASK HAS IMAGE LATER
        if(UUID != null){
            ImageView image = findViewById(R.id.imageView);
            Picasso.get().load("https://taskmasterc6f89889284d4607b011673a6c4953d3152608-mac.s3-us-west-2.amazonaws.com/" + UUID).into(image);
        }

    }
}
