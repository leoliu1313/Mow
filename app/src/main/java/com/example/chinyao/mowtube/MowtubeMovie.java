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
    public ArrayList<String> genres;
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
            b.genres = new ArrayList<String>();
            Integer id;
            for (int i=0; i < jsonArray.length(); i++) {
                id = new Integer((Integer) jsonArray.get(i));
                b.genres.add(genresIdToName(id));
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
                new ArrayList<String>(
                        Arrays.asList(
                            "The Legend of Tarzan",
                            "Independence Day: Resurgence",
                            "Finding Dory",
                            "Ice Age: Collision Course",
                            "Ghostbusters",
                            "The Purge: Election Year",
                            "The Secret Life of Pets",
                            "Star Trek Beyond",
                            "Central Intelligence",
                            "Now You See Me 2"
                        )
                )
        );
    }

    // TODO: bad hard code implementation
    // https://api.themoviedb.org/3/genre/movie/list?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed
    public static String genresIdToName(int id) {
        switch (id) {
            case 28:
                return "Action";
            case 12:
                return "Adventure";
            case 16:
                return "Animation";
            case 35:
                return "Comedy";
            case 80:
                return "Crime";
            case 99:
                return "Documentary";
            case 18:
                return "Drama";
            case 10751:
                return "Family";
            case 14:
                return "Fantasy";
            case 10769:
                return "Foreign";
            case 36:
                return "History";
            case 27:
                return "Horror";
            case 10402:
                return "Music";
            case 9648:
                return "Mystery";
            case 10749:
                return "Romance";
            case 878:
                return "Science Fiction";
            case 10770:
                return "TV Movie";
            case 53:
                return "Thriller";
            case 10752:
                return "War";
            case 37:
                return "Western";
            default:
                return "";
        }
    }
}
