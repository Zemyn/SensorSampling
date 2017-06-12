package emblab.colostate.sensorsampling.PDR;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;

import emblab.colostate.sensorsampling.PDR.direction.IMT;

/**
 * Created by Zemel on 5/25/17.
 */

public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager sensorManager;

    private Sensor gsensor;
    private Sensor msensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];

    private float azimuth = 0f;
    private float currectAzimuth = 0;

    public ImageView arrowView = null;

    private IMT imtAlg = new IMT();

    public Compass(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, gsensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,SensorManager.SENSOR_DELAY_GAME);
    }


    public void stop() {
        sensorManager.unregisterListener(this);
    }

    private void adjustArrow() {
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set");
            return;
        }

        Log.i(TAG, "will set rotation from " + currectAzimuth + " to " + azimuth);

        Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currectAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrowView.startAnimation(an);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];

                //z axis don't change it
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];

                cacheData(mGeomagnetic[0],mGeomagnetic[1]);

                //todo show the directon of the calibrated data from IMT alg
                IMTcalibration();
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;
                adjustArrow();
            }
        }
    }

    private Queue<float[]> tri_acc = new LinkedList<>();
    private float[][] selectedVector;

    /**
     * Cache 3 data to the queue
     * @param x the data along the x axis
     * @param y the data along y axis
     */
    private void cacheData(float x, float y){
        while(tri_acc.size()>=3){
            tri_acc.poll();
        }
        tri_acc.offer(new float[]{x,y});
    }

    private void IMTcalibration(){

        if(tri_acc.size()>=3){
            this.selectedVector = imtAlg.vectorSelection(tri_acc);
        }

        if (this.selectedVector != null){
            //the direction should be along the y axis of the
            float[] direct = new float[]{1,0};
            float[] calibratedDirection = imtAlg.getMagnetVector(this.selectedVector,direct);
            //here could show the
            mGeomagnetic[0] = calibratedDirection[0];
            mGeomagnetic[1] = calibratedDirection[1];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
