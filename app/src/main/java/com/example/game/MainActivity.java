package com.example.game;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity
        implements GameSync.OnMain, SensorEventListener, GameSync.OnGameOver {

    public static final String MODE_ARG = "mode";

    private GameSync game;

    private Sensor accelometer;

    GameMode mode;

    private SimpleLocation simpleLocation;


    String playerName;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefManager = new SharedPrefManager(this);
        simpleLocation = new SimpleLocation(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            simpleLocation.beginUpdates();
        }
        playerName = getIntent().getStringExtra("name");

        mode = GameMode.fromInt(
                getIntent().getIntExtra(MODE_ARG, GameMode.BUTTON_EASY.ordinal()));

        FloatingActionButton moveLeftBtn = findViewById(R.id.leftArrow);
        FloatingActionButton moveRightBtn = findViewById(R.id.rightArrow);
        game = new GameSync(this, mode);


        moveLeftBtn.setOnClickListener((v) -> {
            game.movePlayerLeft();
        });
        moveRightBtn.setOnClickListener((v) -> {
            game.movePlayerRight();
        });


        game.startAllTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            simpleLocation.beginUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mode == GameMode.SENSOR_EASY || mode == GameMode.SENSOR_HARD) {
            SensorManager sensorManager = getSystemService(SensorManager.class);
            accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.cancelAllTasks();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mode == GameMode.SENSOR_EASY || mode == GameMode.SENSOR_HARD) {
            SensorManager sensorManager = getSystemService(SensorManager.class);
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void runOnMainThread(Runnable r) {
        runOnUiThread(r);
    }


    private float lastX;
    private long lastSensorMovement;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float xMovement = sensorEvent.values[0]; // Movement in x
        long timeNow = System.currentTimeMillis();
        if (Math.abs(xMovement - lastX) > 0.5f) {
            if (timeNow - lastSensorMovement > 200) {
                if (xMovement - lastX < 0.5f) { // left ?
                    game.movePlayerLeft();
                } else { // right
                    game.movePlayerRight();
                }
                lastSensorMovement = timeNow;
                lastX = xMovement;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Unimplemented
    }

    @Override
    public void gameOver(int endScore) {
        Score score = new Score(playerName, endScore, simpleLocation.getLongitude(), simpleLocation.getLatitude());
        if (sharedPrefManager.addScore(score)) {
            Toast.makeText(this, "GameAsync over! New high score!: " + endScore, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "GameAsync over! score: " + endScore, Toast.LENGTH_LONG).show();
        }
        finish();
    }
}