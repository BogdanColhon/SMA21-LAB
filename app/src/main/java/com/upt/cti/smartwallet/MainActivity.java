package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MONTH = "";
    private final static String PREFERENCES_SETTINGS = "prefs_settings";
    private SharedPreferences sharedPreferences;
    private TextView tStatus;
    private ListView listPayments;
    private DatabaseReference databaseReference;
    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_main);

        tStatus = findViewById(R.id.tStatus);
        listPayments = findViewById(R.id.listPayments);
        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);

        sharedPreferences =  getSharedPreferences(PREFERENCES_SETTINGS, Context.MODE_PRIVATE);
        currentMonth = sharedPreferences.getInt(TAG_MONTH, -1);
        if (currentMonth == -1)
            currentMonth = Month.monthFromTimestamp(AppState.getCurrentTimeDate());


        // setup firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-5eb90-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();


        AppState.get().setDatabaseReference(databaseReference);
        AppState.get().getDatabaseReference().child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    System.out.println(currentMonth+'\''+Month.monthFromTimestamp(snapshot.getKey()));
                    if (currentMonth == Month.monthFromTimestamp(snapshot.getKey()))
                    {

                        Payment payment = snapshot.getValue(Payment.class);
                        tStatus.setText("Found " + payments.size() + " payments for " +
                                Month.intToMonthName(currentMonth) + ".");
                    if (payment != null) {
                        payment.timestamp = snapshot.getKey();

                        if (!payments.contains(payment)) {
                            payments.add(payment);

                        }


                        adapter.notifyDataSetChanged();
                    }}

                } catch (Exception e) {
                    e.printStackTrace();
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

    public void clicked(View view){
        switch(view.getId()){
            case R.id.fabAdd:
                AppState.get().setDatabaseReference(databaseReference);
                AppState.get().setCurrentPayment(null);
                startActivity(new Intent(this, AddPaymentActivity.class));
                break;
            case R.id.bNext:
                currentMonth++;
                if(currentMonth == 12) currentMonth = 0;
                sharedPreferences.edit().putInt(TAG_MONTH, currentMonth).apply();
                System.out.println(Month.intToMonthName(currentMonth));
                recreate();
                break;
            case R.id.bPrevious:
                currentMonth--;
                if(currentMonth == -1) currentMonth = 11;
                sharedPreferences.edit().putInt(TAG_MONTH, currentMonth).apply();
                System.out.println(Month.intToMonthName(currentMonth));
                recreate();
                break;
        }
    }
    public enum Month {
        January, February, March, April, May, June, July, August,
        September, October, November, December;

        public static int monthNameToInt(Month month) {
            return month.ordinal();
        }

        public static Month intToMonthName(int index) {
            return Month.values()[index];
        }

        public static int monthFromTimestamp(String timestamp) {
            // 2016-11-02 14:15:16
            int month = Integer.parseInt(timestamp.substring(5, 7));
            return month - 1;
        }
    }
}