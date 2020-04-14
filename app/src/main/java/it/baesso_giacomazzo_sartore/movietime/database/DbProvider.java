package it.baesso_giacomazzo_sartore.movietime.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DbProvider extends ContentProvider {


    public static final String AUTORITY = "it.baesso_giacomazzo_sartore.movietime.database.DbProvider";
    public static final String BASE_PATH_MOVIES = "movies";
    public static final int ALL_MOVIE = 1;
    public static final int SINGLE_MOVIE = 0;
    public static final String MIME_TYPE_MOVIES = ContentResolver.CURSOR_DIR_BASE_TYPE + "vnd.all_movies";
    public static final String MIME_TYPE_MOVIE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "vnd.single_movie";
    public static final Uri MOVIES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTORITY + "/" + BASE_PATH_MOVIES);
    private DbHelper mDb;
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES, ALL_MOVIE);
        mUriMatcher.addURI(AUTORITY, BASE_PATH_MOVIES + "/#", SINGLE_MOVIE);
    }


    @Override
    public boolean onCreate() {
        mDb = DbHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase vDb = mDb.getReadableDatabase();
        SQLiteQueryBuilder vBuilder = new SQLiteQueryBuilder();
        switch (mUriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                vBuilder.setTables(DbStrings.TABLE_NAME);
                vBuilder.appendWhere(DbStrings._ID + " = " + uri.getLastPathSegment());
                break;
            case ALL_MOVIE:
                vBuilder.setTables(DbStrings.TABLE_NAME);
                break;
        }

        Cursor vCursor = vBuilder.query(vDb, projection, selection, selectionArgs, null, null, sortOrder);
        vCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return vCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case SINGLE_MOVIE:
                return MIME_TYPE_MOVIE;

            case ALL_MOVIE:
                return MIME_TYPE_MOVIES;

        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        if (mUriMatcher.match(uri) == ALL_MOVIE) {
            SQLiteDatabase vDb = mDb.getWritableDatabase();
            long vResult = vDb.insert(DbStrings.TABLE_NAME, null, values);
            String vResultString = ContentResolver.SCHEME_CONTENT + "://" + BASE_PATH_MOVIES + "/" + vResult;
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(vResultString);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String vTable = "", vQuery = "";
        SQLiteDatabase vDb = mDb.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case ALL_MOVIE:
                vTable = DbStrings.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_MOVIE:
                vTable = DbStrings.TABLE_NAME;
                vQuery = DbStrings._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    vQuery += " AND " + selection;
                }
                break;
        }
        int vDeletedRows = vDb.delete(vTable, vQuery, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return vDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String vTable = "", vQuery = "";
        SQLiteDatabase vDb = mDb.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case ALL_MOVIE:
                vTable = DbStrings.TABLE_NAME;
                vQuery = selection;
                break;
            case SINGLE_MOVIE:
                vTable = DbStrings.TABLE_NAME;
                vQuery = DbStrings._ID + " = " + uri.getLastPathSegment();
                if (selection != null) {
                    vQuery += " AND " + selection;
                }
                break;
        }
        int vUpdatedRows = vDb.update(vTable, values, vQuery, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return vUpdatedRows;
    }
}
