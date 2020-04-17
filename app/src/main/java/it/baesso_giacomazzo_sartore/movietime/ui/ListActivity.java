package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentProvider;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.skydoves.transformationlayout.TransitionExtensionKt;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;
import it.baesso_giacomazzo_sartore.movietime.objects.PopularResult;

import static com.skydoves.transformationlayout.TransitionExtensionKt.onTransformationStartContainer;

public class ListActivity extends AppCompatActivity implements ListActivityInterface {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    List<Movie> cachedMovies;

    int spanCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TransitionExtensionKt.onTransformationStartContainer(ListActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        prepareSpanCount();

        recyclerView = findViewById(R.id.listActivity_recyclerView);
        mLayoutManager = new GridLayoutManager(ListActivity.this, spanCount);
        recyclerView.setLayoutManager(mLayoutManager);

        if(isNetworkAvailable())
        {
            WebService webService = WebService.getInstance();
            webService.getAllPopular(ListActivity.this, getString(R.string.api_key), "it-IT", 1);
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

        showCustomSnackbar("Connessione a internet assente", R.drawable.ic_warning_black_24dp, R.color.colorAccent);
    }

    @Override
    public void showApiCallResult(PopularResult result) {
        showList(result.getResults());
    }

    void showList(List<Movie> list)
    {
        mAdapter = new RecyclerViewFilmsAdapter(list, ListActivity.this);
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
}
