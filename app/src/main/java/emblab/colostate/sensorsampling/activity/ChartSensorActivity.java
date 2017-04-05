package emblab.colostate.sensorsampling.activity;

import
        android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import emblab.colostate.sensorsampling.R;
import emblab.colostate.sensorsampling.Utils.Algorithm;
import emblab.colostate.sensorsampling.Utils.TriDVector;

public class ChartSensorActivity extends AppCompatActivity implements SensorEventListener {
    private LineChart chart;
    private SensorManager sm;
    private Sensor accSensor;
    private Button btnStart, btnStop;
    private TextView txvGVector;
    private final int MAX_QUEUE_NUM = 256;
    private int signal_order = 2;

    private Queue<TriDVector> sigList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_sensor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sm.registerListener(ChartSensorActivity.this,accSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sm.unregisterListener(ChartSensorActivity.this);
            }
        });

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        chart = (LineChart) findViewById(R.id.chart);
        chartSetting();

        txvGVector = (TextView) findViewById(R.id.txv_g_vector);
    }

    private void chartSetting() {
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.LTGRAY);
        chart.setVisibleXRangeMaximum(120);
        iniChartData();
    }

    private void iniChartData(){
        LineData data = new LineData();

        List<Entry> Xvalue = new ArrayList();
        List<Entry> Yvalue = new ArrayList();
        List<Entry> Zvalue = new ArrayList();

        Xvalue.add(new Entry(0,0));
        //Xvalue.add(new Entry(1,1));
        //Xvalue.add(new Entry(2,1));
        LineDataSet Xset = new LineDataSet(Xvalue,"X value");
        Xset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Xset.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        Xset.setValueTextColor(ColorTemplate.getHoloBlue());
        Xset.setLineWidth(1.5f);
        Xset.setDrawCircles(false);
        Xset.setDrawValues(false);
        Xset.setFillAlpha(65);
        Xset.setFillColor(ColorTemplate.getHoloBlue());
        Xset.setHighLightColor(Color.rgb(244, 117, 117));
        Xset.setDrawCircleHole(false);

        Yvalue.add(new Entry(0,0));
        //Yvalue.add(new Entry(1,2));
        //Yvalue.add(new Entry(2,2));
        LineDataSet Yset = new LineDataSet(Yvalue,"Y value");
        Yset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Yset.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        Yset.setValueTextColor(ColorTemplate.getHoloBlue());
        Yset.setLineWidth(1.5f);
        Yset.setDrawCircles(false);
        Yset.setDrawValues(false);
        Yset.setFillAlpha(65);
        Yset.setFillColor(ColorTemplate.getHoloBlue());
        Yset.setHighLightColor(Color.rgb(244, 117, 117));
        Yset.setDrawCircleHole(false);

        Zvalue.add(new Entry(0,0));
        //Zvalue.add(new Entry(1,3));
        //Zvalue.add(new Entry(2,3));
        LineDataSet Zset = new LineDataSet(Zvalue,"Z value");
        Zset.setAxisDependency(YAxis.AxisDependency.LEFT);
        Zset.setColor(ColorTemplate.COLORFUL_COLORS[2]);
        Zset.setValueTextColor(ColorTemplate.getHoloBlue());
        Zset.setLineWidth(1.5f);
        Zset.setDrawCircles(false);
        Zset.setDrawValues(false);
        Zset.setFillAlpha(65);
        Zset.setFillColor(ColorTemplate.getHoloBlue());
        Zset.setHighLightColor(Color.rgb(0, 204, 0));
        Zset.setDrawCircleHole(false);

        data.addDataSet(Xset);
        data.addDataSet(Yset);
        data.addDataSet(Zset);

        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        chart.setData(data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];// get the current sensor data
        float y = event.values[1];
        float z = event.values[2];

        LineData currentData = chart.getData();

        LineDataSet x_set = (LineDataSet) currentData.getDataSetByIndex(0);
        LineDataSet y_set = (LineDataSet) currentData.getDataSetByIndex(1);
        LineDataSet z_set = (LineDataSet) currentData.getDataSetByIndex(2);

        if(signal_order >= MAX_QUEUE_NUM) {
            x_set.removeFirst();
            y_set.removeFirst();
            z_set.removeFirst();

            sigList.poll();
        }
        signal_order++;

        x_set.addEntry(new Entry(signal_order,x));
        y_set.addEntry(new Entry(signal_order,y));
        z_set.addEntry(new Entry(signal_order,z));

        //add to the window
        sigList.add(new TriDVector(x,y,z));
        TriDVector g_v = Algorithm.getGravityVector(sigList);
        //show the g vector
        txvGVector.setText("The g vector is: "+String.valueOf(g_v.x)+" "+String.valueOf(g_v.y)+" "+String.valueOf(g_v.z));


        currentData.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();

    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    protected void onResume() {
        super.onResume();

        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //sm.registerListener(this,accSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
}
