package io.ffem.integration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.reflect.Field;

public class BaseActivity extends AppCompatActivity {

    MainActivity.DialogDismissListener dismissListener = dialog -> {
        setAppTheme();
        finish();
        final Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    };

    void setAppTheme() {
        try {
            String theme = getSelectedTheme();
            if (theme != null) {
                setTheme(getThemeResourceId(theme));
            }
        } catch (Exception ignored) {
        }
    }

    String getSelectedTheme() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.selected_theme_key), "");
    }


    private int getThemeResourceId(String theme) {
        int resourceId = -1;
        try {
            Class res = R.style.class;
            Field field = res.getField("AppTheme_" + theme);
            resourceId = field.getInt(null);

        } catch (Exception ignored) {
        }
        return resourceId;
    }

    void showToastMessage(@SuppressWarnings("SameParameterValue") @StringRes int stringKey) {
        Toast toast = Toast.makeText(this, stringKey,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.show();
    }
}
