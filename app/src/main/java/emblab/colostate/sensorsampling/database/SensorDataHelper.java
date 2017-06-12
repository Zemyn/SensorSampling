package emblab.colostate.sensorsampling.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Zemel on 4/5/17.
 */

public class SensorDataHelper extends SQLiteOpenHelper {

    public static String TABLE_ACC;

    private static final String TAG = "SensorDataHelper";
    private static final String DATABASE_NAME = "sensor_data.db";
    private static final int DATABASE_VERSION = 1;

    private static String DATABASE_CREATE;

    public SensorDataHelper(Context context){
        //super(context, Environment.getExternalStorageDirectory()+ File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // Get Table name
    public static String getTableAcc() {
        return AccEntry.TABLE_NAME;
    }

    // Get Database creation statement
    public static String getDATABASE_CREATE() {
        return DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + getTableAcc() + " ( "
                + AccEntry.COLUMN_ID + " integer primary key autoincrement, "
                + AccEntry.SENSOR_TYPE + " text, "
                + AccEntry.COLUMN_TIME + " real, "
                + AccEntry.COLUMN_X + " real, "
                + AccEntry.COLUMN_Y + " real, "
                + AccEntry.COLUMN_Z + " real " + ");";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate DB called");
        sqLiteDatabase.execSQL(getDATABASE_CREATE());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACC);
        onCreate(sqLiteDatabase);
    }
}
