package it.baesso_giacomazzo_sartore.movietime.API;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

class DbSaver {
    static void DbSaving(Context context, List<Movie> movies){

        context.getContentResolver().delete(DbProvider.MOVIES_URI, MovieDbStrings.WATCH_LATER + " = 0", null);

        for (Movie movie : movies)
        {
            ContentValues cv = new ContentValues();
            cv.put(MovieDbStrings._ID, movie.getId());
            cv.put(MovieDbStrings.ORIGINAL_TITLE, movie.getOriginal_title().replace("'", ""));
            cv.put(MovieDbStrings.OVERVIEW, movie.getOverview().replace("'", ""));
            cv.put(MovieDbStrings.POSTER_PATH, movie.getPoster_path());
            cv.put(MovieDbStrings.BACKDROP_PATH, movie.getBackdrop_path());
            cv.put(MovieDbStrings.VOTE_AVERAGE, movie.getVote_average());
            cv.put(MovieDbStrings.ADULT, movie.isAdult()? 1 : 0);
            cv.put(MovieDbStrings.WATCH_LATER, 0);

            try
            {
                context.getContentResolver().insert(DbProvider.MOVIES_URI, cv);
            }
            catch (Exception ex)
            {
                if(ex.getMessage() != null)
                    Log.w("Warning ", ex.getMessage());
            }
        }
    }
}
