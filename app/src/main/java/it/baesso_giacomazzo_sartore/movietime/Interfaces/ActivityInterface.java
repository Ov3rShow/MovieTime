package it.baesso_giacomazzo_sartore.movietime.Interfaces;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.Classes.Movie;

public interface ActivityInterface {
    void showApiCallResult(List<Movie> movies);
    void showSnackBar(String text, int icon, int backgroundColor, int textIconColor);
    void refreshList();
    void showSearchResult(List<Movie> movies);
}
