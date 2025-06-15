package com.example.electricitybill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BillDB.db";
    public static final String TABLE_NAME = "bill_table";

    // Column constants
    public static final String COL_ID = "ID";
    public static final String COL_MONTH = "MONTH";
    public static final String COL_UNIT = "UNIT";
    public static final String COL_REBATE = "REBATE";
    public static final String COL_TOTAL = "TOTAL";
    public static final String COL_FINAL = "FINAL";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MONTH + " TEXT, " +
                COL_UNIT + " INTEGER, " +
                COL_REBATE + " REAL, " +
                COL_TOTAL + " REAL, " +
                COL_FINAL + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String month, int unit, double rebate, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MONTH, month);
        cv.put(COL_UNIT, unit);
        cv.put(COL_REBATE, rebate);
        cv.put(COL_TOTAL, total);
        cv.put(COL_FINAL, finalCost);
        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
