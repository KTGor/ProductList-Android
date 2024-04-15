package com.bignerdranch.android.product;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.product.database.ProductBaseHelper;
import com.bignerdranch.android.product.database.ProductCursorWrapper;
import com.bignerdranch.android.product.database.ProductDbSchema;
import com.bignerdranch.android.product.database.ProductDbSchema.ProductTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductLab {

    private static ProductLab sProductLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ProductLab get(Context context){
        if(sProductLab == null){
            sProductLab = new ProductLab(context);
        }
        return sProductLab;
    }

    private ProductLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new ProductBaseHelper(mContext).getWritableDatabase();
    }

    public void addProduct(Product p){
        ContentValues values = getContentValues(p);
        mDatabase.insert(ProductTable.NAME, null, values);
    }

    public void deleteProduct(Product p) {
        mDatabase.delete(ProductTable.NAME, ProductTable.Cols.PRODUCTID + " = ?", new String[] { p.getProductId().toString() });
    }

    public void deleteAllProduct(Product p){
        mDatabase.delete(ProductTable.NAME, null, null);
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        ProductCursorWrapper cursor = queryProducts(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                products.add(cursor.getProduct());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return products;
    }

    public Product getProduct(UUID id) {
        ProductCursorWrapper cursor = queryProducts(ProductTable.Cols.PRODUCTID + " = ?", new String[] {id.toString()});

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getProduct();
        }finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Product product){ //photos live here
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, product.getPhotoFilename());
    }

    public void updateProduct(Product product){
        String prodIdString = product.getProductId().toString();
        ContentValues values = getContentValues(product);

        mDatabase.update(ProductTable.NAME, values, ProductTable.Cols.PRODUCTID + " = ?", new String[] {prodIdString});
    }

    private ProductCursorWrapper queryProducts(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                ProductTable.NAME,
                null, //columns - null (selects all columns)
                whereClause,
                whereArgs,
                null, //groupBy
                null,  //having
                null  //orderBy
        );
        return new ProductCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Product product){
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.PRODUCTID, product.getProductId().toString());
        values.put(ProductTable.Cols.NAME, product.getName());
        values.put(ProductTable.Cols.DATE, product.getDate().getTime());
        values.put(ProductTable.Cols.QUANTITY, product.getQuantity());
        values.put(ProductTable.Cols.AVAILABILITY, product.isAvailability() ? 1 : 0);
        values.put(ProductTable.Cols.BRAND, product.getBrand());

        return values;
    }

}
