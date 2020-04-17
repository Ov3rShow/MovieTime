package it.baesso_giacomazzo_sartore.movietime.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skydoves.transformationlayout.TransformationAppCompatActivity;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;

public class DetailActivity extends TransformationAppCompatActivity {

    ImageView imageView;
    TextView titleTxtView, overviewTxtView;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.detail_img);
        titleTxtView = findViewById(R.id.detail_title);
        overviewTxtView = findViewById(R.id.detail_overview);
        ratingBar = findViewById(R.id.detail_rating);

        if(getIntent().getExtras() != null)
        {
            String image = getIntent().getExtras().getString(DbStrings.BACKDROP_PATH);
            String title = getIntent().getExtras().getString(DbStrings.ORIGINAL_TITLE);
            String overview = getIntent().getExtras().getString(DbStrings.OVERVIEW);
            double rating = getIntent().getExtras().getDouble(DbStrings.VOTE_AVERAGE);

            if(image == null)
                image = getIntent().getExtras().getString(DbStrings.POSTER_PATH);

            if(image != null)
                Glide.with(DetailActivity.this)
                        .load("https://image.tmdb.org/t/p/w500".concat(image))
                        .apply(new RequestOptions().centerCrop())
                        .placeholder(getDrawable(R.drawable.placeholder))
                        .error(getDrawable(R.drawable.error))
                        .into(imageView);


            titleTxtView.setText(title);
            overviewTxtView.setText(overview);
            ratingBar.setRating((float)rating/2);
        }
    }
}
