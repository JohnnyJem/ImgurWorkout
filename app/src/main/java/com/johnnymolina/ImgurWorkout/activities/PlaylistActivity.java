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
import java.util.Locale;

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
    String timeString;
    TextToSpeech tts;
    String textTitle;
    CountDownTimer timer;
   public String albumTitleIntent;
    String countDownText;
    TextView toolbarRightTextView;
    View fabPlayNow;
    View fabGoNext;
    int timesCalled = 0;
    TextView setCount;
    TextView setTotal;
    TextView currentSetReps;
    int spinnerTotal;
    int spinnerCount;
    boolean spinner1Miss = false;
    boolean spinner2Miss = false;
    boolean spinner3Miss = false;
    boolean spinner4Miss = false;
    boolean spinner5Miss = false;
    int spinnerSwitch = 2;
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
         spinnerCount = 1;
         spinnerTotal = 0;


    }



    @Override
    protected void onResume() {
        super.onResume();


/*--Grabbing the bundle from libraryAlbumViewerActivity.class ---*/
        Bundle b = getIntent().getExtras();
        albumID = "";
        albumID = b.getString("ALBUM_ID");
        chronoTime = b.getInt("CHRONO_TIME");
        timeString = b.getString("timestring");


  //using the Bundle info to lookup the appropriate album and its images we will be using.
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", albumID)
                .findAll();

       albumImages = realm.where(ImgurImage.class)
                .contains("album", albumID, false)
                .findAll();


        //total numer of images we will be displaying. 1 page per image.
        albumSize = albumImages.size();

        //set the amount of reps

        toolbarRightTextView.setText(1 +" of "+albumSize );

        CharSequence albumTitle = albumQuery.get(0).getTitle().toString();
        albumTitleIntent = albumTitle.toString();

//Making the layout that displays our updating slide position/total where the total is based on the amount of images we have.
        toolbarRightTextView.setVisibility(View.VISIBLE);
        this.getSupportActionBar().setTitle(albumTitle);


        //SETTING UP VIEWPAGER AND ADAPTER
        pager = (CustomViewPager) findViewById(R.id.view_pager);
        PlaylistFragmentAdapter playlistFragmentAdapter = new PlaylistFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(playlistFragmentAdapter);
        playlistFragmentAdapter.notifyDataSetChanged();
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(5);


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
                spinnerSwitch = 2;
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

                if(albumImages.get(pager.getCurrentItem()).isSwitchValue()) {
                    spinnerCount++;
                    setCount.setText("Set " + spinnerCount + " of ");
                    final ImageView image = (ImageView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.card_image_view);
                    final TextView restCountDown = (TextView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.count_down_rest);
                    image.setVisibility(View.INVISIBLE);
                    countDownRest(restCountDown, image);
                }else{
                   final TextView timerCountDown = (TextView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.count_down_timer);
                    countDownTimerTimed(timerCountDown);
                }


            }
        });

        fabGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    changePage(pager.getCurrentItem());
            }
        });

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

