package it.baesso_giacomazzo_sartore.movietime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper helper;

    private static final String DB_NAME = "movie.db";
    private static final int VERSION = 1;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DbHelper getInstance(Context context)
    {
        if(helper == null)
            helper = new DbHelper(context);

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        getWritableDatabase().execSQL(DbStrings.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
