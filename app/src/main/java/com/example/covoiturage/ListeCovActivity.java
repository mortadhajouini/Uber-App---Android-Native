package com.example.covoiturage;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListeCovActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> covoituragesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_cov);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter pour consulter votre liste.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listView = findViewById(R.id.listView);
        covoituragesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, covoituragesList);
        listView.setAdapter(adapter);

        DatabaseReference userPurchasedRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.getUid())
                .child("PurchasedCovoiturages");

        userPurchasedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                covoituragesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String departure = snapshot.child("departure").getValue(String.class);
                    String destination = snapshot.child("destination").getValue(String.class);

                    if (departure != null && destination != null) {
                        covoituragesList.add("Départ: " + departure + " -> Destination: " + destination);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListeCovActivity.this, "Erreur de chargement: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
