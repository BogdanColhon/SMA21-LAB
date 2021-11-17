package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentAdapter;

import java.util.ArrayList;
import java.util.List;

public class SecondMainActivity extends AppCompatActivity {


    private TextView tStatus;
    private Button bPrevious,bNext;
    private FloatingActionButton fabAdd;
    private ListView listPayments;
    private DatabaseReference databaseReference;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        tStatus = findViewById(R.id.tStatus);
        bPrevious =  findViewById(R.id.bPrevious);
        bNext =  findViewById(R.id.bNext);
        fabAdd = findViewById(R.id.fabAdd);
        listPayments = findViewById(R.id.listPayments);
        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);

        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-5eb90-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        databaseReference.child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Payment newPayment = snapshot.getValue(Payment.class);
                    if (newPayment != null) {
                        newPayment.timestamp = snapshot.getKey();
                        if (!payments.contains(newPayment))
                        {
                            payments.add(newPayment);
                        }
                        adapter.notifyDataSetChanged();
                    }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}