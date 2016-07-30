package com.example.chinyao.mow.mowdigest;

import com.example.chinyao.mow.mowdigest.model.MowdigestArticleSearch;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by chinyao on 7/30/2016.
 */
public interface MowdigestApiInterface {
    @GET("/svc/mostpopular/v2/all-sections/arts/1.json")
    public Call<MowdigestArticleSearch> getSearch();

    /*
    String BASE_URL = "http://api.nytimes.com/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
            */
}