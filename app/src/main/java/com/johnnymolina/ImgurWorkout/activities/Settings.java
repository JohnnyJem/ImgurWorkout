package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.imgurworkout.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

public class Settings extends BaseActivity {
    FrameLayout parent;
    RelativeLayout settingsActivity;

    Button clearImageCache;
    Button clearCache;
    Switch ttsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        settingsActivity= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_settings, null);
        parent.addView(settingsActivity);

        clearImageCache = (Button) findViewById(R.id.button_clear_image_cache);
        clearCache = (Button) findViewById(R.id.button_clear_cache);
        ttsSwitch = (Switch) findViewById(R.id.switch_turn_off_tts);
        ttsSwitch.setChecked(ttsValue);
        settings = getSharedPreferences( ttsPreference, MODE_PRIVATE);



        clearImageCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageCache();
            }
        });


        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache();
            }
        });

        ttsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTts(ttsSwitch.isChecked());
            }
        });



}






}
