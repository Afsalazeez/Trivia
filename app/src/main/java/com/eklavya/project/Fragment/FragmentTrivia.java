package com.eklavya.project.Fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eklavya.project.Data.Trivia;
import com.eklavya.project.Data.TriviaList;
import com.eklavya.project.Global;
import com.eklavya.project.MainActivity;
import com.eklavya.project.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentTrivia extends Fragment implements View.OnClickListener{

    private View view;
    private TextView question, optionA, optionB, optionC, optionD, pass;
    private Global g;
    private Trivia trivia;

    private int position;

    public static FragmentTrivia newInstance(int position){
        FragmentTrivia frag = new FragmentTrivia();
        Bundle bundle = new Bundle();

        bundle.putInt("POSITION", position);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_trivia, container, false);

        g = Global.getInstance(getContext());

        position = getArguments().getInt("POSITION");
        trivia = g.currentList.get(position);

        init();
        setup();
        return view;
    }

    private void init(){
        g = Global.getInstance(getContext());
        question = view.findViewById(R.id.question);
        optionA = view.findViewById(R.id.option_A);
        optionB = view.findViewById(R.id.option_B);
        optionC = view.findViewById(R.id.option_C);
        optionD = view.findViewById(R.id.option_D);
        pass = view.findViewById(R.id.skip);

        optionA.setOnClickListener(this);
        optionB.setOnClickListener(this);
        optionC.setOnClickListener(this);
        optionD.setOnClickListener(this);

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) v;
                g.playSound(R.raw.correct);
                text.setBackgroundResource(R.drawable.rounded_correct);
                text.setTextColor(Color.BLACK);
                ((MainActivity) getContext()).nextQuestion();
            }
        });
    }


    private void setup(){
        List<String> list = new ArrayList<>();
        list.add(trivia.getCorrectAnswer());
        list.addAll(trivia.getIncorrectAnswers());
        Collections.shuffle(list);

        question.setText(Html.fromHtml(trivia.getQuestion()));
        optionA.setText(Html.fromHtml(list.get(0)));
        optionB.setText(Html.fromHtml(list.get(1)));
        optionC.setText(Html.fromHtml(list.get(2)));
        optionD.setText(Html.fromHtml(list.get(3)));
    }

    @Override
    public void onClick(View v) {
        if(!g.getIsGameOver()) {
            TextView text = (TextView) v;
            String option = text.getText().toString();
            MainActivity instance = ((MainActivity) getContext());
            if (option.contentEquals(Html.fromHtml(trivia.getCorrectAnswer()))) {
                g.playSound(R.raw.correct);
                text.setBackgroundResource(R.drawable.rounded_correct);
                text.setTextColor(Color.BLACK);
                instance.onAnswerClicked(true, trivia.getDifficulty());
            } else {
                g.playSound(R.raw.incorrect);
                text.setBackgroundResource(R.drawable.rounded_wrong);
                text.setTextColor(Color.WHITE);
                instance.onAnswerClicked(false, trivia.getDifficulty());
            }
        }
    }
}
