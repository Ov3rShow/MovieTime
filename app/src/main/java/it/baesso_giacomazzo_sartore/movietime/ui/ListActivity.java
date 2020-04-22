package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;

public class ListActivity extends AppCompatActivity implements ListActivityInterface, MaterialSearchBar.OnSearchActionListener {

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    List<Movie> cachedMovies;

    int spanCount;
    int nextPageToDownload = 1;

    ProgressBar progressBar;
    Chip goToTop;
    MaterialSearchBar searchBar;

    //enum per gestire se la lista è ordinata

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

                        //imposti lo stato di ordinamento
                    }
                }

                return false;
            }
        });

        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
                //cerca

                List<Movie> movieSearched = new ArrayList<Movie>();


                Cursor movieCursor = getContentResolver().query(DbProvider.MOVIES_URI, null, MovieDbStrings.ORIGINAL_TITLE + " LIKE =?",
                        new String[]{String.valueOf(charSequence)}, null);

               for (int x=0;x<movieCursor.getCount();x++){

                   Movie movie = new Movie();
                   movie.setId(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings._ID)));
                   movie.setOriginal_title(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.ORIGINAL_TITLE)));
                   movie.setOverview(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.OVERVIEW)));
                   movie.setPoster_path(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                   movie.setBackdrop_path(movieCursor.getString(movieCursor.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                   movie.setVote_average(movieCursor.getDouble(movieCursor.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                   movie.setAdult(movieCursor.getInt(movieCursor.getColumnIndex(MovieDbStrings.ADULT)) == 1);
                   movieSearched.add(movie);
               }

                showList(movieSearched);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    progressBar.setVisibility(View.VISIBLE);
                    refreshList();
                }
            }
        });

        goToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
                goToTop.setVisibility(View.INVISIBLE);
            }
        });

        updateMoviesList();
    }

    void updateMoviesList() {
        if (isNetworkAvailable()) {
            WebService webService = WebService.getInstance();
            webService.getAllPopular(ListActivity.this, getString(R.string.api_key), "it-IT", nextPageToDownload);
            nextPageToDownload++;
        } else {
            prepareOfflineList();
        }
    }

    void prepareOfflineList() {
        cachedMovies = new ArrayList<>();
        Cursor movies = ListActivity.this.getContentResolver().query(DbProvider.MOVIES_URI, null, null, null, null);

        if (movies != null) {
            while (movies.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(movies.getString(movies.getColumnIndex(MovieDbStrings._ID)));
                movie.setOriginal_title(movies.getString(movies.getColumnIndex(MovieDbStrings.ORIGINAL_TITLE)));
                movie.setOverview(movies.getString(movies.getColumnIndex(MovieDbStrings.OVERVIEW)));
                movie.setPoster_path(movies.getString(movies.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                movie.setBackdrop_path(movies.getString(movies.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                movie.setVote_average(movies.getDouble(movies.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                movie.setAdult(movies.getInt(movies.getColumnIndex(MovieDbStrings.ADULT)) == 1);

                cachedMovies.add(movie);
            }

            showList(cachedMovies);
        }

        progressBar.setVisibility(View.INVISIBLE);
        showCustomSnackbar("Connessione a internet assente", R.drawable.ic_warning_black_24dp, R.color.colorAccent);
    }

    @Override
    public void showApiCallResult(PopularResult result) {
        if (mAdapter != null && mAdapter.getMovies() != null) {
            mAdapter.getMovies().addAll(result.getResults());
            mAdapter.notifyDataSetChanged();
            goToTop.setVisibility(View.VISIBLE);
        } else
            showList(result.getResults());

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSnackBar(String text, int icon, int backgroundColor) {
        showCustomSnackbar(text, icon, backgroundColor);
    }

    @Override
    public void refreshList() {
        updateMoviesList();
    }

    void showList(List<Movie> list) {
        List<Movie> filteredList = new ArrayList<>();

        if (PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false)) {
            for (Movie movie : list) {
                if (!movie.isAdult())
                    filteredList.add(movie);
            }
        } else
            filteredList.addAll(list);

        mAdapter = new RecyclerViewFilmsAdapter(filteredList, ListActivity.this);
        recyclerView.setAdapter(mAdapter);
        //controlli se la lista è già stata ordinata e in caso la ordini
        mAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    void showCustomSnackbar(String text, int icon, int backgroundColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(16);
        snackbar.setBackgroundTint(getColor(backgroundColor));
        snackbar.setTextColor(getColor(R.color.textDark));
        snackbar.show();
    }

    void prepareSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = 3;
        else
            spanCount = 2;
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Toast.makeText(ListActivity.this, "Search: " + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
