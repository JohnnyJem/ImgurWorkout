package com.johnnymolina.imgurworkout.activities;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.johnnymolina.imgurworkout.R;

import com.johnnymolina.imgurworkout.adapters.RealmLogModelAdapter;
import com.johnnymolina.imgurworkout.adapters.RealmRecyclerViewLogAdapter;
import com.johnnymolina.imgurworkout.customViews.SimpleDividerItemDecoration;
import com.johnnymolina.imgurworkout.network.model.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class LogActivity extends BaseActivity {

    private Realm realm;
   // private LogAdapter adapter;
   private RealmRecyclerViewLogAdapter adapter;
    RelativeLayout activityLibrary;
    TextToSpeech tts;


    //ButterKnife Injections
    @Bind(R.id.log_edit_texts_drawer)      LinearLayout editTextDrawer;
    @Bind(R.id.log_edit_text_DateTime)     EditText dateTime;
    @Bind(R.id.log_edit_text_album_name)   EditText albumName;
    @Bind(R.id.log_edit_text_time_length)  EditText timeLength;
    @Bind(R.id.log_edit_text_workout_type) EditText workoutType;
    @Bind(R.id.log_edit_text_notes)        EditText notes;
    @Bind(R.id.fab_log_open)               View fabLogOpen;
    @Bind(R.id.fab_log_add)                View fabLogAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLibrary= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_log, null);
        parent.addView(activityLibrary);
        ButterKnife.bind(this);

        realm = Realm.getInstance(this);


/*------Setting all the edit text boxs to respond to the softkeyboard enter---*/
        dateTime.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            dateTime.clearFocus();
                            workoutType.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        workoutType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            workoutType.clearFocus();
                            albumName.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        albumName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            albumName.clearFocus();
                            timeLength.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        timeLength.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            timeLength.clearFocus();
                            notes.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        notes.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            fabLogAdd.callOnClick();
                            //forces softkeyboard to close on submission
                            InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        //Adapter Setup
        adapter = new RealmRecyclerViewLogAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv_log_activity);
        LinearLayoutManager rvLayoutManager = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(rvLayoutManager);
        rvLayoutManager.setReverseLayout(true);
        rvLayoutManager.setStackFromEnd(true);
        rv.setItemAnimator(null);
        rv.addItemDecoration(new SimpleDividerItemDecoration(getBaseContext()));
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

        RealmResults<Log> events = realm.where(Log.class).findAllSorted("logID", true);
        RealmLogModelAdapter realmAdapter = new RealmLogModelAdapter(getBaseContext(), events, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
/*-----Grabbing data from an incoming completed workout intent and attaching data to editTextViews--------*/
        Bundle b= null;
       if(getIntent().hasExtra("Playlist Bundle")){

             b =getIntent().getExtras().getBundle("Playlist Bundle");
        }


        if (b != null){
            String dateTimeIntent = b.getString("dateTime");
            String albumNameIntent = b.getString("albumName");
            int timeStart = b.getInt("startTime");

            int endTime = (int) (System.currentTimeMillis()/1000);
            int differenceTime = (endTime - timeStart);
            int totalSecs = differenceTime;
            int hours = totalSecs / 3600;
            int  minutes = (totalSecs % 3600) / 60;
            int seconds = totalSecs % 60;

            String timeString = String.format("%02d : %02d : %02d", hours, minutes, seconds);

            fabLogOpen.callOnClick();

            dateTime.setText(dateTimeIntent);
            albumName.setText(albumNameIntent);
            timeLength.setText(""+timeString);
            workoutType.setText("");
            notes.setText("");
        }

        /*-----Grabbing data and attaching to edit text completed-------*/



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
        ButterKnife.unbind(this);
        realm.close();
       // Remember to close Realm when done.
    }



        @OnClick(R.id.fab_log_open)
        public void onLogOpenClick(View v) {
            editTextDrawer.setVisibility(View.VISIBLE);
            fabLogOpen.setVisibility(View.GONE);
            fabLogAdd.setVisibility(View.VISIBLE);
            dateTime.setText("");
            albumName.setText("");
            timeLength.setText("");
            workoutType.setText("");
            notes.setText("");
        }

       @OnClick(R.id.fab_log_add)
        public void onLogAddClick(View v) {
            editTextDrawer.setVisibility(View.GONE);
            fabLogOpen.setVisibility(View.VISIBLE);
            fabLogAdd.setVisibility(View.GONE);

            if (dateTime.getText().toString().matches("") || albumName.getText().toString().matches("") || timeLength.getText().toString().matches("") || workoutType.getText().toString().matches("") || notes.getText().toString().matches("")) {
                Toast.makeText(getBaseContext(),"Please fill out all fields",Toast.LENGTH_SHORT).show();
            }
            else{
                /*----REALM WORK START. TO STORE EDIT TEXT VIEWS TEXT---*/
                realm.beginTransaction();
                Log realmLogObject = realm.createObject(Log.class);
                //increment index
                int nextID = (int) (realm.where(Log.class).maximumInt("logID") + 1);

                realmLogObject.setLogID(nextID);
                realmLogObject.setDateTime(dateTime.getText().toString());
                realmLogObject.setAlbumCompletedName(albumName.getText().toString());
                realmLogObject.setTimeLength(timeLength.getText().toString());
                realmLogObject.setWorkoutType(workoutType.getText().toString());
                realmLogObject.setNote(notes.getText().toString());
                realm.commitTransaction();
                /*-----REALM WORK DONE so update the adapter and clear the Edit TextViews----*/
                dateTime.setText("");
                albumName.setText("");
                timeLength.setText("");
                workoutType.setText("");
                notes.setText("");
                adapter.notifyDataSetChanged();
            }
        }

}
