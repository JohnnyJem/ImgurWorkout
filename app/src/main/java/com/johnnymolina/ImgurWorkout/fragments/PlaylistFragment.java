package com.johnnymolina.imgurworkout.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.johnnymolina.imgurworkout.R;
import com.johnnymolina.imgurworkout.activities.PlaylistActivity;
import com.johnnymolina.imgurworkout.network.model.ImgurImage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.Icicle;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */

public class PlaylistFragment extends Fragment {

    //Objects
    Realm realm;
    Context context;
    RealmResults<ImgurImage> albumImages;
    Map<Integer,Integer> mRepsReferenceMap;
    Map<Integer,Integer> mRepsandSetsMap;
    CountDownTimer timer;
    Iterator it;
    Bundle saved;

    //Variables
    String imagesAlbumID;
    int imagesFragmentPosition;
    int startTime;
    @Icicle int spinnerCount;
    @Icicle int spinnerTotal;
    @Icicle int time;
    @Icicle int timeRest;
    String imageLink;
    Boolean booleanAutoContinue = true;

    //Butterknife Injections
    CardView cardViewFragment;
    @Bind(R.id.count_down_timer) TextView repsAndTimeCountDown;
    @Bind(R.id.count_down_rest) TextView restCountDown;
    @Bind(R.id.card_title_text) TextView imageTitle;
    @Bind(R.id.card_description_text) TextView imageDescription;
    @Bind(R.id.card_image_view) ImageView image;
    @Bind(R.id.set_count) TextView setCount;
    @Bind(R.id.set_total) TextView setTotal;

    public PlaylistFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflation of views
        cardViewFragment =(CardView) inflater.inflate(R.layout.fragment_card_playlist, container, false);
        ButterKnife.bind(this, cardViewFragment);
        context = cardViewFragment.getContext();
        //Return the finished fragment
          return cardViewFragment;
    }


    //Used to setup each new fragment created.
    public static PlaylistFragment newInstance(int position,String albumID){
        PlaylistFragment frag = new PlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ALBUM_ID", albumID);
        bundle.putInt("POSITION", position); //pass the position
        frag.setArguments(bundle);
        //return a set-up fragment
        return(frag);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        realm = Realm.getInstance(context);

        //Grabbing bundle Args
        Bundle args = getArguments();
        imagesFragmentPosition = args.getInt("POSITION");  //where do we get this again?
        imagesAlbumID = args.getString("ALBUM_ID");
        startTime = args.getInt("startTime");
        //tagging the image of this fragment for tts speech to access it from activity.
        imageTitle.setTag(imagesFragmentPosition);

        //Find all realm image objects based on retrieved albumID
        albumImages = realm.where(ImgurImage.class)
                .contains("album",imagesAlbumID, false)
                .findAll();


        if (savedInstanceState == null) {
            time = albumImages.get(imagesFragmentPosition).getSlideValue();
            timeRest = albumImages.get(imagesFragmentPosition).getRestValue() * 30 * 1000;
        }

        if (savedInstanceState== null) {
            if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
                //Mapping in references of whether or not each spinner holds a rep higher than 0. To be pulled out later.
                mRepsReferenceMap = new LinkedHashMap<>();
                mRepsReferenceMap.put(1, albumImages.get(imagesFragmentPosition).getSpinner1());
                mRepsReferenceMap.put(2, albumImages.get(imagesFragmentPosition).getSpinner2());
                mRepsReferenceMap.put(3, albumImages.get(imagesFragmentPosition).getSpinner3());
                mRepsReferenceMap.put(4, albumImages.get(imagesFragmentPosition).getSpinner4());
                mRepsReferenceMap.put(5, albumImages.get(imagesFragmentPosition).getSpinner5());
                //place all the nonzero spinner amounts that we stored in mRepsReferenceMap into mRepsandSetsMap
                mRepsandSetsMap = new LinkedHashMap<>();
                it = mRepsReferenceMap.entrySet().iterator();
                int currentSet = 1;

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    int value = (int) pair.getValue();
                    if (value != 0) {
                        mRepsandSetsMap.put(currentSet, value);
                        currentSet++;
                        it.remove();
                        mRepsReferenceMap.remove(pair.getKey());
                    }
                }
                spinnerTotal = mRepsandSetsMap.size();
                spinnerCount = 1;
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (imagesFragmentPosition == 0) {
            if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
                if (spinnerTotal > 0) {
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
                    setCount.setText("Set " + spinnerCount + " of ");
                    setTotal.setText("" + spinnerTotal);
                    if (mRepsandSetsMap.get(spinnerCount)!=null) {
                        repsAndTimeCountDown.setText(mRepsandSetsMap.get(spinnerCount) + " Reps");
                    }
                } else {
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
                }
            } else {
                if (timer == null) {
                    repsAndTimeCountDown.setText("Ready to Countdown");
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);

                } else {
                    countDownTimerTimed();
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
                }
            }
        }


        //set image Title
        if(!albumImages.get(imagesFragmentPosition).getTitle().contains("null")) {
            imageTitle.setText(albumImages.get(imagesFragmentPosition).getTitle());
        }
        //Set image Description
        if (!albumImages.get(imagesFragmentPosition).getDescription().contains("null")){
            imageDescription.setText(albumImages.get(imagesFragmentPosition).getDescription());
        }
        //grab sys image link
        if (albumImages.get(imagesFragmentPosition).getSysLink().equals("null")){
            imageLink ="file:///data/data/com.johnnymolina.imgurworkout/files/"+ albumImages.get(imagesFragmentPosition).getLink().substring(albumImages.get(imagesFragmentPosition).getLink().lastIndexOf('/') + 1);
        }else{
            imageLink= albumImages.get(imagesFragmentPosition).getLink();
        }

        //start with Glide
        Glide.with(context)
                .load(imageLink)
                .thumbnail(0.2f)
                .crossFade(3)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.imageplaceholder)
                .into(image);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

