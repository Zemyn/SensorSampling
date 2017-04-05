package emblab.colostate.sensorsampling.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.visualization.cartesianChart.CartesianChartGrid;
import com.telerik.widget.chart.visualization.cartesianChart.GridLineRenderMode;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.cartesianChart.axes.CategoricalAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.LineSeries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import emblab.colostate.sensorsampling.R;
import emblab.colostate.sensorsampling.Utils.SeismicDataPoint;

public class AccSensorActivity extends AppCompatActivity implements SensorEventListener {

    private static final int X_AXIS_INDEX = 0;
    private static final int Y_AXIS_INDEX = 1;
    private static final int Z_AXIS_INDEX = 2;

    private SensorManager sm;
    private Sensor mAccelerometer;
    private int framesCount = 0;

    private int coolDown;
    private int defaultCoolDown = 5;

    private ViewGroup chartContainer;
    private RadCartesianChartView chart;

    private Queue<SeismicDataPoint> seismicActivityBuffer;
    private List<SeismicDataPoint> allSeismicActivity;
    private int bufferSize = 100;

    boolean stopped = true;

    private int currentAxisIndex = X_AXIS_INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_sensor);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        chartContainer = (ViewGroup) findViewById(R.id.chart_container);

        this.seismicActivityBuffer = new LinkedList<>();
        this.allSeismicActivity = new ArrayList<>();

        // Adding points to fill the screen at initial state.
        for (int i = -this.bufferSize; i < 0; i++) {
            this.seismicActivityBuffer.add(new SeismicDataPoint(i, 0));
        }
        //Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show();

        this.coolDown = this.defaultCoolDown;

        Button startBtn = (Button) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                stopped = false;
            }
        });

        Button stopBtn = (Button) findViewById(R.id.stop_btn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        Button resetBtn = (Button) findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // The activity is forced to landscape, so X and Y are different in phone and tablet devices.
        final RadioGroup axisSelectionMenu = (RadioGroup) findViewById(R.id.axis_selection);
        axisSelectionMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.axis_x:
                        currentAxisIndex = X_AXIS_INDEX;
                        break;
                    case R.id.axis_y:
                        currentAxisIndex = Y_AXIS_INDEX;
                        break;
                    case R.id.axis_z:
                        currentAxisIndex = Z_AXIS_INDEX;
                        break;
                    default:
                        throw new IllegalArgumentException("there are only 3 axes");
                }
            }
        });


    }

    @Override
    protected void onResume() {

        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onResume();
        sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*analysis the Acc vector getting from the accelerometer*/
        if (this.stopped || this.coolDown-- > 0) {
            return;
        }

        this.coolDown = this.defaultCoolDown;

        if (this.seismicActivityBuffer.size() > this.bufferSize) {
            this.seismicActivityBuffer.remove();
        }

        SeismicDataPoint point = new SeismicDataPoint(this.framesCount++, event.values[this.currentAxisIndex]);

        // add the point to a collection of all the points that should be visible on the screen
        this.seismicActivityBuffer.add(point);
        this.allSeismicActivity.add(point);

        // draw the chart with all the points that should be visible*
        this.chartContainer.removeAllViews();
        this.chart = createChart(seismicActivityBuffer);
        this.chartContainer.addView(this.chart);
        //this.needle.updatePosition(point.y);
    }

    private RadCartesianChartView createChart(Iterable<SeismicDataPoint> dataPoints){
        RadCartesianChartView chart = new RadCartesianChartView(this);
        LinearAxis vAxis = new LinearAxis();

        vAxis.setMaximum(30);
        vAxis.setMinimum(-30);

        CategoricalAxis hAxis = new CategoricalAxis();
        hAxis.setShowLabels(false);

        DataPointBinding categoryBinding = new DataPointBinding() {
            @Override
            public Object getValue(Object o) throws IllegalArgumentException {
                return ((SeismicDataPoint) o).x;
            }
        };

        DataPointBinding yBinding = new DataPointBinding() {
            @Override
            public Object getValue(Object o) throws IllegalArgumentException {
                return ((SeismicDataPoint) o).y;
            }
        };

        DataPointBinding zBinding = new DataPointBinding() {
            @Override
            public Object getValue(Object o) throws IllegalArgumentException {
                return ((SeismicDataPoint) o).z;
            }
        };


        LineSeries series = new LineSeries();
        series.setCategoryBinding(categoryBinding);
        series.setValueBinding(yBinding);
        series.setValueBinding(zBinding);
        series.setData(dataPoints);

        CartesianChartGrid grid = new CartesianChartGrid();

        chart.setGrid(grid);
        chart.setVerticalAxis(vAxis);
        chart.setHorizontalAxis(hAxis);
        chart.getSeries().add(series);
        chart.setEmptyContent("");

        hAxis.setTickColor(Color.TRANSPARENT);
        grid.setMajorYLinesRenderMode(GridLineRenderMode.INNER_AND_LAST);
        /*grid.setMajorYLineDashArray(
                new float[]{
                        this.needle.typedValueToPixels(TypedValue.COMPLEX_UNIT_DIP, 10),
                        this.needle.typedValueToPixels(TypedValue.COMPLEX_UNIT_DIP, 4)
                }
        );*/
        vAxis.setLineColor(Color.TRANSPARENT);

        return chart;
    }

    private void stop(){
        this.stopped = true;
        this.chartContainer.removeAllViews();
        this.chart = createChart(this.allSeismicActivity);
        this.chartContainer.addView(this.chart);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}
