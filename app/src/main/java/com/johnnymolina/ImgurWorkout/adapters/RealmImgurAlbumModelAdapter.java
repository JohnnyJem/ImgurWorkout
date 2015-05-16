package com.johnnymolina.ImgurWorkout.adapters;

        import android.content.Context;

        import com.johnnymolina.ImgurWorkout.network.model.ImgurAlbum;

        import io.realm.RealmResults;

public class RealmImgurAlbumModelAdapter extends RealmModelAdapter<ImgurAlbum> {
    public RealmImgurAlbumModelAdapter(Context context, RealmResults<ImgurAlbum> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}