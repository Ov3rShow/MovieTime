package it.baesso_giacomazzo_sartore.movietime;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public interface ActivityInterface {
    void showApiCallResult(List<Movie> movies);
    void showSnackBar(String text, int icon, int backgroundColor, int textIconColor);
    void refreshList();
    void showSearchResult(List<Movie> movies);
}
