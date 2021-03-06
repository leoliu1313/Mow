package com.example.chinyao.mow.mowtweebook.utility;

import android.content.Context;

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
	private static MowtweebookRestClient client;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		client = null;
	}

	public static MowtweebookRestClient getRestClient() {
		if (client == null) {
			client = (MowtweebookRestClient) MowtweebookRestClient.getInstance(
					MowtweebookRestClient.class,
					MowtweebookRestApplication.context);
		}
		return client;
	}
}