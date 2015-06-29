package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.johnnymolina.imgurworkout.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {

    private RelativeLayout aboutActivity;
    //Butterknife Injections
    @Bind(R.id.about_thanks_to) TextView aboutThanks;
    @Bind(R.id.about_powered_by) TextView aboutPoweredBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aboutActivity = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_about, null);
        parent.addView(aboutActivity);
        ButterKnife.bind(this);

        aboutThanks.setMovementMethod(LinkMovementMethod.getInstance());
        aboutPoweredBy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
