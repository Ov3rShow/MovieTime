package it.baesso_giacomazzo_sartore.movietime.API;

import android.content.Context;
import android.graphics.Movie;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;
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

    public void getAllPopular(final Context context, String key, String localization, final int page){

        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", key);
        parameters.put("language", localization);
        parameters.put("page", String.valueOf(page));

        todoService.listPopular(parameters).enqueue(new Callback<PopularResult>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<PopularResult> call, Response<PopularResult> response) {

                if(context instanceof ListActivityInterface)
                    ((ListActivityInterface)context).showApiCallResult(response.body());

                if(response.body() != null && response.body().getResults() != null /*&& (page == 1 && DbSaver.dbSaveTimeCheck(context))*/)
                {
                    if((page == 1 && DbSaver.dbSaveTimeCheck(context)) || page > 1)
                        DbSaver.DbSaving(context, response.body().getResults(), page);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<PopularResult> call, Throwable t) {
                Log.e("Filtro ", t.getMessage());
            }
        });
    }
}
