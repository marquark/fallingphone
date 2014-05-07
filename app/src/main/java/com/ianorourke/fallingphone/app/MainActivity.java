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

    //TODO: Service? - https://github.com/commonsguy/cw-android/tree/master/Notifications/FakePlayer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        accelerometerReader = new AccelerometerReader(this);
        accelerometerReader.setInterface(this);

        mp = MediaPlayer.create(this, R.raw.scream2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void playSound(float accel) {
        if (!soundEnabled) return;

        if (accel > 0.1f) {
            if (!mp.isPlaying()) {
                mp.start();
            } else {
                if (mp.getCurrentPosition() >= 524) {
                    mp.seekTo(308);
                }
            }
        }
    }
}
