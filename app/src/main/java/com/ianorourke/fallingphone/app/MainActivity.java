package com.ianorourke.fallingphone.app;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity implements AccelerometerReader.AccelerometerReaderInterface {
    static AccelerometerReader accelerometerReader;
    static MediaPlayer mp;

    boolean soundEnabled = false;
    boolean sensorEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        accelerometerReader = new AccelerometerReader(this);
        accelerometerReader.setInterface(this);

        mp = MediaPlayer.create(this, R.raw.scream2);
    }

    public float maxAcceleration = 0.0f;

    public void receivedValues(float x, float y, float z) {
        TextView xV = (TextView) findViewById(R.id.xaccel);
        TextView yV = (TextView) findViewById(R.id.yaccel);
        TextView zV = (TextView) findViewById(R.id.zaccel);

        xV.setText(String.format("X: %.2f", x));
        yV.setText(String.format("Y: %.2f", y));
        zV.setText(String.format("Z: %.2f", z));

        float totalAcceleration = (float) Math.sqrt(x * x + y * y + z * z);

        if (totalAcceleration > maxAcceleration) maxAcceleration = totalAcceleration;

        TextView total = (TextView) findViewById(R.id.totalaccel);
        TextView max = (TextView) findViewById(R.id.maxaccel);

        total.setText(String.format("Total: %.2f", totalAcceleration));
        max.setText(String.format("Max: %.2f", maxAcceleration));

        playSound(totalAcceleration);
    }

    public void onResetClick(View v) {
        maxAcceleration = 0.0f;
        receivedValues(0.0f, 0.0f, 0.0f);
    }

    public void enabledBoxClicked(View v) {
        CheckBox sensorEnabled = (CheckBox) findViewById(R.id.sensor_box);
        CheckBox soundEnabled = (CheckBox) findViewById(R.id.sound_box);

        this.sensorEnabled = sensorEnabled.isChecked();

        accelerometerReader.setEnabled(this.sensorEnabled);
        soundEnabled.setEnabled(this.sensorEnabled);

        if (!soundEnabled.isEnabled()) soundEnabled.setChecked(false);

        this.soundEnabled = soundEnabled.isChecked();
    }

    final int MIN_TRACK = 308;
    final int MAX_TRACK = 524;
    final float MAX_ACCEL = 6.0f;

    public void playSound(float accel) {
        if (!soundEnabled) return;

        if (accel > MAX_ACCEL) {
            if (!mp.isPlaying()) {
                mp.start();
            } else {
                if (mp.getCurrentPosition() >= MAX_TRACK) {
                    mp.seekTo(MIN_TRACK);
                }
            }
        }
    }
}
