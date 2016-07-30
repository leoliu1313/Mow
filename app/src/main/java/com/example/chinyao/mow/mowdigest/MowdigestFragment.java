package com.example.chinyao.mow.mowdigest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chinyao.mow.R;
import com.example.chinyao.mow.mowdigest.model.MowdigestArticleSearch;
import com.example.chinyao.mow.mowdigest.model.MowdigestNews;
import com.example.chinyao.mow.mowdigest.swipe.MowdigestFakeRecyclerAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chinyao on 7/29/2016.
 */
public class MowdigestFragment extends Fragment {
    // ButterKnife
    // http://guides.codepath.com/android/Reducing-View-Boilerplate-with-Butterknife
    @BindView(R.id.mowtube_recycler_view)
    RecyclerView theRecyclerView;

    private int mode = 1;
    private MowdigestApiInterface apiService;

    public static final String BASE_URL = "http://api.nytimes.com";
    public static final String MOST_POPULAR = "/svc/mostpopular/v2/mostviewed"; /* /all-sections/1.json */
    public static final String MOST_POPULAR_API_KEY = "fb2092b45dc44c299ecf5098b9b1209d";
    private static final int HTTP_Mode = 2;
    // 1: android-async-http
    // 2: retrofit

    public static MowdigestFragment newInstance(int mode) {
        MowdigestFragment theFragment = new MowdigestFragment();

        theFragment.mode = mode;

        return theFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Note that this can have more than RecyclerView
        SwipeRefreshLayout theRootContainer =
                (SwipeRefreshLayout) inflater.inflate(R.layout.mowtube_stream_fragment, container, false);

        // ButterKnife
        ButterKnife.bind(this, theRootContainer);

        setupRecyclerView(theRecyclerView);

        Log.d("MowdigestFragment", "5 mode " + mode);
        if (mode == 1) {
            if (HTTP_Mode == 1) {
                AsyncHttpClient client = new AsyncHttpClient();
                // Turn off Debug Log
                // client.setLoggingEnabled(false);
                String url = BASE_URL + MOST_POPULAR + "/all-sections/1.json";
                RequestParams params = new RequestParams();
                params.put("api-key", MOST_POPULAR_API_KEY);
                Log.d("MowdigestFragment", "url " + url);
                client.get(url, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d("MowdigestFragment", "onSuccess HTTP_Mode " + HTTP_Mode);
                                Log.d("MowdigestFragment",
                                        "statusCode " + statusCode);
                                MowdigestArticleSearch theSearch = MowdigestArticleSearch.parseJSON(response.toString());
                                Log.d("MowdigestFragment",
                                        "theSearch.getResults().size() " + theSearch.getResults().size());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                Log.d("MowdigestFragment", "HTTP_Mode " + HTTP_Mode);
                            }
                        }
                );
            }
            else if (HTTP_Mode == 2) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(MowdigestFragment.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                apiService = retrofit.create(MowdigestApiInterface.class);
                final Call<MowdigestArticleSearch> call = apiService.getSearch("all-sections", "1", MOST_POPULAR_API_KEY);
                call.enqueue(new Callback<MowdigestArticleSearch>() {
                    @Override
                    public void onResponse(Call<MowdigestArticleSearch> call, Response<MowdigestArticleSearch> response) {
                        Log.d("MowdigestFragment", "onResponse HTTP_Mode " + HTTP_Mode);
                        Log.d("MowdigestFragment",
                                "statusCode " + response.code());
                        MowdigestArticleSearch theSearch = response.body();
                        Log.d("MowdigestFragment",
                                "theSearch.getResults().size() " + theSearch.getResults().size());
                    }

                    @Override
                    public void onFailure(Call<MowdigestArticleSearch> call, Throwable t) {
                        Log.d("MowdigestFragment", "HTTP_Mode " + HTTP_Mode);
                    }
                });
            }
        }

        // orientation issue
        // http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments
        // http://stackoverflow.com/questions/32478968/android-viewpager-orientation-change
        // https://medium.com/@roideuniverse/android-viewpager-fragmentpageradapter-and-orientation-changes-256c23bee035#.ufb2ywv33
        // http://stackoverflow.com/questions/28982512/handling-orientation-change-with-viewpager-fragmentpageradapter
        // https://github.com/codepath/android_guides/wiki/ViewPager-with-FragmentPagerAdapter
        // http://guides.codepath.com/android/Handling-Configuration-Changes
        // http://guides.codepath.com/android/Understanding-App-Resources
        setRetainInstance(true);

        return theRootContainer;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if (mode == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(
                    new MowdigestFakeRecyclerAdapter(
                            getActivity(),
                            new ArrayList<String>(Arrays.asList(""))
                    )
            );
        }
        else if (mode == 2){
            // TODO
            // recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(
                    new MowdigestRecyclerAdapter(
                            getActivity(),
                            MowdigestNews.debug(),
                            recyclerView
                    )
            );
        }
    }
}
