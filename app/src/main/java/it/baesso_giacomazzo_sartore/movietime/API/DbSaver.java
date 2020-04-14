package it.baesso_giacomazzo_sartore.movietime.API;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.baesso_giacomazzo_sartore.movietime.database.DbHelper;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;

public class DbSaver {
    public void DbSaving(){
        SQLiteDatabase vDb = new DbHelper(DbSaver.this).getWritableDatabase();
        Cursor vCursor = vDb.query(DbStrings.TABLE_NAME,
                new String[]{DbStrings._ID, DbStrings.ORIGINAL_TITLE, DbStrings.OVERVIEW, DbStrings.POSTER_PATH, DbStrings.BACKDROP_PATH, DbStrings.VOTE_AVERAGE, DbStrings.ADULT},
                null, null, null, null, null);
        vCursor.getContentResolver().query(DbProvider.MOVIES_URI,null,null,null);
    }
}
