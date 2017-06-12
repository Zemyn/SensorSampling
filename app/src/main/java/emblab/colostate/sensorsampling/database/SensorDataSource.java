package emblab.colostate.sensorsampling.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import emblab.colostate.sensorsampling.Utils.AccSignal;
import emblab.colostate.sensorsampling.model.AccResult;

/**
 * Created by Zemel on 4/5/17.
 */

public class SensorDataSource {
    private SQLiteDatabase database;
    private SensorDataHelper dbHelper;

    String[] projection = {
            AccEntry.COLUMN_ID,
            AccEntry.SENSOR_TYPE,
            AccEntry.COLUMN_TIME,
            AccEntry.COLUMN_X,
            AccEntry.COLUMN_Y,
            AccEntry.COLUMN_Z
    };

    public SensorDataSource(Context context){
        dbHelper = new SensorDataHelper(context);
    }

    /**
     * Open DB
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Create Table in DB
     *
     * @throws SQLException
     */
    public void createTable() throws SQLException {
        dbHelper.onCreate(database);
    }

    /**
     * Close DB
     */
    public void close() {
        dbHelper.close();
    }

    public void writeInfo(long time_stamp, float x, float y, float z){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccEntry.COLUMN_TIME,time_stamp);
        values.put(AccEntry.SENSOR_TYPE,"ACC");
        values.put(AccEntry.COLUMN_X,x);
        values.put(AccEntry.COLUMN_Y,y);
        values.put(AccEntry.COLUMN_Z,z);

        long newRowId = db.insert(dbHelper.TABLE_ACC,null,values);
    }

    public List getInfo(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection =  AccEntry.SENSOR_TYPE + " = ?";
        String[] selectionArgs = { "ACC" };
        String sortOrder = AccEntry.COLUMN_TIME + " DESC";

        Cursor cursor = db.query(
                dbHelper.TABLE_ACC,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<AccResult> item = new ArrayList<>();
        while(cursor.moveToNext()) {
            AccResult as = new AccResult();
            as.id = cursor.getLong(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_ID));
            as.type = cursor.getString(cursor.getColumnIndexOrThrow(AccEntry.SENSOR_TYPE));
            as.timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_TIME));
            as.x = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_X));
            as.y = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_Y));
            as.z = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_Z));

            item.add(as);
        }
        cursor.close();
        return item;
    }

}
