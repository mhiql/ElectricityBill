package com.example.electricitybill;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class BillDetailActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper db;
    ArrayList<String> billList = new ArrayList<>();
    ArrayList<String> fullList = new ArrayList<>();
    ArrayList<Integer> idList = new ArrayList<>(); // To store bill IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Monthly Bills");
        setContentView(R.layout.activity_bill_detail);

        listView = findViewById(R.id.listViewBill);
        db = new DatabaseHelper(this);

        Cursor cursor = db.getAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            // Use consistent column names from DatabaseHelper
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MONTH));
            int unit = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT));
            double rebate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REBATE));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TOTAL));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FINAL));

            idList.add(id);  // Save the ID
            billList.add(month + " - RM " + String.format("%.2f", finalCost));
            fullList.add("Month: " + month +
                    "\nUnit: " + unit +
                    "\nRebate: " + (rebate * 100) + "%" +
                    "\nTotal: RM " + String.format("%.2f", total) +
                    "\nFinal: RM " + String.format("%.2f", finalCost));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textItem, billList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BillDetailActivity.this);
            builder.setTitle("Bill Details");
            builder.setMessage(fullList.get(i));

            builder.setPositiveButton("OK", null);

            builder.setNegativeButton("Delete", (dialog, which) -> {
                // Confirmation dialog
                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(BillDetailActivity.this);
                confirmBuilder.setTitle("Confirm Delete");
                confirmBuilder.setMessage("Are you sure you want to delete this bill?");

                confirmBuilder.setPositiveButton("YES", (confirmDialog, confirmWhich) -> {
                    int deleteId = idList.get(i);
                    boolean deleted = db.deleteData(deleteId);
                    if (deleted) {
                        Toast.makeText(BillDetailActivity.this, "Bill deleted", Toast.LENGTH_SHORT).show();
                        billList.remove(i);
                        fullList.remove(i);
                        idList.remove(i);
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                    } else {
                        Toast.makeText(BillDetailActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                });

                confirmBuilder.setNegativeButton("NO", null);
                confirmBuilder.show();
            });

            builder.show();
        });
    }
}
