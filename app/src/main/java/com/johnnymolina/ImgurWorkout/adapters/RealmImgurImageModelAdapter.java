package com.johnnymolina.ImgurWorkout.adapters;

import android.content.Context;

import com.johnnymolina.ImgurWorkout.network.model.ImgurImage;

import io.realm.RealmResults;

public class RealmImgurImageModelAdapter extends RealmModelAdapter<ImgurImage> {
    public RealmImgurImageModelAdapter(Context context, RealmResults<ImgurImage> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}