package it.baesso_giacomazzo_sartore.movietime.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.baesso_giacomazzo_sartore.movietime.ListActivityInterface;
import it.baesso_giacomazzo_sartore.movietime.PrefsManager;
import it.baesso_giacomazzo_sartore.movietime.R;
import it.baesso_giacomazzo_sartore.movietime.Security;

public class ParentalControlDialog extends DialogFragment {

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vView = inflater.inflate(R.layout.dialog_parental_control, container, false);

        final TextView textViewPsw = vView.findViewById(R.id.parent_edit_pwd);
        final TextView textViewConfirmPsw = vView.findViewById(R.id.parent_edit_confpwd);
        final TextView txtViewPswNotMatch = vView.findViewById(R.id.parent_psw_not_match);
        TextView btnSavePsw = vView.findViewById(R.id.parent_btn_confirm);
        TextView btnDiscardPsw = vView.findViewById(R.id.parent_btn_discard);

        btnSavePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textViewPsw.getText().toString().equals("") && textViewPsw.getText().toString().equals(textViewConfirmPsw.getText().toString()))
                {
                    PrefsManager.getInstance(context).setPreference(getString(R.string.pref_password), Security.md5(textViewPsw.getText().toString()));
                    PrefsManager.getInstance(context).setPreference(getString(R.string.pref_parental_control_enabled), true);

                    if(context instanceof ListActivityInterface)
                    {
                        ((ListActivityInterface)context).refreshList();
                        ((ListActivityInterface)context).showSnackBar("Parental control attivato", R.drawable.ic_lock_black_24dp, R.color.colorAccent, R.color.textDark);
                    }


                    dismiss();
                }
                else if(textViewPsw.getText().toString().equals(""))
                {
                    txtViewPswNotMatch.setText("La password non può essere vuota");
                    txtViewPswNotMatch.setVisibility(View.VISIBLE);
                }
                else
                {
                    txtViewPswNotMatch.setText("Le password non coincidono");
                    txtViewPswNotMatch.setVisibility(View.VISIBLE);
                }
            }
        });

        btnDiscardPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return vView;
    }


}
