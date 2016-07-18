package com.example.chinyao.simpletodo;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

/**
 * Created by chinyao on 6/20/2016.
 */
public class TodoModel extends AbstractDataProvider.Data {

    public Long _id; // for cupboard

    static private long currentId = 0;
    public String content;
    public String date;
    public String priority;
    public long mId;
    public int mViewType;
    public boolean mPinned;

    // we need this for SQL
    public TodoModel() {
        this.content = "";
        this.date = "";
        this.priority = "";
        this.mId = currentId;
        this.mViewType = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;;
        this.mPinned = false;
        currentId++;
    }

    public TodoModel(String content, String date, String priority) {
        if (content != null) this.content = content;
        if (date != null) this.date = date;
        if (priority != null) this.priority = priority;
        this.mId = currentId;
        this.mViewType = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;;
        this.mPinned = false;
        currentId++;
    }

    public void refresh(TodoModel theTodoModel) {
        this.content = theTodoModel.content;
        this.date = theTodoModel.date;
        this.priority = theTodoModel.priority;
        this.mId = theTodoModel.mId;
        this.mViewType = theTodoModel.mViewType;
        this.mPinned = theTodoModel.mPinned;
    }

    @Override
    public boolean isSectionHeader() {
        return false;
    }

    @Override
    public int getViewType() {
        return mViewType;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public boolean isPinned() {
        return mPinned;
    }

    @Override
    public void setPinned(boolean pinned) {
        mPinned = pinned;
    }
}
