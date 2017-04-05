package emblab.colostate.sensorsampling.Utils;

import java.util.Queue;

/**
 * Created by Zemel on 2/4/17.
 */

public class Algorithm {

    /**
     * Created by Zemel on 2/4/17.
     * @Description return the nomalized gravity vector
     */
    static public TriDVector getGravityVector(Queue<TriDVector> window) {
        float xAverage = 0f, yAverage = 0f, zAverage = 0f;
        int num = window.size();

        for (TriDVector sig : window) {
            xAverage = xAverage +sig.x;
            yAverage = xAverage +sig.y;
            zAverage = xAverage +sig.z;
        }
        xAverage = xAverage/num;
        yAverage = yAverage/num;
        zAverage = zAverage/num;

        float mean = (float) Math.sqrt(xAverage*xAverage+yAverage*yAverage+zAverage*zAverage);
        TriDVector gVector = new TriDVector(xAverage/mean,yAverage/mean,zAverage/mean);
        return gVector;
    }

    static public float getStd(float[] z){
        float sum = 0f;
        int num = 0;
        for(float e: z){
            sum = sum + e;
            num++;
        }
        return sum/num;
    }

}
