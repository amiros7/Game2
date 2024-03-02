package com.example.game;

import android.content.Context;
import android.widget.ImageView;

public class Coin extends GameObject {
    public Coin(Context context) {
        super(new ImageView(context));
        getView().setImageResource(R.drawable.coin);
    }
}
