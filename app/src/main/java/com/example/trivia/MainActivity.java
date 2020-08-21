package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.util.prefs;

import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCouterTextview;
    private TextView questionScore;
    private TextView highestScoreText;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton restartButton;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int highestScore = 0;
    private prefs pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new prefs(MainActivity.this);

        highestScore = pref.getHighestScore();

        nextButton = findViewById(R.id.next_button);
        previousButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questionCouterTextview = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);
        questionScore = findViewById(R.id.score_text);
        restartButton = findViewById(R.id.replay_button);
        highestScoreText = findViewById(R.id.highscore_text);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        restartButton.setOnClickListener(this);

        questionList = new QuestionBank().getQuestion(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                currentQuestionIndex = pref.getState();

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCouterTextview.setText(MessageFormat.format("{0} out of {1}",
                        currentQuestionIndex + 1, questionList.size()));
                questionScore.setText(MessageFormat.format("Score: {0}", score));
//                Log.d("hmm", "processFinished: "+ questionArrayList);
                highestScoreText.setText(MessageFormat.format("Highest Score: {0}", highestScore));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_button:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1);
                    updateNextQuestion();
                } else {
                    Toast.makeText(MainActivity.this, "You are on the first question", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.next_button:
                    if (currentQuestionIndex < (questionList.size() - 1)) {
                        currentQuestionIndex = (currentQuestionIndex + 1);
                        updateNextQuestion();
                    } else {
                        Toast.makeText(MainActivity.this, "You are on the last question", Toast.LENGTH_SHORT).show();
                    }
                break;
            case R.id.true_button:
               checkAnswer(true);
               updateNextQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateNextQuestion();
                break;
            case R.id.replay_button:
                currentQuestionIndex = 0;
                score = 0;
                replayFadeAnim();
                updateNextQuestion();
        }
    }

    private void checkAnswer(boolean userChoice) {
        boolean correctAns = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (correctAns == userChoice) {
            score = score + 10;
            FadeCorrectAnimation();
            Toast.makeText(MainActivity.this, "+10", Toast.LENGTH_SHORT).show();
            ;
        } else {
            ShakeWrongAns();
            if (score > 2) score = score - 3;
            else score = 0;

            if (score > 0) {
                Toast.makeText(MainActivity.this, "-3", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(MainActivity.this, "(-3)\n(No scores to deduct)", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNextQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCouterTextview.setText(MessageFormat.format("{0} out of {1}",
                currentQuestionIndex + 1, questionList.size()));
        questionScore.setText(MessageFormat.format("Score: {0}", score));
    }

    private void ShakeWrongAns() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_it);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                next();
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void FadeCorrectAnimation() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation corrAn = new AlphaAnimation(1.0f, 0.0f);

        corrAn.setDuration(200);
        corrAn.setRepeatCount(1);
        corrAn.setRepeatMode(Animation.REVERSE);


        cardView.setAnimation(corrAn);
        corrAn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                next();
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void replayFadeAnim() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation corrAn = new AlphaAnimation(1.0f, 0.0f);

        corrAn.setDuration(400);
        corrAn.setRepeatCount(1);
        corrAn.setRepeatMode(Animation.REVERSE);


        cardView.setAnimation(corrAn);
    }

    private void next() {
        if (currentQuestionIndex < (questionList.size() - 1)) {
            currentQuestionIndex = (currentQuestionIndex + 1);
            updateNextQuestion();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pref.putHighestScore(score);
        pref.saveState(currentQuestionIndex);
    }

}