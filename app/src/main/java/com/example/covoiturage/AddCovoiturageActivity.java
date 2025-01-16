package com.example.covoiturage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;

public class AddCovoiturageActivity extends AppCompatActivity {

    private EditText dateTimeInput, priceInput;
    private AutoCompleteTextView destinationInput, departureInput;
    private Button continueButton;
    private DatabaseReference databaseReference;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_covoiturage);

        databaseReference = FirebaseDatabase.getInstance().getReference("Covoiturages");

        destinationInput = findViewById(R.id.destinationInput);
        departureInput = findViewById(R.id.departureInput);
        dateTimeInput = findViewById(R.id.dateTimeInput);
        priceInput = findViewById(R.id.priceInput);
        continueButton = findViewById(R.id.continueButton);

        client = new OkHttpClient();

        continueButton.setOnClickListener(view -> {
            String destination = destinationInput.getText().toString().trim();
            String departure = departureInput.getText().toString().trim();
            String dateTime = dateTimeInput.getText().toString().trim();
            String price = priceInput.getText().toString().trim();

            if (destination.isEmpty() || departure.isEmpty() || dateTime.isEmpty() || price.isEmpty()) {
                Toast.makeText(AddCovoiturageActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = databaseReference.push().getKey();

            Covoiturage covoiturage = new Covoiturage(id, destination, departure, dateTime, price);

            if (id != null) {
                databaseReference.child(id).setValue(covoiturage)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(AddCovoiturageActivity.this, "Covoiturage ajouté avec succès!", Toast.LENGTH_SHORT).show();

                            // Start the MapActivity after adding the covoiturage
                            Intent intent = new Intent(AddCovoiturageActivity.this, MapActivity.class);
                            intent.putExtra("covoiturageId", id);
                            intent.putExtra("destination", destination);
                            intent.putExtra("departure", departure);
                            intent.putExtra("dateTime", dateTime);
                            intent.putExtra("price", price);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddCovoiturageActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        destinationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 2) {
                    fetchPlaceSuggestions(charSequence.toString(), destinationInput);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        departureInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 2) {
                    fetchPlaceSuggestions(charSequence.toString(), departureInput);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchPlaceSuggestions(String query, AutoCompleteTextView inputField) {
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddCovoiturageActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        ArrayList<String> placeNames = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject place = jsonArray.getJSONObject(i);
                            placeNames.add(place.getString("display_name")); // Get the name of the place
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCovoiturageActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, placeNames);
                            inputField.setAdapter(adapter);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(AddCovoiturageActivity.this, "Erreur de traitement des suggestions", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }
}
