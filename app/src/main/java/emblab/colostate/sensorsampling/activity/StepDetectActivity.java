package emblab.colostate.sensorsampling.activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import emblab.colostate.sensorsampling.R;

public class StepDetectActivity extends Activity implements SensorEventListener {
    private SensorManager sm;
    private Sensor stepDetSensor;
    private TextView txv_step_counter;
    private boolean activityRunning;
    private float count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detect);


        txv_step_counter = (TextView) findViewById(R.id.txv_step_count);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetSensor = sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        if (stepDetSensor != null) {
            sm.registerListener(this, stepDetSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(this, "registered", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        //sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        Toast.makeText(this, "changed ", Toast.LENGTH_LONG).show();
        if (activityRunning) {
            count = count+e.values[0];
            txv_step_counter.setText(String.valueOf(count));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
