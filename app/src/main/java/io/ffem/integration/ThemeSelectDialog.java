package io.ffem.integration;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

@SuppressWarnings("ALL")
public class ThemeSelectDialog extends DialogFragment {

    private MainActivity.DialogDismissListener dismissListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        String theme = getSelectedTheme();
        int selectedThemeIndex = 0;
        String[] themes = getResources().getStringArray(R.array.themes);
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].equals(theme)) {
                selectedThemeIndex = i;
                break;
            }
        }

        builder.setTitle(R.string.selected_theme)
                .setSingleChoiceItems(R.array.themes, selectedThemeIndex, (dialog, which) -> setPreference(which));

        return builder.create();
    }

    private void setPreference(int which) {
        SharedPreferences sharedPref =
                Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String[] themes = getResources().getStringArray(R.array.themes);

        editor.putString(getString(R.string.selected_theme_key), themes[which]);
        editor.apply();

        dismiss();
    }

    private String getSelectedTheme() {
        SharedPreferences sharedPref =
                Objects.requireNonNull(getActivity()).getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.selected_theme_key), "");
    }

    public void DismissListener(MainActivity.DialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.dialogDismissed(null);
        }
    }
}
