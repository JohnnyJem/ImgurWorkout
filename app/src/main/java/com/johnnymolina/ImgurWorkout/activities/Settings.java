package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.imgurworkout.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Settings extends BaseActivity {

    RelativeLayout settingsActivity;

    @Bind(R.id.button_clear_image_cache) Button clearImageCache;
    @Bind(R.id.button_clear_cache) Button clearCache;
    @Bind(R.id.switch_turn_off_tts) Switch ttsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsActivity= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_settings, null);
        parent.addView(settingsActivity);
        ButterKnife.bind(this);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
