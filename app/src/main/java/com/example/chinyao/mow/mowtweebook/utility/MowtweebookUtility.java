package com.example.chinyao.mow.mowtweebook.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowtweebook.activity.MowtweebookActivity;
import com.example.chinyao.mow.mowtweebook.model.MowtweebookTweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chinyao on 8/14/2016.
 */

// TODO: this class is static. use interface instead.
// TODO: this class is static. use interface instead.
// TODO: this class is static. use interface instead.
public class MowtweebookUtility {
	private static MowtweebookActivity theActivity = null;
	private static Context theContext = null;

	// TODO: this setup function is needed. not sure if it works for rotation.
	// TODO: this setup function is needed. not sure if it works for rotation.
	// TODO: this setup function is needed. not sure if it works for rotation.
	public static void setupContext(Context context) {
		theContext = context;
		if (context instanceof MowtweebookActivity) {
			theActivity = (MowtweebookActivity) context;
		}
		else {
			theActivity = null;
		}
	}

	public static Context getTheContext() {
		return theContext;
	}

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
				tmp = tmp.replaceAll(" second.*$", "s"); // 1 second ago, 2 seconds ago
				tmp = tmp.replaceAll(" minute.*$", "m"); // 1 minute ago, 2 minutes ago
				tmp = tmp.replaceAll(" hour.*$", "h"); // 1 hour ago, 2 hours ago
				tmp = tmp.replaceAll(" days.*$", "d"); // yesterday
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

	public static void createPostUpdateDialog() {
		if (theContext != null) {
			MaterialDialog theDialog =
					new MaterialDialog.Builder(theContext)
							.inputType(InputType.TYPE_CLASS_TEXT)
							.positiveText(theContext.getResources().getString(R.string.save_button))
							.inputRangeRes(1, 100, R.color.mowColorAccentLight)
							.input(null, "", new MaterialDialog.InputCallback() {
								@Override
								public void onInput(@NonNull MaterialDialog dialog,
													CharSequence input) {
									if (theActivity != null) {
										theActivity.showProgressBar();
									}
									MowtweebookRestApplication.getRestClient().postUpdate(
											input.toString(),
											null,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											Log.d("postUpdate", response.toString());
											if (theActivity != null) {
												theActivity.theHomeTimelineFragment.tweets.add(0,
														MowtweebookTweet.parseJSON(3, response.toString()));
												theActivity.theHomeTimelineFragment.notifyAdapter();

												theActivity.theUserTimelineFragment.tweets.add(1, // profile is index 0
														MowtweebookTweet.parseJSON(3, response.toString()));
												theActivity.theUserTimelineFragment.notifyAdapter();

												theActivity.hideProgressBar();
											}
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
											Log.d("postUpdate", errorResponse.toString());
											if (theActivity != null) {
												theActivity.hideProgressBar();
											}
										}
									});
								}
							}).build();
			if (theDialog.getInputEditText() != null) {
				theDialog.getInputEditText().setSingleLine(false);
			}
			theDialog.show();
		}
	}

	public static void searchUserTimeline(String input) {
		if (theActivity != null) {
			theActivity.viewPager.setCurrentItem(1);
			theActivity.theUserTimelineFragment.clearAndrefreshAsync(input);
		}
	}

	public static void moreUserTimeline(String input) {
		if (theActivity != null) {
			theActivity.theUserTimelineFragment.doSearch(input);
		}
	}
}
