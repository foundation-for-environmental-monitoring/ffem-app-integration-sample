# ffem-app-integration-sample
This sample app shows how to run a ffem Match test from your app

&nbsp;

# Run this sample app

1. Install ffem Match app on the phone <a href="https://play.google.com/store/apps/details?id=io.ffem.match" target="_blank">from Play store</a>
1. Clone and run this project on the same phone (<a href="https://developer.android.com/training/basics/firstapp/running-app" target="_blank">How to run apps</a>) 
1. Click <b>Launch Test</b> and complete the soil/water test  
1. Click <b>Save</b> to get the result in your app

![App flow](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/assets/4124856/df35d2d4-7ec2-4a27-a508-c39744a5a85e)


&nbsp;
____________
# Code to launch ffem Match and run a specific test:

```java
      Intent intent = new Intent("ffem.match");

      // Add the testId
      Bundle data = new Bundle();
      data.putString("testId", "WC-FM-F");
     
      intent.putExtras(data)   
      startActivityForResult(intent, 100);
 ```
- Note: testId data is available in <a href="https://github.com/foundation-for-environmental-monitoring/ffem-match/blob/master/colorCard/match-parameters.json" target="_blank">match-parameters.json</a>

&nbsp;
# Code to get the returned result:
```java
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
          if (resultCode == RESULT_OK) {
            String jsonString = intent.getStringExtra("resultJson");
          }
    }
 ```

&nbsp;
# Get dummy result
During development use the following code to quickly get the test result without running the actual soil/water test

```java
      // Add the testId
      ...

      // Include this line in development testing only
      data.putBoolean("debugMode", true);

      ...
      startActivityForResult(intent, 100);
 ```
![dummy result flow](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/assets/4124856/e5e5d254-1bf1-43b4-9759-a70a1d285010)


&nbsp;
____________


# Example of returned result:
```json
 {
       "testType": "Card",
       "sampleType": "Water",       
       "name": "Fluoride",
       "parameterId": "WC-FM-F",
       "result": [{
           "dilution": 3,
           "name": "Fluoride",
           "unit": "mg/l",
           "id": 1,
           "value": "> 2.00"
       }],
       "testDate": "2018-09-19 01:05"
   }
```
# Notes:

- value can sometimes have a '>' (greater than) meaning that the actual result could be higher
- dilution: 0 or 1 = 'No Dilution', 2 = '2 Times Dilution', etc... For info only, no further calculation required
- A higher dilution factor usually denotes that the accuracy of the result may be lower

&nbsp;
____________
 

# Code in this project

- [setupTestInformation](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/blob/8ca44d58b85916d72fa7dc3bb96a986c10f0f261/app/src/main/java/io/ffem/integration/MainActivity.kt#L71) shows how to set up the data
- [launchTest](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/blob/8ca44d58b85916d72fa7dc3bb96a986c10f0f261/app/src/main/java/io/ffem/integration/MainActivity.kt#L44) shows how to launch ffem Match from your app
- [displayResult](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/blob/8ca44d58b85916d72fa7dc3bb96a986c10f0f261/app/src/main/java/io/ffem/integration/MainActivity.kt#L138) shows how to extract the json result returned

&nbsp;
____________


# If ffem Match app is not installed on user's phone
- If ffem Match is not installed then you can request the user to install from https://play.google.com/store/apps/details?id=io.ffem.match

![app not found](https://github.com/foundation-for-environmental-monitoring/ffem-app-integration-sample/assets/4124856/370eeda9-66e7-45ec-9d36-b6df8de3f3b6)


&nbsp;
____________






