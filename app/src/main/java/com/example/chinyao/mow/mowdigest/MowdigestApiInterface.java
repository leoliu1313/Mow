package com.example.chinyao.mow.mowdigest;

import com.example.chinyao.mow.mowdigest.model.MowdigestArticleSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by chinyao on 7/30/2016.
 */
public interface MowdigestApiInterface {
    // GET Retrofit.Builder().baseUrl() + the following link filled with @Path + ?@Query=
    @GET(MowdigestFragment.MOST_POPULAR + "/{section}/{time_period}.json")
    Call<MowdigestArticleSearch> getSearch(@Path("section") String section,
                                           @Path("time_period") String time_period,
                                           @Query("api-key") String api_key
    );
}