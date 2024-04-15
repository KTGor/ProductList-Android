package com.bignerdranch.android.product.database;

public class ProductDbSchema {

    public static final class ProductTable {
        public static final String NAME = "products";


        public static final class Cols {
            public static final String PRODUCTID = "productid";
            public static final String NAME = "name";
            public static final String DATE = "date";
            public static final String QUANTITY  = "quantity";
            public static final String AVAILABILITY = "availability";
            public static final String BRAND = "brand";
        }
    }
}
