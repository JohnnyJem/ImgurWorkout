package com.johnnymolina.ImgurWorkout.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.ImgurWorkout.R;

public class Tutorial extends BaseActivity{

    FrameLayout parent;
    RelativeLayout tutorialActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        tutorialActivity= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_tutorial, null);
        parent.addView(tutorialActivity);
    }

}
