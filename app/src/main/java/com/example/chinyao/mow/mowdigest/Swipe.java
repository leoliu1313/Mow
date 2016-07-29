package com.example.chinyao.mow.mowdigest;

/**
 * Created by chinyao on 7/29/2016.
 */
public class Swipe {

    private String description;

    private String imagePath;

    public Swipe(String imagePath, String description) {
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
