package stackquestions.mitacsinternship;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

public class ParametricActivity extends AppCompatActivity {

    Spinner startTime, endTime, howOften, duration, noiseSmoothing, noiseThreshold;
    Switch gps, storeOriginal;
    String[] startTimeValues = {"Now", "1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "1 hour"};
    String[] endTimeValues = {"30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes", "30 minutes", "1 hour"};
    String[] howOftenValues = {"10 seconds", "1 minute", "5 minutes", "10 minutes"};
    String[] durationValues = {"5 seconds", "10 seconds", "30 seconds", "1 minute", "2 minutes",
            "5 minutes", "10 minutes"};
    String[] noiseThresholdValues = {"0", "500", "1000", "2000", "5000", "10000"};

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
    }
}
