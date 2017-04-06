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

public class AccDataHelper extends SQLiteOpenHelper {

    public static String TABLE_ACC;

    private static final String TAG = "AccDataHelper";
    private static final String DATABASE_NAME = "acc_signal.db";
    private static final int DATABASE_VERSION = 1;


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";

    private static String DATABASE_CREATE;

    public AccDataHelper(Context context){
        super(context, Environment.getExternalStorageDirectory()+ File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
    }

    public AccDataHelper(Context context, String tableName){
        super(context, Environment.getExternalStorageDirectory()+ File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
        setTableAcc(tableName);
    }

    // Set Table name
    public static void setTableAcc(String tableName) {
        TABLE_ACC = tableName;
    }

    // Get Table name
    public static String getTableAcc() {
        return TABLE_ACC;
    }

    // Get Database creation statement
    public static String getDATABASE_CREATE() {
        return DATABASE_CREATE = "create table " + getTableAcc() + " ( "
                + COLUMN_ID + " integer primary key autoincrement , "
                + COLUMN_X + " real, "
                + COLUMN_Y + " real, "
                + COLUMN_Z + " real, "
                + COLUMN_ID + "real" + ");";
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
