package com.johnnymolina.ImgurWorkout.network.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/*-------- This is the realm.io model used to store JSON elements from Imgur.com api calls*/
public class ImgurAlbum extends RealmObject {
    // The id String consists of AlbumID+AlbumUnique generated through NextSessionId() method.
    // It associates this main ImgurAlbum object with its child ImgurImages through the unique ID.
    private String id;
    private String title;
    private String description;
    private String link;
    private String tags;
    private int length;


    private RealmList<ImgurImage> images;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public RealmList<ImgurImage> getImages() {
        return images;
    }

    public void setImages(RealmList<ImgurImage> images) {
        this.images = images;
    }
}
