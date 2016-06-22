package com.example.chinyao.simpletodo;

/**
 * Created by chinyao on 6/20/2016.
 */
public class TodoModel {

    public Long _id; // for cupboard

    public String content;
    public String date;
    public String priority;

    // we need this for SQL
    public TodoModel() {
        this.content = "";
        this.date = "";
        this.priority = "";
    }

    public TodoModel(String content, String date, String priority) {
        if (content != null) this.content = content;
        if (date != null) this.date = date;
        if (priority != null) this.priority = priority;
    }

    public void refresh(TodoModel theTodoModel) {
        this.content = theTodoModel.content;
        this.date = theTodoModel.date;
        this.priority = theTodoModel.priority;
    }
}
