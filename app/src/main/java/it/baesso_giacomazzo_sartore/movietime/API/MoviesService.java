package it.baesso_giacomazzo_sartore.movietime.API;

import java.util.Map;

import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;
import it.baesso_giacomazzo_sartore.movietime.objects.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MoviesService {

    @GET("/3/movie/popular")
    Call<PopularResult> listPopular(@QueryMap Map<String, String> parameters);

    @GET("/3/movie/{movie_id}/similar")
    Call<PopularResult> getSimilarMovies(@Path("movie_id") String movieId, @QueryMap Map<String, String> parameters);

    @GET("/3/search/movie")
    Call<SearchResult> searchMovie(@QueryMap Map<String, String> parameters);

}
