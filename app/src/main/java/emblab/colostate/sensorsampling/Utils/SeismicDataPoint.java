package emblab.colostate.sensorsampling.Utils;

/**
 * Created by Zemel on 1/28/17.
 */

public class SeismicDataPoint {
    public int x;
    public float y;
    public float z;

    public SeismicDataPoint(int x, float y) {
        this.x = x;
        this.y = y;
        this.z = 1f;
    }
}
