package it.baesso_giacomazzo_sartore.movietime.database;

import android.provider.BaseColumns;

public class DbStrings implements BaseColumns {

     public static final String TABLE_NAME = "movie";
     static final String ORIGINAL_TITLE = "original_title";      //title
     static final String OVERVIEW = "overview";                  //trama
     static final String POSTER_PATH = "poster_path";            //immagine lista
     static final String BACKDROP_PATH = "backdrop_path";        //immagine dettaglio
     static final String VOTE_AVERAGE = "vote_average";          //voto
     static final String ADULT = "adult";                        //film categorizzato come per "adulti" 0/1

     static final String CREATE_TABLE = "create table film (" + _ID
             + " integer primary key, "
             + ORIGINAL_TITLE + " text, "
             + OVERVIEW + " text, "
             + POSTER_PATH + " text, "
             + BACKDROP_PATH + " text, "
             + VOTE_AVERAGE + " REAL, "
             + ADULT + " INTEGER)";
}
