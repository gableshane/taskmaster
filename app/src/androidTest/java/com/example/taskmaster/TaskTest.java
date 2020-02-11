package com.example.taskmaster;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    Task tasky;

    @Before
    public void MakeTask(){
        tasky = new Task("Test","Test Description");
    }

    @Test
    public void testConstructor(){
        Boolean actual;
        if(tasky instanceof Task){
            actual = true;
        } else {
            actual = false;
        }
        assertTrue("Tasky is an instance of a task",actual);
    }

    @Test
    public void getTitle() {
        assertEquals("Tasky title should be test","Test",tasky.getTitle());
    }

    @Test
    public void getDescription() {
        assertEquals("Tasky description should be Test Description","Test Description",tasky.getDescription());
    }

    @Test
    public void setTitle() {
        tasky.setTitle("Banana");
        assertEquals("Tasky title should now be Banana","Banana",tasky.getTitle());
    }

    @Test
    public void setDescription() {
        tasky.setDescription("Apple");
        assertEquals("Tasky description should be Apple","Apple",tasky.getDescription());
    }
}