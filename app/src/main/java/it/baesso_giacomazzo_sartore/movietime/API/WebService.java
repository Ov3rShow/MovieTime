package it.baesso_giacomazzo_sartore.movietime.API;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public void getAllPopular(final ListActivityInterface listActivityInterface, String key, String localization, int page){

        Map<String, String> parameters = new HashMap<>();
        parameters.put("api_key", key);
        parameters.put("language", localization);
        parameters.put("page", String.valueOf(page));

        todoService.listPopular(parameters).enqueue(new Callback<PopularResult>() {
            @Override
            public void onResponse(Call<PopularResult> call, Response<PopularResult> response) {
                listActivityInterface.showApiCallResult(response.body());

                //TODO avviare salvataggio su db se è passato il tempo limite
            }

            @Override
            public void onFailure(Call<PopularResult> call, Throwable t) {
                Log.e("Filtro ",t.getMessage());
            }
        });
    }
}
