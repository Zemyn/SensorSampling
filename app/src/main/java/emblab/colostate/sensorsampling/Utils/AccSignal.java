package emblab.colostate.sensorsampling.Utils;

/**
 * Created by Zemel on 3/17/17.
 */

public class AccSignal {
    public int index;
    public float x;
    public float y;
    public float z;
    private SType type;

    public AccSignal(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMagnitude(){
        double square_sum = x*x+y*y+z*z;
        return Math.sqrt(square_sum);
    }

    public SType getType(){
        return this.type;
    }

    public void setType(SType s){
        this.type = s;
    }
}
