package com.example.cardhubapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeController extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        // Inflar y agregar cinco veces
        for (int i = 0; i < 15; i++) {

            LayoutInflater inflater = LayoutInflater.from(this);
            LinearLayout inflatedLayout = (LinearLayout) inflater.inflate(R.layout.card_element, containerLayout, false);

            containerLayout.addView(inflatedLayout);

    }
    }

    @Override
    public void onClick(View view) {

    }
}

