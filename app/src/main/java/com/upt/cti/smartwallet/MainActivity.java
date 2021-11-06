package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Float.parseFloat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

public class MainActivity extends AppCompatActivity {
    private final static String PREFS_SETTINGS = "prefs_settings";
    private SharedPreferences prefsUser;
    private TextView tStatus;
    private EditText eSearch, eIncome, eExpenses;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private  ValueEventListener databaseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefsUser = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        tStatus = (TextView) findViewById(R.id.tStatus);
        eSearch = (EditText) findViewById(R.id.eSearch);
        eIncome = (EditText) findViewById(R.id.eIncome);
        currentMonth=prefsUser.getString("CurrentMonth", null);
        eSearch.setText(currentMonth);
        eExpenses = (EditText) findViewById(R.id.eExpenses);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-5eb90-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();
    }
    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bSearch:
                if (!eSearch.getText().toString().isEmpty()) {
                    // save text to lower case (all our months are stored online in lower case)
                    currentMonth=prefsUser.getString("CurrentMonth", null);
                    currentMonth = eSearch.getText().toString().toLowerCase();
                    prefsUser.edit().putString("CurrentMonth", currentMonth).apply();

                    tStatus.setText("Searching ...");
                    System.out.println(currentMonth=="march");
                   // if(currentMonth=="january"||currentMonth=="february"||currentMonth=="march")
                    createNewDBListener();
                } else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bUpdate:
                if(!eIncome.getText().toString().isEmpty() && !eExpenses.getText().toString().isEmpty()){
                    currentMonth = eSearch.getText().toString().toLowerCase();
                    tStatus.setText("Searching ...");
                    createNewUpdateDbListener();
                }else {
                    Toast.makeText(this, "Search field may not be empty", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void createNewDBListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                MonthlyExpenses monthlyExpense = dataSnapshot.getValue(MonthlyExpenses.class);
                // explicit mapping of month name from entry key
                monthlyExpense.month = dataSnapshot.getKey();

                eIncome.setText(String.valueOf(monthlyExpense.getIncome()));
                eExpenses.setText(String.valueOf(monthlyExpense.getExpenses()));
                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
    private void createNewUpdateDbListener() {
        // remove previous databaseListener
        if (databaseReference != null && currentMonth != null && databaseListener != null)
            databaseReference.child("calendar").child(currentMonth).removeEventListener(databaseListener);

        databaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                MonthlyExpenses monthlyExpense = new MonthlyExpenses(currentMonth, parseFloat(eIncome.getText().toString()),  parseFloat(eExpenses.getText().toString()));
                // whenever data at this location is updated.
                databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(monthlyExpense.getExpenses());
                databaseReference.child("calendar").child(currentMonth).child("income").setValue(monthlyExpense.getIncome());

                tStatus.setText("Found entry for " + currentMonth);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        // set new databaseListener
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }
}