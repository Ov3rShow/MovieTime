package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public class WatchLaterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    int spanCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_later);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("Da vedere");
        }


        prepareSpanCount();

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
            while (movies.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(movies.getString(movies.getColumnIndex(MovieDbStrings._ID)));
                movie.setOriginal_title(movies.getString(movies.getColumnIndex(MovieDbStrings.ORIGINAL_TITLE)));
                movie.setOverview(movies.getString(movies.getColumnIndex(MovieDbStrings.OVERVIEW)));
                movie.setPoster_path(movies.getString(movies.getColumnIndex(MovieDbStrings.POSTER_PATH)));
                movie.setBackdrop_path(movies.getString(movies.getColumnIndex(MovieDbStrings.BACKDROP_PATH)));
                movie.setVote_average(movies.getDouble(movies.getColumnIndex(MovieDbStrings.VOTE_AVERAGE)));
                movie.setAdult(movies.getInt(movies.getColumnIndex(MovieDbStrings.ADULT)) == 1);

                moviesList.add(movie);
            }

            movies.close();

            mAdapter = new RecyclerViewFilmsAdapter(moviesList, WatchLaterActivity.this);
            recyclerView.setAdapter(mAdapter);
            //controlli se la lista è già stata ordinata e in caso la ordini
            mAdapter.notifyDataSetChanged();
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
}
