package it.baesso_giacomazzo_sartore.movietime.database;

import android.provider.BaseColumns;

public class DbStrings implements BaseColumns {

     public static final String TABLE_NAME = "movie";
     public static final String ORIGINAL_TITLE = "original_title";      //title
     public static final String OVERVIEW = "overview";                  //trama
     public static final String POSTER_PATH = "poster_path";            //immagine lista
     public static final String BACKDROP_PATH = "backdrop_path";        //immagine dettaglio
     public static final String VOTE_AVERAGE = "vote_average";          //voto
     public static final String ADULT = "adult";                        //film categorizzato come per "adulti" 0/1

     public static final String CREATE_TABLE = "create table movie (" + _ID
             + " integer primary key, "
             + ORIGINAL_TITLE + " text, "
             + OVERVIEW + " text, "
             + POSTER_PATH + " text, "
             + BACKDROP_PATH + " text, "
             + VOTE_AVERAGE + " REAL, "
             + ADULT + " INTEGER)";
}
