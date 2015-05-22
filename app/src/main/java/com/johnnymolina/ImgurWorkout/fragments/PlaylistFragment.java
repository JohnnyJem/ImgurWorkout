package com.johnnymolina.ImgurWorkout.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
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
    TextView countDownTimer;
    TextView countDownText;
    CardView cardViewFragment;
    TextView imageTitle;
    ImageView image;


    public PlaylistFragment(){

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//TODO: find out where this bundle comes from
        Bundle args = getArguments();
        imagesFragmentPosition = args.getInt("POSITION");
        imagesAlbumID = args.getString("ALBUM_ID");
        chronoTime = args.getInt("CHRONO_TIME");


        cardViewFragment =(CardView) inflater.inflate(R.layout.fragment_card_playlist, container, false);
        imageTitle = (TextView) cardViewFragment.findViewById(R.id.card_title_text);
        TextView imageDescription = (TextView) cardViewFragment.findViewById(R.id.card_description_text);
        image = (ImageView) cardViewFragment.findViewById(R.id.card_image_view);
        TextView repPosition =(TextView) cardViewFragment.findViewById(R.id.rep_position);
        countDownTimer = (TextView) cardViewFragment.findViewById(R.id.count_down_timer);
        countDownText = (TextView) cardViewFragment.findViewById(R.id.count_down_text);


        context = cardViewFragment.getContext();
        realm = Realm.getInstance(context);
        RealmResults<ImgurAlbum> albumQuery = realm.where(ImgurAlbum.class)
                .equalTo("id",imagesAlbumID )
                .findAll();


        RealmResults<ImgurImage> albumImages = realm.where(ImgurImage.class)
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


        int setSpinnerCount = 0;
        int setCurrentCount = 1;
        if (albumImages.get(imagesFragmentPosition).getSpinner1() != 0)
            setSpinnerCount++;
        if (albumImages.get(imagesFragmentPosition).getSpinner2() != 0)
            setSpinnerCount++;
        if (albumImages.get(imagesFragmentPosition).getSpinner3() != 0)
            setSpinnerCount++;
        if (albumImages.get(imagesFragmentPosition).getSpinner4() != 0)
            setSpinnerCount++;
        if (albumImages.get(imagesFragmentPosition).getSpinner5() != 0)
            setSpinnerCount++;

        repPosition.setText("Set"+ setCurrentCount +"of"+setSpinnerCount);



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

//test
//setting up the timers


    return cardViewFragment;

    }


    @Override
    public void onResume() {
        super.onResume();

        ((PlaylistActivity) getActivity()).firstExecution();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {

            //START TEXT TO SPEECH ON WINDOW CHANGE
            String title = this.imageTitle.getText().toString();
            ((PlaylistActivity) getActivity()).announceTitle(title);

            //START COUNTDOWN
             TextView countDownTime = this.countDownTimer;
            TextView countDownText = this.countDownText;
            ((PlaylistActivity) getActivity()).countDown(countDownTime, countDownText);

            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");
                // TODO stop countdown
                ((PlaylistActivity) getActivity()).closeCountDown();

            }
        }
    }


    //Used to setup each new fragment created.
   public static PlaylistFragment newInstance(int position,String albumID,int chronoTime){

       PlaylistFragment frag = new PlaylistFragment();

       Bundle bundle = new Bundle();
       bundle.putString("ALBUM_ID",albumID);
       bundle.putInt("POSITION", position);
       bundle.putInt("CHRONO_TIME", chronoTime);
       frag.setArguments(bundle);

        //return a set-up fragment
        return(frag);
    }

}
