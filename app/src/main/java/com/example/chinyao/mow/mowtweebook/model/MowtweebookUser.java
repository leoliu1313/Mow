package com.example.chinyao.mow.mowtweebook.model;

import org.parceler.Parcel;

/**
 * Created by chinyao on 8/7/2016.
 */

@Parcel
public class MowtweebookUser {
	String id_str;

	String profile_banner_url;
	String profile_image_url;
	String name;
	String screen_name; // @
	String description; // tagline
	String friends_count; // following
	String followers_count; // follower
	String statuses_count; // number of tweets

	String location;

	public MowtweebookUser() {}

	public String getProfile_banner_url() {
		return profile_banner_url;
	}

	public String getName() {
		return name;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public String getDescription() {
		return description;
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
