package com.example.chinyao.mow.mowdigest;

import com.example.chinyao.mow.mowdigest.model.MowdigestArticleSearch;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chinyao on 7/30/2016.
 */
public interface MowdigestApiInterface {
    @GET(MowdigestFragment.MOST_POPULAR)
    Call<MowdigestArticleSearch> getSearch(@Query("api-key") String api_key);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(MowdigestFragment.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}