package com.johnnymolina.ImgurWorkout.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.johnnymolina.ImgurWorkout.R;
import com.johnnymolina.ImgurWorkout.network.model.Log;

/**
 * Created by Johnny Molina on 5/15/2015.
 */

public class RealmRecyclerViewLogAdapter extends RealmRecyclerViewAdapter<Log> {
    //http://gradlewhy.ghost.io/realm-results-with-recyclerview/
    Context context;
    private class LogViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTime;
        public TextView albumCompletedName;
        public TextView timeLength;
        public ImageView workoutType;
        public TextView note;

        public LogViewHolder(View view) {
            super(view);
            context= view.getContext();

            workoutType = (ImageView) view.findViewById(R.id.log_row_image);
            dateTime = (TextView) view.findViewById(R.id.log_row_date_time);
            albumCompletedName = (TextView) view.findViewById(R.id.log_row_album_name);
            timeLength = (TextView) view.findViewById(R.id.log_row_time_length);
            note = (TextView) view.findViewById(R.id.log_row_note);

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

        Glide.with(context)
                .load(R.drawable.imageplaceholder)
                .asBitmap()
                .thumbnail(0.3f)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.imageplaceholder)
                .into(avh.workoutType);


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


