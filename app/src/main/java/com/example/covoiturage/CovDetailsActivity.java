package com.example.covoiturage;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CovDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cov_details);

        String departure = getIntent().getStringExtra("departure");
        String destination = getIntent().getStringExtra("destination");
        String dateTime = getIntent().getStringExtra("dateTime");
        String price = getIntent().getStringExtra("price");

        TextView textViewDeparture = findViewById(R.id.textViewDeparture);
        TextView textViewDestination = findViewById(R.id.textViewDestination);
        TextView textViewDateTime = findViewById(R.id.textViewDateTime);
        TextView textViewPrice = findViewById(R.id.textViewPrice);

        textViewDeparture.setText( departure);
        textViewDestination.setText(destination);
        textViewDateTime.setText(dateTime);
        textViewPrice.setText(price + " TND");

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> finish());

        Button buttonPay = findViewById(R.id.buttonPay);
        buttonPay.setOnClickListener(v -> {
            Intent intent = new Intent(CovDetailsActivity.this, PaymentActivity.class);
            intent.putExtra("price", price);
            startActivity(intent);
        });
    }
}
