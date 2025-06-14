package com.s23010163.sandaruwan;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TemperatureActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvTemperature;
    private SensorManager sensorManager;
    private Sensor ambientTempSensor;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private final float TEMP_THRESHOLD = 63.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatue); // Match your layout name

        tvTemperature = findViewById(R.id.tvTemperature);

        // Initialize Sensor Manager and ambient temperature sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        }

        if (ambientTempSensor == null) {
            Toast.makeText(this, "Ambient Temperature Sensor not available", Toast.LENGTH_LONG).show();
        }

        // Load the warning audio from res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.warning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ambientTempSensor != null) {
            sensorManager.registerListener(this, ambientTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopAudio();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            tvTemperature.setText(String.format("%.1f°C", temperature));

            if (temperature > TEMP_THRESHOLD) {
                playAudio();
                Toast.makeText(this, "Warning! High Temperature!", Toast.LENGTH_SHORT).show();
            } else {
                stopAudio();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    private void playAudio() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isPlaying = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
