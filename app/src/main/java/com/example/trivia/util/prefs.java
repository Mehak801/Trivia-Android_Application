package com.example.trivia.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class prefs {
    private SharedPreferences preferences;

    public prefs(Activity activity){
        this.preferences = activity.getPreferences(activity.MODE_PRIVATE);
    }

    public void putHighestScore(int score){
        int currentScore = score;
        int lastScore = preferences.getInt("high_score",0);

        if(currentScore>lastScore){
//            new highest
            preferences.edit().putInt("high_score",currentScore).apply();
        }
    }

    public int getHighestScore(){
        return preferences.getInt("high_score",0);
    }

    public void saveState(int idx){
        preferences.edit().putInt("idx_state",idx).apply();
    }

    public int getState(){
        return preferences.getInt("idx_state",0);
    }

}
