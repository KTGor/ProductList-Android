package com.bignerdranch.android.product.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.product.Product;
import com.bignerdranch.android.product.database.ProductDbSchema.ProductTable;

import java.util.Date;
import java.util.UUID;

public class ProductCursorWrapper extends CursorWrapper {

    public ProductCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Product getProduct(){
        String prodIdString = getString(getColumnIndex(ProductTable.Cols.PRODUCTID));
        String name = getString(getColumnIndex(ProductTable.Cols.NAME));
        long date = getLong(getColumnIndex(ProductTable.Cols.DATE));
        String quantity = getString(getColumnIndex(ProductTable.Cols.QUANTITY));
        int isAvailability = getInt(getColumnIndex(ProductTable.Cols.AVAILABILITY));
        String brand = getString(getColumnIndex(ProductTable.Cols.BRAND));

        Product product = new Product(UUID.fromString(prodIdString));
        product.setName(name);
        product.setDate(new Date(date));
        product.setQuantity(quantity);
        product.setAvailability(isAvailability != 0);
        product.setBrand(brand);

        return product;
    }
}
