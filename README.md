# ffem-app-integration-sample
Sample app to demonstrate external app integration with ffem Water and ffem Soil

# Setup

1. Install ffem Soil and/or ffem Water apps [from Play store](https://play.google.com/store/apps/developer?id=Foundation+for+Environmental+Monitoring)
1. Download and install Git and add it to your PATH
1. Download and install Android Studio
1. Clone this project locally
1. Open the project in Android Studio
1. Build and run the app on the same phone that has the above ffem apps installed [How to run apps on device](https://developer.android.com/training/basics/firstapp/running-app) 

# Using the app

1. Launch ffem Integration app
1. Select the test to perform from the drop down on the top
1. Ensure 'Request a dummy result' is checked
1. Click the 'Launch Test' button
1. ffem Water or ffem Soil will launch
1. Click the Next button
1. The app will then return a dummy result back
1. The json result will be displayed. Use this result as required in your app


# How to use the code

- The code required for integration is in the MainActivity class
- 'setupTestInformation' shows how to setup the data required before calling the external app
- 'launchTest' shows how to launch the external app
- 'displayResult' shows how to extract the json result returned
- Copy the above functions to your app code and make changes to the test data as required

# Where to find the meta info for the tests

- For Soil tests get the info from <a href="https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/master/caddisfly-app/app/src/soil/assets/tests.json" target="_blank">Soil tests.json</a>
- For Water tests get the info from <a href="https://github.com/foundation-for-environmental-monitoring/ffem-app/blob/master/caddisfly-app/app/src/water/assets/tests.json" target="_blank">Water tests.json</a>

# What info is needed to call ffem app

- The only required info is the 'uuid' from the above json files
- This will tell ffem Soil or ffem Water which test to launch
- To launch appropriate ffem app set intent action to either 'io.ffem.soil' or 'io.ffem.water'


```java
      // Create intent with appropriate app to launch
      Intent intent = new Intent("io.ffem.soil");

      // Add the test uuid to the intent. (See how to get uuids above)
      Bundle data = new Bundle();
      data.putString("testId", "3353f5cf-1cd2-4bf5-b47f-15d3db917add");
      
      // To make testing the integration easier also set debugMode to true
      // This will make the ffem apps return a dummy result
      // This way conducting an actual water or soil test can be avoided 
      // while testing the integration to save development time
      data.putBoolean("debugMode", true);
      // Note: do not use the above line for production app.
      
      intent.putExtras(data)   

      // Start the external app activity
      startActivityForResult(intent, 100);
 ```

- After the Soil or Water test is finished the result gets returned to your app
```java
      public void onActivityResult(int requestCode, int resultCode, Intent intent) {
          if (resultCode == RESULT_OK) {
            String jsonString = intent.getStringExtra("resultJson");
          }
    }
 ```

- Display the info provided in the json result as required in your app. Example of json below:
```json
 {
       "type": "io.ffem.soil",
       "name": "Available Iron",
       "uuid": "3353f5cf-1cd2-4bf5-b47f-15d3db917add",
       "result": [{
           "dilution": 3,
           "name": "Available Iron",
           "unit": "mg/l",
           "id": 1,
           "value": "> 30.00"
       }],
       "testDate": "2018-09-19 01:05"
   }
```
# Additional info on returned json result

- The value property has the result value. Suffix the unit property value to this
- Note: the value can sometimes have a '>' (greater than) meaning that the actual result could be higher
- For dilution: 0 or 1 = 'No Dilution',2 = '2 Times Dilution',etc... For info only, no further calculation required
- A higher dilution factor usually denotes that the accuracy of the result may be lower

# If ffem apps are not found for launching
- If ffem Soil or ffem Water is not installed on the phone then you can request the user to install them
- e.g. you could provide the link to the app install page https://play.google.com/store/apps/details?id=io.ffem.soil









