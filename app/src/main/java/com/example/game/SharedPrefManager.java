package com.example.game;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SharedPrefManager {

    private SharedPreferences sp;

    private final Gson g = new Gson();

    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences("com.example.game", Context.MODE_PRIVATE);
    }


    public boolean addScore(Score score) {
        List<Score> allScores = getScores();
        List<Score> playerScores = allScores
                .stream()
                .filter(s -> s.getPlayerName().equals(score.getPlayerName()))
                .collect(Collectors.toList());
        if (playerScores.isEmpty()) {
            allScores.add(score);
            saveScores(allScores);
            return true;
        } else {
            Score lastScore = playerScores.get(0);
            if (lastScore.getScore() < score.getScore()) {
                lastScore.setScore(score.getScore());
                lastScore.setLatitude(score.getLatitude());
                lastScore.setLongitude(score.getLongitude());
                saveScores(allScores);
                return true;
            }
        }
        return false;
    }

    private void saveScores(List<Score> allScores) {

        Set<String> scores_strings = allScores.stream()
                .map(g::toJson)
                .collect(Collectors.toSet());

        sp.edit().putStringSet("scores", scores_strings)
                .apply();

    }

    public List<Score> getScores() {
        Set<String> scores_strings = sp.getStringSet("scores", new HashSet<>());
        List<Score> scores = new ArrayList<>();
        for (String scoreJson : scores_strings) {
            scores.add(g.fromJson(scoreJson, Score.class));
        }
        Collections.sort(scores);
        return scores;
    }
}
