package com.example.chinyao.simpletodo;

/**
 * Created by chinyao on 6/20/2016.
 */
public class SettingModel {

    public Long _id; // for cupboard
    public boolean firstTime; // bunny name

    public SettingModel() {
        this.firstTime = true;
    }

    public SettingModel(boolean firstTime) {
        this.firstTime = firstTime;
    }
}
