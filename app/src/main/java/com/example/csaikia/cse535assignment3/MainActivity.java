package com.example.csaikia.cse535assignment3;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OnClickListener {
    static int ACCE_FILTER_DATA_MIN_TIME = 100;
    boolean started = false;
    Button walkButton;
    Button runButton;
    Button jumpButton;
    String columns = "";
    Sensor accelerometer;
    SQLiteDatabase db;
    String table_name;
    long lastSaved = System.currentTimeMillis();
    String query_vals = "";
    String query_cols = "";
    String activity = "";
    int j;
    RunnableDemo runnableDemo = new RunnableDemo();
    double arr_x[] = new double[50];
    double arr_y[] = new double[50];
    double arr_z[] = new double[50];
    double min_x, min_y, min_z, max_x, max_y, max_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final File file = new File(Environment.getExternalStorageDirectory()+ File.separator+"Android/Data/CSE535_ASSIGNMENT3");
        if (!file.exists()) {
            file.mkdirs();
        }

        String[] axes = new String[] {"x","y","z"};
        for (int i=1;i<=50;i++) {
            for (String s: axes) {
                columns = columns + "Accel_"+s+"_"+i+" double, ";
            }
        }

        columns = columns + "Activity_Label varchar);";
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        for (int i=1;i<=50;i++) {
            for (String s: axes) {
                query_cols = query_cols + "Accel_"+s+"_"+i+",";
            }
        }
        query_cols = query_cols + "Activity_label";

        setContentView(R.layout.activity_main);
        try {
            db = SQLiteDatabase.openDatabase(file.toString()+"/group2", null, SQLiteDatabase.CREATE_IF_NECESSARY);
            db.beginTransaction();
            table_name = "assignment_3";
            try {
                db.execSQL("create table if not exists " + table_name + " (" + " ID integer PRIMARY KEY autoincrement, " + columns);
                db.setTransactionSuccessful();
            } catch (SQLiteException e) {
                Toast.makeText(MainActivity.this, "Unable to connect to database", Toast.LENGTH_LONG).show();
            } finally {
                db.endTransaction();
            }
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, "Unable to create the database", Toast.LENGTH_LONG).show();
        }


        // Buttons initialize
        runButton = (Button) findViewById(R.id.run);
        runButton.setOnClickListener(this);
        walkButton = (Button) findViewById(R.id.walk);
        walkButton.setOnClickListener(this);
        jumpButton = (Button) findViewById(R.id.jump);
        jumpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run: {
                activity = "Running";
                j = 0;
                min_x = Double.MAX_VALUE;
                min_y = Double.MAX_VALUE;
                min_z = Double.MAX_VALUE;
                max_x = Double.MIN_VALUE;
                max_y = Double.MIN_VALUE;
                max_z = Double.MIN_VALUE;
                started = true;
                Toast.makeText(MainActivity.this, "Start running", Toast.LENGTH_LONG).show();
                Log.d("chaynika","running");
                Thread thread = new Thread(runnableDemo);
                thread.start();
                break;
            }
            case R.id.jump: {
                activity = "Jumping";
                j = 0;
                min_x = Double.MAX_VALUE;
                min_y = Double.MAX_VALUE;
                min_z = Double.MAX_VALUE;
                max_x = Double.MIN_VALUE;
                max_y = Double.MIN_VALUE;
                max_z = Double.MIN_VALUE;
                started = true;
                Toast.makeText(MainActivity.this, "Start jumping", Toast.LENGTH_LONG).show();
                Log.d("chaynika","jumping");
                Thread thread = new Thread(runnableDemo);
                thread.start();
                break;
            }
            case R.id.walk: {
                activity = "Walking";
                j = 0;
                min_x = Double.MAX_VALUE;
                min_y = Double.MAX_VALUE;
                min_z = Double.MAX_VALUE;
                max_x = Double.MIN_VALUE;
                max_y = Double.MIN_VALUE;
                max_z = Double.MIN_VALUE;
                started = true;
                Toast.makeText(MainActivity.this, "Start walking", Toast.LENGTH_LONG).show();
                Log.d("chaynika","walking");
                Thread thread = new Thread(runnableDemo);
                thread.start();
                break;
            }
        }
    }

    public void populate_database(String activity, int k) {
        Log.d("chaynika","j is " + k);
        while (k<50) {
            arr_x[k] = min_x + Math.random()*(max_x-min_x);
            arr_y[k] = min_y + Math.random()*(max_y-min_y);
            arr_z[k] = min_z + Math.random()*(max_z-min_z);
            k++;
        }
        query_vals = "";
        for (int i=0;i<50;i++) {
            query_vals = query_vals + arr_x[i] + "," + arr_y[i] + "," + arr_z[i] + ",";
        }
        query_vals = query_vals + "'" + activity + "'";
        db.execSQL("INSERT INTO " + table_name + "(" + query_cols + ")" + " values (" + query_vals + ");");
        //db.close();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if ((System.currentTimeMillis() - lastSaved) >= ACCE_FILTER_DATA_MIN_TIME) {
                //System.out.println(System.currentTimeMillis());
                lastSaved = System.currentTimeMillis();
                arr_x[j]= event.values[0];
                arr_y[j] = event.values[1];
                arr_z[j] = event.values[2];
                min_x = Math.min(min_x,arr_x[j]);
                min_y = Math.min(min_y,arr_y[j]);
                min_z = Math.min(min_z,arr_z[j]);
                max_x = Math.max(max_x,arr_x[j]);
                max_y = Math.max(max_y,arr_y[j]);
                max_z = Math.max(max_z,arr_z[j]);
                j++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class RunnableDemo implements Runnable {
        //public Thread thread;

        public void run() {
            try {
                Thread.sleep(5000);
                populate_database(activity, j);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            started = false;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

}
