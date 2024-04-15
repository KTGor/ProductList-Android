package com.bignerdranch.android.product;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class ProductListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new ProductListFragment();
    }

}
