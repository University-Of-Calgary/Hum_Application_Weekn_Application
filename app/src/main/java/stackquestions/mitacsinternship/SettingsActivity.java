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
            Toast.makeText(SettingsActivity.this, "Restored Text is Not Null", Toast.LENGTH_SHORT).show();
            String startTime = sharedPreferences.getString(timeStartKey, "0");
            String endTime = sharedPreferences.getString(timeEndKey, "0");
            String howOften = sharedPreferences.getString(timeOftenKey, "0");
            String duration = sharedPreferences.getString(timeRecordingKey, "0");
            String threshold = sharedPreferences.getString(thresholdNoiseKey, "0");
            String gps = sharedPreferences.getString(gpsValueKey, "No");
            String original = sharedPreferences.getString(originalStoreKey, "No");

            preferencesText.setText("The start time inside settings activity is : " + startTime);
            System.out.println("Start Time in Settings Activity is : " + startTime);
        // }
    }
}
