package com.aneeqshah.whatstheweather;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    TextView weatherDataText1;

    public void back(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        weatherDataText1 = findViewById(R.id.textView2);
        Intent intent = getIntent();
        weatherDataText1.setText(intent.getStringExtra("info"));
    }
}
