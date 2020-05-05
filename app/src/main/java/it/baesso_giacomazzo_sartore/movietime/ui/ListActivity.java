package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public class ListActivity extends AppCompatActivity implements ActivityInterface, MaterialSearchBar.OnSearchActionListener {

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    List<Movie> cachedMovies;

    int spanCount;
    int nextPageToDownload = 1;

    ProgressBar progressBar;
    Chip goToTop;
    MaterialSearchBar searchBar;

    boolean activityStartedOffline = false;

    final int WATCH_LATER_ACTIVITY_CODE = 1;
    final int DETAIL_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        prepareSpanCount();

        recyclerView = findViewById(R.id.listActivity_recyclerView);
        mLayoutManager = new GridLayoutManager(ListActivity.this, spanCount);
        recyclerView.setLayoutManager(mLayoutManager);

        progressBar = findViewById(R.id.listActivity_progressBar);
        goToTop = findViewById(R.id.listActivity_chip);

        searchBar = findViewById(R.id.listActivity_searchBar);
        searchBar.setOnSearchActionListener(this);

        searchBar.inflateMenu(R.menu.movie_menu);

        //controllo per vedere se la connessione ad internet è presente
        if(!isNetworkAvailable())
        {
            showCustomSnackbar("Connessione a internet assente", R.drawable.ic_warning_black_24dp, R.color.colorAccent, R.color.textDark);
            activityStartedOffline = true;
            nextPageToDownload = PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_page_index), 1);
        }

        //diamo il click sulle opzioni del menu
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.toolbar_parentalControl: {
                        DialogFragment dialog;

                        if (PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false)) {
                            dialog = new RemoveParentalControlDialog();
                        } else {
                            dialog = new ParentalControlDialog();
                        }

                        dialog.show(getSupportFragmentManager(), "TAG_AGGIUNTA");

                        break;
                    }
                    case R.id.toolbar_sortByName:
                    {
                        mAdapter.sortList();
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                    case R.id.toolbar_watchLater:
                    {
                        startActivityForResult(new Intent(ListActivity.this, WatchLaterActivity.class), WATCH_LATER_ACTIVITY_CODE);
                    }
                }

                return false;
            }
        });

        searchBar.setCardViewElevation(10);

        //gestione della barra di ricerca
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //metodo che utilizza la parola scritta nella barra per trovare uno/più film
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("") && i2 == 0 && i1 == 0)
                    return;

                List<Movie> movieSearched = new ArrayList<>();

                if(isNetworkAvailable())
                {
                    WebService webService = WebService.getInstance();
                    webService.searchMovie(ListActivity.this, "it-IT", charSequence.toString(), getString(R.string.api_key));
                }else{
                    Cursor movieCursor = getContentResolver().query(DbProvider.MOVIES_URI, null, MovieDbStrings.TITLE + " LIKE '%" + charSequence + "%'", null, null);

                    if(movieCursor == null)
                        return;

                    while(movieCursor.moveToNext()){

                        Movie movie = new Movie();
                        movie.setId(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings._ID)));
                        movie.setTitle(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.TITLE)));
                        movie.setOverview(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.OVERVIEW)));
                        movie.setPoster_path(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                        movie.setBackdrop_path(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                        movie.setVote_average(movieCursor.getDouble(movieCursor.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                        movie.setAdult(movieCursor.getInt(movieCursor.getColumnIndex(MovieDbStrings.ADULT)) == 1);
                        movieSearched.add(movie);
                    }

                    movieCursor.close();

                    showList(movieSearched);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        //gestione dello scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(isNetworkAvailable())
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        refreshList();
                    }
                }
                else if (!recyclerView.canScrollVertically(-1))
                {
                    goToTop.setVisibility(View.INVISIBLE);
                }

                if(recyclerView.computeVerticalScrollOffset() > 1000)
                    goToTop.setVisibility(View.VISIBLE);
            }
        });

        //utilizzo di un chip per tornare all'inizio della lista dei film
        goToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                goToTop.setVisibility(View.INVISIBLE);
            }
        });

        updateMoviesList(true);
    }

    //metodo utilizzato per l'aggiornamento della lista dei film; lista per popolarità
    void updateMoviesList(boolean clearDb) {
        if (isNetworkAvailable()) {
            WebService webService = WebService.getInstance();
            webService.getAllPopular(ListActivity.this, getString(R.string.api_key), "it-IT", nextPageToDownload, clearDb);
            nextPageToDownload++;
            PrefsManager.getInstance(ListActivity.this).setPreference(getString(R.string.pref_page_index), nextPageToDownload);
        } else {
            prepareOfflineList();
        }
    }

    //gestione della lista dei film se si è offline
    void prepareOfflineList() {
        cachedMovies = new ArrayList<>();
        Cursor movies = ListActivity.this.getContentResolver().query(DbProvider.MOVIES_URI, null, null, null, null);

        if (movies != null) {
            while (movies.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(movies.getString(movies.getColumnIndex(MovieDbStrings._ID)));
                movie.setTitle(movies.getString(movies.getColumnIndex(MovieDbStrings.TITLE)));
                movie.setOverview(movies.getString(movies.getColumnIndex(MovieDbStrings.OVERVIEW)));
                movie.setPoster_path(movies.getString(movies.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                movie.setBackdrop_path(movies.getString(movies.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                movie.setVote_average(movies.getDouble(movies.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                movie.setAdult(movies.getInt(movies.getColumnIndex(MovieDbStrings.ADULT)) == 1);

                cachedMovies.add(movie);
            }

            movies.close();

            showList(cachedMovies);
        }

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showApiCallResult(List<Movie> movies) {
        if (mAdapter != null && mAdapter.getMovies() != null)
        {

            if(activityStartedOffline)
            {
                for(Movie m : movies)
                {
                    if(!mAdapter.checkIfExists(m.getId()))
                        mAdapter.getMovies().add(m);
                }

                activityStartedOffline = false;
            }
            else
                mAdapter.getMovies().addAll(movies);

            mAdapter.notifyDataSetChanged();
        } else
            showList(movies);

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSnackBar(String text, int icon, int backgroundColor, int textIconColor) {
        showCustomSnackbar(text, icon, backgroundColor, textIconColor);
    }

    @Override
    public void refreshList() {
        updateMoviesList(false);
    }

    @Override
    public void showSearchResult(List<Movie> movies) {
        showList(movies);
    }

    //metodo per la visualizzazione della lista dei film
    void showList(List<Movie> list) {
        List<Movie> filteredList = new ArrayList<>();

        if (PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false)) {
            for (Movie movie : list) {
                if (!movie.isAdult())
                    filteredList.add(movie);
            }
        } else
            filteredList.addAll(list);

        mAdapter = new RecyclerViewFilmsAdapter(filteredList, ListActivity.this, R.layout.cell_layout);
        recyclerView.setAdapter(mAdapter);
    }

    //metodo per il controllo dell'accessibilità ad internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    //metodo per la visualizzazione/creazione della snackbar
    void showCustomSnackbar(String text, int icon, int backgroundColor, int textIconColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(16);
        snackbar.setBackgroundTint(getColor(backgroundColor));
        snackbar.setTextColor(getColor(textIconColor));

        snackbar.show();
    }

    //metodo per la gestione dello spazio delle card, gestito sia per verticale che orizzontale
    void prepareSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = 3;
        else
            spanCount = 2;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        prepareOfflineList();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mAdapter == null)
            return;

        if(requestCode == WATCH_LATER_ACTIVITY_CODE || requestCode == DETAIL_ACTIVITY_CODE)
        {
            mAdapter.notifyDataSetChanged();
        }
    }

}
