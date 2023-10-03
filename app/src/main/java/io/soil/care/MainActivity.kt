package io.soil.care

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

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
            SOIL_PH -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "a3e0e480-858a-455a-bfa4-2636348f3ef2"
            }

            AVAILABLE_IRON -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "3353f5cf-1cd2-4bf5-b47f-15d3db917add"
            }
            AVAILABLE_NEW_NITROGEN -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "09dd29db-7330-4c10-bdbc-e278e073a51a"
            }
            AVAILABLE_NEW_PHOSPHORUS -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "d8fdd8cb-de6a-46ee-9ff6-f046e0f2afa0"
            }
            AVAILABLE_NEW_POTASH -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "fc4e4e96-4f49-48a3-85fd-d0a2c7975654"
            }

            CALCIUM_MAGNESIUM -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "52ec4ca0-d691-4f2b-b17a-232c2966974a"
            }
            AVAILABLE_BORON -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "29e609a0-0f57-4365-a4eb-4eb84c61c9ab"
            }
            AVAILABLE_COPPER -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "4ae79049-8b6a-4230-b2b9-b3882ddfb68c"
            }
            AVAILABLE_ZINC -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "5eb9929f-54d1-41fe-b9f0-6c216ca0e693"
            }
            ORGANIC_CARBON -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "1830c0f7-b31e-4093-8b50-e804cb209ae8"
            }
            AVAILABLE_MANGANESE -> {
                appTitle = "ffem Soil"
                externalAppAction = "io.ffem.soil"
                "420d1caf-aff8-4eb9-8062-10307285d9a3"
            }


            else -> {
                appTitle = "ffem Water"
                externalAppAction = "io.ffem.water"
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
        intent?.extras?.let { extras ->
            val jsonResult = JSONObject()

            if (extras.containsKey(RESULT_JSON)) {
                val jsonString = extras.getString(RESULT_JSON)
                try {
                    val originalJson = JSONObject(jsonString)
                    val testType = originalJson.getString("name")
                    val testUuid = originalJson.getString("uuid")

                    jsonResult.put("type", externalAppAction)
                    jsonResult.put("name", testType)
                    jsonResult.put("uuid", testUuid)

                    val resultsArray = JSONArray()
                    val originalResults = originalJson.getJSONArray("result")
                    for (i in 0 until originalResults.length()) {
                        val originalResult = originalResults.getJSONObject(i)
                        val dilution = originalResult.getInt("dilution")
                        val name = originalResult.getString("name")
                        val unit = originalResult.getString("unit")
                        val id = originalResult.getInt("id")
                        val value = originalResult.getString("value")

                        val resultObject = JSONObject()
                        resultObject.put("dilution", dilution)
                        resultObject.put("name", name)
                        resultObject.put("unit", unit)
                        resultObject.put("id", id)
                        resultObject.put("value", value)
                        resultObject.put("testDate", SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()))

                        resultsArray.put(resultObject)
                    }

                    jsonResult.put("result", resultsArray)

                    // Now you have the JSON in the desired format in the 'jsonResult' object
                    // You can display it in your custom view or use it as needed.
                    val formattedJsonString = jsonResult.toString(2)
                    b.textViewName.text = formattedJsonString
                } catch (e: JSONException) {
                    e.printStackTrace()
                    // Handle JSON parsing error
                }
            }

            // Find the UI elements in the layout
            val textViewType = findViewById<TextView>(R.id.textViewType)
            val textViewName = findViewById<TextView>(R.id.textViewName)
            val textViewUUID = findViewById<TextView>(R.id.textViewUUID)
            val linearResultsContainer = findViewById<LinearLayout>(R.id.linearResultsContainer)


            // Set values in the UI elements
            textViewType.text = "Type: " + jsonResult.getString("type")
            textViewName.text = "Name: " + jsonResult.getString("name")
            textViewUUID.text = "UUID: " + jsonResult.getString("uuid")


            // Inside your function
            if (extras.containsKey(RESULT_JSON)) {
                // Dynamically create and add result items
                val resultsArray = jsonResult.getJSONArray("result")
                // Inside your loop where you create and add result items
                // Inside your loop where you create and add result items
                for (i in 0 until resultsArray.length()) {
                    val resultItem = resultsArray.getJSONObject(i)
                    val name = resultItem.getString("name")
                    val unit = resultItem.getString("unit")
                    val value = resultItem.getString("value")
                    val testDate = resultItem.getString("testDate")

                    // Check if an item with the same name already exists in linearResultsContainer
                    val existingView = linearResultsContainer.findViewWithTag<View>(name)

                    if (existingView != null) {
                        // An item with the same name already exists, show an alert dialog
                        AlertDialog.Builder(this)
                            .setTitle("Item Already Exists")
                            .setMessage("$name already exists in the results. It can't be added unless deleted.")
                            .setPositiveButton("OK", null)
                            .show()
                    } else {
                        // Create a LinearLayout to hold each result item and delete button
                        val resultLayout = LinearLayout(this)
                        resultLayout.orientation = LinearLayout.HORIZONTAL

                        // Set a unique tag for each resultLayout based on the name (or any unique identifier)
                        resultLayout.tag = name

                        val resultTextView = TextView(this)
                        resultTextView.text = "$name : $value $unit, \nTest Date: $testDate"

                        // Create a delete button
                        val deleteButton = Button(this)
                        deleteButton.text = "Del"
                        deleteButton.setOnClickListener {
                            // Get the tag (name) of the clicked resultLayout
                            val clickedName = resultLayout.tag.toString()

                            // Create an AlertDialog to confirm the delete action
                            AlertDialog.Builder(this)
                                .setTitle("Confirm Delete")
                                .setMessage("Are you sure you want to delete $clickedName result?")
                                .setPositiveButton("Delete") { _, _ ->
                                    // User clicked the "Delete" button in the dialog
                                    // Remove the result item from the JSON array and the UI
                                    resultsArray.remove(i)
                                    linearResultsContainer.removeView(resultLayout)
                                }
                                .setNegativeButton("Cancel", null) // Do nothing if the user cancels
                                .show()
                        }

                        // Add the TextView and delete button to the resultLayout
                        resultLayout.addView(deleteButton)
                        resultLayout.addView(resultTextView)

                        // Add the resultLayout to the linearResultsContainer
                        linearResultsContainer.addView(resultLayout)
                    }
                }
            } else if (intent.hasExtra(TEST_VALUE)) {
                // Handle the case when 'TEST_VALUE' is present but not 'RESULT_JSON'
                val resultString = intent.getStringExtra(TEST_VALUE)
                try {
                    if (resultString != null) {
                        b.textViewName.text = getString(R.string.result_display, resultString)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}