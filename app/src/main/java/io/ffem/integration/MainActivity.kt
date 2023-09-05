package io.ffem.integration

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONException
import org.json.JSONObject

/**
 * All the code required for integration with ffem apps is in the
 * two functions launchTest and onActivityResult below.
 *
 *
 * The result is in the json format. Example:
 *
 *
 * <pre>
 * {
 * "type": "io.ffem.soil",
 * "name": "Available Iron",
 * "uuid": "3353f5cf-1cd2-4bf5-b47f-15d3db917add",
 * "result": [{
 * "dilution": 3,    (Note: 0 or 1 = 'No Dilution', 2 = '2 Times Dilution', etc...)
 * "name": "Available Iron",
 * "unit": "mg/l",
 * "id": 1,
 * "value": "> 30.00"
 * }],
 * "testDate": "2018-09-19 01:05"
 * }
</pre> *
 */
class MainActivity : MainBaseActivity() {
    private var appTitle: String? = null
    private var externalAppAction: String? = null

    /**
     * Method called by launch test button.
     *
     * @param view the View
     */
    fun launchTest(@Suppress("UNUSED_PARAMETER") view: View?) {
        val data = Bundle()
        try { // setup the details required by the ffem app to launch the test
            setupTestInformation(data)
            // Specify which app to start with the action string (Water or Soil app)
            val intent = Intent(externalAppAction)
            intent.putExtras(data)
            // Start the external app activity
            startTest.launch(intent)
        } catch (e: ActivityNotFoundException) {
            // The ffem app was not found so request the user to install the app from Play store
            showAppNotInstalledDialog(appTitle, externalAppAction)
        }
    }

    /**
     * Set up all the test information required by the ffem app
     * See README.md file on how to find uuid for a test parameter
     *
     *
     * appTitle:            Which app to call ffem Water or ffem Soil
     * externalAppAction:   The id of the app to launch (ffem Water or ffem Soil)
     * testId               The id of the test the app should start after launch
     * debugMode            Whether the app should return a dummy result or not
     *
     * @param data the bundle data
     */
    private fun setupTestInformation(data: Bundle) {
        appTitle = "ffem Match"
        externalAppAction = "ffem.match"

        val testId = when (selectedTest) {

            // See README.md file to get test ids for all tests
            WATER_AVAILABLE_IRON -> {
                "WC-FM-Fe"
            }

            SOIL_CALCIUM_MAGNESIUM -> {
                "ST-FM-CaMg"
            }

            WATER_CALCIUM_MAGNESIUM -> {
                "WT-FM-CaMg"
            }

            WATER_FLUORIDE -> {
                "WC-FM-F"
            }

            WATER_RESIDUAL_CHLORINE -> {
                "WC-FM-Cl"
            }

            WATER_TOTAL_ALKALINITY -> {
                "WT-FM-TAlk"
            }

            WATER_TOTAL_HARDNESS -> {
                "WT-FM-THrd"
            }

            else -> {
                "invalid-test-id"
            }
        }

        // Specify the id of the test to be launched in the ffem app
        data.putString(TEST_ID_KEY, testId)

        // Check whether to run the external app in debug mode to receive dummy results
        // todo: For testing of app integration only. Remove this line for Production app
        if (b.checkDummyResult.isChecked) {
            data.putBoolean("debugMode", true)
        }
    }

    private var startTest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                // Here we receive the result from the ffem app
                displayResult(it.data)
                showToastMessage(R.string.result_received)
            } else {
                clearResultDisplay()
            }
        }

    /**
     * Here we are displaying the json text in our demo app
     * But the json can be parsed and used as required in your app
     *
     * @param intent the Intent containing the result
     */
    private fun displayResult(intent: Intent?) {
        intent!!.extras
        if (intent.hasExtra(RESULT_JSON)) {
            // Display JSON result
            val jsonString = intent.getStringExtra(RESULT_JSON)
            try {
                if (jsonString != null) {
                    b.textResult.text = JSONObject(jsonString).toString(2)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (intent.hasExtra(TEST_VALUE)) {
            // if json does not exist then use this alternate simple result
            val resultString = intent.getStringExtra(TEST_VALUE)
            try {
                if (resultString != null) {
                    b.textResult.text = getString(R.string.result_display, resultString)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}