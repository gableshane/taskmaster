package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button save = findViewById(R.id.saveUsername);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView username = findViewById(R.id.username);
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = p.edit();
                editor.putString("username",username.getText().toString());
                editor.apply();

                Intent i = new Intent(Settings.this,MainActivity.class);
                Settings.this.startActivity(i);
            }
        });
    }
}
