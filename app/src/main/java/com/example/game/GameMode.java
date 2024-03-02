package com.example.game;

public enum GameMode {

    BUTTON_EASY,
    BUTTON_HARD,
    SENSOR_EASY,
    SENSOR_HARD,
    UNDEFINED;

    public static GameMode fromInt(int val) {
        switch (val) {
            case 0:
                return BUTTON_EASY;
            case 1:
                return BUTTON_HARD;
            case 2:
                return SENSOR_EASY;
            case 3:
                return SENSOR_HARD;
        }
        return UNDEFINED;
    }


    // as this increases
    // the maximum delay is decreased
    // so the speed is increased
    public int maxSpeedDelayObstacleBound() {
        switch (this) {
            case BUTTON_EASY:
            case SENSOR_EASY:
                return 500;
            case BUTTON_HARD:
            case SENSOR_HARD:
                return 250;
        }
        return 0;
    }


    // as this increases
    // the maximum delay is increased
    // so the speed is decreased
    public int minSpeedDelayObstacleBound() {
        switch (this) {
            case BUTTON_EASY:
            case SENSOR_EASY:
                return 2500;
            case BUTTON_HARD:
            case SENSOR_HARD:
                return 1000;
        }
        return 0;
    }

    // as this increases
    // the delay between obstacle spawns increases
    public int obstacleSpawnDelay() {
        switch (this) {
            case BUTTON_EASY:
            case SENSOR_EASY:
                return 3000;
            case BUTTON_HARD:
            case SENSOR_HARD:
                return 1000;
        }
        return 0;
    }


    // as this increases
    // the player 'moves' faster
    public long timePerMeter() {
        switch (this) {
            case BUTTON_EASY:
            case SENSOR_EASY:
                return 1000;
            case BUTTON_HARD:
            case SENSOR_HARD:
                return 900;
        }
        return 0;
    }
}
