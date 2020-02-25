package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.CreateTodoMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateTaskInput;
import type.CreateTodoInput;

public class AddTask extends AppCompatActivity {

    //    MyDatabase myDb;
    private AWSAppSyncClient mAWSAppSyncClient;
    private static String TAG = "stg.AddTaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

//        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "Task_Master").allowMainThreadQueries().build();

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        Button addTask = findViewById(R.id.addTaskButton);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleInput = findViewById(R.id.taskTitle);
                String title = titleInput.getText().toString();

                EditText descriptionInput = findViewById(R.id.taskDescription);
                String description = descriptionInput.getText().toString();

//                Task newTask = new Task(title, description);
                runTaskCreateMutation(title,description);
//                myDb.taskDao().save(newTask);


                TextView submitted = findViewById(R.id.submitted);
                submitted.setVisibility(View.VISIBLE);
            }
        });
    }


    public void runTaskCreateMutation(String taskTitle, String taskDescription) {
        CreateTaskInput createTaskInput = CreateTaskInput.builder().
                name(taskTitle).
                description(taskDescription).
                build();

        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTaskInput).build())
                .enqueue(mutationCallback);
    }

    private GraphQLCall.Callback<CreateTaskMutation.Data> mutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response) {
            Log.i(TAG, response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }

        };
}

