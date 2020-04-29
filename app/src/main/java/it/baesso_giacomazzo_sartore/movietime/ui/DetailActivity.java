package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView titleTxtView, overviewTxtView;
    ScaleRatingBar ratingBar;
    ImageView ageLimit;
    View divider;

    RecyclerView recyclerView;
    RecyclerViewFilmSmallAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle("Dettagli film");
        }

        imageView = findViewById(R.id.detail_img);
        titleTxtView = findViewById(R.id.detail_title);
        overviewTxtView = findViewById(R.id.detail_overview);
        ratingBar = findViewById(R.id.detail_rating);
        ageLimit = findViewById(R.id.detail_ageLimitImg);
        divider = findViewById(R.id.detail_divider);

        recyclerView = findViewById(R.id.detail_recyclerView);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        List<Movie> list = new ArrayList<>();

        Movie m1 = new Movie();
        m1.setOriginal_title("Film prova");

        list.add(m1);
        list.add(m1);
        list.add(m1);

        mAdapter = new RecyclerViewFilmSmallAdapter(list, this);
        recyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overviewTxtView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        if(getIntent().getExtras() != null)
        {
            String image = getIntent().getExtras().getString(MovieDbStrings.BACKDROP_PATH);
            String title = getIntent().getExtras().getString(MovieDbStrings.ORIGINAL_TITLE);
            String overview = getIntent().getExtras().getString(MovieDbStrings.OVERVIEW);
            double rating = getIntent().getExtras().getDouble(MovieDbStrings.VOTE_AVERAGE);
            boolean isAdult = getIntent().getExtras().getBoolean(MovieDbStrings.ADULT);

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

            if(isAdult)
            {
                divider.setVisibility(View.VISIBLE);
                ageLimit.setVisibility(View.VISIBLE);
            }


            if(overview == null || overview.equals(""))
                overviewTxtView.setText("La trama di questo film non è disponibile :(");
            else
                overviewTxtView.setText(overview);

            titleTxtView.setText(title);
            ratingBar.setRating((float)rating/2);
        }
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
