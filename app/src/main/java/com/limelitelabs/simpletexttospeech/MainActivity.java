package com.limelitelabs.simpletexttospeech;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView timerCountTextView;
    Toolbar toolbar;
    Context context;

    ImageButton helpActionButton, playActionButton, pauseActionButton, stopActionButton;
    Spinner localeSelector;
    Switch voiceGenderSwitch;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    CountDownTimer countDownTimer;
    int currentCountDownTimeInSecs = 15;
    TextToSpeech textToSpeechTranslator;
    boolean isTextToSpeechTranslatorInitiated = false;

    String translationMessage;

    HashMap<String, String> textToSpeechTranslatorParamsMap;
    Bundle textToSpeechTranslatorParamsBundle;

    String translationMessageFilepath;
    File translationMessageFile;

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translationMessageFilepath = getApplicationContext().getFilesDir().getPath() + "/media1.wav";
        translationMessageFile = new File(translationMessageFilepath);

        context = this;

        timerCountTextView = (TextView) findViewById(R.id.main_timer_count);
        helpActionButton = (ImageButton) findViewById(R.id.main_help_action);
        playActionButton = (ImageButton) findViewById(R.id.main_play_action);
        pauseActionButton = (ImageButton) findViewById(R.id.main_pause_action);
        stopActionButton = (ImageButton) findViewById(R.id.main_stop_action);
        localeSelector = (Spinner) findViewById(R.id.main_language_spinner);
        voiceGenderSwitch = (Switch) findViewById(R.id.main_gender_switch);

        toolbar = (Toolbar) findViewById(R.id.start_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple TTS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        alertDialogBuilder = new AlertDialog.Builder(context);

        translationMessage = getResources().getString(R.string.translation_message);

        textToSpeechTranslatorParamsMap = new HashMap<>();
        textToSpeechTranslatorParamsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "YES");

        textToSpeechTranslatorParamsBundle = new Bundle();
        textToSpeechTranslatorParamsBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "YES");

        textToSpeechTranslator = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                isTextToSpeechTranslatorInitiated = true;
                generateTranslationFile();
            }
        });

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
                pauseActionButton.setVisibility(View.VISIBLE);
                stopActionButton.setVisibility(View.VISIBLE);
                mediaPlayer.start();

                countDownTimer.cancel();
            }
        });
        pauseActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    continueCountDown();
                }
            }
        });
        stopActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    continueCountDown();
                }
            }
        });
        textToSpeechTranslator.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {

                File fileTTS = new File(translationMessageFilepath);

                if (fileTTS.exists()) {
                    Log.d("DEBUG", "successfully created fileTTS: " + fileTTS.length());
                    mediaPlayer = MediaPlayer.create(context, Uri.fromFile(fileTTS));
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        Log.d("DEBUG", "Error thrown");
                        e.printStackTrace();
                    }
                } else {
                    Log.d("DEBUG", "failed while creating fileTTS");
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        localeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("DEBUG", "" + position);
                int status;
                switch (position) {
                    case 0:
                        status = textToSpeechTranslator.setLanguage(Locale.US);
                        Log.d("DEBUG", "" + status);
                        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {

                            Intent checkIntent = new Intent();
                            checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivityForResult(checkIntent, 99);

                        } else {
                            generateTranslationFile();
                        }
                        break;
                    case 1:
                        status = textToSpeechTranslator.setLanguage(Locale.UK);
                        Log.d("DEBUG", "" + status);
                        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Intent checkIntent = new Intent();
                            checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivityForResult(checkIntent, 99);
                        } else {
                            generateTranslationFile();
                        }
                        break;
                    case 2:
                        status = textToSpeechTranslator.setLanguage(Locale.CHINESE);
                        Log.d("DEBUG", "" + status);
                        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Intent checkIntent = new Intent();
                            checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivityForResult(checkIntent, 99);
                        } else {
                            generateTranslationFile();
                        }
                        break;
                    case 3:
                        status = textToSpeechTranslator.setLanguage(Locale.FRENCH);
                        Log.d("DEBUG", "" + status);
                        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Intent checkIntent = new Intent();
                            checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivityForResult(checkIntent, 99);
                        } else {
                            generateTranslationFile();
                        }
                        break;
                    case 4:
                        status = textToSpeechTranslator.setLanguage(Locale.GERMAN);
                        Log.d("DEBUG", "" + status);
                        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Intent checkIntent = new Intent();
                            checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                            startActivityForResult(checkIntent, 99);
                        } else {
                            generateTranslationFile();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int status = textToSpeechTranslator.setLanguage(Locale.US);
                if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (textToSpeechTranslator.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE) {
                        Intent checkIntent = new Intent();
                        checkIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(checkIntent);
                    } else {
                        Toast.makeText(context, "Language not available.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    generateTranslationFile();
                }
            }
        });

        voiceGenderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                } else {
                    alertDialog = alertDialogBuilder.setMessage("This voice is not supported in the current engine. Do you want to download a supported engine?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String appPackageName = "com.ivona.tts";
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            voiceGenderSwitch.setChecked(true);
                        }
                    }).create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        textToSpeechTranslator.shutdown();
        mediaPlayer.release();
    }

    public void continueCountDown() {
        countDownTimer = getCountDownTimer();
        countDownTimer.start();
    }

    public CountDownTimer getCountDownTimer() {
        return new CountDownTimer(currentCountDownTimeInSecs * 1000, 1000) {
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

    public void generateTranslationFile() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int i = textToSpeechTranslator.synthesizeToFile(translationMessage, textToSpeechTranslatorParamsBundle, translationMessageFile, "");
            Log.d("DEBUG", "" + i);
        } else {
            int i = textToSpeechTranslator.synthesizeToFile(translationMessage, textToSpeechTranslatorParamsMap, translationMessageFilepath);
            Log.d("DEBUG", "" + i);
        }
    }
}
