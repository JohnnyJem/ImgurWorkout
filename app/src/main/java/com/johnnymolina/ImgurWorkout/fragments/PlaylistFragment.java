package com.johnnymolina.imgurworkout.fragments;


import android.app.Activity;
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
import com.johnnymolina.imgurworkout.network.model.ImgurAlbum;
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
    CountDownTimer timer;
    Iterator it;

    //Variables
    String imagesAlbumID;
    int chronoTime;
    int imagesFragmentPosition;
    int startTime;
    int spinnerTotal;
    @Icicle int spinnerCount;
    String imageLink;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflation of views
        cardViewFragment =(CardView) inflater.inflate(R.layout.fragment_card_playlist, container, false);
        ButterKnife.bind(this,cardViewFragment);
        context = cardViewFragment.getContext();

        //Variables
        //TODO:
        spinnerCount = 1;
        spinnerTotal = 0;

        //Grabbing bundle Args
        Bundle args = getArguments();
        imagesFragmentPosition = args.getInt("POSITION");  //where do we get this again?
        imagesAlbumID = args.getString("ALBUM_ID");
        startTime = args.getInt("startTime");
        //tagging the image of this fragment for tts speech to access it from activity.
        imageTitle.setTag(imagesFragmentPosition);

        //Find all realm image objects based on retrieved albumID
        realm = Realm.getInstance(context);
        albumImages = realm.where(ImgurImage.class)
                .contains("album",imagesAlbumID, false)
                .findAll();

        //set image Title
        if(albumImages.get(imagesFragmentPosition).getTitle().contains("null")){
            imageTitle.setText("");
        }else {
            imageTitle.setText(albumImages.get(imagesFragmentPosition).getTitle());
        }

        //Set image Description
        if (albumImages.get(imagesFragmentPosition).getDescription().contains("null")){
            imageDescription.setText("");
        }else {
            imageDescription.setText(albumImages.get(imagesFragmentPosition).getDescription());
        }
        //grab sys image link
        if (albumImages.get(imagesFragmentPosition).getSysLink() != "null"){
           imageLink ="file:///data/data/com.johnnymolina.imgurworkout/files/"+ albumImages.get(imagesFragmentPosition).getLink().substring(albumImages.get(imagesFragmentPosition).getLink().lastIndexOf('/') + 1);
        }else{
            imageLink= albumImages.get(imagesFragmentPosition).getLink().toString();
        }

        //start with Glide
        Glide.with(context)
                .load(imageLink)
                .thumbnail(0.2f)
                .crossFade(3)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.imageplaceholder)
                .into(image);

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



    /*--------------------------------Very important method that executes on each page---------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(realm == null) {
            realm = Realm.getInstance(context);
        }

        if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
        }else{
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);
        }


        if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
            //Mapping in references of whether or not each spinner holds a rep higher than 0. To be pulled out later.
            mRepsReferenceMap = new LinkedHashMap<Integer, Integer>();

            if (albumImages.get(imagesFragmentPosition).getSpinner1() != 0){
                spinnerTotal++;
                mRepsReferenceMap.put(1, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner1()));
            }else{
                mRepsReferenceMap.put(1, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner1()));
            }
            if (albumImages.get(imagesFragmentPosition).getSpinner2() != 0){
                spinnerTotal++;
                mRepsReferenceMap.put(2, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner2()));
            }else{
                mRepsReferenceMap.put(2, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner2()));
            }
            if (albumImages.get(imagesFragmentPosition).getSpinner3() != 0){
                spinnerTotal++;
                mRepsReferenceMap.put(3, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner3()));
            }else{
                mRepsReferenceMap.put(3, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner3()));
            }
            if (albumImages.get(imagesFragmentPosition).getSpinner4() != 0){
                spinnerTotal++;
                mRepsReferenceMap.put(4, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner4()));
            }else{
                mRepsReferenceMap.put(4, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner4()));
            }
            if (albumImages.get(imagesFragmentPosition).getSpinner5() != 0){
                spinnerTotal++;
                mRepsReferenceMap.put(5, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner5()));
            }else{
                mRepsReferenceMap.put(5, Integer.valueOf(albumImages.get(imagesFragmentPosition).getSpinner5()));
            }

            //Todo: Write a method that setsText for last spinner used.
            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner1() + " Reps");

            //goes through the Map and sets the Rep amount in the textview and then removes the key&value from the map.
            //An iterator goes through order...
            it = mRepsReferenceMap.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                int value = (int) pair.getValue();

                if ( value != 0){
                    repsAndTimeCountDown.setText(pair.getValue() + " Reps");
                    it.remove();
                    mRepsReferenceMap.remove(pair.getKey());
                    break;
                }

            }
            setCount.setText("Set " + spinnerCount + " of ");
            setTotal.setText("" + spinnerTotal);
        }else {
            repsAndTimeCountDown.setText("Ready to Countdown");
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        realm.close(); // Remember to close Realm when done.
        if(timer!=null){
            timer.cancel();
        }
    }

    /*--------------------------------------------------------------------------------------------------------*/

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }





    /*--------------------Some methods called from the PlaylistActivity to update views--*/
    public void countDownRest(){

        image.setVisibility(View.INVISIBLE);
        int time = albumImages.get(imagesFragmentPosition).getRestValue()*30*1000;

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
        repsAndTimeCountDown.setText("");

        timer= new CountDownTimer(time, 350) {
            public void onTick(long millisUntilFinished) {
                restCountDown.setText("Resting for " + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
            }

            public void onFinish() {
                image.setVisibility(View.VISIBLE);
                restCountDown.setText("");

                it = mRepsReferenceMap.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                   int value = (int) pair.getValue();

                    if (value != 0){
                        repsAndTimeCountDown.setText(pair.getValue() + " Reps");
                        it.remove();
                        mRepsReferenceMap.remove(pair.getKey());
                        break;
                    }

                }
                    spinnerCount++;
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
        if(spinnerTotal!=1 && spinnerTotal !=0 || !albumImages.get(imagesFragmentPosition).isSwitchValue()) {
            image.setVisibility(View.INVISIBLE);

            int time = albumImages.get(imagesFragmentPosition).getRestValue() * 30 * 1000;

            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            repsAndTimeCountDown.setText("");
            setCount.setText("");
            setTotal.setText("");

            if (imagesFragmentPosition + 1 != albumImages.size()) {
                imageTitle.setText("Up Next: " + (albumImages.get(imagesFragmentPosition + 1).getTitle()));

            }
            timer = new CountDownTimer(time, 350) {
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
        int time = albumImages.get(imagesFragmentPosition).getSlideValue();

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

        timer= new CountDownTimer(time*1000, 350) {
             public void onTick(long millisUntilFinished) {
                repsAndTimeCountDown.setText("" + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
             }
             public void onFinish() {
                repsAndTimeCountDown.setText("");
                ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
             }
              }.start();
    }


    public void setCountText(){
        setCount.setText("Set " + spinnerCount + " of ");
    }


}
