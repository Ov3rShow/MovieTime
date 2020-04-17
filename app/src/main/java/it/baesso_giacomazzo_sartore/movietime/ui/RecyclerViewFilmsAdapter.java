package it.baesso_giacomazzo_sartore.movietime.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.skydoves.transformationlayout.TransformationCompat;
import com.skydoves.transformationlayout.TransformationLayout;

import java.util.List;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public class RecyclerViewFilmsAdapter extends RecyclerView.Adapter<RecyclerViewFilmsAdapter.MyViewHolder> {

    private List<Movie> movies;
    private Context context;

    RecyclerViewFilmsAdapter(List<Movie> movies, Context context)
    {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);

        return new MyViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ImageView imageView = holder.cellView.findViewById(R.id.cell_imageView);
        TextView textView = holder.cellView.findViewById(R.id.cell_textView);
        CardView cardView = holder.cellView.findViewById(R.id.cell_cardView);
        final TransformationLayout transformationLayout = holder.cellView.findViewById(R.id.cell_transformationLayout);

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500".concat(movies.get(position).getPoster_path()))
                .apply(new RequestOptions().centerCrop())
                .placeholder(context.getDrawable(R.drawable.placeholder))
                .error(context.getDrawable(R.drawable.error))
                .into(imageView);

        textView.setText(movies.get(position).getOriginal_title());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(DbStrings.ORIGINAL_TITLE, movies.get(position).getOriginal_title());
                bundle.putString(DbStrings.OVERVIEW, movies.get(position).getOverview());
                bundle.putString(DbStrings.BACKDROP_PATH, movies.get(position).getBackdrop_path());
                bundle.putString(DbStrings.POSTER_PATH, movies.get(position).getPoster_path());
                bundle.putDouble(DbStrings.VOTE_AVERAGE, movies.get(position).getVote_average());
                bundle.putBoolean(DbStrings.ADULT, movies.get(position).isAdult());
                intent.putExtras(bundle);
                TransformationCompat.INSTANCE.startActivity(transformationLayout, intent);
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
}
