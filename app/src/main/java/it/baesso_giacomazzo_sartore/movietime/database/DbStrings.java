package it.baesso_giacomazzo_sartore.movietime.database;

import android.provider.BaseColumns;

public class DbStrings implements BaseColumns {
     static final String TITOLO = "titolo";
     static final String TRAMA = "trama";
     static final String POSTER = "poster";
     static final String IMMAGINE = "immagine";

     static final String CREATE_TABLE = "create table film (" + _ID + " integer primary key, " + TITOLO + " text, " + TRAMA + " text, " + POSTER + " text, " + IMMAGINE + " text)";
}
