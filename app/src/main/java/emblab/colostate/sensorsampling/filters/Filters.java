package emblab.colostate.sensorsampling.Utils;

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


}
