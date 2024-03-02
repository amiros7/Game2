package com.example.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {



    public interface ScoreClickListener {
        void onScoreClick(Score score);
    }
    private List<Score> scoreList;
    private ScoreClickListener scoreClickListener;

    public ScoreAdapter(List<Score> scoreList, ScoreClickListener scoreClickListener) {
        this.scoreList = scoreList;
        this.scoreClickListener = scoreClickListener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_item,parent,false);
        return new ScoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Score score = scoreList.get(position);
        holder.populate(score);
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv, scoreTv, dateTv, locationTv;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.playerNameTv);
            scoreTv = itemView.findViewById(R.id.playerScoreTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            locationTv = itemView.findViewById(R.id.locationTv);
        }

        public void populate(Score score) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(score.getDate());

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            String date = day + "/" + month + "/" + year;


            nameTv.setText("Name: " + score.getPlayerName());
            scoreTv.setText("Score: " + score.getScore());
            dateTv.setText("Date: " + date);
            locationTv.setText("Longitude: " + score.getLongitude() + ", Latitude: " + score.getLatitude());
            itemView.setOnClickListener(v -> scoreClickListener.onScoreClick(score));
        }
    }
}
