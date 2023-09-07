# ffem-app-integration-sample
Sample app to demonstrate external app integration with ffem Match

# Setup

1. Clone and open this project in Android Studio
1. Install ffem Match app on the phone [from Play store](https://play.google.com/store/apps/details?id=io.ffem.match)
1. Build and run this project app on the same phone [How to run apps](https://developer.android.com/training/basics/firstapp/running-app) 

# Using the app

1. Launch ffem Integration app and click <b>Launch Test</b>
2. Follow the prompts to complete the test  
4. Click <b>Save</b> and a json result will be returned to the integrated app
   ![RunTest](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/assets/4124856/fc0817d6-6dd6-4e35-86e0-dbd5194ca587)


# The code

- The code required for integration is in the MainActivity class
- 'setupTestInformation' shows how to setup the data required before calling the external app
- 'launchTest' shows how to launch the external app
- 'displayResult' shows how to extract the json result returned
- Copy the above functions to your app code and make changes to the test data as required

# Intent action and testId

- Set intent action to 'ffem.match'
- Set 'testId' for the required test
- testId is in <a href="https://github.com/foundation-for-environmental-monitoring/ffem-match/blob/master/colorCard/match-parameters.json" target="_blank">match-parameters.json</a>


```java
      // Create intent with appropriate app to launch
      Intent intent = new Intent("ffem.match");

      // Add the test uuid to the intent. (See how to get uuids above)
      Bundle data = new Bundle();
      data.putString("testId", "WC-FM-F");
      
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

- After the Soil or Water test is finished the result is returned to your app
```java
      public void onActivityResult(int requestCode, int resultCode, Intent intent) {
          if (resultCode == RESULT_OK) {
            String jsonString = intent.getStringExtra("resultJson");
          }
    }
 ```

- Use the returned json result as required in your app. Example of json below:
```json
 {
       "type": "ffem.match",
       "name": "Fluoride",
       "uuid": "WC-FM-F",
       "result": [{
           "dilution": 3,
           "name": "Fluoride",
           "unit": "mg/l",
           "id": 1,
           "value": "> 1.00"
       }],
       "testDate": "2018-09-19 01:05"
   }
```
# The returned json result

- The value property has the result value. Suffix the unit property value to this
- Note: the value can sometimes have a '>' (greater than) meaning that the actual result could be higher
- For dilution: 0 or 1 = 'No Dilution',2 = '2 Times Dilution',etc... For info only, no further calculation required
- A higher dilution factor usually denotes that the accuracy of the result may be lower

# If ffem Match app is not installed
- If ffem Match is not installed on the phone then you can request the user to install them
- e.g. you could provide the link to the app install page https://play.google.com/store/apps/details?id=io.ffem.match









