package com.eklavya.project;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklavya.project.Data.Trivia;
import com.eklavya.project.Data.TriviaList;
import com.eklavya.project.R;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar loader;
    private Global g;

    private LinearLayout buttonLayout;

    private TextView highscore;
    private TextView gk, films, celebrities, books, music, tv, art, video_games, board_games,
            computers, gadgets, mathematics, nature, animals, mythology, history,
            politics, geography, vehicles, sports, comics, anime, cartoons;

    private SharedPreferences prefs;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        prefs = getSharedPreferences(Config.PREFERENCES, MODE_PRIVATE);
        g = Global.getInstance(getApplicationContext());
        g.reset();

        getView();

    }


    private void getView(){
        loader = findViewById(R.id.loader);
        buttonLayout = findViewById(R.id.button_layout);

        highscore = findViewById(R.id.high_score);
        highscore.setTag("highscore");
        highscore.setOnClickListener(this);


        dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setContentView(R.layout.exit_window);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if(prefs.contains(Config.SCORE)){
            highscore.setVisibility(View.VISIBLE);
        }
        else{
            highscore.setVisibility(View.GONE);
        }

        gk = findViewById(R.id.b_general_knowledge);
        gk.setTag("gk");
        gk.setOnClickListener(this);

        films = findViewById(R.id.b_films);
        films.setTag("films");
        films.setOnClickListener(this);

        celebrities = findViewById(R.id.b_celebrities);
        celebrities.setTag("celebrities");
        celebrities.setOnClickListener(this);

        books = findViewById(R.id.b_books);
        books.setTag("books");
        books.setOnClickListener(this);

        music = findViewById(R.id.b_music);
        music.setTag("music");
        music.setOnClickListener(this);

        tv = findViewById(R.id.b_tv);
        tv.setTag("tv");
        tv.setOnClickListener(this);

        art = findViewById(R.id.b_art);
        art.setTag("art");
        art.setOnClickListener(this);

        video_games = findViewById(R.id.b_video_games);
        video_games.setTag("video_games");
        video_games.setOnClickListener(this);

        board_games = findViewById(R.id.b_board_games);
        board_games.setTag("board_games");
        board_games.setOnClickListener(this);

        computers = findViewById(R.id.b_computers);
        computers.setTag("computers");
        computers.setOnClickListener(this);

        gadgets = findViewById(R.id.b_gadgets);
        gadgets.setTag("gadgets");
        gadgets.setOnClickListener(this);

        mathematics = findViewById(R.id.b_mathematics);
        mathematics.setTag("mathematics");
        mathematics.setOnClickListener(this);

        mythology = findViewById(R.id.b_mythology);
        mythology.setTag("mythology");
        mythology.setOnClickListener(this);

        history = findViewById(R.id.b_history);
        history.setTag("history");
        history.setOnClickListener(this);

        politics = findViewById(R.id.b_politics);
        politics.setTag("politics");
        politics.setOnClickListener(this);

        geography = findViewById(R.id.b_geography);
        geography.setTag("geography");
        geography.setOnClickListener(this);

        nature = findViewById(R.id.b_nature);
        nature.setTag("nature");
        nature.setOnClickListener(this);

        animals = findViewById(R.id.b_animals);
        animals.setTag("animals");
        animals.setOnClickListener(this);

        vehicles = findViewById(R.id.b_vehicles);
        vehicles.setTag("vehicles");
        vehicles.setOnClickListener(this);

        sports = findViewById(R.id.b_sports);
        sports.setTag("sports");
        sports.setOnClickListener(this);

        comics = findViewById(R.id.b_comics);
        comics.setTag("comics");
        comics.setOnClickListener(this);

        anime = findViewById(R.id.b_anime);
        anime.setTag("anime");
        anime.setOnClickListener(this);

        cartoons = findViewById(R.id.b_cartoon);
        cartoons.setTag("cartoons");
        cartoons.setOnClickListener(this);
    }

    private void getCurrentList(final View view){
        final TextView textView = (TextView) view;
        loader.setVisibility(View.VISIBLE);

        String[] randos = {"easy","medium","hard"};
        int pos = new Random().nextInt(3);

        g.getApiService().getQuestion("5", g.chosenCategory,randos[pos],"multiple").enqueue(new Callback<TriviaList>() {
            @Override
            public void onResponse(Call<TriviaList> call, Response<TriviaList> response) {
                if(response.isSuccessful()){
                    TriviaList list = response.body();
                    List<Trivia> ls = list.getResults();

                    if(ls.isEmpty()){
                        getCurrentList(view);
                        /* Toast.makeText(StartActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                        textView.setBackgroundResource(R.drawable.background_more_rounded);
                        textView.setTextColor(Color.WHITE);
                        enableAll(true);*/
                    }
                    else {
                        g.currentList.clear();
                        g.currentList.addAll(ls);
                        g.loadNextList();
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(StartActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    textView.setBackgroundResource(R.drawable.background_more_rounded);
                    textView.setTextColor(Color.WHITE);
                    enableAll(true);
                }
                loader.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<TriviaList> call, Throwable t) {
                Toast.makeText(StartActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                textView.setBackgroundResource(R.drawable.background_more_rounded);
                textView.setTextColor(Color.WHITE);
                loader.setVisibility(View.INVISIBLE);
                enableAll(true);

            }
        });
    }

    @Override
    public void onClick(View v) {
        TextView textView = (TextView) v;
        String text = v.getTag().toString();

        g.playSound(R.raw.correct);

        boolean b_high_score_clicked = false;

        switch(text){
            case "highscore":
                showDialogWindow(v);
                b_high_score_clicked = true;
                textView.setTextColor(Color.WHITE);

                break;
            case "gk":
                g.chosenCategory = Config.GENERAL_KNOWLEDGE;
                break;
            case "films":
                g.chosenCategory = Config.ENTERTAINMENT_FILMS;
                break;
            case "celebrities":
                g.chosenCategory = Config.CELEBRITIES;
                break;
            case "books":
                g.chosenCategory = Config.ENTERTAINMENT_BOOKS;
                break;
            case "music":
                g.chosenCategory = Config.ENTERTAINMENT_MUSIC;
                break;
            case "tv":
                g.chosenCategory = Config.ENTERTAINMENT_TELEVISION;
                break;
            case "art":
                g.chosenCategory = Config.ART;
                break;
            case "video_games":
                g.chosenCategory = Config.ENTERTAINMENT_VIDEOGAMES;
                break;
            case "board_games":
                g.chosenCategory = Config.ENTERTAINMENT_BOARD_GAMES;
                break;
            case "computers":
                g.chosenCategory = Config.SCIENCE_COMPUTERS;
                break;
            case "gadgets":
                g.chosenCategory = Config.SCIENCE_GADGETS;
                break;
            case "mathematics":
                g.chosenCategory = Config.SCIENCE_MATHEMATICS;
                break;
            case "nature":
                g.chosenCategory = Config.SCIENCE_NATURE;
                break;
            case "animals":
                g.chosenCategory = Config.ANIMALS;
                break;
            case "mythology":
                g.chosenCategory = Config.MYTHOLOGY;
                break;
            case "history":
                g.chosenCategory = Config.HISTORY;
                break;
            case "politics":
                g.chosenCategory = Config.POLITICS;
                break;
            case "geography":
                g.chosenCategory = Config.GEOGRAPHY;
                break;
            case "vehicles":
                g.chosenCategory = Config.VEHICLES;
                break;
            case "sports":
                g.chosenCategory = Config.SPORTS;
                break;
            case "comics":
                g.chosenCategory = Config.ENTERTAINMENT_COMICS;
                break;
            case "anime":
                g.chosenCategory = Config.ENTERTAINMENT_ANIME_AND_MANGA;
                break;
            case "cartoons":
                g.chosenCategory = Config.ENTERTAINMENT_CARTOON_AND_ANIMATIONS;
                break;
        }

        if(!b_high_score_clicked) {
            textView.setBackgroundResource(R.drawable.more_rounded_correct);
            textView.setTextColor(Color.BLACK);

            if (g.isNetworkAvailable()) {
                enableAll(false);
                getCurrentList(v);
            } else {
                Toast.makeText(this, "Network Unvailable", Toast.LENGTH_SHORT).show();
                textView.setBackgroundResource(R.drawable.background_more_rounded);
                textView.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    private void enableAll(boolean val){
        for(int i=0;i<buttonLayout.getChildCount();i++){
            ViewGroup child = (ViewGroup) buttonLayout.getChildAt(i);
            for(int j=0;j<child.getChildCount();j++){
                child.getChildAt(j).setEnabled(val);
            }
        }
    }

    private void showDialogWindow(View v){

        final TextView textView = (TextView) v;

        // set the custom dialog components - text, image and button

        int marks = prefs.getInt(Config.SCORE, -1);
        int questionsCorrectlyAnswered = prefs.getInt(Config.ANSWERED, 0);
        int questionsEncountered = prefs.getInt(Config.ENCOUNTERED, 1);
        String wisdom = prefs.getString(Config.WISDOM, Config.DEFAULT_ERROR);
        String category = prefs.getString(Config.CATEGORY, Config.HIGH_SCORE);

        TextView title, score, fun_trivia, correct_number, total_questions;
        Button exit_button;

        title = dialog.findViewById(R.id.textView);
        title.setText(category);

        score = dialog.findViewById(R.id.exit_score);
        fun_trivia = dialog.findViewById(R.id.exit_trivia);
        exit_button = dialog.findViewById(R.id.exit_button);
        correct_number = dialog.findViewById(R.id.correct_number);
        total_questions = dialog.findViewById(R.id.total_questions);

        String correct_string = Integer.toString(questionsCorrectlyAnswered);
        String total_string = Integer.toString(questionsEncountered);
        String score_string = Integer.toString(marks);

        correct_number.setText(correct_string);
        total_questions.setText(total_string);
        score.setText(score_string);

        fun_trivia.setText(wisdom);


        // if button is clicked, close the custom dialog
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setTextColor(Color.parseColor("#00AF7F"));

                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
