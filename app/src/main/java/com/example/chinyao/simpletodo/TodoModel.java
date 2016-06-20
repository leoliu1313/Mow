package com.example.chinyao.simpletodo;

/**
 * Created by chinyao on 6/20/2016.
 */
public class TodoModel {

    public Long _id; // for cupboard
    public String content; // bunny name

    public TodoModel() {
        this.content = "";
    }

    public TodoModel(String content) {
        this.content = content;
    }
}