/*--Settting our Fragment Adapter class--------------------------------------*/
    class PlaylistFragmentAdapter extends FragmentStatePagerAdapter {
        public PlaylistFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (PlaylistFragment.newInstance(position, albumID, chronoTime));
        }

        @Override
        public int getCount() {
            return (albumSize);
        }
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
    public void firstExecution(RealmResults<ImgurImage> albumImages){


        if (albumImages.get(pager.getCurrentItem()).isSwitchValue()) {
                fabGoNext.setVisibility(View.INVISIBLE);
                fabPlayNow.setVisibility(View.VISIBLE);
        }else{
                fabGoNext.setVisibility(View.VISIBLE);
                fabPlayNow.setVisibility(View.INVISIBLE);
            }

        if (albumImages.get(pager.getCurrentItem()).isSwitchValue()) {
            spinnerTotal = 0;
            spinnerCount = 1;

            if (albumImages.get(pager.getCurrentItem()).getSpinner1() != 0){
                spinnerTotal++;}
            else{
                spinner1Miss = true;
            }
            if (albumImages.get(pager.getCurrentItem()).getSpinner2() != 0) {
                spinnerTotal++;
            }else{
                spinner2Miss= true;
            }
            if (albumImages.get(pager.getCurrentItem()).getSpinner3() != 0) {
                spinnerTotal++;
            }else{
                spinner3Miss= true;
            }
            if (albumImages.get(pager.getCurrentItem()).getSpinner4() != 0) {
                spinnerTotal++;
            }else{
                spinner4Miss = true;
            }
            if (albumImages.get(pager.getCurrentItem()).getSpinner5() != 0) {
                spinnerTotal++;
            }else{
                spinner5Miss = true;
            }
            //THIS IS THE KEY TO GETTING THE VIEWS FROM THE CURRENT FRAGMENT. USE PAGER.GETCHILDAT.
            //TO SET A NEW ID TO THE TEXTVIEW THE PLAYLISTACTIVITY HAS REGISTERED. THE NEW ID WILL
            //NOW EQUAL THE ONE OF THE CURRENT PAGE!!
            setCount =(TextView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.rep_count);
            setTotal =(TextView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.rep_total);
            currentSetReps = (TextView) pager.getChildAt(pager.getCurrentItem()).findViewById(R.id.count_down_timer);


            if (albumImages.get(pager.getCurrentItem()).getSpinner1() == 0) {
                currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner1() + " Reps");
                spinnerTotal++;
            }else{
                currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner1() + " Reps");

            }

            setCount.setText("Set " + spinnerCount + " of ");
            setTotal.setText("" + spinnerTotal);
        }else {
            fabPlayNow.setVisibility(View.VISIBLE);
            fabGoNext.setVisibility(View.INVISIBLE);
        }


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
            b.putString("lengthTime", timeString);
            intent.putExtra("Playlist Bundle", b);
            startActivity(intent);
            finish();
        } else {
            if (currentPosition == pager.getCurrentItem())
                pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }




    public void countDownRest(TextView tv, ImageView img){
        final TextView countDownTimer = tv;
        final ImageView imageHideShow = img;
        int time = albumImages.get(pager.getCurrentItem()).getRestValue()*1000;
        fabPlayNow.setVisibility(View.INVISIBLE);
        fabGoNext.setVisibility(View.INVISIBLE);
        currentSetReps.setText("");
        timer= new CountDownTimer(time, 350) {
            public void onTick(long millisUntilFinished) {
                countDownTimer.setText("Resting for " + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
            }
            public void onFinish() {
              imageHideShow.setVisibility(View.VISIBLE);
                countDownTimer.setText("");

                switch (spinnerSwitch) {
                    case 1:
                        if (albumImages.get(pager.getCurrentItem()).getSpinner1() != 0){
                        currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner1()+" Reps");
                        break;}

                    case 2:
                        if (albumImages.get(pager.getCurrentItem()).getSpinner2() != 0){
                        currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner2()+" Reps");
                            spinnerSwitch++;
                        break;}else {
                            spinnerSwitch = 3;
                        }

                    case 3:
                        if (albumImages.get(pager.getCurrentItem()).getSpinner3() != 0 ){
                        currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner3()+" Reps");
                            spinnerSwitch++;
                        break;}else{
                            spinnerSwitch++;
                        }

                    case 4:
                        if (albumImages.get(pager.getCurrentItem()).getSpinner4() != 0 ){
                            currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner4()+" Reps");
                            spinnerSwitch++;
                            break;}else{
                            spinnerSwitch++;
                        }

                    case 5:
                        if (albumImages.get(pager.getCurrentItem()).getSpinner5() != 0){
                            currentSetReps.setText(albumImages.get(pager.getCurrentItem()).getSpinner5()+" Reps");
                            break;}

                    default: currentSetReps.setText("");
                        break;
                }





                if ( spinnerCount >= spinnerTotal ){
                    fabPlayNow.setVisibility(View.INVISIBLE);
                    fabGoNext.setVisibility(View.VISIBLE);
                }else{
                    fabPlayNow.setVisibility(View.VISIBLE);
                }
            }
        }.start();
    }

    public void countDownTimerTimed(TextView tv){
        fabPlayNow.setVisibility(View.INVISIBLE);
        final TextView countDownTimer = tv;
        int time = albumImages.get(pager.getCurrentItem()).getSlideValue();
        if (time == 0 )
            time = 10;

        timer= new CountDownTimer(time*1000, 350) {
            public void onTick(long millisUntilFinished) {
                countDownTimer.setText("" + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
            }
            public void onFinish() {
                countDownTimer.setText("");
                fabGoNext.setVisibility(View.VISIBLE);
            }
        }.start();
    }



}





