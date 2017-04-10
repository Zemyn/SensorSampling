package emblab.colostate.sensorsampling.filters;

import emblab.colostate.sensorsampling.Utils.TriDVector;

/**
 * Created by Zemel on 2/3/17.
 */

public class Filters {
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.

    /**
     * @author Zemin
     * create at 2017/02/03
     * @Description The low pass filter for the signal processing:
     * for i from 1 to n
     * y[i] := y[i-1] + α*(x[i]-y[i-1])
     */
    static public float[] lowPassFilter(float[] input, float[] output){
        if (output == null) return input;
        for (int i = 0; i<input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    /**
     * @author Zemin
     * create at 2017/04/06
     * @Description The complementary filter for the signal processing:
     * for i from 1 to n
     * y[i] := y[i-1] + α*(x[i]-y[i-1])
     */
    static public void complementaryFilter(float[] accData, float[] gyrData,double pitch, double roll){
        float dt = 0.01f; //
        float ACCELEROMETER_SENSITIVITY = 8192.0f;
        float GYROSCOPE_SENSITIVITY = 65.536f;
        double pitchAcc, rollAcc;

        pitch += (gyrData[0]/GYROSCOPE_SENSITIVITY)*dt; //Angle around x-axis
        roll -= (gyrData[1]/GYROSCOPE_SENSITIVITY)*dt; //Angle around y-axis

        // Compensate for drift with accelerometer data if !bullshit
        // Sensitivity = -2 to 2 G at 16Bit -> 2G = 32768 && 0.5G = 8192
        float forceMagnitudeApprox = Math.abs(accData[0]) + Math.abs(accData[1]) + Math.abs(accData[2]);
        if (forceMagnitudeApprox > 8192 && forceMagnitudeApprox < 32768){
            // Turning around the X axis results in a vector on the Y-axis
            pitchAcc = Math.atan2((double)accData[1], (double)accData[2]) * 180 / Math.PI;
            pitch = pitch * 0.98 + pitchAcc * 0.02;

            // Turning around the Y axis results in a vector on the X-axis
            rollAcc = Math.atan2((double)accData[0],(double)accData[2]) * 180 / Math.PI;
            roll = roll * 0.98 + roll * 0.02;
        }
    }

    /**
     * @Description Choose 3 consecutive vectors which are not parallel
     * */
    static public TriDVector[] vectorSelection(TriDVector[] input){
        int len = input.length;
        for(int i=0; i<=len-3;++i){
            TriDVector s_1 = input[i];
            TriDVector s_2 = input[i+1];
            TriDVector s_3 = input[i+2];

            if(checkParallel(s_1,s_2)||checkParallel(s_1,s_3)||checkParallel(s_2,s_3)){
                continue;
            }else{
                TriDVector[] r = {s_1,s_2,s_3};
                return r;
            }
        }
        return null;
    }

    static public boolean checkParallel(TriDVector v_1,TriDVector v_2){
        float delta_x = v_1.x/v_2.x;
        float delta_y = v_1.y/v_2.y;
        float delta_z = v_1.z/v_2.z;
        if(delta_x == delta_y && delta_y==delta_z){
            return true;
        } else{
            return false;
        }
    }


    /**
     * Implement the IMF algorithm
     */
    static public void IMF(TriDVector[] megdata, TriDVector g_vector){ //here the vector should be on the horizontal plane
        TriDVector[] choosed_vectors = vectorSelection(megdata);
        double threshold = 10.0;
        double r_g = choosed_vectors[0].getMagnitude();
        while(r_g>threshold){
            for(int theta_G=0;theta_G<=360;theta_G++){
                double[][] m = TriDVector.buildRotateMatrix(g_vector,theta_G);
                //Here use the fist selected vector as the basic vector
                TriDVector testVector = TriDVector.getRotatedVector(m,choosed_vectors[0]);
                //TODO
            }
        }
    }


}
