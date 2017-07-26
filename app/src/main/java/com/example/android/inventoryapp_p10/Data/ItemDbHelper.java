package com.example.android.inventoryapp_p10.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bianka Matyas on 21/07/2017.
 */

public class ItemDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME +
                "(" +
                ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemContract.ItemEntry.COLUMN_ITEM_NAME + " TEXT," +
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE + " TEXT NOT NULL DEFAULT 0," +
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 0," +
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE + " BLOB NOT NULL," +
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT NOT NULL, " +
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
