package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.ResultListener;
import com.amplifyframework.storage.result.StorageUploadFileResult;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

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

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                        AWSMobileClient.getInstance().showSignIn(
                                AddTask.this,
                                SignInUIOptions.builder()
                                        .nextActivity(MainActivity.class)
                                        .build(),
                                new Callback<UserStateDetails>() {
                                    @Override
                                    public void onResult(UserStateDetails result) {
                                        Log.d(TAG, "onResult: " + result.getUserState());
                                        switch (result.getUserState()){
                                            case SIGNED_IN:
                                                Log.i("INIT", "logged in!");
                                                break;
                                            case SIGNED_OUT:
                                                Log.i(TAG, "onResult: User did not choose to sign-in");
                                                break;
                                            default:
                                                AWSMobileClient.getInstance().signOut();
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e(TAG, "onError: ", e);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("INIT", "Initialization error.", e);
                    }
                }
        );

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 543 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            ImageView imageView = findViewById(R.id.imageView2);
            imageView.setImageURI(selectedImage);
            uploadWithTransferUtility(selectedImage);

        }
    }

        public void filePicker (View v){
            Log.i(TAG, "clicked");

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            } else {
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 543);
            }

        }

//    private void uploadFile() {
//        File sampleFile = new File(getApplicationContext().getFilesDir(), "sample.txt");
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter(sampleFile));
//            writer.append("Howdy World!");
//            writer.close();
//        }
//        catch(Exception e) {
//            Log.e("StorageQuickstart", e.getMessage());
//        }
//
//        Amplify.Storage.uploadFile(
//                "myUploadedFileName.txt",
//                sampleFile.getAbsolutePath(),
//                new ResultListener<StorageUploadFileResult>() {
//                    @Override
//                    public void onResult(StorageUploadFileResult result) {
//                        Log.i("StorageQuickStart", "Successfully uploaded: " + result.getKey());
//                    }
//
//                    @Override
//                    public void onError(Throwable error) {
//                        Log.e("StorageQuickstart", "Upload error.", error);
//                    }
//                }
//        );
//    }

    public void uploadWithTransferUtility(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        // String picturePath contains the path of selected Image
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

//        File file = new File(getApplicationContext().getFilesDir(), "sample.txt");

        System.out.println(TAG + picturePath);
        final String uuid = UUID.randomUUID().toString();
//
//        Amplify.Storage.uploadFile(
//                uuid,
//                new File(picturePath).getAbsolutePath(),
//                new ResultListener<StorageUploadFileResult>() {
//                    @Override
//                    public void onResult(StorageUploadFileResult result) {
//                        Log.i("StorageQuickStart", "Successfully uploaded: " + result.getKey());
//                    }
//
//                    @Override
//                    public void onError(Throwable error) {
//                        Log.e("StorageQuickstart", "Upload error.", error);
//                    }
//                }
//        );
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + uuid,
                        new File(picturePath), CannedAccessControlList.PublicRead);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {

            }
            });
    }
}
