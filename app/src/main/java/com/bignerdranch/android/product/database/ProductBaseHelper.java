package com.bignerdranch.android.product.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.product.Product;
import com.bignerdranch.android.product.database.ProductDbSchema.ProductTable;

import java.util.UUID;

public class ProductBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "productBase.db";

    public ProductBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + ProductTable.NAME + " (" + " _id integer primary key autoincrement, " + ProductTable.Cols.PRODUCTID + ", "
                + ProductTable.Cols.NAME + ", " + ProductTable.Cols.DATE + ", " + ProductTable.Cols.QUANTITY + ", " + ProductTable.Cols.AVAILABILITY
                + ", " + ProductTable.Cols.BRAND + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
