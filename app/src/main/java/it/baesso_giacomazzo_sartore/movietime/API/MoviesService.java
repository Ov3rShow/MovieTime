package it.baesso_giacomazzo_sartore.movietime.API;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MoviesService {
    @GET("FILMS")
    Call<List<DbStrings>> listFavourites();
}
