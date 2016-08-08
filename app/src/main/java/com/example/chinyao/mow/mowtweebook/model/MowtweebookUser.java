package com.example.chinyao.mow.mowtweebook.model;

/**
 * Created by chinyao on 8/7/2016.
 */
public class MowtweebookUser {
	String profile_image_url;
	String location;
	String id_str;
	String favourites_count;
	String followers_count;
	String description;
	String statuses_count;
	String friends_count;
	String screen_name;
	String name;

	public String getName() {
		return name;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public String getDescription() {
		return description;
	}

	public String getFavourites_count() {
		return favourites_count;
	}

	public String getFollowers_count() {
		return followers_count;
	}

	public String getFriends_count() {
		return friends_count;
	}

	public String getId_str() {
		return id_str;
	}

	public String getLocation() {
		return location;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public String getStatuses_count() {
		return statuses_count;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
}
