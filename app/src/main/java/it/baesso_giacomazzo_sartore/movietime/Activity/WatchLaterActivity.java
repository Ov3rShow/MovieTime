package it.baesso_giacomazzo_sartore.movietime.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.Interfaces.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.Database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.Database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.Classes.Movie;
import it.baesso_giacomazzo_sartore.movietime.Adapter.RecyclerViewFilmsAdapter;

public class WatchLaterActivity extends AppCompatActivity implements ActivityInterface {

    //activity del watch later, lista di film che mettiamo "da vedere"

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    LinearLayout emptyListAlert;

    int spanCount;

    final int DETAIL_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Guarda più tardi");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
        }

        prepareSpanCount();

        emptyListAlert = findViewById(R.id.watchLaterActivity_emptyListAlert);
        recyclerView = findViewById(R.id.watchLaterActivity_recyclerView);
        mLayoutManager = new GridLayoutManager(WatchLaterActivity.this, spanCount);
        recyclerView.setLayoutManager(mLayoutManager);

        showMovies();
    }

    void showMovies()
    {
        List<Movie> moviesList = new ArrayList<>();
        Cursor movies = WatchLaterActivity.this.getContentResolver().query(DbProvider.MOVIES_URI, null, MovieDbStrings.WATCH_LATER + " = 1", null, null);

        if (movies != null) {

            if(movies.getCount() == 0)
            {
                emptyListAlert.setVisibility(View.VISIBLE);
            }
            else
            {
                while (movies.moveToNext()) {
                    Movie movie = new Movie();
                    movie.setId(movies.getString(movies.getColumnIndex(MovieDbStrings._ID)));
                    movie.setTitle(movies.getString(movies.getColumnIndex(MovieDbStrings.TITLE)));
                    movie.setOverview(movies.getString(movies.getColumnIndex(MovieDbStrings.OVERVIEW)));
                    movie.setPoster_path(movies.getString(movies.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                    movie.setBackdrop_path(movies.getString(movies.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                    movie.setVote_average(movies.getDouble(movies.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                    movie.setAdult(movies.getInt(movies.getColumnIndex(MovieDbStrings.ADULT)) == 1);

                    moviesList.add(movie);
                }
            }
            movies.close();

            mAdapter = new RecyclerViewFilmsAdapter(moviesList, WatchLaterActivity.this, R.layout.cell_layout, true);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }else{
            emptyListAlert.setVisibility(View.VISIBLE);
        }
    }

    void prepareSpanCount() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = 3;
        else
            spanCount = 2;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showApiCallResult(List<Movie> result) {

    }

    @Override
    public void showSnackBar(String text, int icon, int backgroundColor, int textIconColor) {
        showCustomSnackbar(text, icon, backgroundColor, textIconColor);
    }

    @Override
    public void refreshList() {
        showMovies();
    }

    @Override
    public void showSearchResult(List<Movie> movies) {

    }

    void showCustomSnackbar(String text, int icon, int backgroundColor, int textIconColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        textView.setCompoundDrawablePadding(16);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTypeface(null, Typeface.BOLD);
        snackbar.setBackgroundTint(getColor(backgroundColor));
        snackbar.setTextColor(getColor(textIconColor));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mAdapter == null)
            return;

        if(requestCode == DETAIL_ACTIVITY_CODE)
        {
            showMovies();
        }
    }
}
