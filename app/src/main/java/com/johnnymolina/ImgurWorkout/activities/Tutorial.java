package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.imgurworkout.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Tutorial extends BaseActivity{

    RelativeLayout tutorialActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialActivity= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_tutorial, null);
        parent.addView(tutorialActivity);

    }

}
