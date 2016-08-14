package com.example.chinyao.mow.mowtweebook.model;

import org.parceler.Parcel;

/**
 * Created by chinyao on 8/7/2016.
 */

@Parcel
public class MowtweebookRT {
	String created_at;
	String text;
	String retweet_count;
	String favorite_count;
	MowtweebookEntities entities;

	public MowtweebookRT() {}

	public MowtweebookEntities getEntities() {
		return entities;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getFavorite_count() {
		return favorite_count;
	}

	public String getRetweet_count() {
		return retweet_count;
	}

	public String getText() {
		return text;
	}
}
