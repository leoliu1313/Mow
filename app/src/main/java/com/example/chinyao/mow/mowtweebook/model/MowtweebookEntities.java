package com.example.chinyao.mow.mowtweebook.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by chinyao on 8/7/2016.
 */

@Parcel
public class MowtweebookEntities {
	List<MowtweebookMedia> media;
	List<MowtweebookUrls> urls;

	public MowtweebookEntities() {}

	public List<MowtweebookMedia> getMedia() {
		return media;
	}
	public List<MowtweebookUrls> getUrls() {
		return urls;
	}
}
