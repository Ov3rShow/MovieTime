package it.baesso_giacomazzo_sartore.movietime.database;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

public class MovieDbStrings {

     @SerializedName("id")
     public static final String _ID = "_id";
     public static final String TABLE_NAME = "movie";
     public static final String TITLE = "title";                        //title
     public static final String OVERVIEW = "overview";                  //trama
     public static final String DATE = "date";                          //data di rilascio
     public static final String POSTER_PATH = "poster_path";            //immagine lista
     public static final String BACKDROP_PATH = "backdrop_path";        //immagine dettaglio
     public static final String VOTE_AVERAGE = "vote_average";          //voto
     public static final String ADULT = "adult";                        //film categorizzato come per "adulti" 0/1
     public static final String WATCH_LATER = "watchLater";             //contrassegnato come da vedere 0 no, 1 si

     public static final String CREATE_TABLE = "create table movie ("
             + "internal_id integer primary key, "
             + _ID + " integer unique, "
             + TITLE + " text, "
             + OVERVIEW + " text, "
             + DATE + " text, "
             + POSTER_PATH + " text, "
             + BACKDROP_PATH + " text, "
             + VOTE_AVERAGE + " REAL, "
             + ADULT + " INTEGER, "
             + WATCH_LATER + " INTEGER)";
}
