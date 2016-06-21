package com.example.chinyao.simpletodo;

import java.util.Date;

/**
 * Created by chinyao on 6/20/2016.
 */
public class TodoModel {

    public Long _id; // for cupboard

    public String content;
    public String priority;
    public Date date;

    public TodoModel() {
        this.content = "";
        this.priority = "";
        this.date = new Date();
    }

    public TodoModel(String content, String priority, Date date) {
        if (content != null) this.content = content;
        if (priority != null) this.priority = priority;
        if (date != null) this.date = date;
    }
}
