package stackquestions.mitacsinternship;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFERENCES = "AudioRecordingPrefs";
    public static final String timeStartKey = "startKey";
    public static final String timeEndKey = "endKey";
    public static final String timeOftenKey = "oftenKey";
    public static final String timeRecordingKey = "recordingKey";
    public static final String thresholdNoiseKey = "thresholdKey";
    public static final String gpsValueKey = "gpsKey";
    public static final String originalStoreKey = "originalKey";

    TextView preferencesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesText = (TextView) findViewById(R.id.settings_tv_preferences);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_WORLD_READABLE);
        // String restoredText = sharedPreferences.getString("text", null);
        //if (restoredText != null){
            /**Toast.makeText(SettingsActivity.this, "Restored Text is Not Null", Toast.LENGTH_SHORT).show();
            String startTime = sharedPreferences.getString(timeStartKey, "0");
            String endTime = sharedPreferences.getString(timeEndKey, "0");
            String howOften = sharedPreferences.getString(timeOftenKey, "0");
            String duration = sharedPreferences.getString(timeRecordingKey, "0");
            String threshold = sharedPreferences.getString(thresholdNoiseKey, "0");
            String gps = sharedPreferences.getString(gpsValueKey, "No");
            String original = sharedPreferences.getString(originalStoreKey, "No");*/

        int startTimeInteger = Integer.parseInt(sharedPreferences.getString(timeStartKey, "0"));
        int endTimeInteger = Integer.parseInt(sharedPreferences.getString(timeEndKey, "1")); // default time in minutes
        int howOftenInteger = Integer.parseInt(sharedPreferences.getString(timeOftenKey, "10")); // default time in seconds
        int durationInteger = Integer.parseInt(sharedPreferences.getString(timeRecordingKey, "5")); // default time in seconds
        int thresholdInteger = Integer.parseInt(sharedPreferences.getString(thresholdNoiseKey, "0")); // default threshold of 0
        String gpsValue = sharedPreferences.getString(gpsValueKey, "No"); // default gps -> not on
        String originalSound = sharedPreferences.getString(originalStoreKey, "No"); // default store original -> no

            // preferencesText.setText("The start time inside settings activity is : " + startTime);
            // System.out.println("Start Time in Settings Activity is : " + startTime);
        // }

        // Print out the values
        System.out.println("Start Time : " + startTimeInteger);
        System.out.println("End Time : " + endTimeInteger);
        System.out.println("How often recording : " + howOftenInteger);
        System.out.println("Duration of Recording : " + durationInteger);
        System.out.println("Threshold Integer : " + thresholdInteger);
        System.out.println("GPS Value : " + gpsValue);
        System.out.println("Original Recording Store : " + originalSound);
    }
}
