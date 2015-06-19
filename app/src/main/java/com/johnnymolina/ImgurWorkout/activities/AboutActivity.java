package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.johnnymolina.imgurworkout.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutActivity extends BaseActivity {

    private FrameLayout parent;
    private RelativeLayout aboutActivity;
    //Butterknife Injections
    @InjectView(R.id.about_thanks_to) TextView aboutThanks;
    @InjectView(R.id.about_powered_by) TextView aboutPoweredBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        aboutActivity = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_about, null);
        parent.addView(aboutActivity);

        ButterKnife.inject(this);

        aboutThanks.setMovementMethod(LinkMovementMethod.getInstance());
        aboutPoweredBy.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
