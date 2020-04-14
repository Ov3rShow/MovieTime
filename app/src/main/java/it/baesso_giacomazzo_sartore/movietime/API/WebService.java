package it.baesso_giacomazzo_sartore.movietime.API;

import android.util.Log;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
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
                .baseUrl("https://themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        todoService = retrofit.create(MoviesService.class);
    }

    public static WebService getInstance(){
        if (instance == null)
            instance = new WebService();
        return instance;
    }

    public void getAllFavourites(final IwebService serviceListener){
        todoService.listFavourites().enqueue(new Callback<List<DbStrings>>() {
            @Override
            public void onResponse(Call<List<DbStrings>> call, Response<List<DbStrings>> response) {
                Log.e("Test","Server response");
            }

            @Override
            public void onFailure(Call<List<DbStrings>> call, Throwable t) {
                Log.e("Test","Server failure");
            }
        });
    }
}
