package it.baesso_giacomazzo_sartore.movietime.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.baesso_giacomazzo_sartore.movietime.R;

public class RecyclerViewFilmsAdapter extends RecyclerView.Adapter<RecyclerViewFilmsAdapter.MyViewHolder> {

    String[] mDataSet;

    public RecyclerViewFilmsAdapter(String[] aDataSet)
    {
        mDataSet = aDataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout, parent, false);

        return new MyViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageView imageView = holder.cellView.findViewById(R.id.cell_imageView);
        TextView textView = holder.cellView.findViewById(R.id.cell_textView);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
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
