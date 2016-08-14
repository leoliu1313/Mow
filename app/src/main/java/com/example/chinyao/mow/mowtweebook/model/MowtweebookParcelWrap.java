package com.example.chinyao.mow.mowtweebook.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by chinyao on 8/14/2016.
 */

@Parcel
public class MowtweebookParcelWrap {
	public List<MowtweebookTweet> tweets;

	// Parcels needs this
	public MowtweebookParcelWrap() {
	}

	public MowtweebookParcelWrap(List<MowtweebookTweet> tweets) {
		this.tweets = tweets;
	}
}
