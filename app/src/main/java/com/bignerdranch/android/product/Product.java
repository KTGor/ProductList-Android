package com.bignerdranch.android.product;

import java.util.Date;
import java.util.UUID;

public class Product {

    private UUID mProductId;
    private String mName;
    private Date mDate;
    private String mQuantity;
    private boolean mAvailability;
    private String mBrand;

    public Product(){
        this(UUID.randomUUID());
    }

    public Product(UUID id){
        mProductId = id;
        mDate = new Date();
    }

    public UUID getProductId() {
        return mProductId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(String quantity) {
        mQuantity = quantity;
    }

    public boolean isAvailability() {
        return mAvailability;
    }

    public void setAvailability(boolean availability) {
        mAvailability = availability;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getPhotoFilename(){ //designating pic location
        return "IMG_" + getProductId().toString() + ".jpg";
    }
}
