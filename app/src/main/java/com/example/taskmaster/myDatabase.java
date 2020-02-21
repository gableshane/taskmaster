package com.example.taskmaster;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class myDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
