package emblab.colostate.sensorsampling.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import emblab.colostate.sensorsampling.R;

public class MagneticActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sm;
    private Sensor megSensor;
    private Sensor gSensor;

    private float[] gSensorReading = new float[3];
    private float[] geomagReading = new float[3];

    private float[] mRotationMatrix = new float[9];
    private float[] mInclineMatrix = new float[9];
    private float[] mOrientation = new float[3];

    private float currentDegree = 0f;
    private ImageView image;

    private TextView txv_mag_field_reading;
    private TextView txv_orientation_reading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetic_sensor);
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

        image = (ImageView) findViewById(R.id.img_compass);

        sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        megSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gSensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);

        txv_mag_field_reading = (TextView)findViewById(R.id.txv_meg_read);
        txv_orientation_reading = (TextView)findViewById(R.id.txv_orientation_read);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s = event.sensor;
        if(s.getType()==Sensor.TYPE_GRAVITY){
            gSensorReading = event.values;
        } else if(s.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            geomagReading = event.values;
        }

        if(gSensorReading != null && geomagReading!=null){
            getOrientation();
        }
    }

    public void stopSensing(View view){
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void getOrientation() {
        //the geomag reading may need to be calibrated

        float[] testReading = new float[3];
        testReading = geomagReading;

        sm.getRotationMatrix(mRotationMatrix,mInclineMatrix,gSensorReading,testReading/*geomagReading*/);
        sm.getOrientation(mRotationMatrix,mOrientation);

        float degree = (float)Math.toDegrees(mOrientation[0]);
        RotateAnimation ra = new RotateAnimation(currentDegree,-degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        //set the animation duration time
        ra.setDuration(210);
        //set the animation stay
        ra.setFillAfter(true);
        // start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

        txv_mag_field_reading.setText("x:"+testReading[0]+" y:"+testReading[1]+" z:"+testReading[2]);
        txv_orientation_reading.setText("-z:"+(float)Math.toDegrees(mOrientation[0])+" x:"+(float)Math.toDegrees(mOrientation[1])+" y:"+(float)Math.toDegrees(mOrientation[2]));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        sm.registerListener(MagneticActivity.this,megSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(MagneticActivity.this,gSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
}
