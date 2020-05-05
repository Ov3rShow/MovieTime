package it.baesso_giacomazzo_sartore.movietime.API;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import it.baesso_giacomazzo_sartore.movietime.DetailActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;
import it.baesso_giacomazzo_sartore.movietime.objects.SearchResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class WebService {
    private static WebService instance;
    private MoviesService todoService;

    private WebService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        todoService = retrofit.create(MoviesService.class);
    }

    public static WebService getInstance(){
        if (instance == null)
            instance = new WebService();
        return instance;
    }

    public void getAllPopular(final Context context, String key, String localization, final int page, final boolean clearDb){

        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", key);
        parameters.put("language", localization);
        parameters.put("page", String.valueOf(page));

        todoService.listPopular(parameters).enqueue(new Callback<PopularResult>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<PopularResult> call, Response<PopularResult> response) {

                if(context instanceof ActivityInterface)
                    ((ActivityInterface)context).showApiCallResult(response.body().getResults());

                if(response.body() != null && response.body().getResults() != null)
                {
                    if((clearDb) || page > 1)
                        DbSaver.DbSaving(context, response.body().getResults(), page, clearDb);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<PopularResult> call, Throwable t) {
                Log.e("Filtro ", t.getMessage());
            }
        });

    }

    public void getSimilarMovies(final Context context, String movieId, String key, String localization){

        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", key);
        parameters.put("language", localization);

        todoService.getSimilarMovies(movieId, parameters).enqueue(new Callback<PopularResult>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<PopularResult> call, Response<PopularResult> response) {

                if(context instanceof DetailActivityInterface && response.body() != null)
                    ((DetailActivityInterface)context).showSimilarMovies(response.body().getResults());
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<PopularResult> call, Throwable t) {
                Log.e("Filtro ", t.getMessage());
            }
        });
    }

    public void searchMovie(final Context context, String localization, String query, String key){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", key);
        parameters.put("language", localization);
        parameters.put("query", query);
        parameters.put("include_adult", String.valueOf(PrefsManager.getInstance(context).getPreference(context.getString(R.string.pref_parental_control_enabled), true)));

        Log.w("SEARCH", "ENTRATO");

        todoService.searchMovie(parameters).enqueue(new Callback<SearchResult>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                Log.w("SEARCH", response.message());
                if(context instanceof ActivityInterface && response.body() != null)
                {
                    ((ActivityInterface)context).showSearchResult(response.body().getResults());
                    Log.w("SEARCH", "ENTRATO1");
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.e("Filtro ", t.getMessage());
            }
        });
    }
}
