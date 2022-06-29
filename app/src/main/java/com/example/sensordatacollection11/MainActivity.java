package com.example.sensordatacollection11;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private boolean recording;
    private boolean isAccelerometerSensorPresent;
    private boolean isGyroscopeSensorPresent;
    private boolean isMagnetometerPresent;
    public long timestamp;
    public float[] accel = new float[3];
    public float[] gyro = new float[3];
    public float[] mag = new float[3];

    // spinners
    Spinner letterSpinner;
    Spinner subjectSpinner;

    // spinners
    String letter;
    String subject;

    // txt
    File file;
    BufferedWriter myBufferedWriter;
    FileWriter fileWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // accelerometer
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            isAccelerometerSensorPresent = true;
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            isAccelerometerSensorPresent = false;
        }

        //sensor gyroscope
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            isGyroscopeSensorPresent = true;
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else {
            isGyroscopeSensorPresent = false;
        }

        //magnetometer sensor
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            isMagnetometerPresent = true;
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else {
            isMagnetometerPresent = false;
        }

        // start button
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = true;
            }
        });

        // stop button
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = false;
            }
        });

        // getting instance of spinner
        letterSpinner = findViewById(R.id.letterSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);

        // apply OnItemSelectedListener
        letterSpinner.setOnItemSelectedListener(this);
        subjectSpinner.setOnItemSelectedListener(this);

        // bridge between values and interface
        letterSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.letter)));

        subjectSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.subject)));
    }

    //  perform action
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        if(parent.getId() == R.id.letterSpinner) {
            letter = letterSpinner.getItemAtPosition(i).toString();
        }
        else if(parent.getId() == R.id.subjectSpinner) {
            subject = subjectSpinner.getItemAtPosition(i).toString();
        }
    }

    // spinner's method
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // auto generated method
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAccelerometerSensorPresent) {
            sensorManager.registerListener(this, accelerometer, 10000);
        }
        if(isGyroscopeSensorPresent) {
            sensorManager.registerListener(this, gyroscope, 10000);
        }
        if(isMagnetometerPresent) {
            sensorManager.registerListener(this, magnetometer, 10000);
        }
    }

    // sensor values changes
    @Override
    public void onSensorChanged(SensorEvent event) {

        timestamp = event.timestamp; // get time in nanoseconds

        if(!recording) { // if start button is not clicked then return
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accel[0] = event.values[0];
            accel[1] = event.values[1];
            accel[2] = event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro[0] = event.values[0];
            gyro[1] = event.values[1];
            gyro[2] = event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mag[0] = event.values[0];
            mag[1] = event.values[1];
            mag[2] = event.values[2];
        }

        if(isAccelerometerSensorPresent && isGyroscopeSensorPresent && isMagnetometerPresent) {
            writeToFile("sensor.txt", timestamp + " " + letter + " " + subject
                    + " " + accel[0] + " " + accel[1] + " " + accel[2]
                    + " " + gyro[0] + " " + gyro[1] + " " + gyro[2]
                    + " " + mag[0] + " " + mag[1] + " " + mag[2] + "\n");
        }
    }

    public void writeToFile(String filename, String text) {

        file = new File(getFilesDir(), filename); // create file

        try {
            fileWriter = new FileWriter(file, true);
            myBufferedWriter = new BufferedWriter(fileWriter);
            myBufferedWriter.append(text);
            myBufferedWriter.flush();
            myBufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }
}