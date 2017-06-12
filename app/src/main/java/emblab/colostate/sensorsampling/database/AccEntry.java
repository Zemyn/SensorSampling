package emblab.colostate.sensorsampling.database;

import android.provider.BaseColumns;

/**
 * Created by Zemel on 6/6/17.
 */

public class AccEntry {
    private AccEntry(){}

    public static final String TABLE_NAME = "ACC_TABLE";
    public static final String SENSOR_TYPE = "SENSOR_TYPE";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "timestamp";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";
}
