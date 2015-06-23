package com.johnnymolina.imgurworkout.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.johnnymolina.imgurworkout.R;
import com.johnnymolina.imgurworkout.adapters.RealmRecyclerViewImgurAlbumAdapter;
import com.johnnymolina.imgurworkout.adapters.RealmImgurAlbumModelAdapter;
import com.johnnymolina.imgurworkout.customViews.SimpleDividerItemDecoration;
import com.johnnymolina.imgurworkout.network.model.ImgurAlbum;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainLibraryActivity extends BaseActivity {

    private Realm realm;
    private RealmRecyclerViewImgurAlbumAdapter adapter;
    FrameLayout parent;
    RelativeLayout activityLibrary;
    TextToSpeech tts;


    //Butterknife Injections
    @InjectView(R.id.search_edit_text) EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (FrameLayout) findViewById(R.id.placeholder);
        activityLibrary= (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_library, null);
        parent.addView(activityLibrary);
        realm = Realm.getInstance(this);
        ButterKnife.inject(this);

        //Adapter configuration
        adapter = new RealmRecyclerViewImgurAlbumAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv.addItemDecoration(new SimpleDividerItemDecoration(getBaseContext()));
        rv.setAdapter(adapter);

        //SearchView configuration
        searchEditText.clearFocus();
    }


    @Override
    public void onResume() {
        super.onResume();
        RealmResults<ImgurAlbum> events = realm.where(ImgurAlbum.class).findAll();
        RealmImgurAlbumModelAdapter realmAdapter = new RealmImgurAlbumModelAdapter(getBaseContext(), events, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();

        realm.close(); // Remember to close Realm when done.
        //check if tts is enabled
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    //Method init when text is changed in EditText
    @OnTextChanged(value = R.id.search_edit_text,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void OnAfterTextChanged(CharSequence text){
        RealmResults<ImgurAlbum> events = realm.where(ImgurAlbum.class)
                .contains("title",searchEditText.getText().toString(),false)
                .findAll();

        RealmImgurAlbumModelAdapter realmAdapter = new RealmImgurAlbumModelAdapter(getBaseContext(), events, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();

        if (searchEditText.getText().toString().equals("")) {
            RealmResults<ImgurAlbum> events2 = realm.where(ImgurAlbum.class).findAll();
            RealmImgurAlbumModelAdapter realmAdapter2 = new RealmImgurAlbumModelAdapter(getBaseContext(), events2, true);
            // Set the data and tell the RecyclerView to draw
            adapter.setRealmAdapter(realmAdapter2);
            adapter.notifyDataSetChanged();
        }
    }

}
