package com.johnnymolina.ImgurWorkout.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.adapters.RealmRecyclerViewImgurImagesAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmRecyclerViewLogAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmImgurImageModelAdapter;
import com.johnnymolina.ImgurWorkout.customViews.SimpleDividerItemDecoration;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;

public class LibraryAlbumViewerActivity extends BaseActivity {

    private Realm realm;
    private RealmRecyclerViewImgurImagesAdapter adapter;
    FrameLayout parent;
    RelativeLayout activityLibraryAlbumViewer;
    String albumID;
    Slider slider;
    int albumSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting up our layout
        parent = (FrameLayout) findViewById(R.id.placeholder);
        activityLibraryAlbumViewer= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_library_album_viewer, null);
        parent.addView(activityLibraryAlbumViewer);
        slider = (Slider) findViewById(R.id.slider_sl_discrete);


        //setting up our realm adapter
        realm = Realm.getInstance(this);
        adapter = new RealmRecyclerViewImgurImagesAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rvimage);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv.addItemDecoration(new SimpleDividerItemDecoration(getBaseContext()));
        rv.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();
/*------Grabbing data passed from mainLibraryActivity.class----*/
        Bundle b = getIntent().getExtras();
        albumID ="";
        albumID = b.getString("ALBUM_ID");//grabbing the AlbumID we clicked on MainLibraryActivity.java


  /*--Grabbing the exact album and images based on information passed from mainLibraryActivity.class----*/
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                                                 .equalTo("id", albumID)
                                                 .findAll();
        final RealmResults<ImgurImage> albumImages = realm.where(ImgurImage.class)
                .contains("album",albumID, false)
                .findAll();

        //The total # of images in our playlist album
        albumSize = albumImages.size();

        //set toolbarTitle
        this.getSupportActionBar().setTitle(albumQuery.get(0).getTitle());

        //Adding all of the images in our album to our Realm adapter and connecting it with our RecyclerviewAdapter.
        RealmImgurImageModelAdapter realmAdapter = new RealmImgurImageModelAdapter(getBaseContext(), albumImages, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }







 // This method prepares the data that will be sent to PlaylistActivity.java for initialization of its viewpages.
    public void play(View view){
       Context context= view.getContext();

  /*---Breaking down the total seconds that the album will take into hours, minutes, seconds for formatting.--*/
        int totalSecs = slider.getValue()*albumSize;
        int hours = totalSecs / 3600;
        int  minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

  /*----HERE WE SET THE VALUES BEING PASSED ON TO PlaylistActivity.java------*/
        String albumIdIntentString = albumID;
        String timeString = String.format("%02d : %02d : %02d", hours, minutes, seconds);
        int chronoTime = slider.getValue();


   /*---Bundling up the values to pass onto PlaylistActivity.class----*/
        final Intent intent = new Intent(this,PlaylistActivity.class);
        intent.putExtra("ALBUM_ID", albumIdIntentString); // albumID for playlistAcitivty to lookup.
        intent.putExtra("CHRONO_TIME", chronoTime); // the time that each viewpager will countdown from.
        intent.putExtra("timestring", timeString); // the value of the total time the workout will last to be anounced via toast.
        context.startActivity(intent);

       //Toast of the total time the excercise will take
        Toast.makeText(getBaseContext(),""+ timeString ,Toast.LENGTH_LONG).show();
    }
















}
