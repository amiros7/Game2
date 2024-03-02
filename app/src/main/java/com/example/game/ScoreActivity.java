package com.example.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity implements ScoreAdapter.ScoreClickListener {
    MapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        mapFragment = new MapFragment();
        ScoreFragment scoreListFragment = new ScoreFragment();


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapFragment, mapFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.scoreListFragment, scoreListFragment)
                .commit();
    }

    @Override
    public void onScoreClick(Score score) {
        mapFragment.zoom(score.getLatitude(), score.getLongitude());
    }
}
