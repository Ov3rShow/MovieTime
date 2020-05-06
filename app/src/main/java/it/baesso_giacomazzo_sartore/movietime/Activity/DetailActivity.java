package it.baesso_giacomazzo_sartore.movietime.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.willy.ratingbar.ScaleRatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.baesso_giacomazzo_sartore.movietime.Utilities.DbSaver;
import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.Interfaces.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.Interfaces.DetailActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.Database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.Database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.Classes.Movie;
import it.baesso_giacomazzo_sartore.movietime.Adapter.RecyclerViewFilmsAdapter;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailActivity extends AppCompatActivity implements DetailActivityInterface, ActivityInterface {

    ImageView imageView;
    TextView titleTxtView, overviewTxtView, dateTextView, noSimilar;
    ScaleRatingBar ratingBar;
    ImageView ageLimit, backBtn, watchLaterBtn;
    View divider;

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    boolean watchLater, isSavedOnDb;
    Movie currentMovie;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        imageView = findViewById(R.id.detail_img);
        titleTxtView = findViewById(R.id.detail_title);
        dateTextView = findViewById(R.id.detail_date);
        noSimilar = findViewById(R.id.detail_noSimilar);
        overviewTxtView = findViewById(R.id.detail_overview);
        ratingBar = findViewById(R.id.detail_rating);
        ageLimit = findViewById(R.id.detail_ageLimitImg);
        divider = findViewById(R.id.detail_divider);
        backBtn = findViewById(R.id.detail_back);
        watchLaterBtn = findViewById(R.id.detail_watchLater);

        //click del bottone <- per chiudere l'attività e tornare alla precedente
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //click su bottone per mettere il film nella lista "da vedere"
        watchLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWatchLater();
            }
        });

        recyclerView = findViewById(R.id.detail_recyclerView);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overviewTxtView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        if(getIntent().getExtras() != null)
        {
            currentMovie = new Movie(getIntent().getExtras().getString(MovieDbStrings._ID), getIntent().getExtras().getString(MovieDbStrings.TITLE),
                    getIntent().getExtras().getString(MovieDbStrings.OVERVIEW),getIntent().getExtras().getString(MovieDbStrings.POSTER_PATH),getIntent().getExtras().getString(MovieDbStrings.DATE) ,
                    getIntent().getExtras().getString(MovieDbStrings.BACKDROP_PATH), getIntent().getExtras().getDouble(MovieDbStrings.VOTE_AVERAGE),
                    getIntent().getExtras().getBoolean(MovieDbStrings.ADULT));

            String image = getIntent().getExtras().getString(MovieDbStrings.BACKDROP_PATH);

            getCurrentWatchLaterStatus();

            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                image = getIntent().getExtras().getString(MovieDbStrings.POSTER_PATH);
            else
                if(image == null)
                    image = getIntent().getExtras().getString(MovieDbStrings.POSTER_PATH);

            if(image == null)
            {
                imageView.setImageDrawable(getDrawable(R.drawable.error));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else
                Glide.with(DetailActivity.this)
                        .load("https://image.tmdb.org/t/p/w500".concat(image))
                        .apply(new RequestOptions().centerCrop())
                        .placeholder(getDrawable(R.drawable.placeholder))
                        .error(getDrawable(R.drawable.error))
                        .into(imageView);

            if(currentMovie.isAdult())
            {
                divider.setVisibility(View.VISIBLE);
                ageLimit.setVisibility(View.VISIBLE);
            }


            if(currentMovie.getOverview() == null || currentMovie.getOverview().equals(""))
                overviewTxtView.setText("Trama non disponibile ☹️");
            else
                overviewTxtView.setText(currentMovie.getOverview());

            titleTxtView.setText(currentMovie.getTitle());
            ratingBar.setRating((float)currentMovie.getVote_average()/2);

            //serve per capire la data di uscita del film, nel caso non si sapesse gestiamo l'eccezzione
            String data = currentMovie.getRelease_date();
            try{
                Date date1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN).parse(data);
                data = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN).format(date1);
            }catch (Exception e){
                data = "Data di uscita sconosciuta";
                e.printStackTrace();
            }
            dateTextView.setText(data);

            WebService.getInstance().getSimilarMovies(DetailActivity.this, currentMovie.getId(), getString(R.string.api_key), "it-IT");
        }
    }

    //metodo per dare la possibilità di tornare ad un film o attività precedente
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //metodo che mostra, una volta cliccato un film, i film simili a quello scelto
    @Override
    public void showSimilarMovies(List<Movie> movies) {
        if(movies.size() == 0){
            noSimilar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            mAdapter = new RecyclerViewFilmsAdapter(movies, DetailActivity.this, R.layout.cell_layout_small, false);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        ScrollView scrollView = findViewById(R.id.detail_scrollView);
        scrollView.scrollTo(0, 0);

        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    //metodo per mettere un film nella categoria "da vedere"
    void setWatchLater()
    {
        watchLater = !watchLater;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDbStrings.WATCH_LATER, watchLater? 1 : 0);
        if(isSavedOnDb)
        {
            if(DetailActivity.this.getContentResolver().update(DbProvider.MOVIES_URI, contentValues , MovieDbStrings._ID + " = " + currentMovie.getId(), null) == 0)
            {
                showCustomSnackbar("Si è verificato un errore :(", R.drawable.ic_warning_black_24dp, R.color.red, R.color.white);
            }
        }
        else
            DbSaver.DbSavingSingle(DetailActivity.this, currentMovie, watchLater);

        if(watchLater)
        {
            watchLaterBtn.setImageDrawable(getDrawable(R.drawable.custom_watch_later_yes));
            showCustomSnackbar("Film aggiunto a guarda più tardi", R.drawable.ic_check_circle_black_24dp, R.color.green, R.color.white);
        }
        else
        {
            watchLaterBtn.setImageDrawable(getDrawable(R.drawable.custom_watch_later_no));
            showCustomSnackbar("Film rimosso da guarda più tardi", R.drawable.ic_check_circle_black_24dp, R.color.red, R.color.white);
        }

    }

    //metodo per avere i vedere i film in "da vedere" al momento
    void getCurrentWatchLaterStatus() {
        Cursor cursor = DetailActivity.this.getContentResolver().query(DbProvider.MOVIES_URI, null, MovieDbStrings._ID + " = " + currentMovie.getId(), null, null, null);

        if (cursor != null) {
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                int watchLaterStatus = cursor.getInt(cursor.getColumnIndex(MovieDbStrings.WATCH_LATER));

                watchLater = watchLaterStatus == 1;
                isSavedOnDb = true;
            }
            else
            {
                watchLater = false;
                isSavedOnDb = false;
            }
            cursor.close();
        }

        if(watchLater)
            watchLaterBtn.setImageDrawable(getDrawable(R.drawable.custom_watch_later_yes));
    }

    //metodo che crea la snackbar
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

    @Override
    public void showApiCallResult(List<Movie> movies) {

    }

    //metodo per visualizzare la Snackbar
    @Override
    public void showSnackBar(String text, int icon, int backgroundColor, int textIconColor) {
        showCustomSnackbar(text, icon, backgroundColor, textIconColor);
    }

    @Override
    public void refreshList() {

    }

    @Override
    public void showSearchResult(List<Movie> movies) {

    }


}
