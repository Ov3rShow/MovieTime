package it.baesso_giacomazzo_sartore.movietime.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.database.DbProvider;
import it.baesso_giacomazzo_sartore.movietime.database.MovieDbStrings;

public class LongPressDialog extends DialogFragment {

    private String movieTitle;
    private String movieId;
    private Context context;
    private boolean isWatchLater = false;

    LongPressDialog(String movieTitle, String movieId)
    {
        this.movieTitle = movieTitle;
        this.movieId = movieId;

        Log.e("ID ", this.movieId);
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
        final CheckBox watchLaterCheckbox = vView.findViewById(R.id.LongPressDialog_WatchLaterCheck);

        getCurrentState();

        watchLaterCheckbox.setChecked(isWatchLater);

        watchLaterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isWatchLater = !isWatchLater;
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieDbStrings.WATCH_LATER, isWatchLater? 1 : 0);
                if(context.getContentResolver().update(DbProvider.MOVIES_URI, contentValues , MovieDbStrings._ID + " = " + movieId, null) > 0)
                {
                    watchLaterCheckbox.setChecked(!watchLaterCheckbox.isChecked());
                }
            }
        });

        return vView;
    }

    private void getCurrentState()
    {
        Cursor cursor = context.getContentResolver().query(DbProvider.MOVIES_URI, null,MovieDbStrings._ID + " = " + movieId, null, null, null);

        if(cursor != null)
        {
            cursor.moveToFirst();
            int watchLaterStatus = cursor.getInt(cursor.getColumnIndex(MovieDbStrings.WATCH_LATER));

            isWatchLater = watchLaterStatus == 1;
            cursor.close();
        }
    }
}
