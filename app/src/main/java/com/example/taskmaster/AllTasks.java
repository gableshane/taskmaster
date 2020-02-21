package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AllTasks extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnListFragmentInteractionListener{

    String TAG = "stg.AllTasks";
    MyDatabase myDb;
    List<Task> listOfTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "Task_Master").allowMainThreadQueries().build();
        this.listOfTasks = myDb.taskDao().getAll();

        RecyclerView recyclerView = findViewById(R.id.allTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyTaskRecyclerViewAdapter(listOfTasks, this));
    }

    @Override
    public void onTaskInteraction(Task task){

        Log.i(TAG, task.getTitle() + " was clicked on.");

        CharSequence charSequence = task.getTitle();
        Toast.makeText(getApplicationContext(), charSequence,Toast.LENGTH_SHORT).show();
    }
}
