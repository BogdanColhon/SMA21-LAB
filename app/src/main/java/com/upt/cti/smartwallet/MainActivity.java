package com.upt.cti.smartwallet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static java.lang.Float.parseFloat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.cti.smartwallet.model.MonthlyExpenses;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnCreateContextMenuListener, AdapterView.OnItemSelectedListener {
    private final static String PREFS_SETTINGS = "prefs_settings";
    private SharedPreferences prefsUser;
    private TextView tStatus;
    private EditText  eIncome, eExpenses;
    private  Spinner sSearch;
    // firebase
    private DatabaseReference databaseReference;
    private String currentMonth;
    private  ValueEventListener databaseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefsUser = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
        final List<MonthlyExpenses> monthlyExpenses = new ArrayList<>();
        final List<String> monthNames = new ArrayList<>();
        tStatus = (TextView) findViewById(R.id.tStatus);
        eIncome = (EditText) findViewById(R.id.eIncome);
        sSearch = (Spinner) findViewById(R.id.spinner);

        eExpenses = (EditText) findViewById(R.id.eExpenses);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-5eb90-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        // spinner adapter
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monthNames);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSearch.setAdapter(sAdapter);
        sSearch.setOnItemSelectedListener(this);
        databaseReference.child("calendar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {

                        // create a new instance of MonthlyExpense
                        MonthlyExpenses monthlyExpenses1 = dataSnapshot.getValue(MonthlyExpenses.class);
                        if(unique(monthNames,monthSnapshot.getKey())==0)
                        monthNames.add(monthSnapshot.getKey());

                }
                if(currentMonth!=null)
                {
                    sSearch.setSelection(monthNames.indexOf(currentMonth));
                }

                // notify the spinner that data may have changed
                sAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        currentMonth=prefsUser.getString("CurrentMonth", null);
    }
    public int unique(List<String> monthNames, String key) {
        for (String it : monthNames)
        {
            if(it==key)
                return 1;
        }
        return 0;
    }
    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bUpdate:
                if(!eIncome.getText().toString().isEmpty() && !eExpenses.getText().toString().isEmpty()){
                    tStatus.setText("Searching ...");
                    MonthlyExpenses monthlyExpense = new MonthlyExpenses(currentMonth, parseFloat(eIncome.getText().toString()),  parseFloat(eExpenses.getText().toString()));
                    // whenever data at this location is updated.
                    databaseReference.child("calendar").child(currentMonth).child("expenses").setValue(monthlyExpense.getExpenses());
                    databaseReference.child("calendar").child(currentMonth).child("income").setValue(monthlyExpense.getIncome());

                    tStatus.setText("Found entry for " + currentMonth);
                    //createNewUpdateDbListener();
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
        databaseReference.child("calendar").child(currentMonth).addValueEventListener(databaseListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object selectedMonth = parent.getItemAtPosition(position);
        currentMonth=selectedMonth.toString();
        prefsUser.edit().putString("CurrentMonth", currentMonth).apply();
        createNewDBListener();
        System.out.println("aici");
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        createNewDBListener();
    }
}