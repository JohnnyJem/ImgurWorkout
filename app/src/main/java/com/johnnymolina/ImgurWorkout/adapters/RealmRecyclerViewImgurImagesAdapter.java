package com.johnnymolina.imgurworkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.johnnymolina.imgurworkout.R;
import com.johnnymolina.imgurworkout.network.model.ImgurImage;
import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

import io.realm.Realm;


public class RealmRecyclerViewImgurImagesAdapter extends RealmRecyclerViewAdapter<ImgurImage> implements View.OnClickListener, View.OnLongClickListener {
    //http://gradlewhy.ghost.io/realm-results-with-recyclerview/
    Context context;



    private class AlbumViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public Switch timeOrRepSwitch;
        public TextView timeOrRepTextviewValue;
        public Spinner spinner1;
        public Spinner spinner2;
        public Spinner spinner3;
        public Spinner spinner4;
        public Spinner spinner5;
        public Spinner spinnerRest;
        public LinearLayout layoutSpinner;
        public LinearLayout layoutSlider;
        public SeekBar slider;
        public TextView timerCountDown;

        RelativeLayout slideMarkers;
        public TextView sliderTextProgress;


        public AlbumViewHolder(View view) {
            super(view);
            context= view.getContext();
            image = (ImageView) view.findViewById(R.id.imgur_img_album_cv);
            title = (TextView) view.findViewById(R.id.imgur_album_title_cv);;
            sliderTextProgress= (TextView) view.findViewById(R.id.slider_text_view);
            timeOrRepTextviewValue = (TextView) view.findViewById(R.id.switch_text);
            slideMarkers = (RelativeLayout) view.findViewById(R.id.slide_markers);
            //Todo: get the default switch value from our realm image object.

            layoutSlider = (LinearLayout) view.findViewById(R.id.slide_layout);
            slider = (SeekBar) view.findViewById(R.id.slider);
            layoutSpinner = (LinearLayout) view.findViewById(R.id.spinners_layout);
            timerCountDown = (TextView) view.findViewById(R.id.count_down_timer);


            spinner1 = (Spinner) view.findViewById(R.id.spinner1);
            spinner2 = (Spinner) view.findViewById(R.id.spinner2);
            spinner3 = (Spinner) view.findViewById(R.id.spinner3);
            spinner4 = (Spinner) view.findViewById(R.id.spinner4);
            spinner5 = (Spinner) view.findViewById(R.id.spinner5);

             String[] state= {"0 reps","1 reps","2 reps","3 reps","4 reps","5 reps","6 reps","7 reps","8 reps","9 reps","10 reps"};
            ArrayAdapter<String> adapter_state = new ArrayAdapter<String>( context,android.R.layout.simple_spinner_item,state);
            spinner1.setAdapter(adapter_state);
            spinner2.setAdapter(adapter_state);
            spinner3.setAdapter(adapter_state);
            spinner4.setAdapter(adapter_state);
            spinner5.setAdapter(adapter_state);



            spinnerRest = (Spinner) view.findViewById(R.id.spinnerRest);
            Integer[] restTimes= {0,30,60,90,120,150,180,210,240};
            ArrayAdapter<Integer> adapter_restTimes = new ArrayAdapter<Integer>( context,android.R.layout.simple_spinner_item,restTimes);
            spinnerRest.setAdapter(adapter_restTimes);


            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    sliderTextProgress.setText("" + progress + " seconds");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSlideValue(slider.getProgress());
                    realm.commitTransaction();
                    realm.close();
                }
            });


            spinnerRest.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setRestValue(spinnerRest.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });


            spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSpinner1(spinner1.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });

            spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSpinner2(spinner2.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });

            spinner3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSpinner3(spinner3.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });

            spinner4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSpinner4(spinner4.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });
            spinner5.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner spinner, View view, int i, long l) {
                    Realm realm = Realm.getInstance(context);
                    ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSpinner5(spinner5.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();
                }
            });






            timeOrRepSwitch = (Switch) view.findViewById(R.id.switch_cv_image_time_or_rep);
            timeOrRepSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Realm realm = Realm.getInstance(context);
                   ImgurImage imagetoUpdate = realm.where(ImgurImage.class)
                            .equalTo("id", getItem(getPosition()).getId())
                            .findFirst();
                    realm.beginTransaction();
                    imagetoUpdate.setSwitchValue(timeOrRepSwitch.isChecked());
                    imagetoUpdate.setRestValue(spinnerRest.getSelectedItemPosition());
                    realm.commitTransaction();
                    realm.close();

                    if (timeOrRepSwitch.isChecked()){
                        timeOrRepTextviewValue.setText("Sets");
                        layoutSpinner.setVisibility(View.VISIBLE);
                        slideMarkers.setVisibility(View.INVISIBLE);
                        layoutSlider.setVisibility(View.INVISIBLE);
                    }else{
                        timeOrRepTextviewValue.setText("Timed");
                        layoutSlider.setVisibility(View.VISIBLE);
                        slideMarkers.setVisibility(View.VISIBLE);
                        layoutSpinner.setVisibility(View.INVISIBLE);
                    }


                }
            });


        }
    }




    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }






    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_library_album_viewer_cardview, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        AlbumViewHolder avh = (AlbumViewHolder) viewHolder;

        //setting up the persistant data
        ImgurImage image = getItem(i);
        ImgurImage imagetoUpdate = getItem(avh.getPosition());


        //set our Viewholders data
        avh.timeOrRepSwitch.setChecked(imagetoUpdate.isSwitchValue());

        avh.spinner1.setSelection(imagetoUpdate.getSpinner1());
        avh.spinner2.setSelection(imagetoUpdate.getSpinner2());
        avh.spinner3.setSelection(imagetoUpdate.getSpinner3());
        avh.spinner4.setSelection(imagetoUpdate.getSpinner4());
        avh.spinner5.setSelection(imagetoUpdate.getSpinner5());
        avh.spinnerRest.setSelection(imagetoUpdate.getRestValue());
        avh.slider.setProgress(imagetoUpdate.getSlideValue());
        avh.timeOrRepSwitch.callOnClick();

        //setting up the Imageview
        if (image.getTitle().contains("null")){
            avh.title.setText("");
        }else {
            avh.title.setText(image.getTitle());
        }

        String imageUrl = image.getLink();
        String imageId = image.getId();
        String imageLink = image.getLink().substring(image.getLink().lastIndexOf('/') + 1);
        String imageUrlSmall = "http://i.imgur.com/" + image.getId() + "t.png";

        //start with Glide

        //start with Glide
        Glide.with(context)


                .load("file:///data/data/com.johnnymolina.imgurworkout/files/"+imageLink )
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.placeholderdrawable)
                .error(R.drawable.imageplaceholder)
                .into(avh.image);
    }

    /* The inner RealmBaseAdapter
     * view count is applied here.
     *
     * getRealmAdapter is defined in RealmRecyclerViewAdapter.
     */
    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}

