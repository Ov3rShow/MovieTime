package it.baesso_giacomazzo_sartore.movietime.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public class RecyclerViewFilmSmallAdapter extends RecyclerView.Adapter<RecyclerViewFilmSmallAdapter.MyViewHolder> {

    private List<Movie> movies;
    private Context context;

    RecyclerViewFilmSmallAdapter(List<Movie> movies, Context context)
    {
        this.movies = movies;
        this.context = context;
    }

    List<Movie> getMovies()
    {
        return movies;
    }

    boolean checkIfExists(String id)
    {
        for (Movie m : movies)
        {
            if(m.getId().equals(id))
                return true;
        }

        return false;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout_small, parent, false);

        return new MyViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ImageView imageView = holder.cellView.findViewById(R.id.cell_imageView);
        final TextView textView = holder.cellView.findViewById(R.id.cell_textView);
        final CardView cardView = holder.cellView.findViewById(R.id.cell_cardView);
        final ImageView watchLaterImg = holder.cellView.findViewById(R.id.cell_watchLater);

        if(movies.get(position).getPoster_path() == null)
        {
            imageView.setImageDrawable(context.getDrawable(R.drawable.error));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500".concat(movies.get(position).getPoster_path()))
                    .apply(new RequestOptions().centerCrop())
                    .placeholder(context.getDrawable(R.drawable.placeholder))
                    .error(context.getDrawable(R.drawable.error))
                    .into(imageView);

        Cursor cursor = context.getContentResolver().query(DbProvider.MOVIES_URI, new String[]{MovieDbStrings.WATCH_LATER}, MovieDbStrings._ID + " = " + movies.get(position).getId(),null,null);
        if(cursor != null)
        {
            cursor.moveToFirst();

            if(cursor.getCount() > 0 && cursor.getInt(cursor.getColumnIndex(MovieDbStrings.WATCH_LATER)) == 1)
                watchLaterImg.setVisibility(View.VISIBLE);
            else
                watchLaterImg.setVisibility(View.INVISIBLE);

            cursor.close();
        }


        textView.setText(movies.get(position).getOriginal_title());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MovieDbStrings.ORIGINAL_TITLE, movies.get(position).getOriginal_title());
                bundle.putString(MovieDbStrings.OVERVIEW, movies.get(position).getOverview());
                bundle.putString(MovieDbStrings.BACKDROP_PATH, movies.get(position).getBackdrop_path());
                bundle.putString(MovieDbStrings.POSTER_PATH, movies.get(position).getPoster_path());
                bundle.putDouble(MovieDbStrings.VOTE_AVERAGE, movies.get(position).getVote_average());
                bundle.putBoolean(MovieDbStrings.ADULT, movies.get(position).isAdult());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LongPressDialog dialog = new LongPressDialog(movies.get(position).getOriginal_title(), movies.get(position).getId(), watchLaterImg);
                dialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "TAG_PREFERITI");
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        View cellView;

        MyViewHolder(@NonNull View cellView) {
            super(cellView);
            this.cellView = cellView;
        }
    }

    void sortList() {
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie movie1, Movie movie2) {
                String s1 = movie1.getOriginal_title();
                String s2 = movie2.getOriginal_title();
                return s1.compareToIgnoreCase(s2);
            }
        });
    }
}
