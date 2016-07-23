package com.example.chinyao.mowtube;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by chinyao on 7/23/2016.
 */
public class MowtubeMovie {
    public String poster_path;
    public Boolean adult;
    public String overview;
    public String release_date;
    public ArrayList<Integer> genre_ids;
    public Integer id;
    public String original_title;
    public String original_language;
    public String title;
    public String backdrop_path;
    public Double popularity;
    public Integer vote_count;
    public Boolean video;
    public Double vote_average;

    // Decodes business json into business model object
    public static MowtubeMovie fromJson(JSONObject jsonObject) {
        MowtubeMovie b = new MowtubeMovie();
        // Deserialize json into object fields
        try {
            b.poster_path = jsonObject.getString("poster_path");
            b.adult = jsonObject.getBoolean("adult");
            b.overview = jsonObject.getString("overview");
            b.release_date = jsonObject.getString("release_date");
            JSONArray jsonArray = jsonObject.getJSONArray("genre_ids");
            b.genre_ids = new ArrayList<Integer>();
            Integer id;
            for (int i=0; i < jsonArray.length(); i++) {
                id = new Integer((Integer) jsonArray.get(i));
                b.genre_ids.add(id);
            }
            b.id = jsonObject.getInt("id");
            b.original_title = jsonObject.getString("original_title");
            b.original_language = jsonObject.getString("original_language");
            b.title = jsonObject.getString("title");
            b.backdrop_path = jsonObject.getString("backdrop_path");
            b.popularity = jsonObject.getDouble("popularity");
            b.vote_count = jsonObject.getInt("vote_count");
            b.video = jsonObject.getBoolean("video");
            b.vote_average = jsonObject.getDouble("vote_average");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return b;
    }
    
    // Decodes array of movie json results into movie model objects
    public static ArrayList<MowtubeMovie> fromJson(JSONArray jsonArray) {
        JSONObject movieJson;
        ArrayList<MowtubeMovie> movies = new ArrayList<MowtubeMovie>(jsonArray.length());
        // Process each result in json array, decode and convert to movie object
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                movieJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            MowtubeMovie movie = MowtubeMovie.fromJson(movieJson);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
    }

    public static ArrayList<MowtubeMovie> fromArrayList(ArrayList<String> input) {
        JSONObject movieJson;
        ArrayList<MowtubeMovie> movies = new ArrayList<MowtubeMovie>(input.size());
        // Process each result in json array, decode and convert to movie object
        for (int i=0; i < input.size(); i++) {
            MowtubeMovie movie = new MowtubeMovie();
            movie.title = input.get(i);
            movies.add(movie);
        }
        return movies;
    }

    public static ArrayList<MowtubeMovie> generateDebugArrayList() {
        return MowtubeMovie.fromArrayList(
                (ArrayList<String>) Arrays.asList(
                        "The Legend of Tarzan",
                        "Independence Day: Resurgence",
                        "Finding Dory",
                        "Ice Age: Collision Course",
                        "Ghostbusters",
                        "The Purge: Election Year",
                        "The Secret Life of Pets",
                        "Star Trek Beyond",
                        "Central Intelligence",
                        "Now You See Me 2")
        );
    }
}
