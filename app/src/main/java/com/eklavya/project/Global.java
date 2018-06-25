package com.eklavya.project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eklavya.project.Data.Trivia;
import com.eklavya.project.Data.TriviaList;
import com.eklavya.project.Retrofit.ApiService;
import com.eklavya.project.Retrofit.ApiUtils;
import com.eklavya.project.Retrofit.NumberService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Global extends Activity {

    private static Global instance;

    private static ApiService mAPIService;
    private static NumberService mNumberService;

    private Context mContext;

    private MediaPlayer mp;
    private String funNumberTrivia;

    public List<Trivia> currentList;
    public List<Trivia> nextList;

    public int questionsCorrectlyAnswered = 0;
    public int questionsEncountered = 1;

    private int score = 0;

    public String chosenCategory;

    private boolean isGameOver = false;

    private ProgressBar progressBar;

    private Global(Context context){
        mContext = context;
        mAPIService = ApiUtils.getAPIService();
        mNumberService = ApiUtils.getNumberService();
        currentList = new ArrayList<>();
        nextList = new ArrayList<>();
    }

    public static synchronized Global getInstance(Context context){
        if(instance == null)
            instance = new Global(context);
        return instance;
    }

    public ApiService getApiService(){
        return mAPIService;
    }

    public NumberService getNumberService(){
        return mNumberService;
    }

    public String getFunNumberTrivia() {
        return funNumberTrivia;
    }

    public void correctAnswer(int marks){
         score += marks;
    }

    public int getScore(){
        return score;
    }

    public boolean getIsGameOver(){
        return isGameOver;
    }

    public void reset(){
        score = 0;
        questionsEncountered = 1;
        questionsCorrectlyAnswered = 0;
        isGameOver = false;
    }

    public boolean isNetworkAvailable()
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null)
            return false;
        else
            return info.isConnected();
    }

    public void loadNextList(){
        if(isNetworkAvailable()) {

            String[] list = {"easy", "medium", "hard"};
            int pos = new Random().nextInt(3);

            getApiService().getQuestion("5", chosenCategory, list[pos], "multiple").enqueue(new Callback<TriviaList>() {
                @Override
                public void onResponse(Call<TriviaList> call, Response<TriviaList> response) {
                    TriviaList list = response.body();
                    if(!list.getResults().isEmpty()) {
                        nextList.clear();
                        nextList.addAll(list.getResults());
                    }
                    else{
                        loadNextList();
                    }
                }

                @Override
                public void onFailure(Call<TriviaList> call, Throwable t) {
                    nextList.clear();
                }
            });
        }
        else{
            nextList.clear();
            Toast.makeText(mContext, "Network Unvailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFunNumberTrivia(final Context context){
        if(isNetworkAvailable()){

            String scoreString = Integer.toString(score);

            String[] randos = {"math","trivia","date","year"};
            int pos = new Random().nextInt(4);

            getNumberService().getStringResponse("/"+scoreString+"/"+randos[pos]).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()){
                        funNumberTrivia = response.body();
                        showDialogGameOver(context, true, null);
                    }
                    else{
                        showDialogGameOver(context, false, Config.UNSUCCESSFUL_ERROR);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showDialogGameOver(context, false, Config.SERVER_ERROR);
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
        }
    }

    public void gameOver(final Context context){
        isGameOver = true;

        progressBar = ((MainActivity) context).progressBar;
        progressBar.setIndeterminate(true);

        if(isNetworkAvailable()) {
            loadFunNumberTrivia(context);
        }
        else{
            showDialogGameOver(context, false, Config.NETWORK_ERROR);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    private void showDialogGameOver(final Context context, final boolean triviaLoadedSuccessfully, final String errorIfAny){
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog);
        dialog.setContentView(R.layout.exit_window);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setTitle("Game Over!");

        // set the custom dialog components - text, image and button

        TextView score, fun_trivia, correct_number, total_questions;
        Button exit_button;

        score = dialog.findViewById(R.id.exit_score);
        fun_trivia = dialog.findViewById(R.id.exit_trivia);
        exit_button = dialog.findViewById(R.id.exit_button);
        correct_number = dialog.findViewById(R.id.correct_number);
        total_questions = dialog.findViewById(R.id.total_questions);

        String correct_string = Integer.toString(questionsCorrectlyAnswered);
        String total_string = Integer.toString(questionsEncountered);
        String score_string = Integer.toString(getScore());

        correct_number.setText(correct_string);
        total_questions.setText(total_string);
        score.setText(score_string);

        if(triviaLoadedSuccessfully) fun_trivia.setText(getFunNumberTrivia());
        else {
            if(errorIfAny == null) fun_trivia.setText(Config.DEFAULT_ERROR);
            else fun_trivia.setText(errorIfAny);
        }

        // if button is clicked, close the custom dialog
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs  = context.getSharedPreferences(Config.PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                int scr = prefs.getInt(Config.SCORE, -1);

                if(getScore()>scr){
                    editor.putInt(Config.SCORE, getScore());
                    editor.putInt(Config.ANSWERED, questionsCorrectlyAnswered);
                    editor.putInt(Config.ENCOUNTERED, questionsEncountered);
                    if(triviaLoadedSuccessfully) editor.putString(Config.WISDOM, getFunNumberTrivia());
                    else {
                        if(errorIfAny == null) editor.putString(Config.WISDOM, Config.DEFAULT_ERROR);
                        else editor.putString(Config.WISDOM, errorIfAny);
                    }

                    String category = getCategoryName();
                    editor.putString(Config.CATEGORY, category);

                    editor.apply();


                }


                ((MainActivity)context).onBackPressed();
            }
        });

        dialog.show();
    }

    public void playSound(int rid){
        if(mp!=null){
            mp.reset();
            mp.release();
        }
        mp = MediaPlayer.create(mContext, rid);
        mp.start();
    }

    private String getCategoryName(){
        String category = "";
        switch(chosenCategory){
            case Config.GENERAL_KNOWLEDGE:
                category = "General Knowledge";
                break;
            case Config.ENTERTAINMENT_BOOKS:
                category = "Books";
                break;
            case Config.ENTERTAINMENT_FILMS:
                category = "Films";
                break;
            case Config.ENTERTAINMENT_MUSIC:
                category = "Music";
                break;
            case Config.ENTERTAINMENT_TELEVISION:
                category = "Television";
                break;
            case Config.ENTERTAINMENT_VIDEOGAMES:
                category = "Video Games";
                break;
            case Config.ENTERTAINMENT_BOARD_GAMES:
                category = "Board Games";
                break;
            case Config.SCIENCE_NATURE:
                category = "Nature";
                break;
            case Config.SCIENCE_COMPUTERS:
                category = "Computers";
                break;
            case Config.SCIENCE_MATHEMATICS:
                category = "Mathematics";
                break;
            case Config.MYTHOLOGY:
                category = "Mythology";
                break;
            case Config.SPORTS:
                category = "Sports";
                break;
            case Config.GEOGRAPHY:
                category = "Geography";
                break;
            case Config.HISTORY:
                category = "History";
                break;
            case Config.POLITICS:
                category = "Politics";
                break;
            case Config.ART:
                category = "Art";
                break;
            case Config.CELEBRITIES:
                category = "Celebrities";
                break;
            case Config.ANIMALS:
                category = "Animals";
                break;
            case Config.VEHICLES:
                category = "Vehicles";
                break;
            case Config.ENTERTAINMENT_COMICS:
                category = "Comics";
                break;
            case Config.SCIENCE_GADGETS:
                category = "Gadgets";
                break;
            case Config.ENTERTAINMENT_ANIME_AND_MANGA:
                category = "Anime and Manga";
                break;
            case Config.ENTERTAINMENT_CARTOON_AND_ANIMATIONS:
                category = "Cartoons";
                break;
        }
        return category;
    }
}
