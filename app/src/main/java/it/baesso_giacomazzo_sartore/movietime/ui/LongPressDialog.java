package it.baesso_giacomazzo_sartore.movietime.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.baesso_giacomazzo_sartore.movietime.API.DbSaver;
import it.baesso_giacomazzo_sartore.movietime.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;
import it.baesso_giacomazzo_sartore.movietime.objects.Movie;

public class LongPressDialog extends DialogFragment {

    private Context context;
    private boolean isWatchLater = false;

    private Switch watchLaterCheckbox;
    private ImageView watchLaterImg;
    private boolean isSavedOnDb;

    private Movie movie;

    LongPressDialog(Movie movie, ImageView watchLaterImg, boolean isSavedOnDb)
    {
        this.movie = movie;
        this.isSavedOnDb = isSavedOnDb;
        this.watchLaterImg = watchLaterImg;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vView = inflater.inflate(R.layout.dialog_long_press, container, false);

        LinearLayout watchLaterLayout = vView.findViewById(R.id.LongPressDialog_WatchLaterLayout);
        watchLaterCheckbox = vView.findViewById(R.id.LongPressDialog_WatchLaterCheck);
        TextView titleTextView = vView.findViewById(R.id.LongPressDialog_Title);
        titleTextView.setText(movie.getTitle());

        getCurrentState();

        watchLaterCheckbox.setChecked(isWatchLater);

        watchLaterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchLaterCheckbox.setChecked(!watchLaterCheckbox.isChecked());
                itemTapped();
            }
        });

        watchLaterCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemTapped();
            }
        });

        return vView;
    }

    private void itemTapped()
    {
        isWatchLater = !isWatchLater;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDbStrings.WATCH_LATER, isWatchLater? 1 : 0);
        if(isSavedOnDb)
            update(contentValues);
        else
            insert();

        //aggiorno la lista solo se l'activity che ha aperto il dialog è la WatchLaterActivity
        if(context instanceof WatchLaterActivity)
            ((ActivityInterface) context).refreshList();

        if(watchLaterCheckbox.isChecked())
            ((ActivityInterface) context).showSnackBar("Film aggiunto a guarda più tardi", R.drawable.ic_check_circle_black_24dp, R.color.green, R.color.white);
        else
            ((ActivityInterface) context).showSnackBar("Film rimosso da guarda più tardi", R.drawable.ic_check_circle_black_24dp, R.color.red, R.color.white);
    }

    private void getCurrentState()
    {
        Cursor cursor = context.getContentResolver().query(DbProvider.MOVIES_URI, null,MovieDbStrings._ID + " = " + movie.getId(), null, null, null);

        if(cursor != null)
        {
            if(cursor.getCount() == 0)
                isWatchLater = false;
            else
            {
                cursor.moveToFirst();
                int watchLaterStatus = cursor.getInt(cursor.getColumnIndex(MovieDbStrings.WATCH_LATER));

                isWatchLater = watchLaterStatus == 1;
                cursor.close();
            }
        }
    }

    private void insert()
    {
        DbSaver.DbSavingSingle(context, movie, true);

        dismiss();

        Log.w("LONGPRESS", "INSERT");
    }

    private void update(ContentValues contentValues)
    {
        Log.w("LONGPRESS", "UPDATE");

        if(context.getContentResolver().update(DbProvider.MOVIES_URI, contentValues , MovieDbStrings._ID + " = " + movie.getId(), null) > 0)
        {
            watchLaterImg.setVisibility(isWatchLater? View.VISIBLE : View.INVISIBLE);

            dismiss();
        }
    }
}
