package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.API.WebService;
import it.baesso_giacomazzo_sartore.movietime.DetailActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailActivity extends AppCompatActivity implements DetailActivityInterface {

    ImageView imageView;
    TextView titleTxtView, overviewTxtView;
    ScaleRatingBar ratingBar;
    ImageView ageLimit, backBtn;
    View divider;

    RecyclerView recyclerView;
    RecyclerViewFilmsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null)
        {
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setSubtitle("Dettagli film");
            getSupportActionBar().hide();
        }

        imageView = findViewById(R.id.detail_img);
        titleTxtView = findViewById(R.id.detail_title);
        overviewTxtView = findViewById(R.id.detail_overview);
        ratingBar = findViewById(R.id.detail_rating);
        ageLimit = findViewById(R.id.detail_ageLimitImg);
        divider = findViewById(R.id.detail_divider);
        backBtn = findViewById(R.id.detail_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
            String movieId = getIntent().getExtras().getString(MovieDbStrings._ID);
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

            WebService.getInstance().getSimilarMovies(DetailActivity.this, movieId, getString(R.string.api_key), "it-IT");
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

    @Override
    public void showSimilarMovies(List<Movie> movies) {
        mAdapter = new RecyclerViewFilmsAdapter(movies, DetailActivity.this, R.layout.cell_layout_small);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScrollView scrollView = findViewById(R.id.detail_scrollView);
        scrollView.scrollTo(0, 0);
    }
}
