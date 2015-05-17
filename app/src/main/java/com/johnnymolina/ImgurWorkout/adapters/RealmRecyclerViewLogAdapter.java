package com.johnnymolina.ImgurWorkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;
import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;
import com.johnnymolina.ImgurWorkout.network.model.Log;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Johnny Molina on 5/15/2015.
 */

public class RealmRecyclerViewLogAdapter extends RealmRecyclerViewAdapter<Log> {
    //http://gradlewhy.ghost.io/realm-results-with-recyclerview/
    Context context;

    private class LogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
        public TextView dateTime;
        public TextView albumCompletedName;
        public TextView timeLength;
        public TextView note;
        public ImageView workoutTypeImageView;

        public LogViewHolder(View view) {
            super(view);
            //get the views context
            context = view.getContext();

            workoutTypeImageView = (ImageView) view.findViewById(R.id.log_row_image);
            dateTime = (TextView) view.findViewById(R.id.log_row_date_time);
            albumCompletedName = (TextView) view.findViewById(R.id.log_row_album_name);
            timeLength = (TextView) view.findViewById(R.id.log_row_time_length);
            note = (TextView) view.findViewById(R.id.log_row_note);






            view.setClickable(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            //delete the long clicked album

            Log log = getItem(getPosition());
            Toast.makeText(v.getContext(), "Deleted " + log.getWorkoutType(), Toast.LENGTH_SHORT).show();
            Realm realm;
            realm = Realm.getInstance(context);
            RealmResults<Log> logToDelete= realm.where(Log.class)
                    .equalTo("logID",getItem(getPosition()).getLogID())
                    .findAll();
            realm.beginTransaction();
            logToDelete.remove(0);
            realm.commitTransaction();
            realm.close();

            notifyItemRemoved(getPosition());
            notifyDataSetChanged();
            return false;
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_row, parent, false);
        return new LogViewHolder(v);
    }




    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        LogViewHolder avh = (LogViewHolder) viewHolder;

        Log log = getItem(i);

            avh.dateTime.setText(log.getDateTime());
            avh.albumCompletedName.setText(log.getAlbumCompletedName());
            avh.timeLength.setText(log.getTimeLength());
            avh.note.setText(log.getNote());
/*----SETup textdrawable--------------*/
        if (log.getWorkoutType() != null && log.getWorkoutType().toString().length() > 0) {

            String workoutTypeFirstLetter = log.getWorkoutType();


            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(workoutTypeFirstLetter);

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(40)
                    .bold()
                    .toUpperCase()
                    .endConfig()
                    .buildRect(workoutTypeFirstLetter, color);
            avh.workoutTypeImageView.setImageDrawable(drawable);
        }else{

            String workoutTypeFirstLetter ="";

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(workoutTypeFirstLetter);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(workoutTypeFirstLetter, color);
            avh.workoutTypeImageView.setImageDrawable(drawable);

        }


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


