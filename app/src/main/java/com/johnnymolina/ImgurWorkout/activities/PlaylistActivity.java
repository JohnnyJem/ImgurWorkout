package com.johnnymolina.ImgurWorkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.customViews.CustomViewPager;
import com.johnnymolina.ImgurWorkout.fragments.PlaylistFragment;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;


public class PlaylistActivity extends BaseActivity {

    private CustomViewPager pager;
    FrameLayout parent;
    RelativeLayout thisActivity;
    String albumID;
    private Realm realm;
    int albumSize;
    int chronoTime;
    int startTime;
    TextToSpeech tts;
    String textTitle;

   public String albumTitleIntent;
    String countDownText;
    TextView toolbarRightTextView;
    View fabPlayNow;
    View fabGoNext;
    int timesCalled = 0;
    TextView setCount;
    TextView setTotal;
    int spinnerTotal;
    int spinnerCount;


    //Fragment stuff

    //end fragment stuff

    int insideSpinnerCount;
    RealmResults<ImgurImage> albumImages;
    RealmResults<ImgurAlbum> albumQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(this);
        //setting up our layout
        parent = (FrameLayout) findViewById(R.id.placeholder);
        thisActivity = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_playlist_pager, null);
        parent.addView(thisActivity);
        fabPlayNow = findViewById(R.id.fab_play_playlist_next);
        fabGoNext = findViewById(R.id.fab_go_next);
        toolbarRightTextView = (TextView) findViewById(R.id.tool_bar_right_text_view);
    }



    @Override
    protected void onResume() {
        super.onResume();
/*--Grabbing the bundle from libraryAlbumViewerActivity.class ---*/
        Bundle b = getIntent().getExtras();
        albumID = "";
        albumID = b.getString("ALBUM_ID");
        chronoTime = b.getInt("CHRONO_TIME");
        startTime = b.getInt("startTime");

  //using the Bundle info to lookup the appropriate album and its images we will be using.
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", albumID)
                .findAll();

       albumImages = realm.where(ImgurImage.class)
                .contains("album", albumID, false)
                .findAll();


        //total numer of images we will be displaying. 1 page per image.
        albumSize = albumImages.size();

        //Making the layout that displays our updating slide position/total where the total is based on the amount of images we have.
        toolbarRightTextView.setText(1 +" of "+albumSize );
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
        pager.setOffscreenPageLimit(2);


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

        //A listener that updates the textview in the toolbar on viewpager position out of total pages everytime we change pages.
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //this spinnerSwitch value is restarted everytime the page changes
                //in order for our countdown timer inner methods to work on every new slide.

                //setting up the total page countdown on the toolbar
                int countDown1 = pager.getCurrentItem() + 1;
                int countDown2 = albumSize;
                countDownText = countDown1 + " of " + countDown2;
                toolbarRightTextView.setText(countDownText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        /*-----SETUP ON CLICK LISTENERS----*/
        fabPlayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = pager.getCurrentItem();
                PlaylistFragmentAdapter adapter = ((PlaylistFragmentAdapter)pager.getAdapter());
                PlaylistFragment fragment = adapter.getFragment(index);

                if(albumImages.get(pager.getCurrentItem()).isSwitchValue()) {
                    fragment.countDownRest();

                }else{
                  fragment.countDownTimerTimed();
                }
            }
        });

        fabGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = pager.getCurrentItem();
                PlaylistFragmentAdapter adapter = ((PlaylistFragmentAdapter)pager.getAdapter());
                PlaylistFragment fragment = adapter.getFragment(index);
                    fragment.countDownRestLast();
            }
        });

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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    protected void onPause() {
        super.onPause();
        //check if tts is enabled
       if(tts!=null){
           tts.stop();
           tts.shutdown();
       }
        finish();

    }


/*--------------------Countdown timer method--------------------------------------------*/

/*----------------------------------UTILITY METHODS---------------------------------------------*/
    //Announces the title of each Image with text-to-speech
    public void announceTitle(String title){
        textTitle = title;
        if (ttsValue == true) {
            tts.speak(textTitle, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    //special method that Forces Countdown start for first pageview
    public void firstExecution(){

        int index = pager.getCurrentItem();
        PlaylistFragmentAdapter adapter = ((PlaylistFragmentAdapter)pager.getAdapter());
        PlaylistFragment fragment = adapter.getFragment(index);
        fragment.firstFragmentExecution();

    }



/*------------------------END UTILITY METHODS-------------------------------------------------*/

 /*--------------------------METHODS CALLED FROM THE FRAGMENT-------------------------------*/



    public void changePage(int currentPosition) {
        int position = pager.getCurrentItem();
        int totalPositions = pager.getAdapter().getCount();
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








}





