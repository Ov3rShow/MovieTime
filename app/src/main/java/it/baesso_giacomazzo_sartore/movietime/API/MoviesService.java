package it.baesso_giacomazzo_sartore.movietime.API;

import java.util.Map;

import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface MoviesService {
    /*@GET("/3/movie/popular?api_key={key}&language={localization}&page={page}")
    Call<List<Movie>> listPopular(@Query("key") String key, @Query("localization") String localization, @Query("page") int page);*/

    @GET("/3/movie/popular")
    Call<PopularResult> listPopular(@QueryMap Map<String, String> parameters);
}
