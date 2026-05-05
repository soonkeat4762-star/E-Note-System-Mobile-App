package com.android.noteapp;

import android.graphics.Bitmap;

public class Thumbnail {
    private final Bitmap thumbnailImage;
    private final int pageNumber;

    public Thumbnail(Bitmap thumbnailImage, int pageNumber) {
        this.thumbnailImage = thumbnailImage;
        this.pageNumber = pageNumber;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
