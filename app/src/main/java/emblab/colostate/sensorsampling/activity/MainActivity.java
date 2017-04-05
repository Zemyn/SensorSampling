package emblab.colostate.sensorsampling.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import emblab.colostate.sensorsampling.R;

public class MainActivity extends AppCompatActivity {
    Button btnToAcc, btnToStep ,btnToGoogleMap,btnToStepDec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnToAcc = (Button) findViewById(R.id.btn_to_acc);
        btnToAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AccSensorActivity.class);
                startActivity(intent);
            }
        });

        btnToStep = (Button) findViewById(R.id.btn_to_vector);
        btnToStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_v = new Intent();
                intent_v.setClass(MainActivity.this, StepActivity.class);
                startActivity(intent_v);
            }
        });

        btnToGoogleMap = (Button) findViewById(R.id.btn_to_magnet);
        btnToGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MagneticActivity.class);
                startActivity(intent);
            }
        });

        btnToStepDec = (Button) findViewById(R.id.btn_to_step_detect);
        btnToStepDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StepDetectActivity.class);
                startActivity(intent);
            }
        });
    }
}
