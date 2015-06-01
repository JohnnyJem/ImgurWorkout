package com.johnnymolina.imgurworkout.network.model;

import io.realm.RealmObject;

/**
 * Created by Johnny Molina on 5/15/2015.
 */
public class Log extends RealmObject{



    //Incremental ID
   private int  logID;

    // Saturday May 15th, 10pm
  private  String dateTime;

    //  Completed (AlbumName)
  private  String albumCompletedName;

    // Hours Minutes Seconds
   private String timeLength;

    //A workout type. Arms, Legs, Shoulders, etc
  private  String workoutType;

    //Any user input
  private  String note;




    //Start getters and setters

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getAlbumCompletedName() {
        return albumCompletedName;
    }

    public void setAlbumCompletedName(String albumCompletedName) {
        this.albumCompletedName = albumCompletedName;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }



}
