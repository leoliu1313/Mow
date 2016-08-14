package com.example.chinyao.mow.mowtweebook.utility;

import android.text.format.DateUtils;

import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by chinyao on 8/14/2016.
 */
public class MowtweebookUtility {
	public static MowtweebookTweet process_tweet(MowtweebookTweet theTweet) {
		if (!theTweet.isMowtweebookProcessed()) {
			// retweet
			if (theTweet.getRetweeted_status() != null) {
				theTweet.getRetweeted_status().setOriginal_user(theTweet.getUser());
				theTweet = theTweet.getRetweeted_status();
			}

			String tmp;

			// image
			if (theTweet.getEntities() != null
					&& theTweet.getEntities().getMedia() != null
					&& theTweet.getEntities().getMedia().size() > 0) {
				tmp = theTweet.getEntities().getMedia().get(0).getMedia_url();
				tmp = tmp.replaceAll("normal", "bigger");
				theTweet.setMowtweebookImageUrl(tmp);
			}

			// tweet content
			tmp = theTweet.getText();
			if (tmp != null) {
				tmp = tmp.replaceAll("^RT @[a-zA-Z0-9]*: ", "");
				tmp = tmp.replaceAll("https://t.co/.*$", "");
			}
			if (theTweet.getEntities() != null
					&& theTweet.getEntities().getUrls() != null
					&& theTweet.getEntities().getUrls().size() > 0
					&& theTweet.getEntities().getUrls().get(0).getExpanded_url() != null) {
				tmp = tmp + " " + theTweet.getEntities().getUrls().get(0).getExpanded_url();
			}
			theTweet.setText(tmp);

			// @id
			tmp = theTweet.getUser().getScreen_name();
			if (tmp != null) {
				tmp = "@" + tmp;
				theTweet.getUser().setScreen_name(tmp);
			}

			// timestamp
			tmp = theTweet.getCreated_at();
			if (tmp != null) {
				tmp = getRelativeTimeAgo(tmp);
				tmp = tmp.replaceAll(" seconds ago", "s");
				tmp = tmp.replaceAll(" minutes ago", "m");
				tmp = tmp.replaceAll(" hours ago", "h");
				tmp = tmp.replaceAll(" dats ago", "d");
				theTweet.setCreated_at(tmp);
			}

			theTweet.setMowtweebookProcessed(true);
		}
		return theTweet;
	}

	// relative timestamp
	// https://gist.github.com/nesquena/f786232f5ef72f6e10a7
	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate;
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
		} catch (ParseException e) {
			e.printStackTrace();
			relativeDate = rawJsonDate;
		}

		return relativeDate;
	}
}
