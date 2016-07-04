package stackquestions.mitacsinternship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class ParametricActivity extends AppCompatActivity {

    Spinner startTime, endTime, howOften, duration, noiseSmoothing, noiseThreshold;
    Switch gps, storeOriginal;
    String[] startTimeValues = {"Now", "1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "1 hour"};
    String[] endTimeValues = {"30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "1 hour"};
    String[] howOftenValues = {"10 seconds", "1 minute", "5 minutes", "10 minutes"};
    String[] durationValues = {"5 seconds", "10 seconds", "30 seconds", "1 minute", "2 minutes",
            "5 minutes", "10 minutes"};
    String[] noiseThresholdValues = {"0", "500", "1000", "2000", "5000", "10000"};
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
                SharedPreferences preferences = getSharedPreferences(PREFERENCES, MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString(timeStartKey, timeStart);
                editor.putString(timeEndKey, timeEnd);
                editor.putString(timeOftenKey, timeOften);
                editor.putString(timeRecordingKey, timeRecording);
                editor.putString(thresholdNoiseKey, thresholdNoise);
                editor.putString(gpsValueKey, gpsValue);
                editor.putString(originalStoreKey, originalStore);
                editor.commit();
                Toast.makeText(ParametricActivity.this, "Settings have been saved", Toast.LENGTH_LONG).show();

                Intent settingsActivity = new Intent(ParametricActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });
    }
}
