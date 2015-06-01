package com.johnnymolina.imgurworkout.adapters;

import android.content.Context;

import com.johnnymolina.imgurworkout.network.model.ImgurImage;

import io.realm.RealmResults;

public class RealmImgurImageModelAdapter extends RealmModelAdapter<ImgurImage> {
    public RealmImgurImageModelAdapter(Context context, RealmResults<ImgurImage> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}