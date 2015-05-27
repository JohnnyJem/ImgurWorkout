package com.johnnymolina.ImgurWorkout.fragments;


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
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.activities.PlaylistActivity;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

Realm realm;
Context context;
String imagesAlbumID;
int chronoTime;
int imagesFragmentPosition;
int startTime;
    TextView repsAndTimeCountDown;
    TextView restCountDown;
    CardView cardViewFragment;
    TextView imageTitle;
    ImageView image;
    TextView setCount;
    TextView setTotal;
    int spinnerTotal;
    int spinnerCount;
    int spinnerSwitch = 2;
    CountDownTimer timer;
    RealmResults<ImgurImage> albumImages;

    public PlaylistFragment(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using the arguments we retrieve from the bundle.
        Bundle args = getArguments();
        imagesFragmentPosition = args.getInt("POSITION");
        imagesAlbumID = args.getString("ALBUM_ID");
        startTime = args.getInt("startTime");

        cardViewFragment =(CardView) inflater.inflate(R.layout.fragment_card_playlist, container, false);
        imageTitle = (TextView) cardViewFragment.findViewById(R.id.card_title_text);
        TextView imageDescription = (TextView) cardViewFragment.findViewById(R.id.card_description_text);
        image = (ImageView) cardViewFragment.findViewById(R.id.card_image_view);
        repsAndTimeCountDown = (TextView) cardViewFragment.findViewById(R.id.count_down_timer);
        restCountDown = (TextView) cardViewFragment.findViewById(R.id.count_down_rest);
        setCount = (TextView) cardViewFragment.findViewById(R.id.set_count);
        setTotal = (TextView) cardViewFragment.findViewById(R.id.set_total);

        context = cardViewFragment.getContext();
        realm = Realm.getInstance(context);
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id", imagesAlbumID)
                .findAll();


         albumImages = realm.where(ImgurImage.class)
                .contains("album",imagesAlbumID, false)
                .findAll();


        //setting image Title
        if(albumImages.get(imagesFragmentPosition).getTitle().contains("null")){
            imageTitle.setText("");
        }else {
            imageTitle.setText(albumImages.get(imagesFragmentPosition).getTitle());
        }


        //Settng image Description
        if (albumImages.get(imagesFragmentPosition).getDescription().contains("null")){
            imageDescription.setText("");
        }else {
            imageDescription.setText(albumImages.get(imagesFragmentPosition).getDescription());
        }


        String imageLink;

        if (albumImages.get(imagesFragmentPosition).getSysLink() != "null"){
           imageLink ="file:///data/data/com.johnnymolina.nextphase/files/"+ albumImages.get(imagesFragmentPosition).getLink().substring(albumImages.get(imagesFragmentPosition).getLink().lastIndexOf('/') + 1);
        }else{
            imageLink= albumImages.get(imagesFragmentPosition).getLink().toString();
        }

        //start with Glide
        //start with Glide
        Glide.with(context)
                .load(imageLink)
                .thumbnail(0.2f)
                .crossFade(3)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.imageplaceholder)
                .into(image);

    return cardViewFragment;

    }



    //Used to setup each new fragment created.
    public static PlaylistFragment newInstance(int position,String albumID){

        PlaylistFragment frag = new PlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ALBUM_ID", albumID);
        bundle.putInt("POSITION", position);
        frag.setArguments(bundle);

        //return a set-up fragment
        return(frag);
    }







    /*--------------------------------Very important method that executes on each page---------------*/
    @Override
    public void onResume() {
        super.onResume();
        ((PlaylistActivity) getActivity()).firstExecution();

        spinnerCount = 1;
        spinnerTotal = 0;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Make sure that we are currently visiblea
        if (this.isVisible()) {

            ((PlaylistActivity) getActivity()).firstExecution();

            spinnerSwitch = 2;


            //START TEXT TO SPEECH ON WINDOW CHANGE
            String title = this.imageTitle.getText().toString();
            ((PlaylistActivity) getActivity()).announceTitle(title);

        }
    }

    /*-------------------------End of methods that we can use at the very start--------------*/



    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
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


                switch (spinnerSwitch) {
                    case 1:
                        if (albumImages.get(imagesFragmentPosition).getSpinner1() != 0){
                            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner1() + " Reps");
                            break;}

                    case 2:
                        if (albumImages.get(imagesFragmentPosition).getSpinner2() != 0){
                            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner2() + " Reps");
                            spinnerSwitch++;
                            break;}else {
                            spinnerSwitch = 3;
                        }

                    case 3:
                        if (albumImages.get(imagesFragmentPosition).getSpinner3() != 0 ){
                            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner3() + " Reps");
                            spinnerSwitch++;
                            break;}else{
                            spinnerSwitch++;
                        }

                    case 4:
                        if (albumImages.get(imagesFragmentPosition).getSpinner4() != 0 ){
                            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner4() + " Reps");
                            spinnerSwitch++;
                            break;}else{
                            spinnerSwitch++;
                        }

                    case 5:
                        if (albumImages.get(imagesFragmentPosition).getSpinner5() != 0){
                            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner5() + " Reps");
                            break;}

                    default: repsAndTimeCountDown.setText("");
                        break;
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
        if(spinnerTotal!=1 && spinnerTotal !=0) {
            image.setVisibility(View.INVISIBLE);
            int time = albumImages.get(imagesFragmentPosition).getRestValue() * 30 * 1000;

            ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
            repsAndTimeCountDown.setText("");

            timer = new CountDownTimer(time, 350) {
                public void onTick(long millisUntilFinished) {
                    restCountDown.setText("Resting for " + String.valueOf(Math.round(millisUntilFinished * 0.001f)));
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

        int time = albumImages.get(imagesFragmentPosition).getSlideValue();
        if (time == 0 )
            time = 10;

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


public void firstFragmentExecution(){

    if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {

        //fabPlayNow = findViewById(R.id.fab_play_playlist_next);
        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);
        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);

    }else{

        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.VISIBLE);
        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.INVISIBLE);

    }

    Map<Boolean,Integer> mRepsReferenceMap = new HashMap<Boolean, Integer>();

    if (albumImages.get(imagesFragmentPosition).isSwitchValue()) {
        spinnerTotal = 0;
        spinnerCount = 1;
        if (albumImages.get(imagesFragmentPosition).getSpinner1() != 0)
            spinnerTotal++;
        if (albumImages.get(imagesFragmentPosition).getSpinner2() != 0)
            spinnerTotal++;
        if (albumImages.get(imagesFragmentPosition).getSpinner3() != 0)
            spinnerTotal++;
        if (albumImages.get(imagesFragmentPosition).getSpinner4() != 0)
            spinnerTotal++;
        if (albumImages.get(imagesFragmentPosition).getSpinner5() != 0)
            spinnerTotal++;

            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner1() + " Reps");


        //this part needs to be better made IF ONLY REALM COULD ACCEPT LISTS.
        //I NEED TO LEARN HOW TO USE ITERABLE INSTEAD OF USING ALL THESE IF ELSES.






        if (albumImages.get(imagesFragmentPosition).getSpinner1() != 0){
            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner1() + " Reps");
        }else {
            repsAndTimeCountDown.setText(albumImages.get(imagesFragmentPosition).getSpinner2() + " Reps");
        }



        setCount.setText("Set " + spinnerCount + " of ");
        setTotal.setText("" + spinnerTotal);
    }else {


        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_play_playlist_next).setVisibility(View.VISIBLE);
        ((PlaylistActivity) getActivity()).findViewById(R.id.fab_go_next).setVisibility(View.INVISIBLE);

    }
}


    public void setCountText(){

        setCount.setText("Set " + spinnerCount + " of ");

    }




}
