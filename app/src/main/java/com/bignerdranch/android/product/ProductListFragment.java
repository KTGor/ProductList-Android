package com.bignerdranch.android.product;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class ProductListFragment extends Fragment {

    private RecyclerView mProductRecyclerView;
    private ProductAdapter mAdapter;
    private boolean mSubtitleVisible;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    public static Intent newIntent(Context packageContext, UUID productId){
        Intent intent = new Intent(packageContext, ProductListActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        mProductRecyclerView = (RecyclerView) view.findViewById(R.id.product_recycler_view);
        mProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));;

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_product_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_product:
                Product product = new Product();
                ProductLab.get(getActivity()).addProduct(product);
                Intent intent = ProductPagerActivity.newIntent(getActivity(), product.getProductId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.delete_all_product:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.confirm_del_all_prod)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Product product = new Product();
                                ProductLab.get(getActivity()).deleteAllProduct(product);
                                Intent intent = ProductListFragment.newIntent(getActivity(), product.getProductId());
                                startActivity(intent);
                                Toast.makeText(getActivity(),R.string.delete_toast, Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        ProductLab productLab = ProductLab.get(getActivity());
        int productCount = productLab.getProducts().size();
        String subtitle = getString(R.string.subtitle_format, productCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(){
        ProductLab productLab = ProductLab.get(getActivity());
        List<Product> products = productLab.getProducts();

        if(mAdapter == null){
            mAdapter = new ProductAdapter(products);
            mProductRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setProducts(products);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

    }

    private class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Product mProduct;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mOutOfStockImageView;


        public ProductHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_product, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.product_name);
            mDateTextView = (TextView) itemView.findViewById(R.id.product_date);
            mOutOfStockImageView = (ImageView) itemView.findViewById(R.id.product_availability);
        }

        public void bind (Product product){
            mProduct = product;
            mTitleTextView.setText(mProduct.getName());
            mDateTextView.setText(mProduct.getDate().toString());
            mOutOfStockImageView.setVisibility(product.isAvailability() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick (View view){
            Intent intent = ProductPagerActivity.newIntent(getActivity(), mProduct.getProductId());
            startActivity(intent);
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder>{

        private List<Product> mProducts;

        public ProductAdapter(List<Product> products){
            mProducts = products;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ProductHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position){
            Product product = mProducts.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount(){
            return mProducts.size();
        }

        public void setProducts(List<Product> products){
            mProducts = products;
        }
    }

}

