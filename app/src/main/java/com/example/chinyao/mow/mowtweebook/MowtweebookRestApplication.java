package com.example.chinyao.mow.mowtweebook;

import android.content.Context;

import com.example.chinyao.mow.mowtweebook.utility.MowtweebookRestClient;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     MowtweebookRestClient client = MowtweebookRestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class MowtweebookRestApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		MowtweebookRestApplication.context = this;
	}

	public static MowtweebookRestClient getRestClient() {
		return (MowtweebookRestClient) MowtweebookRestClient.getInstance(
				MowtweebookRestClient.class,
				MowtweebookRestApplication.context);
	}
}