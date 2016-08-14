package com.example.chinyao.mow.mowtweebook.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.chinyao.mow.mowtweebook.utility.MowtweebookUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chinyao on 8/14/2016.
 */
@Table(name = "Tweet")
public class MowtweebookPersistentTweet extends Model {
	// gson cannot parse this class due to "extends Model"
	@Column(name = "id_str", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	String id_str;
	@Column(name = "json_response")
	String json_response;
	@Column(name = "mode")
	Integer mode;

	// activeandroid needs this
	public MowtweebookPersistentTweet() {
		super();
	}

	public MowtweebookPersistentTweet(int mode, String id_str, String json_response) {
		super();
		if (mode == 1) {
			this.id_str = "home_timeline/" + id_str;
		}
		else if (mode == 2) {
			this.id_str = "user_timeline/" + id_str;
		}
		this.json_response = json_response;
		this.mode = mode;
	}

	public static ArrayList<MowtweebookTweet> getAll(int mode) {
		// This is how you execute a query
		List<MowtweebookPersistentTweet> persistentTweets = new Select()
				.from(MowtweebookPersistentTweet.class)
				.where("mode=" + mode)
				.orderBy("id_str DESC")
				.execute();

		Gson gson = new GsonBuilder().create();
		ArrayList<MowtweebookTweet> tweets = new ArrayList<>();
		for (int i = 0; i < persistentTweets.size(); i++) {
			MowtweebookTweet theTweet = gson.fromJson(
					persistentTweets.get(i).json_response,
					MowtweebookTweet.class
			);
			theTweet = MowtweebookUtility.process_tweet(theTweet);
			tweets.add(MowtweebookUtility.process_tweet(theTweet));
		}
		return tweets;
	}

	public static void deleteAll() {
		// This is how you execute a query
		new Delete().from(MowtweebookPersistentTweet.class).execute();
	}
}
