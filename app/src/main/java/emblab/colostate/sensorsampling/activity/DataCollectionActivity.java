package emblab.colostate.sensorsampling.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import emblab.colostate.sensorsampling.R;
import emblab.colostate.sensorsampling.database.AccEntry;
import emblab.colostate.sensorsampling.database.SensorDataHelper;

public class DataCollectionActivity extends Activity implements SensorEventListener {
    private SensorDataHelper mDbHelper = new SensorDataHelper(this);
    Button btn_start_collect, btn_stop_collect,btn_show_data,btn_drop_table;
    private Sensor mAccelerometer,mGravity,mMagnetic,mLinear,mGyro;

    private SensorManager sm;
    private TextView txv_show;
    private SQLiteDatabase SensorDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGravity = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mMagnetic = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLinear = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        btn_start_collect = (Button) findViewById(R.id.btn_start_collection);
        btn_stop_collect = (Button) findViewById(R.id.btn_stop_collection);
        btn_show_data = (Button) findViewById(R.id.btn_show_data);
        btn_drop_table = (Button) findViewById(R.id.btn_drop_table);

        txv_show = (TextView) findViewById(R.id.txv_show_sensor_data);
        txv_show.setText(Environment.getExternalStorageDirectory().getAbsolutePath()+"");

        SensorDb = mDbHelper.getReadableDatabase();
        mDbHelper.onCreate(SensorDb);
    }

    public void startClick(View v) {
        sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,mLinear,SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this,mGyro,SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(DataCollectionActivity.this, "Start sensoring", Toast.LENGTH_LONG).show();
    }

    public void stopClick(View v){
        sm.unregisterListener(this);
        Toast.makeText(DataCollectionActivity.this, "Stop sensoring" , Toast.LENGTH_LONG).show();
    }

    public void showDataClick(View v){
         String[] projection = {
                AccEntry.COLUMN_ID,
                AccEntry.SENSOR_TYPE,
                AccEntry.COLUMN_TIME,
                AccEntry.COLUMN_X,
                AccEntry.COLUMN_Y,
                AccEntry.COLUMN_Z
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = AccEntry.SENSOR_TYPE + " = ?";
        String[] selectionArgs = { "1" };

        String sortOrder = AccEntry.COLUMN_ID + " ASC";//asc,desc
        Cursor cursor = SensorDb.query(
                AccEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );


        List itemIds = new ArrayList<>();

        String showing = "";
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(AccEntry.SENSOR_TYPE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_TIME));
            float x = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_X));
            float y = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_Y));
            float z = cursor.getFloat(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_Z));
            showing += itemId+" "+type+" "+time+" "+x+" "+y+" "+z+"\n";
            itemIds.add(itemId);
        }
        cursor.close();

        //Toast.makeText(DataCollectionActivity.this, ""+itemIds.get(0) , Toast.LENGTH_LONG).show();
        txv_show.setText("The data: \n"+showing);
    }

    public void dropTable(View v){
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AccEntry.TABLE_NAME;
        SensorDb.execSQL(SQL_DELETE_ENTRIES);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long time_stamp = event.timestamp;
        int sensorType = event.sensor.getType();

        if(sensorType == Sensor.TYPE_ACCELEROMETER||
                sensorType == Sensor.TYPE_GRAVITY||
                sensorType == Sensor.TYPE_GYROSCOPE||
                sensorType == Sensor.TYPE_LINEAR_ACCELERATION||
                sensorType == Sensor.TYPE_MAGNETIC_FIELD||
                sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            saveData(time_stamp,sensorType,event);
        }
    }

    private void saveData(long time_stamp, int sensor_type, SensorEvent event){
        float x ,y ,z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        ContentValues values = new ContentValues();
        values.put(AccEntry.SENSOR_TYPE, sensor_type);
        values.put(AccEntry.COLUMN_TIME, time_stamp);
        values.put(AccEntry.COLUMN_X, x);
        values.put(AccEntry.COLUMN_Y, y);
        values.put(AccEntry.COLUMN_Z, z);
        long newRowId = SensorDb.insert(AccEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    protected void onStop() {
        super.onStop();
        SensorDb.close();
    }
}
