package com.johnnymolina.ImgurWorkout.adapters;

        import android.content.Context;

        import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;

        import io.realm.RealmResults;

public class RealmImgurAlbumAdapter extends RealmModelAdapter<ImgurAlbum> {
    public RealmImgurAlbumAdapter(Context context, RealmResults<ImgurAlbum> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}