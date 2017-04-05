package emblab.colostate.sensorsampling.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by Zemel on 4/5/17.
 */

public class AccDataHelper extends SQLiteOpenHelper {

    public static String TABLE_WIFI;

    private static final String TAG = "AccDataHelper";
    private static final String DATABASE_NAME = "acc_signal.db";
    private static final int DATABASE_VERSION = 1;

    public AccDataHelper(Context context, String tableName){
        super(context, Environment.getExternalStorageDirectory()+ File.separator+ DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Set Table name
    public static void setTableWifi(String tableName) {
        TABLE_WIFI = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
