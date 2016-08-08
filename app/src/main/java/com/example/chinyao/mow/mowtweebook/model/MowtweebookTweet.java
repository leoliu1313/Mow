package com.example.chinyao.mow.mowtweebook.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by chinyao on 8/7/2016.
 */
public class MowtweebookTweet {
	String created_at;
	String id_str;
	String favorited;
	String text;
	String retweet_count;
	String retweeted;
	MowtweebookUser user;
	MowtweebookEntities entities;

	// boolean default is false
	// Boolean default is null
	boolean fullSpan;

	public MowtweebookTweet() {
	}

	public static MowtweebookTweet parseJSON(String response) {
		Gson gson = new GsonBuilder().create();
		MowtweebookTweet boxOfficeMovieResponse = gson.fromJson(response, MowtweebookTweet.class);
		return boxOfficeMovieResponse;
	}

	public boolean isFullSpan() {
		return fullSpan;
	}

	public void setFullSpan(boolean fullSpan) {
		this.fullSpan = fullSpan;
	}

	public MowtweebookEntities getEntities() {
		return entities;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getFavorited() {
		return favorited;
	}

	public String getId_str() {
		return id_str;
	}

	public String getRetweet_count() {
		return retweet_count;
	}

	public String getRetweeted() {
		return retweeted;
	}

	public String getText() {
		return text;
	}

	public MowtweebookUser getUser() {
		return user;
	}
}
