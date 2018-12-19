package io.ffem.integration;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static io.ffem.integration.Constants.EXTERNAL_REQUEST;
import static io.ffem.integration.Constants.RESULT_JSON;
import static io.ffem.integration.Constants.TEST_ID_KEY;

public class MainActivity extends BaseActivity {

    private static final String playStoreUrl = "https://play.google.com/store/apps/details?id=";
    private static final String AVAILABLE_IRON = "Soil - Available Iron";
    private static final String CALCIUM_MAGNESIUM = "Soil - Exchangeable Calcium and Magnesium";
    private static final String FLUORIDE = "Water - Fluoride";
    private static final String INVALID_TEST = "Invalid Test Example";
    private TextView textResult;
    private CheckBox debugMode;
    private String selectedTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAppTheme();

        setContentView(R.layout.activity_main);

        initialize();
    }

    /**
     * Method called by launch test button.
     *
     * @param view the View
     */
    @SuppressWarnings("unused")
    public void launchTest(@SuppressWarnings("unused") View view) {

        String appTitle;
        String externalAppAction;
        String testId;

        switch (selectedTest) {
            case AVAILABLE_IRON:
                appTitle = "ffem Soil";

                // To launch ffem Soil app
                externalAppAction = "io.ffem.soil";

                // Look up test id in json file at:
                // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
                testId = "3353f5cf-1cd2-4bf5-b47f-15d3db917add";
                break;

            case CALCIUM_MAGNESIUM:
                appTitle = "ffem Soil";

                // To launch ffem Soil app
                externalAppAction = "io.ffem.soil";

                // Look up test id in json file at:
                // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
                testId = "52ec4ca0-d691-4f2b-b17a-232c2966974a";
                break;

            case FLUORIDE:
                appTitle = "ffem Water";

                // To launch ffem Water app
                externalAppAction = "io.ffem.water";

                // Look up test id in json file at:
                // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/water/assets/tests.json
                testId = "f0f3c1dd-89af-49f1-83e7-bcc31c3006cf";
                break;

            default:
                appTitle = "ffem Water";

                // To launch ffem Water app
                externalAppAction = "io.ffem.water";

                testId = "invalid-test-id";
                break;

        }

        Bundle data = new Bundle();

        // Check whether to run the external app in debug mode to receive dummy results
        if (debugMode.isChecked()) {
            // todo: For testing of app integration only. Remove this line for Production app
            data.putBoolean("debugMode", true);
        }

        // Specify the id of the test to be launched in the ffem app
        data.putString(TEST_ID_KEY, testId);

        // todo: NOTE: This theme feature is not yet ready and will not work. To be completed.
        data.putString("theme", getSelectedTheme());

//        if (selectedTheme.equals(GREEN_THEME)) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            data.putByteArray("image", byteArray);
//        }

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
        Spinner spinner = findViewById(R.id.spinner);

        ArrayList<String> tests = new ArrayList<>();
        tests.add(AVAILABLE_IRON);
        tests.add(CALCIUM_MAGNESIUM);
        tests.add(FLUORIDE);
        tests.add(INVALID_TEST);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tests);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearResultDisplay();
                selectedTest = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onThemeSelectClick(MenuItem item) {
        DialogFragment dialogFragment = new ThemeSelectDialog();

        ((ThemeSelectDialog) dialogFragment).DismissListener(dismissListener);

        new Handler().post(() -> dialogFragment.show(getSupportFragmentManager(), null));

    }

    public interface DialogDismissListener {
        void dialogDismissed(DialogInterface dialog);
    }
}
