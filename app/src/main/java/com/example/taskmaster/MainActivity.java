package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnListFragmentInteractionListener{


    private String TAG = "stg.MainActivity";
    List<Task> listOfTasks = new ArrayList<>();
    myDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = Room.databaseBuilder(getApplicationContext(), myDatabase.class, "Task_Master").allowMainThreadQueries().build();
        this.listOfTasks = myDb.taskDao().getAll();


        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = p.getString("username","default");

        if(username != "default"){
            TextView greeting = findViewById(R.id.greeting);
            greeting.setText(username + "'s tasks");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyTaskRecyclerViewAdapter(listOfTasks, this));


        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(MainActivity.this,Settings.class);
                MainActivity.this.startActivity(goToSettings);
            }
        });


        Button addTaskButton = findViewById(R.id.addTask);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTaskIntent);
            }
        });

        Button allTaskButton = findViewById(R.id.allTasks);
        allTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });
    }
    @Override
    public void onTaskInteraction(Task task){

        Log.i(TAG, task.getTitle() + " was clicked on.");

        Intent goToTaskDetail = new Intent(this, TaskDetail.class);
        goToTaskDetail.putExtra("taskTitle",task.getTitle());
        goToTaskDetail.putExtra("taskDescription",task.getDescription());

        this.startActivity(goToTaskDetail);
    }
}
