package emblab.colostate.sensorsampling.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import emblab.colostate.sensorsampling.R;
import emblab.colostate.sensorsampling.database.AccEntry;
import emblab.colostate.sensorsampling.database.SensorDataHelper;

public class DataCollectionActivity extends Activity implements SensorEventListener {
    private SensorDataHelper mDbHelper = new SensorDataHelper(this);
    Button btn_start_collect, btn_stop_collect;
    private Sensor mAccelerometer;

    private SensorManager sm;
    private TextView txv_show;
    private SQLiteDatabase SensorDb;

    //private String  sdcard = Environment.getExternalStorageDirectory().getPath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btn_start_collect = (Button) findViewById(R.id.btn_start_collection);
        btn_stop_collect = (Button) findViewById(R.id.btn_stop_collection);
        txv_show = (TextView) findViewById(R.id.txv_show_sensor_data);
        txv_show.setText(this.getFilesDir()+"");

        SensorDb = mDbHelper.getReadableDatabase();
        mDbHelper.onCreate(SensorDb);
    }

    public void startClick(View v) {
        //sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        ContentValues values = new ContentValues();
        values.put(AccEntry.SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER);
        values.put(AccEntry.COLUMN_TIME, 1f);
        values.put(AccEntry.COLUMN_X,1f);
        values.put(AccEntry.COLUMN_Y,2f);
        values.put(AccEntry.COLUMN_Z,3f);
        long newRowId = SensorDb.insert(AccEntry.TABLE_NAME, null, values);
        //Toast.makeText(DataCollectionActivity.this, "here is the material", Toast.LENGTH_LONG).show();
    }

    public void stopClick(View v){
        //sm.unregisterListener(this);
        //SensorDb.close();

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


        String sortOrder = AccEntry.COLUMN_ID + " DESC";//asc
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
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(AccEntry.COLUMN_ID));
            itemIds.add(itemId);
        }
        cursor.close();

        //Toast.makeText(DataCollectionActivity.this, ""+itemIds.get(0) , Toast.LENGTH_LONG).show();
        String showing = "";
        for(Object s : itemIds){
            showing += " "+ (long)s;
        }
        txv_show.setText(showing);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            //assign directions accelometer
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //txv_show.setText(x+" "+y+" "+z);
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
