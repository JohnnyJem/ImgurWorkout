package com.johnnymolina.imgurworkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.johnnymolina.imgurworkout.R;
import com.johnnymolina.imgurworkout.adapters.RealmRecyclerViewImgurAlbumAdapter;
import com.johnnymolina.imgurworkout.adapters.RealmImgurAlbumModelAdapter;
import com.johnnymolina.imgurworkout.customViews.SimpleDividerItemDecoration;
import com.johnnymolina.imgurworkout.network.model.ImgurAlbum;
import com.johnnymolina.imgurworkout.network.model.ImgurImage;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainLibraryActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private Realm realm;
    private RealmRecyclerViewImgurAlbumAdapter adapter;
    RelativeLayout activityLibrary;
    TextToSpeech tts;
    Future<File> downloading;
    HashMap<Integer,String> url_link_map;
    HashMap<String,String> url_maps;

    //Butterknife Injections
    @Bind(R.id.search_edit_text) EditText searchEditText;
    @Bind(R.id.slider) SliderLayout mDemoSlider;
    @Bind(R.id.custom_indicator) PagerIndicator pagerIndicator;
    @Bind(R.id.add_button) ImageButton addToolbarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLibrary= (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_library, null);
        parent.addView(activityLibrary);
        realm = Realm.getInstance(this);
        ButterKnife.bind(this);




        url_maps = new HashMap<String, String>();
        url_maps.put("Fundamental Bodyweight Exercises","http://i.imgur.com/ZcwYxMJb.jpg");
        url_maps.put("Undoing the Damage of Sitting", "http://i.imgur.com/ppTmboSb.jpg");
        url_maps.put("Dynamic Stretches", "http://i.imgur.com/CyTh5KEb.jpg");
        url_maps.put("Simple Ab Workout", "http://i.imgur.com/GHaKpOab.jpg");

        url_link_map = new HashMap<Integer, String>();
        url_link_map.put(1,"http://imgur.com/a/oP05D");
        url_link_map.put(2,"http://imgur.com/a/0HVsD");
        url_link_map.put(3,"http://imgur.com/a/8nYFm");
        url_link_map.put(4,"http://imgur.com/a/Y6xmq");


        int mapPosition =1;
        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name).toString().replace("\"", ""))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            textSliderView.getBundle()
                    .putString("link", url_link_map.get(mapPosition));
            mapPosition++;
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
       // mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);



        //Adapter configuration
        adapter = new RealmRecyclerViewImgurAlbumAdapter();
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        rv.addItemDecoration(new SimpleDividerItemDecoration(getBaseContext()));
        rv.setAdapter(adapter);

        //SearchView configuration
        searchEditText.clearFocus();

        addToolbarButton.setVisibility(View.VISIBLE);
    }

    @Override @OnClick(R.id.add_button)
    public void goToImgurImportActivity() {
        super.goToImgurImportActivity();
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
        //check if tts is enabled
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        realm.close(); // Remember to close Realm when done.
        ButterKnife.unbind(this);
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


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
        String httpLink= slider.getBundle().get("link").toString();
        final Intent intent = new Intent(this,Imgur.class);
        intent.putExtra("link",httpLink);
        this.startActivity(intent);
    }
    @Override
    public void onPageSelected(int position) {
        //Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.add_new_album) {
            goToImgurImportActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
