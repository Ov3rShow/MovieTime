package it.baesso_giacomazzo_sartore.movietime.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.Database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.Database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.Classes.Movie;

public class DbSaver {

    public static void DbSaving(Context context, List<Movie> movies, int page, boolean clearDb){

        Log.w("PULIZIA DB", String.valueOf(DbSaver.dbSaveTimeCheck(context)));

        if(clearDb && DbSaver.dbSaveTimeCheck(context))
            context.getContentResolver().delete(DbProvider.MOVIES_URI, MovieDbStrings.WATCH_LATER + " = 0", null);

        for (Movie movie : movies)
        {
            ContentValues cv = new ContentValues();
            cv.put(MovieDbStrings._ID, movie.getId());
            cv.put(MovieDbStrings.TITLE, movie.getTitle().replace("'", ""));
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
                Log.w("Errore", ex.getMessage());
            }
        }
    }

    static boolean dbSaveTimeCheck(Context context)
    {
        long lastSaveTime = PrefsManager.getInstance(context).getPreference(context.getString(R.string.save_cache_db), Long.valueOf(0));

        //se il valore è 0 questa è la prima volta quindi devo salvare i film
        if(lastSaveTime == 0)
        {
            PrefsManager.getInstance(context).setPreference(context.getString(R.string.save_cache_db), System.currentTimeMillis());
            return true;
        }
        //altrimenti li aggiorno se è passato almeno un giorno dall'ultimo salvataggio
        else if(System.currentTimeMillis() - lastSaveTime > 60 * 60 * 24 * 1000) // 1 volta al giorno
        {
            PrefsManager.getInstance(context).setPreference(context.getString(R.string.save_cache_db), System.currentTimeMillis());
            return true;
        }

        return false;
    }

    public static void DbSavingSingle(Context context, Movie movie, boolean watchLater){

        ContentValues cv = new ContentValues();
        cv.put(MovieDbStrings._ID, movie.getId());
        cv.put(MovieDbStrings.TITLE, movie.getTitle().replace("'", ""));
        cv.put(MovieDbStrings.OVERVIEW, movie.getOverview().replace("'", ""));
        cv.put(MovieDbStrings.POSTER_PATH, movie.getPoster_path());
        cv.put(MovieDbStrings.BACKDROP_PATH, movie.getBackdrop_path());
        cv.put(MovieDbStrings.VOTE_AVERAGE, movie.getVote_average());
        cv.put(MovieDbStrings.ADULT, movie.isAdult()? 1 : 0);
        cv.put(MovieDbStrings.WATCH_LATER, watchLater? 1 : 0);

        try
        {
            context.getContentResolver().insert(DbProvider.MOVIES_URI, cv);
        }
        catch (Exception ex)
        {
            Log.w("Errore", ex.getMessage());
        }
    }
}
