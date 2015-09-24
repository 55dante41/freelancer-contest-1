package com.limelitelabs.simpletexttospeech;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView timerCountTextView;
    Toolbar toolbar;
    Context context;

    ImageButton helpActionButton, playActionButton, pauseActionButton, stopActionButton;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    CountDownTimer countDownTimer;
    int currentCountDownTimeInSecs = 15;
    TextToSpeech textToSpeechTranslator;
    boolean isTextToSpeechTranslatorInitiated = false;
    int pauseIndex = -1;

    String translationMessage;
    String[] translationMessageWords;

    HashMap<String, String> textToSpeechTranslatorParamsMap;
    Bundle textToSpeechTranslatorParamsBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        timerCountTextView = (TextView) findViewById(R.id.main_timer_count);
        helpActionButton = (ImageButton) findViewById(R.id.main_help_action);
        playActionButton = (ImageButton) findViewById(R.id.main_play_action);
        pauseActionButton = (ImageButton) findViewById(R.id.main_pause_action);
        stopActionButton = (ImageButton) findViewById(R.id.main_stop_action);
        toolbar = (Toolbar) findViewById(R.id.start_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple TTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialogBuilder = new AlertDialog.Builder(context);

        translationMessage = getResources().getString(R.string.translation_message);
        translationMessageWords = translationMessage.split(" ");

        textToSpeechTranslatorParamsMap = new HashMap<>();
        textToSpeechTranslatorParamsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "YES");

        textToSpeechTranslatorParamsBundle = new Bundle();
        textToSpeechTranslatorParamsBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "YES");

        textToSpeechTranslator = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isTextToSpeechTranslatorInitiated = true;
            }
        });

        if(textToSpeechTranslator.isLanguageAvailable(Locale.ENGLISH) == TextToSpeech.LANG_MISSING_DATA) {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, 99);
        }

        countDownTimer = getCountDownTimer();
        countDownTimer.start();

        helpActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                alertDialog = alertDialogBuilder.setTitle("Message")
                        .setMessage(translationMessage)
                        .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                alertDialog.show();
            }
        });

        playActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTextToSpeechTranslatorInitiated) {
                    if(!textToSpeechTranslator.isSpeaking()) {
                        pauseActionButton.setVisibility(View.VISIBLE);
                        stopActionButton.setVisibility(View.VISIBLE);
                        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeechTranslator.speak(translationMessage, TextToSpeech.QUEUE_FLUSH, textToSpeechTranslatorParamsBundle, "");
                        } else {
                            Log.d("DEBUG", "pauseIndex: " + pauseIndex);
                            int startIndex = 0;
                            if(pauseIndex > -1) {
                                startIndex = pauseIndex;
                            }
                            for(int i = startIndex; i < translationMessageWords.length; i++) {
                                textToSpeechTranslator.speak(translationMessageWords[i], TextToSpeech.QUEUE_ADD, textToSpeechTranslatorParamsMap);
                            }
                        }
                        countDownTimer.cancel();
                    }
                } else {
                    Toast.makeText(context,
                            "Please wait for the text-to-speech translator to start...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        pauseActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSpeechTranslator.isSpeaking()) {
                    textToSpeechTranslator.stop();
                    continueCountDown();
                }
            }
        });
        stopActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSpeechTranslator.isSpeaking()) {
                    textToSpeechTranslator.stop();
                    continueCountDown();
                }
                pauseIndex = -1;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        textToSpeechTranslator.shutdown();
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                textToSpeechTranslator = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        isTextToSpeechTranslatorInitiated = true;
                    }
                });
                textToSpeechTranslator.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        pauseIndex++;
                        if(pauseIndex >= translationMessageWords.length -1) {
                            Log.d("DEBUG", "called this..");
                            Log.d("DEBUG", currentCountDownTimeInSecs + "");
                            countDownTimer = getCountDownTimer();
                            countDownTimer.start();
                            pauseIndex = -1;
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });

            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void continueCountDown() {
        countDownTimer = getCountDownTimer();
        countDownTimer.start();
    }

    public CountDownTimer getCountDownTimer() {
        return new CountDownTimer(currentCountDownTimeInSecs*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                currentCountDownTimeInSecs = (int) (millisUntilFinished / 1000);
                timerCountTextView.setText("" + currentCountDownTimeInSecs);
            }

            public void onFinish() {
                startActivity(new Intent(context, StartActivity.class));
                finish();
            }
        };
    }
}
