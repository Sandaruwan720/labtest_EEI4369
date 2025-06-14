package com.s23010163.sandaruwan;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TemperatureActivity extends AppCompatActivity {

    private TextView tvTemperature;
    private TextView tvSeekValue;
    private SeekBar seekBarTemp;
    private float currentTemp = 24f; // default
    private MediaPlayer mediaPlayer;
    private final float TEMP_THRESHOLD = 63.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperatue);

        // Initialize UI components
        tvTemperature = findViewById(R.id.tvTemperature);
        tvSeekValue = findViewById(R.id.tvSeekValue);
        seekBarTemp = findViewById(R.id.seekBarTemp);

        // Set initial values
        tvTemperature.setText(currentTemp + "°C");
        tvSeekValue.setText(currentTemp + "°C");

        // Initialize media player
        mediaPlayer = MediaPlayer.create(this, R.raw.warning); // warning.mp3 must exist in res/raw

        // SeekBar change listener
        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTemp = progress;
                String tempText = currentTemp + "°C";
                tvTemperature.setText(tempText);
                tvSeekValue.setText(tempText);

                if (currentTemp > TEMP_THRESHOLD) {
                    playAlertSound();
                    Toast.makeText(TemperatureActivity.this, "Temperature too high!", Toast.LENGTH_SHORT).show();
                } else {
                    stopAlertSound(); // Stop immediately when temp is low
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playAlertSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopAlertSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0); // Reset sound
        }
    }
}
