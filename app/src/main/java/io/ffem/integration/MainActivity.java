package io.ffem.integration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static io.ffem.integration.Constants.EXTERNAL_REQUEST;
import static io.ffem.integration.Constants.RESULT_JSON;
import static io.ffem.integration.Constants.TEST_ID_KEY;

public class MainActivity extends AppCompatActivity {

    // ********* Soil Configuration **********

    // To launch ffem Soil app
    private static final String EXTERNAL_APP_ACTION = "io.ffem.soil";

    // Look up test id in json file at:
    // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
    private static final String TEST_ID = "3353f5cf-1cd2-4bf5-b47f-15d3db917add"; // Available Iron

    // ***************************************


    // ********* Water Configuration *********

    // To launch ffem Water app
    // private static final String EXTERNAL_APP_ACTION = "io.ffem.water";

    // Look up test id in json file at:
    // https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/water/assets/tests.json
    // private static final String TEST_ID = "f0f3c1dd-89af-49f1-83e7-bcc31c3006cf"; // Fluoride

    // ***************************************

    private TextView textResult;
    private CheckBox debugMode;

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

        Bundle data = new Bundle();

        // Check whether to run the external app in debug mode to receive dummy results
        if (debugMode.isChecked()) {
            // todo: For testing of app integration only. Remove this line for Production app
            data.putBoolean("debugMode", true);
        }

        // Specify the id of the test to be launched in the ffem app
        data.putString(TEST_ID_KEY, TEST_ID);

        // Specify which app to start with the action string (Water or Soil app)
        Intent intent = new Intent(EXTERNAL_APP_ACTION);
        intent.putExtras(data);

        // Start the external app activity
        startActivityForResult(intent, EXTERNAL_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == EXTERNAL_REQUEST && resultCode == RESULT_OK) {

            displayResult(intent);

            Toast.makeText(this, R.string.result_received, Toast.LENGTH_SHORT).show();
        } else {

            clearResultDisplay();

            Toast.makeText(this, R.string.no_result, Toast.LENGTH_SHORT).show();
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
    }
}
