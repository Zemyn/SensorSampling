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

    /**
     * @Parameter the rotate axis vectir v, and the rotate angle theta
     * @Description
     *         [cosθ+(1-cosθ)x^2    (1-cosθ)xy+(sinθ)z  (1-cosθ)xz+(sinθ)y  ]
     * M(v,θ)= [(1-cosθ)yx+(sinθ)z  cosθ+(1-cosθ)y^2    (1-cosθ)yz+(sinθ)x  ]
     *         [(1-cosθ)zx+(sinθ)y  (1-cosθ)zy+(sinθ)x  cosθ+(1-cosθ)z^2    ]
     */
    static public double[][] buildRotateMatrix(TriDVector v, int theta){
        double[][] M = new double[3][3];
        M[0][0] = Math.cos(theta)+(1-Math.cos(theta))*v.x*v.x;
        M[0][1] = (1-Math.cos(theta))*v.y*v.x+Math.sin(theta)*v.z;
        M[0][2] = (1-Math.cos(theta))*v.x*v.z+Math.cos(theta)*v.y;

        M[1][0] = (1-Math.cos(theta))*v.y*v.x+Math.sin(theta)*v.z;
        M[1][1] = Math.cos(theta)+(1-Math.cos(theta))*v.y*v.y;
        M[1][2] = (1-Math.cos(theta))*v.y*v.z+Math.sin(theta)*v.x;

        M[2][0] = (1-Math.cos(theta))*v.z*v.x+Math.sin(theta)*v.y;
        M[2][1] = (1-Math.cos(theta))*v.z*v.y+Math.sin(theta)*v.x;
        M[2][2] = Math.cos(theta)+(1-Math.cos(theta))*v.z*v.z;
        return M;
    }

    static public TriDVector getRotatedVector(double[][] m, TriDVector v){
        TriDVector r;
        double x = m[0][0]*v.x+m[0][1]*v.y+m[0][2]*v.z;
        double y = m[1][0]*v.x+m[1][1]*v.y+m[1][2]*v.z;
        double z = m[2][0]*v.x+m[2][1]*v.y+m[2][2]*v.z;
        return new TriDVector((float)x,(float)y,(float)z);
    }

    public void nomalized(){
        double magnitude = this.getMagnitude();
        this.x = (float)(this.x/magnitude);
        this.y = (float)(this.y/magnitude);
        this.z = (float)(this.z/magnitude);
    }

    public void multiply(double m){
        this.x = (float)(this.x * m);
        this.y = (float)(this.y * m);
        this.z = (float)(this.z * m);
    }
}
