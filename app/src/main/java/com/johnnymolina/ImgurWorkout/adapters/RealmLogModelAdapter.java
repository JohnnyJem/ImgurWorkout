package com.johnnymolina.imgurworkout.adapters;

import android.content.Context;

import com.johnnymolina.imgurworkout.network.model.Log;

import io.realm.RealmResults;

/**
 * Created by Johnny Molina on 5/15/2015.
 */
public class RealmLogModelAdapter extends RealmModelAdapter<Log> {
    public RealmLogModelAdapter(Context context, RealmResults<Log> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}