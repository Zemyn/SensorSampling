package emblab.colostate.sensorsampling.PDR.stepDetect;

import java.util.Arrays;

/**
 * Created by Zemel on 3/16/17.
 */

public class ZeroPassAlg {

    private final static int WINDOW_SIZE = 250;

    private float low_pass_param = 0.02f;

    public ZeroPassAlg(){}

    /**
     * Take the gravity vector of a window
     * @return
     */
    protected float[] getGravityVector(float[][] accSignals){
        int len = accSignals.length;
        float x=0f,y=0f,z=0f;
        for (float[] a: accSignals){
            x += a[0];
            y += a[1];
            z += a[2];
        }
        float[] r = {x/len,y/len,z/len};
        return r;
    }

    /**
     * project the acceleration signal to the gravity direction
     * @param a the acceleration signal
     * @param g the gravity signal
     * @return a vector that represent the a on the gravity direction component
     */
    protected float[] projectToGravity(float[] a, float[] g){
        float s = (a[0]*g[0]+a[1]*g[1]+a[2]*g[1])/(g[0]*g[0]+g[1]*g[1]+g[2]*g[2]);
        return new float[] {g[0]*s,g[1]*s,g[2]*s};
    }

    /**
     * calculate a - g
     * @param a the acceleration signal
     * @param g the gravity vector
     */
    protected void removeGravity(float[] a ,float[] g){
        a[0] -= g[0];
        a[1] -= g[1];
        a[2] -= g[2];
    }

    /**
     * here the low pass filter may need to be optimized
     * @param values
     * @param lastAcc
     */
    protected float[] lowPassFilter(float[] values,  float[] lastAcc){
        float[] r = new float[3];
        r[0]+=low_pass_param*(values[0]-lastAcc[0]);
        r[1]+=low_pass_param*(values[1]-lastAcc[1]);
        r[2]+=low_pass_param*(values[2]-lastAcc[2]);
        return r;
    }

    /**
     * @param accMag should be the magnitude along the G vector
     * @return
     */
    protected void accAnalysis(float[] accMag){
        int start = -1,mid = -1,end = -1;
        int peak, valley;
        int i = 1;
        while(i<WINDOW_SIZE){
            if(accMag[i-1]<=0 && accMag[i]>=0){
                if (i>mid){
                    end = i;
                    mid = -1;
                    start = -1;
                }
            }
            if(accMag[i-1]>=0 && accMag[i]<=0){
                mid = i;
            }
            i++;
        }
        float[] stepSignal = Arrays.copyOfRange(accMag,start,end);
        findMaxMin(0,0,stepSignal);

    }

    private void findMaxMin(float max, float min, float[] src){
        for(float a:src){
            if(a>max){
                max = a;
            }
            if(a<min){
                min = a;
            }
        }
    }



}
