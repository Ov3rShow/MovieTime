package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;

public class ListActivity extends AppCompatActivity implements ListActivityInterface {

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    List<Movie> cachedMovies;

    int spanCount;
    int nextPageToDownload = 1;

    ProgressBar progressBar;
    Chip goToTop;

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

    void updateMoviesList()
    {
        if(isNetworkAvailable())
        {
            WebService webService = WebService.getInstance();
            webService.getAllPopular(ListActivity.this, getString(R.string.api_key), "it-IT", nextPageToDownload);
            nextPageToDownload++;
        }
        else
        {
            prepareOfflineList();
        }
    }

    void prepareOfflineList()
    {
        cachedMovies = new ArrayList<>();
        Cursor movies = ListActivity.this.getContentResolver().query(DbProvider.MOVIES_URI, null,null,null,null);

        if(movies != null)
        {
            while(movies.moveToNext())
            {
                Movie movie = new Movie();
                movie.setId(movies.getString(movies.getColumnIndex(DbStrings._ID)));
                movie.setOriginal_title(movies.getString(movies.getColumnIndex(DbStrings.ORIGINAL_TITLE)));
                movie.setOverview(movies.getString(movies.getColumnIndex(DbStrings.OVERVIEW)));
                movie.setPoster_path(movies.getString(movies.getColumnIndex(DbStrings.POSTER_PATH)));
                movie.setBackdrop_path(movies.getString(movies.getColumnIndex(DbStrings.BACKDROP_PATH)));
                movie.setVote_average(movies.getDouble(movies.getColumnIndex(DbStrings.VOTE_AVERAGE)));
                movie.setAdult(movies.getInt(movies.getColumnIndex(DbStrings.ADULT)) == 1);

                cachedMovies.add(movie);
            }

            showList(cachedMovies);
        }

        progressBar.setVisibility(View.INVISIBLE);
        showCustomSnackbar("Connessione a internet assente", R.drawable.ic_warning_black_24dp, R.color.colorAccent);
    }

    @Override
    public void showApiCallResult(PopularResult result) {
        if(mAdapter != null && mAdapter.getMovies() != null)
        {
            mAdapter.getMovies().addAll(result.getResults());
            mAdapter.notifyDataSetChanged();
            goToTop.setVisibility(View.VISIBLE);
        }
        else
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

    void showList(List<Movie> list)
    {
        List<Movie> filteredList = new ArrayList<>();

        if(PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false))
        {
            for (Movie movie : list)
            {
                if(!movie.isAdult())
                    filteredList.add(movie);
            }
        }
        else
            filteredList.addAll(list);

        mAdapter = new RecyclerViewFilmsAdapter(filteredList, ListActivity.this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null)
            return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    void showCustomSnackbar(String text, int icon, int backgroundColor)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(16);
        snackbar.setBackgroundTint(getColor(backgroundColor));
        snackbar.setTextColor(getColor(R.color.textDark));
        snackbar.show();
    }

    void prepareSpanCount()
    {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = 3;
         else
            spanCount = 2;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.toolbar_parentalControl:
            {
                DialogFragment dialog;

                if(PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false))
                {
                    dialog = new RemoveParentalControlDialog();
                }
                else
                {
                    dialog = new ParentalControlDialog();
                }

                dialog.show(getSupportFragmentManager(), "TAG_AGGIUNTA");

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Movie> sortList(List<Movie> list) {

        for (Movie movie : list)
        {
            Collections.sort(list, new Comparator<Movie>() {
                @Override
                public int compare(Movie movie1, Movie movie2) {
                    String s1 = movie1.getOriginal_title();
                    String s2 = movie2.getOriginal_title();
                    return s1.compareToIgnoreCase(s2);
                }
            });
        }
        return list;
    }

    /*
    void showList(List<Movie> list)
    {
        List<Movie> filteredList = new ArrayList<>();

        if(PrefsManager.getInstance(ListActivity.this).getPreference(getString(R.string.pref_parental_control_enabled), false))
        {
            for (Movie movie : list)
            {
                if(!movie.isAdult())
                    filteredList.add(movie);
            }
        }
        else
            filteredList.addAll(list);

        mAdapter = new RecyclerViewFilmsAdapter(filteredList, ListActivity.this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    */
}
