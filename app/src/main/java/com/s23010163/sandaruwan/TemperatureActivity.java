package com.s23010163.sandaruwan;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TemperatureActivity extends AppCompatActivity implements SensorEventListener {

    private TextView tvTemperature, tvSeekValue;
    private SeekBar seekBarTemp;
    private float currentTemp = 24f;
    private final float TEMP_THRESHOLD = 63.0f;
    private MediaPlayer mediaPlayer;

    private SensorManager sensorManager;
    private Sensor tempSensor;
    private boolean isSensorAvailable = false;
    private boolean isManualOverride = false; // Track manual SeekBar change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatue);

        tvTemperature = findViewById(R.id.tvTemperature);
        tvSeekValue = findViewById(R.id.tvSeekValue);
        seekBarTemp = findViewById(R.id.seekBarTemp);

        mediaPlayer = MediaPlayer.create(this, R.raw.warning);

        updateTemperatureViews(currentTemp);
        seekBarTemp.setProgress((int) currentTemp);

        // Sensor setup
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isSensorAvailable = tempSensor != null;
        }

        // SeekBar listener
        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    isManualOverride = true;
                    currentTemp = progress;
                    updateTemperatureViews(currentTemp);
                    checkTemperatureWarning();
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable) {
            sensorManager.registerListener(this, tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isManualOverride) {
            currentTemp = event.values[0];
            updateTemperatureViews(currentTemp);
            seekBarTemp.setProgress((int) currentTemp);
            checkTemperatureWarning();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void updateTemperatureViews(float temperature) {
        String text = (int) temperature + "°C";
        tvTemperature.setText(text);
        tvSeekValue.setText(text);
    }

    private void checkTemperatureWarning() {
        if (currentTemp > TEMP_THRESHOLD) {
            playWarningSound();
            Toast.makeText(this, "Temperature too high!", Toast.LENGTH_SHORT).show();
        } else {
            stopWarningSound();
        }
    }

    private void playWarningSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopWarningSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }
}
