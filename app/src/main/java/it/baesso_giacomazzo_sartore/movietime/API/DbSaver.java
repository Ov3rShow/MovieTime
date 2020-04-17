package it.baesso_giacomazzo_sartore.movietime.API;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.database.DbHelper;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

class DbSaver {
    static void DbSaving(Context context, List<Movie> movies){

        for (Movie movie : movies)
        {
            ContentValues cv = new ContentValues();
            cv.put(DbStrings.ORIGINAL_TITLE, movie.getOriginal_title());
            cv.put(DbStrings.OVERVIEW, movie.getOverview());
            cv.put(DbStrings.POSTER_PATH, movie.getPoster_path());
            cv.put(DbStrings.BACKDROP_PATH, movie.getBackdrop_path());
            cv.put(DbStrings.VOTE_AVERAGE, movie.getVote_average());
            cv.put(DbStrings.ADULT, movie.isAdult()? 1 : 0);

            context.getContentResolver().insert(DbProvider.MOVIES_URI, cv);
        }
    }
}
