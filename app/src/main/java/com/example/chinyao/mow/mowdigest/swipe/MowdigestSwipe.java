package com.example.chinyao.mow.mowdigest.swipe;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestSwipe {

    private String description;

    private String imagePath;

    public MowdigestSwipe(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }
}
