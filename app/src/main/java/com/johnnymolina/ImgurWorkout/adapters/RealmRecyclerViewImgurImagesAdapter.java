package com.johnnymolina.ImgurWorkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.rey.material.widget.Switch;


public class RealmRecyclerViewImgurImagesAdapter extends RealmRecyclerViewAdapter<ImgurImage> implements View.OnClickListener, View.OnLongClickListener {
    //http://gradlewhy.ghost.io/realm-results-with-recyclerview/
    Context context;



    private class AlbumViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public Switch timeOrRepSwitch;
        public TextView timeOrRepTextviewValue;
        public AlbumViewHolder(View view) {
            super(view);
            context= view.getContext();
            image = (ImageView) view.findViewById(R.id.imgur_img_album_cv);
            title = (TextView) view.findViewById(R.id.imgur_album_title_cv);;
            timeOrRepSwitch = (Switch) view.findViewById(R.id.switch_cv_image_time_or_rep);
            timeOrRepTextviewValue = (TextView) view.findViewById(R.id.switch_value);
            //Todo: get the default switch value from our realm image object.

            timeOrRepSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeOrRepSwitch.isChecked();

                    if (timeOrRepSwitch.isChecked() == true)
                        timeOrRepTextviewValue.setText("Sets");
                    else
                        timeOrRepTextviewValue.setText("Timed");
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
        ImgurImage image = getItem(i);

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


        .load("file:///data/data/com.johnnymolina.nextphase/files/"+imageLink )
                .asBitmap()
                .thumbnail(0.3f)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
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

