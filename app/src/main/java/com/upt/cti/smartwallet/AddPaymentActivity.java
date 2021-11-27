package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upt.cti.smartwallet.model.Payment;
import com.upt.cti.smartwallet.ui.PaymentType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddPaymentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private EditText eName;
    private EditText eCost;
    private Spinner sType;
    private TextView tTimestamp;

    private Payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        setTitle("Add or edit payment");

        // ui
        eName = (EditText) findViewById(R.id.eName);
        eCost = (EditText) findViewById(R.id.eCost);
        sType = (Spinner) findViewById(R.id.sType);
        tTimestamp = (TextView) findViewById(R.id.tTimestamp);

        // spinner adapter
        String[] types = PaymentType.getTypes();
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(sAdapter);

        // initialize UI if editing
        payment = AppState.get().getCurrentPayment();
        if (payment != null) {
            eName.setText(payment.getName());
            eCost.setText(String.valueOf(payment.getCost()));
            tTimestamp.setText("Time of payment: " + payment.timestamp);
            try {
                sType.setSelection(Arrays.asList(types).indexOf(payment.getType()));
            } catch (Exception e) {
            }
        } else {
            tTimestamp.setText("");
        }
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSave:
                if (payment != null) {
                    System.out.println("aici jos!");
                    save(payment.timestamp);
                }else
                    save(AppState.getCurrentTimeDate());
                break;
            case R.id.bDelete:
                if (payment != null)
                    delete(payment.timestamp);
                else
                    Toast.makeText(this, "Payment does not exist", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void save(String timestamp) {

        Map<String, Object> map = new HashMap<>();
        map.put("cost", Double.parseDouble(eCost.getText().toString()));
        map.put("name", eName.getText().toString());
        map.put("type",sType.getSelectedItem().toString());

        System.out.println("timestamp: " + timestamp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-5eb90-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).updateChildren(map);
        finish();
    }

    private void delete(String timestamp) {
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).removeValue();
        finish();
    }
}