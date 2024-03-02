package com.example.game;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class GameSync {

    public interface OnMain {
        void runOnMainThread(Runnable r);
    }

    public interface OnGameOver {
        void gameOver(int score);
    }

    public static final int LANES = 5;
    public static final int ROWS = 12;
    public static final int MAX_LIVES = 3;


    private GameObject[][] gameObjects;

    private GridLayout gameLayout;
    private LinearLayout livesLayout;
    private TextView odometerTv;

    private int[] obstacle_lanes;
    private int[] coin_lanes;

    private AtomicInteger odometer = new AtomicInteger(0);
    private AtomicIntegerArray active_obstacle_lanes;

    private int player_lane;

    private OnMain onMain;
    private OnGameOver onGameOver;
    private GameMode mode;

    private boolean isGameOver;
    private int lives;


    private Handler handler;

    private GameSounds gameSounds;

    class OdometerTask extends TimerTask {

        @Override
        public void run() {
            if (!isGameOver) {
                handler.postDelayed(this, mode.timePerMeter());
            } else {
                return;
            }
            odometer.incrementAndGet();
            odometerTv.setText(String.format(Locale.ENGLISH, "Distance: %d", odometer.get()));
        }
    }

    class ObstacleMovement extends TimerTask {
        private int milies;

        private final int movementLane;

        public ObstacleMovement(int movementLane, int milies) {
            this.movementLane = movementLane;
            this.milies = milies;
        }

        @Override
        public void run() {
            if (isGameOver) return;
            if (!moveObstacle(movementLane)) {
                active_obstacle_lanes.set(movementLane, 0);
            } else {
                handler.postDelayed(this, milies);
            }
        }
    }

    class ObstacleTask extends TimerTask {

        Random rand = new Random();


        @Override
        public void run() {
            if (!isGameOver) {
                handler.postDelayed(this, 2000);
            } else {
                return;
            }
            try {
                if (activeLanes() < LANES - 1) {
                    int movementLane = Math.min(LANES - 1, rand.nextInt(LANES));
                    if (active_obstacle_lanes.get(movementLane) == 1) {
                        return;
                    }
                    active_obstacle_lanes.set(movementLane, 1);
                    int movementPhase = Math.max(mode.maxSpeedDelayObstacleBound(),
                            rand.nextInt(mode.minSpeedDelayObstacleBound()));
                    ObstacleMovement movement = new ObstacleMovement(movementLane, movementPhase);
                    handler.postDelayed(movement, movementPhase);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static AtomicInteger numCoinTasks = new AtomicInteger(0);

    class CoinTask extends TimerTask {
        Random rand = new Random();

        class CoinMovement extends TimerTask {
            private int movementLane;
            private int randomMilies;


            public CoinMovement(int movementLane, int randomMilies) {
                this.movementLane = movementLane;
                this.randomMilies = randomMilies;
            }

            @Override
            public void run() {
                if (isGameOver) {
                    return;
                }
                if (!moveCoin(movementLane)) {
                    gameLayout.removeView(gameObjects[0][chosenIndex].getView());
                    gameObjects[0][chosenIndex] = whatEverOtherShitThatWasThereBefore;
                    numCoinTasks.decrementAndGet();
                } else {
                    handler.postDelayed(this, randomMilies);
                }
            }
        }

        public CoinTask(Context context) {
            coin = new Coin(context);
        }


        Coin coin;
        GameObject whatEverOtherShitThatWasThereBefore;

        int chosenIndex = 0;

        @Override
        public void run() {

            int random = rand.nextInt(100);
            if (!isGameOver) {
                handler.postDelayed(this, 4000);
            } else {
                return;
            }
            if (random < 50 && numCoinTasks.get() < 1) {
                numCoinTasks.incrementAndGet();

                for (int i = 0; i < LANES; i++) {
                    if (obstacle_lanes[i] > 1 && obstacle_lanes[1] < 9) {
                        if (gameObjects[0][i] instanceof Obstacle) {
                            continue;
                        }
                        chosenIndex = i;
                        whatEverOtherShitThatWasThereBefore = gameObjects[0][i];
                        gameObjects[0][i] = coin;
                        gameObjects[0][i].setPosition(1, i);
                        gameLayout.addView(coin.getView());
                        int randomMillisecond = Math.max(500, rand.nextInt(1000));
                        CoinMovement movement = new CoinMovement(i, randomMillisecond);

                        handler.postDelayed(movement, randomMillisecond);

                        break;
                    }
                }

            }
        }
    }


    public GameSync(MainActivity context, GameMode mode) {
        this.mode = mode;
        player_lane = LANES / 2;
        lives = 3;
        handler = new Handler();
        obstacle_lanes = new int[LANES];
        coin_lanes = new int[LANES];
        active_obstacle_lanes = new AtomicIntegerArray(LANES);
        livesLayout = context.findViewById(R.id.livesLayout);
        gameLayout = context.findViewById(R.id.gameLayout);
        odometerTv = context.findViewById(R.id.odometer);
        gameLayout.setRowCount(ROWS);
        gameLayout.setColumnCount(LANES);
        gameSounds = new GameSounds(context);
        onGameOver = context;
        gameObjects = new GameObject[ROWS][LANES];


        gameObjects[ROWS - 1][player_lane] = new Player(context);
        gameObjects[ROWS - 1][player_lane].setPosition(ROWS - 1, LANES / 2);
        for (int i = 0; i < LANES; i++) {
            gameObjects[0][i] = new Obstacle(context);
            gameObjects[0][i].setPosition(0, i);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < LANES; j++) {
                if (gameObjects[i][j] == null) {
                    gameObjects[i][j] = new GameObject(new ImageView(context));
                    gameObjects[i][j].setPosition(i, j);
                }
                gameLayout.addView(gameObjects[i][j].getView());
            }
        }
        onMain =  context;
    }

    private int activeLanes() {
        int active = 0;
        for (int i = 0; i < active_obstacle_lanes.length(); i++)
            active += active_obstacle_lanes.get(i);
        return active;
    }


    public void movePlayerLeft() {
        if (player_lane == 0) return;
        int row = ROWS - 1;
        GameObject o = gameObjects[row][player_lane - 1];
        if (o instanceof Obstacle) {
            resetObstacle(player_lane - 1);
            playerHit();
        } else if (o instanceof Coin) {
            consumeCoin(player_lane - 1);
            removeCoin(player_lane - 1);
        }
        swap(row, player_lane, row, player_lane - 1);
        player_lane--;
    }

    private void playerHit() {
        lives--;
        gameSounds.bang();
        renderLives();
        if (lives == 0) {
            cancelAllTasks();
            isGameOver = true;
            onGameOver.gameOver(odometer.get());
        }
    }

    public void movePlayerRight() {
        if (player_lane == LANES - 1) return;
        int row = ROWS - 1;
        GameObject o = gameObjects[row][player_lane + 1];
        if (o instanceof Obstacle) {
            resetObstacle(player_lane + 1);
            playerHit();
        } else if (o instanceof Coin) {
            consumeCoin(player_lane + 1);
            removeCoin(player_lane + 1);
        }
        swap(row, player_lane, row, player_lane + 1);
        player_lane++;
    }

    private void resetObstacle(int lane) {
        int row = obstacle_lanes[lane];
        obstacle_lanes[lane] = 0;
        GameObject o = gameObjects[0][lane];
        if (o instanceof Coin) {
            removeCoin(lane);
        }
        swap(row, lane, 0, lane);
    }

    private void removeCoin(int lane) {
        int row = coin_lanes[lane];
        gameLayout.removeView(gameObjects[row][lane].getView());
        gameObjects[row][lane] = new GameObject(new ImageView(gameLayout.getContext()));
        coin_lanes[lane] = 0;
    }


    private boolean moveCoin(int lane) {
        int row = coin_lanes[lane];
        if (row + 1 >= ROWS) {
            removeCoin(lane);
            return false; // moved up (reset)
        }
        GameObject o = gameObjects[row + 1][lane];
        if (o instanceof Obstacle) {
            removeCoin(lane);
            return false;
        }
        if (o instanceof Player) {
            consumeCoin(lane);
            removeCoin(lane);
            return false;
        }
        coin_lanes[lane]++;
        swap(row, lane, row + 1, lane);
        return true;
    }

    private void consumeCoin(int lane) {
        odometer.set(odometer.get() + 100);
        odometerTv.setText(String.format(Locale.ENGLISH, "Distance: %d", odometer.get()));
    }

    private boolean moveObstacle(int lane) { // returns true if moved down
        int row = obstacle_lanes[lane];

        if (row + 1 >= ROWS) {
            resetObstacle(lane);
            return false; // moved up (reset)
        }

        // check collision
        GameObject o = gameObjects[row + 1][lane];
        if (o instanceof Player) {
            resetObstacle(lane);
            playerHit();
            renderLives();

            return false;
        } else if (o instanceof Coin) {
            removeCoin(lane);
        }

        obstacle_lanes[lane]++;
        swap(row, lane, row + 1, lane);
        return true;
    }

    private void renderLives() {
        for (int i = 0; i < MAX_LIVES; i++) {
            livesLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        for (int i = lives - 1; i >= 0; i--) {
            livesLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }


    private void swap(int r1, int c1, int r2, int c2) {
        GameObject temp = gameObjects[r1][c1];
        gameObjects[r1][c1] = gameObjects[r2][c2];
        gameObjects[r2][c2] = temp;
        gameObjects[r1][c1].setPosition(r1, c1);
        gameObjects[r2][c2].setPosition(r2, c2);
    }


    public void startAllTasks() {
        isGameOver = false;
        handler.postDelayed(new CoinTask(gameLayout.getContext()), 4000);
        handler.postDelayed(new ObstacleTask(), 2000);
        handler.postDelayed(new OdometerTask(), mode.timePerMeter());
    }

    public void cancelAllTasks() {
        isGameOver = true;
    }


}
