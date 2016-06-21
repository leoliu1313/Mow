package com.example.chinyao.simpletodo;

/**
 * Created by chinyao on 6/20/2016.
 */
public class TodoModel {

    public Long _id; // for cupboard

    public String content;
    public String priority;
    public String date;

    // we need this for SQL
    public TodoModel() {
        this.content = "";
        this.priority = "";
        this.date = "";
    }

    public TodoModel(String content, String priority, String date) {
        if (content != null) this.content = content;
        if (priority != null) this.priority = priority;
        if (date != null) this.date = date;
    }

    public void refresh(TodoModel theTodoModel) {
        this.content = theTodoModel.content;
        this.priority = theTodoModel.priority;
        this.date = theTodoModel.date;
    }
}
