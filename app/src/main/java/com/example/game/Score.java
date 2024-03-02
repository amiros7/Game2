package com.example.game;

public class Score implements Comparable<Score> {


    private String playerName;
    private int score;
    private double latitude;
    private double longitude;


    private long date;

    public Score(String playerName, int score, double latitude, double longitude) {
        this.playerName = playerName;
        this.score = score;
        this.latitude = latitude;
        this.longitude = longitude;
        date = System.currentTimeMillis();
    }

    public Score() {
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int compareTo(Score score) {
        return Integer.compare(score.getScore(), getScore());
    }
}
