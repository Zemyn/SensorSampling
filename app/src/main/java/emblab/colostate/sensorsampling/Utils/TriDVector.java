package emblab.colostate.sensorsampling.Utils;

/**
 * Created by Zemel on 2/4/17.
 */

public class TriDVector {
    public float x;
    public float y;
    public float z;

    public TriDVector(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TriDVector getDirection(){
        double mag = this.getMagnitude();
        float x_mag = (float) (x/mag);
        float y_mag = (float) (y/mag);
        float z_mag = (float) (z/mag);
        TriDVector d = new TriDVector(x_mag,y_mag,z_mag);
        return d;
    }

    public double getMagnitude(){
        return Math.sqrt(x*x+y*y+z*z);
    }

    static public TriDVector multiply(TriDVector a, TriDVector b){
        TriDVector r  = new TriDVector(a.x*b.x,a.y*b.y,a.z*b.z);
        return r;
    }
}
