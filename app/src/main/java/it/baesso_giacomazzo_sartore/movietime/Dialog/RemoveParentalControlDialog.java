package it.baesso_giacomazzo_sartore.movietime.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.baesso_giacomazzo_sartore.movietime.Interfaces.ActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.Utilities.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.Utilities.Security;

public class RemoveParentalControlDialog extends DialogFragment {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vView = inflater.inflate(R.layout.dialog_remove, container, false);

        final EditText editTextPsw = vView.findViewById(R.id.DialogRemove_psw);
        TextView textViewConfirm = vView.findViewById(R.id.DialogRemove_Yes);
        TextView textViewDiscard = vView.findViewById(R.id.DialogRemove_No);
        final TextView textViewIncorrectPsw = vView.findViewById(R.id.DialogRemove_incorrect_psw);

        textViewConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPsw = PrefsManager.getInstance(context).getPreference(getString(R.string.pref_password), "");

                if(Security.md5(editTextPsw.getText().toString()).equals(currentPsw))
                {
                    PrefsManager.getInstance(context).setPreference(getString(R.string.pref_parental_control_enabled), false);

                    if(context instanceof ActivityInterface)
                    {
                        ((ActivityInterface)context).refreshList();
                        ((ActivityInterface)context).showSnackBar("Parental control disattivato", R.drawable.ic_lock_black_24dp, R.color.colorAccent, R.color.textDark);
                    }

                    dismiss();
                }
                else
                {
                    textViewIncorrectPsw.setText("Password errata");
                    textViewIncorrectPsw.setVisibility(View.VISIBLE);
                }
            }
        });

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return vView;
    }
}
