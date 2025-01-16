package com.example.covoiturage;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    private String selectedCovoiturageId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter pour continuer.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedCovoiturageId = "covoiturageId1";

        handlePaymentSuccess();
    }

    private void handlePaymentSuccess() {
        if (selectedCovoiturageId != null && currentUser != null) {
            DatabaseReference userPurchasedRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(currentUser.getUid())
                    .child("PurchasedCovoiturages")
                    .child(selectedCovoiturageId);

            DatabaseReference covoiturageRef = FirebaseDatabase.getInstance()
                    .getReference("Covoiturages")
                    .child(selectedCovoiturageId);

            covoiturageRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    userPurchasedRef.setValue(task.getResult().getValue()).addOnCompleteListener(moveTask -> {
                        if (moveTask.isSuccessful()) {
                            covoiturageRef.removeValue();
                            Toast.makeText(PaymentActivity.this, "Paiement réussi, covoiturage ajouté à votre liste !", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(PaymentActivity.this, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PaymentActivity.this, "Covoiturage introuvable.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
