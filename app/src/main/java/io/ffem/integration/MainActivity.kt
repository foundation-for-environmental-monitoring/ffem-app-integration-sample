package io.ffem.integration

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

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
            startActivityForResult(intent, EXTERNAL_REQUEST)
        } catch (e: ActivityNotFoundException) { // The ffem app was not found so request the user to install the app from Play store
            val builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
            builder.setTitle(R.string.app_not_found)
                    .setMessage(String.format(Locale.US, getString(R.string.install_app), appTitle))
                    .setPositiveButton(R.string.go_to_play_store) { _: DialogInterface?, _: Int ->
                        startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse(Constants.playStoreUrl + externalAppAction)))
                    }
                    .setNegativeButton(android.R.string.cancel
                    ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                    .setCancelable(false)
                    .show()
        }
    }

    /**
     * Here we receive the result from the ffem app
     *
     * @param requestCode The code we used to make the request
     * @param resultCode  Did we get a result ok or was it cancelled
     * @param intent      The intent containing the result
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == EXTERNAL_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                displayResult(intent)
                showToastMessage(R.string.result_received)
            } else {
                clearResultDisplay()
            }
        }
    }

    /**
     * Set up all the test information required by the ffem app
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
        val testId: String
        when (selectedTest) {
            Constants.AVAILABLE_IRON -> {
                appTitle = "ffem Soil"
                // To launch ffem Soil app
                externalAppAction = "io.ffem.soil"
                // Look up test 'uuid' in json file at:
// https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
                testId = "3353f5cf-1cd2-4bf5-b47f-15d3db917add"
            }
            Constants.CALCIUM_MAGNESIUM -> {
                appTitle = "ffem Soil"
                // To launch ffem Soil app
                externalAppAction = "io.ffem.soil"
                // Look up test 'uuid' in json file at:
// https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/soil/assets/tests.json
                testId = "52ec4ca0-d691-4f2b-b17a-232c2966974a"
            }
            Constants.FLUORIDE -> {
                appTitle = "ffem Water"
                // To launch ffem Water app
                externalAppAction = "io.ffem.water"
                // Look up test 'uuid' in json file at:
// https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/develop/caddisfly-app/app/src/water/assets/tests.json
                testId = "f0f3c1dd-89af-49f1-83e7-bcc31c3006cf"
            }
            Constants.FLUORIDE_LITE -> {
                appTitle = "ffem Lite"
                // To launch ffem Lite app
                externalAppAction = "io.ffem.lite"
                // Look up test 'uuid' in json file at:
// https://github.com/foundation-for-environmental-monitoring/ffem-lite/blob/master/app/src/main/res/raw/calibration.json
                testId = "f0f3c1dd-89af-49f1-83e7-bcc31cb61159"
            }
            else -> {
                appTitle = "ffem Water"
                // To launch ffem Water app
                externalAppAction = "io.ffem.water"
                testId = "invalid-test-id"
            }
        }
        // Specify the id of the test to be launched in the ffem app
        data.putString(Constants.TEST_ID_KEY, testId)
        // Check whether to run the external app in debug mode to receive dummy results
        if (dummyResultCheckBox!!.isChecked) { // todo: For testing of app integration only. Remove this line for Production app
            data.putBoolean("debugMode", true)
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
        if (intent.hasExtra(Constants.RESULT_JSON)) { // Display JSON result
            val jsonString = intent.getStringExtra(Constants.RESULT_JSON)
            try {
                if (jsonString != null) {
                    textResult!!.text = JSONObject(jsonString).toString(2)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val EXTERNAL_REQUEST = 1
    }
}