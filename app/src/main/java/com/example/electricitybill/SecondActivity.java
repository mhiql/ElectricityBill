package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

public class SecondActivity extends AppCompatActivity {

    EditText edtUnit;
    Spinner spinnerMonth;
    RadioGroup radioGroupRebate;
    Button btnCalculate, btnViewData, backBtn2;
    DatabaseHelper db;

    String selectedMonth;
    private double rebatePercent = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Smart Bill Estimator");
        setContentView(R.layout.activity_second);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        edtUnit = findViewById(R.id.edtUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnViewData = findViewById(R.id.btnViewData);
        backBtn2 = findViewById(R.id.backBtn2);
        db = new DatabaseHelper(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        radioGroupRebate.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb0) rebatePercent = 0.0;
            else if (checkedId == R.id.rb1) rebatePercent = 0.01;
            else if (checkedId == R.id.rb2) rebatePercent = 0.02;
            else if (checkedId == R.id.rb3) rebatePercent = 0.03;
            else if (checkedId == R.id.rb4) rebatePercent = 0.04;
            else if (checkedId == R.id.rb5) rebatePercent = 0.05;
        });


        btnCalculate.setOnClickListener(view -> {
            String unitStr = edtUnit.getText().toString();
            selectedMonth = spinnerMonth.getSelectedItem().toString();

            if (unitStr.isEmpty()) {
                Toast.makeText(this, "Please enter units used!", Toast.LENGTH_SHORT).show();
                return;
            }

            int units = Integer.parseInt(unitStr);
            double totalCharges = calculateCharges(units);
            double finalCost = totalCharges - (totalCharges * rebatePercent);

            boolean inserted = db.insertData(selectedMonth, units, rebatePercent, totalCharges, finalCost);

            if (inserted) {
                Toast.makeText(this, "Bill Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving to database", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewData.setOnClickListener(v -> {
            startActivity(new Intent(SecondActivity.this, BillDetailActivity.class));
        });

        backBtn2.setOnClickListener(v -> {
            startActivity(new Intent(SecondActivity.this, MainActivity.class));
        });
    }

    private double calculateCharges(int units) {
        double charge = 0;

        if (units <= 200) {
            charge = units * 0.218;
        } else if (units <= 300) {
            charge = (200 * 0.218) + (units - 200) * 0.334;
        } else if (units <= 600) {
            charge = (200 * 0.218) + (100 * 0.334) + (units - 300) * 0.516;
        } else {
            charge = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + (units - 600) * 0.546;
        }

        return charge;
    }

}