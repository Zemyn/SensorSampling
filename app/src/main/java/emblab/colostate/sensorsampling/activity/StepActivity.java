package emblab.colostate.sensorsampling.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import emblab.colostate.sensorsampling.R;
import emblab.colostate.sensorsampling.Utils.TriDVector;

public class StepActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sm;
    private Sensor gSensor;
    private Sensor lSensor;

    private TriDVector g_vector = null;
    private TriDVector v_vector = null;
    private TextView txv_v_magnitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txv_v_magnitude = (TextView)findViewById(R.id.v_vector);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gSensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        lSensor = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s = event.sensor;
        if (s.getType() == Sensor.TYPE_GRAVITY){
            //Get the direction of the gravity
            g_vector  = new TriDVector(event.values[0],event.values[1],event.values[2]);
        } else if(s.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //TODO
            if(g_vector!=null){
                TriDVector l_vector = new TriDVector(event.values[0],event.values[1],event.values[2]);
                v_vector = TriDVector.multiply(g_vector,l_vector);
                double real_v = v_vector.getMagnitude()-g_vector.getMagnitude();
                txv_v_magnitude.setText(""+real_v);
            } else {
                //TODO
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(StepActivity.this,gSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(StepActivity.this,lSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
