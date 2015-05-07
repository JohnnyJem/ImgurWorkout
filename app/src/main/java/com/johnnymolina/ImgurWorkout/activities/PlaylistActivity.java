package com.johnnymolina.ImgurWorkout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.customViews.CustomViewPager;
import com.johnnymolina.ImgurWorkout.fragments.PlaylistFragment;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;

import io.realm.Realm;
import io.realm.RealmResults;


public class PlaylistActivity extends BaseActivity {

    private CustomViewPager pager;
    FrameLayout parent;
    LinearLayout thisActivity;
    String albumID;
    private Realm realm;
    int albumSize;
    int chronoTime;
    TextToSpeech tts;
    String textTitle;
    CountDownTimer timer;
    CountDownTimer timer2;
    TextView countDownText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(this);

        parent = (FrameLayout) findViewById(R.id.placeholder);
        thisActivity = (LinearLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_playlist_pager, null);
        parent.addView(thisActivity);



    }


    @Override
    protected void onResume() {
        super.onResume();
        Bundle b = getIntent().getExtras();
        albumID = "";
        albumID = b.getString("ALBUM_ID");
        chronoTime = b.getInt("CHRONO_TIME");

        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", albumID)
                .findAll();


        RealmResults<ImgurImage> albumImages = realm.where(ImgurImage.class)
                .contains("album", albumID, false)
                .findAll();

        albumSize = albumImages.size();

        CharSequence albumTitle = albumQuery.get(0).getTitle().toString();

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

    }

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

    public void changePage(int currentPosition) {

        int position = pager.getCurrentItem();
        int totalPositions = pager.getAdapter().getCount();
        if (position + 1 == totalPositions) {
            Toast.makeText(this, "Workout Complete", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getBaseContext(),MainLibraryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("WORKOUT_COMPLETE", "Workout Complete");
            startActivity(intent);
            finish();


        } else {
            if (currentPosition == pager.getCurrentItem())
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }
    }

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
                        changePage(currentPosition);

                    }
                }.start();
            }
        }.start();

    }

    public void closeCountDown(){
            timer.cancel();
        if (timer2!=null)
            timer2.cancel();

    }

    public void firstExecution(){
        if (pager.getCurrentItem() == 0) {
            countDown((TextView) pager.getChildAt(0).findViewById(R.id.count_down_timer),(TextView) pager.getChildAt(0).findViewById(R.id.count_down_text));

        }
    }

}





