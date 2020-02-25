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

import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity implements MyTaskRecyclerViewAdapter.OnListFragmentInteractionListener{


    private String TAG = "stg.MainActivity";
    List<Task> listOfTasks;
//    MyDatabase myDb;

    private AWSAppSyncClient mAWSAppSyncClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();


//        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "Task_Master").allowMainThreadQueries().build();
//        this.listOfTasks = myDb.taskDao().getAll();

        listOfTasks = new ArrayList<>();
        runTaskQuery();


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

    public void runTaskQuery(){
        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(tasksCallback);
    }

    // I asked Micah for help with this and then looked at his repo to see if I had done something wrong...
    // It turns out it was just broken because my emulator wasn't updating for some reason. It worked once I uninstalled the app.

    private GraphQLCall.Callback<ListTasksQuery.Data> tasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {

            Log.i("Results", response.data().listTasks().items().toString());

            for(ListTasksQuery.Item item : response.data().listTasks().items()){
                Task task = new Task(item.name(),item.description());
                listOfTasks.add(task);
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("ERROR", e.toString());
        }
    };
}
