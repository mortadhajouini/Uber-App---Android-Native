package com.example.covoiturage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

        GeoPoint startPoint = new GeoPoint(36.8065, 10.1815);
        map.getController().setCenter(startPoint);

        databaseReference = FirebaseDatabase.getInstance().getReference("Covoiturages");

        loadCovoiturages();

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        Button addCovoiturageButton = findViewById(R.id.addCovoiturageButton);
        addCovoiturageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, AddCovoiturageActivity.class);
            startActivity(intent);
        });
    }

    private void loadCovoiturages() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String destination = snapshot.child("destination").getValue(String.class);
                    String departure = snapshot.child("departure").getValue(String.class);
                    String dateTime = snapshot.child("dateTime").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);

                    GeoPoint point = new GeoPoint(36.8 + Math.random() * 0.1, 10.1 + Math.random() * 0.1);

                    Marker marker = new Marker(map);
                    marker.setPosition(point);
                    marker.setTitle("Destination: " + destination + "\nDeparture: " + departure);
                    marker.setSubDescription("Date/Time: " + dateTime + "\nPrice: " + price + " TND");

                    marker.setOnMarkerClickListener((m, mapView) -> {
                        Intent intent = new Intent(MapActivity.this, CovDetailsActivity.class);
                        intent.putExtra("departure", departure);
                        intent.putExtra("destination", destination);
                        intent.putExtra("dateTime", dateTime);
                        intent.putExtra("price", price);
                        startActivity(intent);
                        return true;
                    });

                    Button purchasedButton = findViewById(R.id.purchasedButton);
                    purchasedButton.setOnClickListener(v -> {
                        Intent intent = new Intent(MapActivity.this, ListeCovActivity.class);
                        startActivity(intent);
                    });


                    map.getOverlays().add(marker);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "Failed to load covoiturages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}
