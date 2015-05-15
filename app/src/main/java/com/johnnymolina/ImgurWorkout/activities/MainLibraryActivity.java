package com.johnnymolina.ImgurWorkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.adapters.ImgurLibraryAlbumAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmImgurAlbumAdapter;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainLibraryActivity extends BaseActivity {

    private Realm realm;
    private ImgurLibraryAlbumAdapter adapter;
    FrameLayout parent;
    RelativeLayout activityLibrary;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        activityLibrary= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_library, null);
        parent.addView(activityLibrary);
        realm = Realm.getInstance(this);

        adapter = new ImgurLibraryAlbumAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
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


        //SearchView configuration

        final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
            searchEditText.clearFocus();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                RealmResults<ImgurAlbum> events = realm.where(ImgurAlbum.class)
                        .contains("title",searchEditText.getText().toString(),false)
                        .findAll();

                RealmImgurAlbumAdapter realmAdapter = new RealmImgurAlbumAdapter(getBaseContext(), events, true);
                // Set the data and tell the RecyclerView to draw
                adapter.setRealmAdapter(realmAdapter);
                adapter.notifyDataSetChanged();

                if (searchEditText.getText().toString().equals("")) {
                    RealmResults<ImgurAlbum> events2 = realm.where(ImgurAlbum.class).findAll();
                    RealmImgurAlbumAdapter realmAdapter2 = new RealmImgurAlbumAdapter(getBaseContext(), events2, true);
                    // Set the data and tell the RecyclerView to draw
                    adapter.setRealmAdapter(realmAdapter2);
                    adapter.notifyDataSetChanged();
                }

            }
        });






    }





    @Override
    public void onResume() {
        super.onResume();

        RealmResults<ImgurAlbum> events = realm.where(ImgurAlbum.class).findAll();
        RealmImgurAlbumAdapter realmAdapter = new RealmImgurAlbumAdapter(getBaseContext(), events, true);
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
        realm.close(); // Remember to close Realm when done.
    }

}
