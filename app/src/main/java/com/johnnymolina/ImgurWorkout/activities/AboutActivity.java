package com.johnnymolina.ImgurWorkout.activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.johnnymolina.ImgurWorkout.R;

public class AboutActivity extends BaseActivity {

   private FrameLayout parent;
   private RelativeLayout aboutActivity;
    private TextView aboutThanks;
    private TextView aboutPoweredBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        aboutActivity = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_about, null);
        parent.addView(aboutActivity);

        aboutThanks = (TextView) findViewById(R.id.about_thanks_to);
        aboutPoweredBy = (TextView) findViewById(R.id.about_powered_by);

        aboutThanks.setMovementMethod(LinkMovementMethod.getInstance());
        aboutPoweredBy.setMovementMethod(LinkMovementMethod.getInstance());
///Test comit
    }




}