if(isVisibleToUser && isResumed()) {
    if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
        if (spinnerTotal > 0 && spinnerCount<spinnerTotal) {
            //Log.i("spinnerTotal>0", String.valueOf(spinnerTotal));
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            setCount.setText("Set " + spinnerCount + " of ");
            setTotal.setText("" + spinnerTotal);
            if (mRepsandSetsMap.get(spinnerCount)!=null) {
                repsAndTimeCountDown.setText(mRepsandSetsMap.get(spinnerCount) + " Reps");
            }
        } else {
            setCount.setText("Set " + spinnerCount + " of ");
            setTotal.setText("" + spinnerTotal);
            if (mRepsandSetsMap.get(spinnerCount)!=null) {
                repsAndTimeCountDown.setText(mRepsandSetsMap.get(spinnerCount) + " Reps");
            }
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
        }
    } else {
        if (timer==null) {
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            if (booleanAutoContinue){
                countDownTimerCountdown();
            }
        }else{
            countDownTimerTimed();
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
        }
    }
}

    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer!=null){
            timer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        realm.close(); // Remember to close Realm when done.
        ButterKnife.unbind(this);
    }




    /*--------------------Some methods called from the PlaylistActivity to update views--*/
    public void countDownRest(){

        if (timer!=null){
            timer.cancel();
        }

        image.setVisibility(View.INVISIBLE);

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
        repsAndTimeCountDown.setText("");

        timer= new CountDownTimer(timeRest, 350) {
            public void onTick(long millisUntilFinished) {
                restCountDown.setText("Resting for " + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
            }

            public void onFinish() {
                image.setVisibility(View.VISIBLE);
                restCountDown.setText("");
                spinnerCount++;
                if (mRepsandSetsMap.get(spinnerCount)!=null) {
                    repsAndTimeCountDown.setText(mRepsandSetsMap.get(spinnerCount) + " Reps");
                }

                if ( spinnerCount >= spinnerTotal ){
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
                  if (spinnerTotal!= 1  && spinnerCount !=0)
                    setCountText();
                }else{
                    setCountText();
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
                }

            }
        }.start();
    }


    public void countDownRestLast(){

        if (timer!=null){
            timer.cancel();
        }

        if(spinnerTotal!=1 && spinnerTotal !=0 || !albumImages.get(imagesFragmentPosition).isSwitchValue()) {
            image.setVisibility(View.INVISIBLE);

            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            repsAndTimeCountDown.setText("");
            setCount.setText("");
            setTotal.setText("");

            if (imagesFragmentPosition + 1 != albumImages.size()) {
                imageTitle.setText("Up Next: " + (albumImages.get(imagesFragmentPosition + 1).getTitle()));

            }
            timer = new CountDownTimer(timeRest, 350) {
                public void onTick(long millisUntilFinished) {
                    restCountDown.setText("Resting for " + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
                    imageDescription.setText("");
                }

                public void onFinish() {
                    image.setVisibility(View.VISIBLE);
                    restCountDown.setText("");

                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
                    ((PlaylistActivity) getActivity()).changePage(imagesFragmentPosition);
                }
            }.start();
        }else{
            ((PlaylistActivity) getActivity()).changePage(imagesFragmentPosition);
        }
    }



    public void countDownTimerTimed(){

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
        if (time >1) {
            if (timer!=null){
                timer.cancel();
            }

           timer = new CountDownTimer(time * 1000, 350) {
                public void onTick(long millisUntilFinished) {
                    repsAndTimeCountDown.setText("" + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
                    time = Math.round(millisUntilFinished * 0.001f);
                }
                public void onFinish() {
                    repsAndTimeCountDown.setText("");
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
                    ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
                if (booleanAutoContinue){
                    countDownRestLast();
                }
                }
            }.start();
        }else {
            repsAndTimeCountDown.setText("");
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
        }
    }

    public void countDownTimerCountdown() {
        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

        if (time > 1) {
            if (timer != null) {
                timer.cancel();
            }
        }
            if (booleanAutoContinue) {
                ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

                timer = new CountDownTimer(6 * 1000, 350) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (Math.round(millisUntilFinished * 0.001f)==6){
                            repsAndTimeCountDown.setText("READY");
                        }
                        if (Math.round(millisUntilFinished * 0.001f)==4){
                            repsAndTimeCountDown.setText("SET");
                        }
                        if (Math.round(millisUntilFinished * 0.001f)==2){
                            repsAndTimeCountDown.setText("GO!");
                        }


                    }

                    @Override
                    public void onFinish() {
                        countDownTimerTimed();
                    }
                }.start();
            }else{
                countDownTimerTimed();
            }

    }

    public void setCountText(){
        setCount.setText("Set " + spinnerCount + " of ");
    }

}

