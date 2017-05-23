package com.thanhtuan.delivery.model;

import android.graphics.Bitmap;

/**
 * Created by Nusib on 5/23/2017.
 */

public class Photo {
    private Bitmap Image;
    private String Description;

    public Photo(Bitmap image, String description) {
        Image = image;
        Description = description;
    }

    public Photo() {
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
