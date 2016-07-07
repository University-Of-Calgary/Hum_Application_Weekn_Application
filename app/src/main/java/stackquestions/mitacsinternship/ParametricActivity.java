package stackquestions.mitacsinternship;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ParametricActivity extends AppCompatActivity {

    Spinner startTime, endTime, howOften, duration, noiseSmoothing, noiseThreshold;
    Switch gps, storeOriginal;
    String[] startTimeValues = {"Now", "1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "60 minutes"};
    String[] endTimeValues = {"1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "60 minutes"};
    String[] howOftenValues = {"10 seconds", "60 seconds", "300 seconds", "600 seconds"};
    String[] durationValues = {"5 seconds", "10 seconds", "30 seconds", "60 seconds", "120 seconds",
            "300 seconds", "600 seconds"};
    String[] noiseThresholdValues = {"Barely Audible", "Very Low", "Low", "Clearly Audible", "Loud", "Very Loud"};

    Map<String, Integer> startTimeHash = new HashMap<String, Integer>();
    Map<String, Integer> endTimeHash = new HashMap<String, Integer>();
    Map<String, Integer> howOftenHash = new HashMap<String, Integer>();
    Map<String, Integer> durationHash = new HashMap<String, Integer>();
    Map<String, Integer> thresholdHash = new HashMap<String, Integer>();

    ImageButton doneButton;
    public static final String PREFERENCES = "AudioRecordingPrefs";
    public static final String timeStartKey = "startKey";
    public static final String timeEndKey = "endKey";
    public static final String timeOftenKey = "oftenKey";
    public static final String timeRecordingKey = "recordingKey";
    public static final String thresholdNoiseKey = "thresholdKey";
    public static final String gpsValueKey = "gpsKey";
    public static final String originalStoreKey = "originalKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametric);

        startTime = (Spinner) findViewById(R.id.parametric_spinner_start);
        endTime = (Spinner) findViewById(R.id.parametric_spinner_end);
        howOften = (Spinner) findViewById(R.id.parametric_spinner_howoften);
        duration = (Spinner) findViewById(R.id.parametric_spinner_duration);
        noiseThreshold = (Spinner) findViewById(R.id.parametric_spinner_threshold);
        gps = (Switch) findViewById(R.id.parametric_switch_gps);
        storeOriginal = (Switch) findViewById(R.id.parametric_switch_storeoriginal);
        doneButton = (ImageButton) findViewById(R.id.parametric_button_storedata);

        // add the values to the hash of start times
        startTimeHash.put(startTimeValues[0], 0);
        for (int i=1; i<startTimeValues.length; i++)
            startTimeHash.put(startTimeValues[i], Integer.parseInt(startTimeValues[i].split(" ")[0]));

        // add the values to the hash of end times
        for (int i=0; i<endTimeValues.length; i++)
            endTimeHash.put(endTimeValues[i], Integer.parseInt(endTimeValues[i].split(" ")[0]));

        // add the values to the hash of how often times, values added in seconds
        for (int i=0; i<howOftenValues.length; i++)
            howOftenHash.put(howOftenValues[i], Integer.parseInt(howOftenValues[i].split(" ")[0]));

        // add the values to the hash of duration times, values added in seconds
        for (int i=0; i<durationValues.length; i++)
            durationHash.put(durationValues[i], Integer.parseInt(durationValues[i].split(" ")[0]));

        // add the values to the hash of threshold values
        thresholdHash.put("Barely Audible", 0);
        thresholdHash.put("Very Low", 5000);
        thresholdHash.put("Low", 10000);
        thresholdHash.put("Clearly Audible", 15000);
        thresholdHash.put("Loud", 20000);
        thresholdHash.put("Very Loud", 25000);

        startTime.setAdapter(new ArrayAdapter<String>(ParametricActivity.this,
                android.R.layout.simple_spinner_dropdown_item, startTimeValues));
        endTime.setAdapter(new ArrayAdapter<String>(ParametricActivity.this,
                android.R.layout.simple_spinner_dropdown_item, endTimeValues));
        howOften.setAdapter(new ArrayAdapter<String>(ParametricActivity.this,
                android.R.layout.simple_spinner_dropdown_item, howOftenValues));
        duration.setAdapter(new ArrayAdapter<String>(ParametricActivity.this,
                android.R.layout.simple_spinner_dropdown_item, durationValues));
        noiseThreshold.setAdapter(new ArrayAdapter<String>(ParametricActivity.this,
                android.R.layout.simple_spinner_dropdown_item, noiseThresholdValues));

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the data from the spinners
                String timeStart = startTime.getSelectedItem().toString();
                String timeEnd = endTime.getSelectedItem().toString();
                String timeOften = howOften.getSelectedItem().toString();
                String timeRecording = duration.getSelectedItem().toString();
                String thresholdNoise = noiseThreshold.getSelectedItem().toString();
                String gpsValue = gps.isChecked() ? "yes" : "no";
                String originalStore = storeOriginal.isChecked() ? "yes" : "no";

                // System.out.println("This is the gps switch value : " + gpsValue);
                // System.out.println("The type of the gps switch value is : " + gpsValue.getClass().getName());
                // SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, MODE_PRIVATE).edit();
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                /**editor.putString(timeStartKey, timeStart);
                editor.putString(timeEndKey, timeEnd);
                editor.putString(timeOftenKey, timeOften);
                editor.putString(timeRecordingKey, timeRecording);
                editor.putString(thresholdNoiseKey, thresholdNoise);
                editor.putString(gpsValueKey, gpsValue);
                editor.putString(originalStoreKey, originalStore);*/

                editor.putString(timeStartKey, startTimeHash.get(timeStart).toString());
                editor.putString(timeEndKey, endTimeHash.get(timeEnd).toString());
                editor.putString(timeOftenKey, howOftenHash.get(timeOften).toString());
                editor.putString(timeRecordingKey, durationHash.get(timeRecording).toString());
                editor.putString(thresholdNoiseKey, thresholdHash.get(thresholdNoise).toString());
                editor.putString(gpsValueKey, gpsValue);
                editor.putString(originalStoreKey, originalStore);
                editor.commit();
                Toast.makeText(ParametricActivity.this, "Settings have been saved", Toast.LENGTH_LONG).show();

                Intent settingsActivity = new Intent(ParametricActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_parametric_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Menu items selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recordingParameters:
                Intent preferencesIntent = new Intent(ParametricActivity.this, RecorderSettings.class);
                startActivity(preferencesIntent);
                return true;

            case R.id.findingGPS:
                Intent gpsIntent = new Intent(ParametricActivity.this, GetGPSDataActivity.class);
                startActivity(gpsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}