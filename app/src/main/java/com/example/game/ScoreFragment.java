package com.example.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreFragment extends Fragment {

    private SharedPrefManager sharedPrefManager;

    private RecyclerView scoresRv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.score_list_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPrefManager = new SharedPrefManager(getContext());
        scoresRv = view.findViewById(R.id.scoresRv);
        scoresRv.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Score> scores = sharedPrefManager.getScores();
        ScoreAdapter adapter = new ScoreAdapter(scores, (ScoreActivity) getActivity());
        scoresRv.setAdapter(adapter);
    }
}
