package io.ffem.integration;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static io.ffem.integration.Constants.EXTERNAL_REQUEST;
import static io.ffem.integration.Constants.RESULT_JSON;
import static io.ffem.integration.Constants.TEST_ID_KEY;

public class MainActivity extends AppCompatActivity {

    private final String playStoreUrl = "https://play.google.com/store/apps/details?id=";

    private RadioButton soilTestCheckBox;
    private TextView textResult;
    private CheckBox debugMode;
    private CheckBox themeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    /**
     * Method called by launch test button.
     *
     * @param view the View
     */
    public void launchTest(@SuppressWarnings("unused") View view) {

        String appTitle;
        String externalAppAction;
        String testId;

        if (soilTestCheckBox.isChecked()) {

            appTitle = "ffem Soil";

            // To launch ffem Soil app
            externalAppAction = "io.ffem.soil";

            // Look up test id in json file at:
            // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
            testId = "3353f5cf-1cd2-4bf5-b47f-15d3db917add"; // Available Iron

        } else {

            appTitle = "ffem Water";

            // To launch ffem Water app
            externalAppAction = "io.ffem.water";

            // Look up test id in json file at:
            // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/water/assets/tests.json
            testId = "f0f3c1dd-89af-49f1-83e7-bcc31c3006cf"; // Fluoride

        }

        Bundle data = new Bundle();

        // Check whether to run the external app in debug mode to receive dummy results
        if (debugMode.isChecked()) {
            // todo: For testing of app integration only. Remove this line for Production app
            data.putBoolean("debugMode", true);
        }

        // Specify the id of the test to be launched in the ffem app
        data.putString(TEST_ID_KEY, testId);

        // Check whether to run the external app in debug mode to receive dummy results
        if (themeCheckBox.isChecked()) {
            data.putString("theme", "Green");
        }

        try {
            // Specify which app to start with the action string (Water or Soil app)
            Intent intent = new Intent(externalAppAction);
            intent.putExtras(data);

            // Start the external app activity
            startActivityForResult(intent, EXTERNAL_REQUEST);

        } catch (ActivityNotFoundException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);

            builder.setTitle(R.string.app_not_found)
                    .setMessage(String.format(Locale.US, getString(R.string.install_app), appTitle))
                    .setPositiveButton(R.string.go_to_play_store, (dialogInterface, i1)
                            -> startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(playStoreUrl + externalAppAction))))
                    .setNegativeButton(android.R.string.cancel,
                            (dialogInterface, i1) -> dialogInterface.dismiss())
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == EXTERNAL_REQUEST) {
            if (resultCode == RESULT_OK) {

                displayResult(intent);

                showToastMessage(R.string.result_received);

            } else {

                clearResultDisplay();

                showToastMessage(R.string.test_cancelled);
            }
        }
    }

    /**
     * Display the results returned from the external app.
     *
     * @param intent the Intent containing the result
     */
    private void displayResult(Intent intent) {
        intent.getExtras();

        // Display JSON result
        String jsonString = intent.getStringExtra(RESULT_JSON);

        try {
            textResult.setText(new JSONObject(jsonString).toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*

        Example of result JSON string:

            {
                "type": "io.ffem.soil",
                "name": "Available Iron",
                "uuid": "3353f5cf-1cd2-4bf5-b47f-15d3db917add",
                "result": [{
                    "dilution": 3,    (Note: 0 or 1 = 'No Dilution', 2 = '2 Times Dilution', etc...)
                    "name": "Available Iron",
                    "unit": "mg/l",
                    "id": 1,
                    "value": "> 30.00"
                }],
                "testDate": "2018-09-19 01:05"
            }

        */
    }

    private void clearResultDisplay() {
        textResult.setText("");
    }

    private void initialize() {
        textResult = findViewById(R.id.result);
        debugMode = findViewById(R.id.debugMode);
        debugMode.setOnCheckedChangeListener((compoundButton, b) -> clearResultDisplay());
        themeCheckBox = findViewById(R.id.themeCheckBox);
        soilTestCheckBox = findViewById(R.id.soilTest);
        soilTestCheckBox.setOnCheckedChangeListener((compoundButton, b) -> clearResultDisplay());
    }

    private void showToastMessage(@StringRes int stringKey) {
        Toast toast = Toast.makeText(this, stringKey,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 300);
        toast.show();
    }
}
