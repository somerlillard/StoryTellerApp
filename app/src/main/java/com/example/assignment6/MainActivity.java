package com.example.assignment6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void startActivity1(View view) {
        Intent photoT = new Intent(this, PhotoTagger.class);
        startActivity(photoT);
    }
    public void startActivity2(View view) {
        Intent sketchT = new Intent(this, SketchTagger.class);
        startActivity(sketchT);
    }
    public void startActivity3(View view) {
        Intent storyT = new Intent(this, StoryTeller.class);
        startActivity(storyT);
    }
}