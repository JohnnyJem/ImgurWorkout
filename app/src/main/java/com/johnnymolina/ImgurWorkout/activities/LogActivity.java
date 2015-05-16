package com.johnnymolina.ImgurWorkout.activities;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.adapters.RealmImgurAlbumModelAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmLogModelAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmRecyclerViewLogAdapter;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.Log;

import io.realm.Realm;
import io.realm.RealmResults;

public class LogActivity extends BaseActivity {

    private Realm realm;
   // private LogAdapter adapter;
   private RealmRecyclerViewLogAdapter adapter;
    FrameLayout parent;
    RelativeLayout activityLibrary;
    TextToSpeech tts;
    View fabLogOpen;
    View fabLogAdd;

    LinearLayout editTextDrawer;
    EditText dateTime;
    EditText albumName;
    EditText timeLength;
    EditText workoutType;
    EditText notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        activityLibrary= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_log, null);
        parent.addView(activityLibrary);
        realm = Realm.getInstance(getBaseContext());

        editTextDrawer = (LinearLayout) findViewById(R.id.log_edit_texts_drawer);
        dateTime = (EditText) findViewById(R.id.log_edit_text_DateTime);
        albumName = (EditText) findViewById(R.id.log_edit_text_album_name);
        timeLength = (EditText) findViewById(R.id.log_edit_text_time_length);
        workoutType = (EditText) findViewById(R.id.log_edit_text_workout_type);
        notes= (EditText) findViewById(R.id.log_edit_text_notes);






        fabLogOpen = findViewById(R.id.fab_log_open);
        fabLogAdd = findViewById(R.id.fab_log_add);

        fabLogOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDrawer.setVisibility(View.VISIBLE);
                fabLogOpen.setVisibility(View.GONE);
                fabLogAdd.setVisibility(View.VISIBLE);

            }
        });

        fabLogAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDrawer.setVisibility(View.GONE);
                fabLogOpen.setVisibility(View.VISIBLE);
                fabLogAdd.setVisibility(View.GONE);

                /*----REALM WORK BEING DONE TO STORE EDIT TEXT TEXT---*/
                realm.beginTransaction();
                Log realmLogObject = realm.createObject(Log.class);
                       realmLogObject.setLogID("");
                        realmLogObject.setDateTime(dateTime.getText().toString());
                        realmLogObject.setAlbumCompletedName(albumName.getText().toString());
                        realmLogObject.setTimeLength(timeLength.getText().toString());
                        realmLogObject.setWorkoutType(workoutType.getText().toString());
                        realmLogObject.setNote(notes.getText().toString());
                realm.commitTransaction();

                /*-----REALM WORK DONE----*/

                adapter.notifyDataSetChanged();

            }
        });












      adapter = new RealmRecyclerViewLogAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv_log_activity);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv.setAdapter(adapter);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (ttsValue == true) {
                    Bundle b = getIntent().getExtras();
                    Intent i = getIntent();
                    String workoutCompleteAnnouncement = "";
                    if (i.hasExtra("WORKOUT_COMPLETE"))
                        workoutCompleteAnnouncement = b.getString("WORKOUT_COMPLETE");
                    tts.speak(workoutCompleteAnnouncement, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();

        RealmResults<Log> events = realm.where(Log.class).findAll();
        RealmLogModelAdapter realmAdapter = new RealmLogModelAdapter(getBaseContext(), events, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //check if tts is enabled
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
       // Remember to close Realm when done.
    }


    public void makeEditTextDrawerVisible(View view){


    }




}
