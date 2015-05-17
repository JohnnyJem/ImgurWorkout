package com.johnnymolina.ImgurWorkout.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.adapters.RealmRecyclerViewImgurImagesAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmRecyclerViewLogAdapter;
import com.johnnymolina.ImgurWorkout.adapters.RealmImgurImageModelAdapter;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.rey.material.widget.Slider;

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
        parent = (FrameLayout) findViewById(R.id.placeholder);
        activityLibraryAlbumViewer= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_library_album_viewer, null);
        parent.addView(activityLibraryAlbumViewer);
        realm = Realm.getInstance(this);
        adapter = new RealmRecyclerViewImgurImagesAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rvimage);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv.setAdapter(adapter);
        //temporary album ID

        slider = (Slider) findViewById(R.id.slider_sl_discrete);


    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle b = getIntent().getExtras();
        albumID ="";
         albumID = b.getString("ALBUM_ID");

      RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                                                 .equalTo("id", albumID)
                                                 .findAll();


        final RealmResults<ImgurImage> albumImages = realm.where(ImgurImage.class)
                .contains("album",albumID, false)
                .findAll();
        albumSize = albumImages.size();

        this.getSupportActionBar().setTitle(albumQuery.get(0).getTitle());

        RealmImgurImageModelAdapter realmAdapter = new RealmImgurImageModelAdapter(getBaseContext(), albumImages, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }



    public void play(View view){
       Context context= view.getContext();
       String albumIdIntentString = albumID;
       String ALBUM_ID = "ALBUM_ID";
       String CHRONO_TIME = "CHRONO_TIME";
        int chronoTime = slider.getValue();
        int totalSecs = slider.getValue()*albumSize;

        int hours = totalSecs / 3600;
        int  minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d : %02d : %02d", hours, minutes, seconds);
        Toast.makeText(getBaseContext(),""+ timeString ,Toast.LENGTH_LONG).show();
        final Intent intent = new Intent(this,PlaylistActivity.class);
        intent.putExtra(ALBUM_ID, albumIdIntentString);
        intent.putExtra(CHRONO_TIME, chronoTime);
        intent.putExtra("timestring", timeString);
        context.startActivity(intent);

    }
}
