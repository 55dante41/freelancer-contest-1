package com.limelitelabs.simpletexttospeech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button goToMainActionButton;
    Context context;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.start_toolbar);
        goToMainActionButton = (Button) findViewById(R.id.start_go_to_main_action);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Simple TTS");

        goToMainActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
            }
        });

    }
}
