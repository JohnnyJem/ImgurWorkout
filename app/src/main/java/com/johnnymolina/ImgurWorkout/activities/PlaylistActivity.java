package com.johnnymolina.imgurworkout.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnymolina.imgurworkout.R;
import com.johnnymolina.imgurworkout.customViews.CustomViewPager;
import com.johnnymolina.imgurworkout.fragments.PlaylistFragment;
import com.johnnymolina.imgurworkout.network.model.ImgurAlbum;
import com.johnnymolina.imgurworkout.network.model.ImgurImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icicle;
import io.realm.Realm;
import io.realm.RealmResults;


public class PlaylistActivity extends BaseActivity {

    //Important
    public CustomViewPager pager;
    private RelativeLayout thisActivity;

    //ButterKnife Injections
   @Bind(R.id.tool_bar_right_text_view) TextView toolbarRightTextView;
   @Bind(R.id.fab_play_playlist_next) View fabPlayNow;
   @Bind(R.id.fab_go_next) View fabGoNext;

    //objects
    private TextToSpeech tts;
    private Realm realm;
    RealmResults<ImgurImage> albumImages;
    RealmResults<ImgurAlbum> albumQuery;

    //variables
    private String albumID;
    private String textTitle;
    private String albumTitleIntent;
    @Icicle String toolbarCountDownText;
    @Icicle int currentAlbPosToolbar;
    @Icicle int currentPage;
    private int albumSize;
    private int chronoTime;
    private int startTime;
    private int timesCalled = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_playlist_pager, null);
        parent.addView(thisActivity);
        ButterKnife.bind(this);
        realm = Realm.getInstance(this);

/*--Grabbing the bundle from libraryAlbumViewerActivity.class ---*/
        Bundle b = getIntent().getExtras();
        albumID = "";
        albumID = b.getString("ALBUM_ID");
        chronoTime = b.getInt("CHRONO_TIME");
        startTime = b.getInt("startTime");

        //using the Bundle info to lookup the appropriate album and its images we will be using.
        albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", albumID)
                .findAll();
        albumImages = realm.where(ImgurImage.class)
                .contains("album", albumID, false)
                .findAll();
        //total numer of images we will be displaying. 1 page per image.
        albumSize = albumImages.size();

        //restore saved state variables
        if (savedInstanceState!= null){
            toolbarRightTextView.setText(toolbarCountDownText);
        }else {
            toolbarRightTextView.setText("1 of "+albumSize);
        }

        //initialize text-to-speech
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (pager.getCurrentItem() == 0) {
                    if (ttsValue == true) {
                        String title = ((TextView) pager.getChildAt(0).findViewById(R.id.card_title_text)).getText().toString();
                        tts.speak(title, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });


        //Making the layout that displays our updating slide position/total where the total is based on the amount of images we have.toolbarRightTextView.setText(1 +" of "+albumSize );
        CharSequence albumTitle = albumQuery.get(0).getTitle().toString();
        albumTitleIntent = albumTitle.toString();
        toolbarRightTextView.setVisibility(View.VISIBLE);
        this.getSupportActionBar().setTitle(albumTitle);

        //SETTING UP VIEWPAGER AND ADAPTER
        pager = (CustomViewPager) findViewById(R.id.view_pager);
        PlaylistFragmentAdapter playlistFragmentAdapter = new PlaylistFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(playlistFragmentAdapter);
        playlistFragmentAdapter.notifyDataSetChanged();
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(1);

    }


    @Override
    protected void onResume() {
        super.onResume();



            //A listener that updates the textview in the toolbar on viewpager position out of total pages everytime we change pages.
            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                //setting up the total page countdown on the toolbar
                currentAlbPosToolbar = pager.getCurrentItem() + 1;
                toolbarCountDownText = currentAlbPosToolbar + " of " + albumSize;
                toolbarRightTextView.setText(toolbarCountDownText);
                String title = ((TextView) pager.findViewWithTag(pager.getCurrentItem())).getText().toString();
                tts.speak(title, TextToSpeech.QUEUE_FLUSH, null);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }




    //Here we call for our new fragments to be created.
    //We give them a position and albumID and use those parameters to create a new instance of Playlistfragment.
    class PlaylistFragmentAdapter extends FragmentStatePagerAdapter {

        private Map<Integer,PlaylistFragment> mPageReferenceMap = new HashMap<Integer, PlaylistFragment>();

        public PlaylistFragmentAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {

            PlaylistFragment myFragment = PlaylistFragment.newInstance(position, albumID);

            mPageReferenceMap.put(Integer.valueOf(position), myFragment);
            return myFragment;
        }

        @Override
        public int getCount() {
            return (albumSize);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(Integer.valueOf(position));
        }

        public PlaylistFragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
        /**
         * After an orientation change, the fragments are saved in the adapter, and
         * I don't want to double save them: I will retrieve them and put them in my
         * list again here.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PlaylistFragment fragment = (PlaylistFragment) super.instantiateItem(container,
                    position);

            mPageReferenceMap.put(position, fragment);
            return fragment;
        }
    }


 /*--------------------------METHODS CALLED FROM THE FRAGMENT-------------------------------*/
    public void changePage(int currentPosition) {
        int position = pager.getCurrentItem();
        int totalPositions = pager.getAdapter().getCount();

        //Check to see if we are on the last page or not
        if (position + 1 == totalPositions && timesCalled < 1) {
            timesCalled++;
            Toast.makeText(this, "Workout Complete", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(),LogActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("WORKOUT_COMPLETE", "Workout Complete");
            Bundle b = new Bundle();
            String currentDate = new SimpleDateFormat("EEE MMM d, hh a", Locale.US).format(new Date());
            String day = currentDate;
            b.putString("dateTime", day);
            b.putString("albumName", albumTitleIntent);
            b.putInt("startTime", startTime);
            intent.putExtra("Playlist Bundle", b);
            startActivity(intent);
            finish();
        } else {
            if (currentPosition == pager.getCurrentItem())
                pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

/*--ONCLICK METHODS----------------------------------------------------------------------------------------*/

    @OnClick(R.id.fab_play_playlist_next)
    public void onPlayPlaylistNextClick(View v) {
        int index = pager.getCurrentItem();
        PlaylistFragmentAdapter adapter = ((PlaylistFragmentAdapter)pager.getAdapter());
        PlaylistFragment fragment = adapter.getFragment(index);

        if(albumImages.get(pager.getCurrentItem()).isSwitchValue()) {
            fragment.countDownRest();
        }else{
            fragment.countDownTimerTimed();
        }
    }

    @OnClick(R.id.fab_go_next)
    public void onGoNextClick(View v) {
        int index = pager.getCurrentItem();
        PlaylistFragmentAdapter adapter = ((PlaylistFragmentAdapter)pager.getAdapter());
        PlaylistFragment fragment = adapter.getFragment(index);
        fragment.countDownRestLast();
    }



}





