package com.johnnymolina.ImgurWorkout.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.activities.LibraryAlbumViewerActivity;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;

import org.w3c.dom.Text;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmRecyclerViewImgurAlbumAdapter extends RealmRecyclerViewAdapter<ImgurAlbum> {
//http://gradlewhy.ghost.io/realm-results-with-recyclerview/
    Context mContext;
    String albumID;
    String ALBUM_ID = "ALBUM_ID";
     Context context;

    private class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public ImageView image;
        public TextView title;
        public TextView description;


        public AlbumViewHolder(View view) {
            super(view);
            //get the views context
            context = view.getContext();


            image = (ImageView) view.findViewById(R.id.imgur_img_cv);
            title = (TextView) view.findViewById(R.id.imgur_title_cv);
            description = (TextView) view.findViewById(R.id.imgur_img_description_cv);
            //make the view from the viewholder clickable.
            view.setClickable(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
        Log.i(ALBUM_ID, "clicked");
            final Intent intent = new Intent(context,LibraryAlbumViewerActivity.class);
            ImgurAlbum album = getItem(getPosition());
            intent.putExtra(ALBUM_ID, album.getId());
            context.startActivity(intent);

        }


        @Override
        public boolean onLongClick(View v) {
            //delete the long clicked album
            ImgurAlbum album = getItem(getPosition());
            Toast.makeText(v.getContext(), "Deleted "+ album.getTitle(), Toast.LENGTH_SHORT).show();
            Realm realm;
            realm = Realm.getInstance(context);

            RealmResults<ImgurAlbum> albumToDelete= realm.where(ImgurAlbum.class)
                    .equalTo("id", album.getId())
                    .findAll();
            RealmResults<ImgurImage> imagesToDelete = realm.where(ImgurImage.class)
                    .equalTo("album", album.getId())
                    .findAll();

            realm.beginTransaction();

            albumToDelete.get(0).removeFromRealm();

            for (int i = 0; i < imagesToDelete.size(); i++){
                String imageLink = imagesToDelete.get(i).getLink().substring(imagesToDelete.get(i).getLink().lastIndexOf('/') + 1);
                String filePath =  "data/data/com.johnnymolina.nextphase/files/"+imageLink;
                File file = new File(filePath);
                 file.delete();
            }

            imagesToDelete.clear();

            realm.commitTransaction();
            realm.close();
            notifyItemRemoved(getPosition());
            notifyDataSetChanged();
            return false;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_library_cardview, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        AlbumViewHolder avh = (AlbumViewHolder) viewHolder;
        ImgurAlbum album = getItem(i);
        avh.title.setText(album.getTitle());

        avh.description.setText(album.getDescription());

        albumID = album.getId();

        String imageId = album.getImages().get(0).getId();
        String imageLink = album.getImages().get(0).getLink().substring(album.getImages().get(0).getLink().lastIndexOf('/') + 1);
        String imageSmallUrl = "http://i.imgur.com/" + imageId + "t.png";

        //start with Glide

        //start with Glide
        Glide.with(context)
       // album.getImages().get(0).getSysLink()
       // context.getFilesDir().toString() + album.getImages().get(0).getId() + ".gif"
                .load("file:///data/data/com.johnnymolina.nextphase/files/"+imageLink)
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
