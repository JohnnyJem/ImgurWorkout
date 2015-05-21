package com.johnnymolina.ImgurWorkout.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.customViews.CustomViewPager;
import com.johnnymolina.ImgurWorkout.fragments.PlaylistFragment;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.johnnymolina.ImgurWorkout.network.model.Log;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    CountDownTimer timer2;
   public String albumTitleIntent;
    String countDownText;
    TextView toolbarRightTextView;
    View fabGoNext;
    int timesCalled = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(this);

        //setting up our layout
        parent = (FrameLayout) findViewById(R.id.placeholder);
        thisActivity = (RelativeLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_playlist_pager, null);
        parent.addView(thisActivity);
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
        timeString = b.getString("timestring");


  //using the Bundle info to lookup the appropriate album and its images we will be using.
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", albumID)
                .findAll();

        RealmResults<ImgurImage> albumImages = realm.where(ImgurImage.class)
                .contains("album", albumID, false)
                .findAll();

        //total numer of images we will be displaying. 1 page per image.
        albumSize = albumImages.size();

        //set the
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
                int countDown1 = pager.getCurrentItem()+1;
                int countDown2 = albumSize;
                countDownText = countDown1+" of "+countDown2;
                toolbarRightTextView.setText(countDownText);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        /*-----SETUP ON CLICK LISTENERS----*/
        fabGoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pager.getCurrentItem() + 1 == pager.getAdapter().getCount()) {
                   closeCountDown();
                    changePage(pager.getCurrentItem());
                    closeCountDown();
                }else {
                    closeCountDown();
                    changePage(pager.getCurrentItem());
                }



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






//Huge method that intializes each pages actions after each page change.
//Called at the end of our countdown Timer from method Countdown
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






    //Announces the title of each Image with text-to-speech
    public void announceTitle(String title){
          textTitle = title;
        if (ttsValue == true) {
            tts.speak(textTitle, TextToSpeech.QUEUE_FLUSH, null);
        }
    }




    public void countDown(TextView tv, TextView tvt){
        final TextView countDownText = tvt;
        final TextView countDownTimer2 = tv ;
        final int currentPosition = pager.getCurrentItem();
      timer= new CountDownTimer(6000, 350) {

            public void onTick(long millisUntilFinished) {
                countDownTimer2.setText("" + millisUntilFinished / 1000);
                if (countDownTimer2.getText().equals("2"))
                countDownText.setText("SET");
            }
            public void onFinish() {
               countDownText.setText("GO!");

             timer2 =  new CountDownTimer(chronoTime*1000, 500) {
                    public void onTick(long millisUntilFinished) {
                        performTick(millisUntilFinished);
                    }

                    private void performTick(long millisUntilFinished) {
                        countDownTimer2.setText("" + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
                    }

                    public void onFinish() {
                        /*TODO: Create and IF else statement here
                        *to change position based on our images attribute
                        * IF the image has a SET attribute do not allow this method to run.
                        * The user will have to manually move on to the next page via a button that
                        * checks if all sets have been completed.
                        * */
                        changePage(currentPosition);
                        closeCountDown();
                    }
                }.start();
            }
        }.start();

    }



    //forces each countDown methods dismissal once a page is not in a users view.
    //Still very buggy. Need to learn about Handlers and how to properly shut them down.
    public void closeCountDown(){
        if (timer!=null)
            this.timer.cancel();
        if (timer2!=null)
            this.timer2.cancel();
    }


    //special method that Forces Countdown start for first pageview
    public void firstExecution(){
        if (pager.getCurrentItem() == 0) {
            countDown((TextView) pager.getChildAt(0).findViewById(R.id.count_down_timer),(TextView) pager.getChildAt(0).findViewById(R.id.count_down_text));

        }
    }

}





