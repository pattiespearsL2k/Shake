package com.lttn.shake11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    private SensorManager mSensorManager;
    private float shake; //Acceleration value differ from gravity
    private float mAccelCurrent;// Current acceleration value and gravity
    private float mAccelLast;// Last acceleration value and gravity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);// truy cap vao cam bien gia toc
        shake = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        //onSensorChanged() được gọi khi dữ liệu cảm biến thay đổi
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            shake = shake * 0.9f + delta;
            if(shake > 6) {
                Log.i("Send SMS", "");

                try{
                   // Toast.makeText(getApplicationContext(),GPS.address, Toast.LENGTH_LONG).show();

                    Intent intent = getIntent();
                    String data = intent.getStringExtra("key");
                    Log.d("key", data);
                    SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage("18121668944", null, "Please help me. I need help immediately. This is where i am now:"+data, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

                    }


                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    }
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    @Override
    //Đăng kí Sensor listener trong hàm onResume()
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    // Hủy đăng kí trong hàm onPause() để tránh việc sử dụng không cần thiết và tiêt kiệm pin cho thiết bị
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }


}
