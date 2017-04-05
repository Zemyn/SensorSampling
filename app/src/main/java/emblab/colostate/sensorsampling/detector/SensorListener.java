package emblab.colostate.sensorsampling.detector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Zemel on 3/30/17.
 */

public class SensorListener implements SensorEventListener {
    Context context;
    private SensorManager mSensorManager = null;
    private double[] lastAcc = new double[]{0.0, 0.0, 0.0};


    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];

    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];

    // accelerometer vector
    private float[] grav = new float[3];

    // the
    private float[] incline = new float[3];

    // magnetic field vector
    private float[] magnet = new float[3];

    public SensorListener(Context ctx){
        this.context = ctx;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s = event.sensor;
        if (s.getType() == Sensor.TYPE_GRAVITY){
            System.arraycopy(event.values, 0, grav, 0, 3);
        }else if(s.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            //TODO to figure out the turning angle
        }else if(s.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, accel, 0, 3);
            calculateAccMagOrientation();
        }else if(s.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values, 0, magnet, 0, 3);
            //TODO here may need to calibrte the magnet matrix
        }
    }

    // calculates orientation angles from accelerometer and magnetometer output
    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, grav, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
