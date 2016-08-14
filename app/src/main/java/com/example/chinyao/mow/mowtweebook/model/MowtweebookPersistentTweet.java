package com.example.chinyao.mow.mowtweebook.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinyao on 8/14/2016.
 */
@Table(name = "Tweet")
public class MowtweebookPersistentTweet extends Model {
	@Column(name = "id_str", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	String id_str;
	@Column(name = "json_response")
	String json_response;

	// activeandroid needs this
	public MowtweebookPersistentTweet() {
		super();
	}

	public MowtweebookPersistentTweet(String id_str, String json_response) {
		super();
		this.id_str = id_str;
		this.json_response = json_response;
	}

	public static ArrayList<MowtweebookTweet> getAll() {
		// This is how you execute a query
		List<MowtweebookPersistentTweet> persistentTweets = new Select()
				.from(MowtweebookPersistentTweet.class)
				.execute();

		Gson gson = new GsonBuilder().create();
		ArrayList<MowtweebookTweet> tweets = new ArrayList<>();
		for (int i = 0; i < persistentTweets.size(); i++) {
			tweets.add(
					gson.fromJson(
							persistentTweets.get(i).json_response,
							MowtweebookTweet.class
					)
			);
		}
		return tweets;
	}
}
