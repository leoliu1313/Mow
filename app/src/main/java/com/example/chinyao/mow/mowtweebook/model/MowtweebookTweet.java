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
	String favorite_count;
	String retweeted;
	String retweet_count;
	String text;
	MowtweebookUser user;
	MowtweebookEntities entities;
	MowtweebookTweet retweeted_status;

	// boolean default is false
	// Boolean default is null
	boolean mowtweebookFullSpan;
	String mowtweebookImageUrl;

	public MowtweebookTweet() {
	}

	public static MowtweebookTweet parseJSON(String response) {
		Gson gson = new GsonBuilder().create();
		MowtweebookTweet boxOfficeMovieResponse = gson.fromJson(response, MowtweebookTweet.class);
		return boxOfficeMovieResponse;
	}

	public String getMowtweebookImageUrl() {
		return mowtweebookImageUrl;
	}

	public void setMowtweebookImageUrl(String mowtweebookImageUrl) {
		this.mowtweebookImageUrl = mowtweebookImageUrl;
	}

	public boolean isMowtweebookFullSpan() {
		return mowtweebookFullSpan;
	}

	public void setMowtweebookFullSpan(boolean mowtweebookFullSpan) {
		this.mowtweebookFullSpan = mowtweebookFullSpan;
	}

	public MowtweebookTweet getRetweeted_status() {
		return retweeted_status;
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

	public String getFavorite_count() {
		return favorite_count;
	}
}
