package com.eklavya.project;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklavya.project.Fragment.FragmentTrivia;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private int mPosInCurrentList = 0;
    Global g;

    private TextView score;
    ProgressBar progressBar;
    private CountDownTimer stopWatch;

    int maxTime = Config.MAX_TIME_LIMIT;
    int countDownInterval = 5;
    int remainingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadNextFragment();
    }

    private void init(){
        g = Global.getInstance(this);
        score = (TextView) findViewById(R.id.score);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        score.setText("0");

        progressBar.setMax(maxTime);
        progressBar.setProgress(maxTime);

        additionalTime(0);
    }

    public void onAnswerClicked(boolean isCorrect, String difficulty){

        stopWatch.cancel();

        int marks = 0;
        float penalty = 0;

        switch(difficulty){
            case "easy":
                marks = 5;
                penalty = 3f;
                break;
            case "medium":
                marks = 8;
                penalty = 5f;
                break;
            case "hard":
                marks = 12;
                penalty = 7.0f;
                break;
        }


        if(isCorrect) {
           g.questionsCorrectlyAnswered++;
           additionalTime(marks*1000);
           g.correctAnswer(marks);
           String scoreString = Integer.toString(g.getScore());
           score.setText(scoreString);

        }
        else{
            additionalTime((int)(-penalty*1000));
        }

        nextQuestion();
    }

    public void nextQuestion(){

        mPosInCurrentList++;
        g.questionsEncountered++;

        if(mPosInCurrentList>=g.currentList.size()){
            if(g.nextList.size()>0){
                g.currentList.clear();
                g.currentList.addAll(g.nextList);

                g.loadNextList();

                mPosInCurrentList = 0;
                loadNextFragment();
            }
            else{
                g.gameOver(this);
            }
        }
        else{
            loadNextFragment();
        }
    }

    private void loadNextFragment(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                FragmentTrivia fragment = FragmentTrivia.newInstance(mPosInCurrentList);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout,fragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
            }
        },100);

    }

    private void additionalTime(int time){
        remainingProgress = progressBar.getProgress();

        int amount = remainingProgress + time;

        if(amount<=0){
            progressBar.setProgress(0);
            g.gameOver(MainActivity.this);
            //basically game over
        }

        if(amount>=maxTime){
            stopWatch = new CountDownTimer(maxTime, countDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int progress = Math.round((millisUntilFinished));
                    progressBar.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    g.gameOver(MainActivity.this);
                }
            }.start();
        }

        else {
            stopWatch = new CountDownTimer(amount, countDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int progress = Math.round((millisUntilFinished));
                    progressBar.setProgress(progress);
                }

                @Override
                public void onFinish() {
                    g.gameOver(MainActivity.this);
                }
            }.start();
        }
    }

    @Override
    public void onBackPressed(){
        stopWatch.cancel();
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
