package com.johnnymolina.imgurworkout.adapters;

        import android.content.Context;

        import com.johnnymolina.imgurworkout.network.model.ImgurAlbum;

        import io.realm.RealmResults;

public class RealmImgurAlbumModelAdapter extends RealmModelAdapter<ImgurAlbum> {
    public RealmImgurAlbumModelAdapter(Context context, RealmResults<ImgurAlbum> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}