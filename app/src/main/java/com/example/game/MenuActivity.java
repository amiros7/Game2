package com.example.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {


    private Button buttonsHardMode, buttonsEasyMode,
            sensorHardMode, sensorEasyMode,
            highestScoresBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonsEasyMode = findViewById(R.id.btnModeEasy);
        buttonsHardMode = findViewById(R.id.btnModeHard);


        sensorEasyMode = findViewById(R.id.btnModeSensorEasy);
        sensorHardMode = findViewById(R.id.btnModeSensorHard);

        highestScoresBtn = findViewById(R.id.highestScoresBtn);


        highestScoresBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ScoreActivity.class);
            startActivity(intent);
        });

        sensorEasyMode.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.MODE_ARG, GameMode.SENSOR_EASY.ordinal());
            openNameRequestAlert(intent);
        });

        sensorHardMode.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.MODE_ARG, GameMode.SENSOR_HARD.ordinal());
            openNameRequestAlert(intent);
        });


        buttonsEasyMode.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.MODE_ARG, GameMode.BUTTON_EASY.ordinal());
            openNameRequestAlert(intent);
        });

        buttonsHardMode.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.MODE_ARG, GameMode.BUTTON_HARD.ordinal());
            openNameRequestAlert(intent);
        });

    }

    public void openNameRequestAlert(Intent intent) {
        View nameLayout = LayoutInflater.from(this).inflate(R.layout.name_alert, null, false);

        EditText nameEt = nameLayout.findViewById(R.id.nameEt);
        new AlertDialog.Builder(this)
                .setView(nameLayout)
                .setTitle("GameAsync Invaders")
                .setPositiveButton("Start game", (dialogInterface, i) -> {
                    String name = nameEt.getText().toString();
                    if (name.trim().isEmpty()) {
                        Toast.makeText(MenuActivity.this, "You must enter a name..", Toast.LENGTH_LONG).show();
                    } else {
                        intent.putExtra("name", name);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
