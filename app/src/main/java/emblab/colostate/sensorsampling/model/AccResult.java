package emblab.colostate.sensorsampling.model;


/**
 * Created by Zemel on 4/5/17.
 */

public class AccResult {
    protected static final String TABLE_NAME = "acc_results";

    /**
     * @uml.property name="id"
     */
    protected int id;

    // Location
    protected float x;
    protected float y;
    protected float z;

    public AccResult(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }


}
