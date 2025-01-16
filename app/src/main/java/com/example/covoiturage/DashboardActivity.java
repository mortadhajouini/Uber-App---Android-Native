package com.example.covoiturage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button addTripButton, searchTripButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addTripButton = findViewById(R.id.add_trip_button);
        searchTripButton = findViewById(R.id.search_trip_button);

        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AddCovoiturageActivity.class);
                startActivity(intent);
            }
        });

        searchTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}
