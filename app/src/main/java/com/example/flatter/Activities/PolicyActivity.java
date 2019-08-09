package com.example.flatter.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.flatter.R;

public class PolicyActivity extends AppCompatActivity {
    private TextView Heading;
    private TextView Body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        Heading = (TextView) findViewById(R.id.heading);
        Body = (TextView) findViewById(R.id.body);


    }
}
