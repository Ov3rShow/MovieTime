package it.baesso_giacomazzo_sartore.movietime;

import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;

public interface ListActivityInterface {
    void showApiCallResult(PopularResult result);
    void showSnackBar(String text, int icon, int backgroundColor, int textIconColor);
    void refreshList();
}
