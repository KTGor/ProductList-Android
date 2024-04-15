package com.bignerdranch.android.product;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.product.database.ProductBaseHelper;
import com.bignerdranch.android.product.database.ProductDbSchema;
import com.bignerdranch.android.product.database.ProductDbSchema.ProductTable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductFragment extends Fragment{

    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    public static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_TIME = 3;

    private Product mProduct;
    private EditText mNameField;
    private EditText mQuantity;

    private Button mDateButton;
    private Button mTimeButton;
    private Button mOrderButton;
    private Button mBrandButton;

    private CheckBox mAvailabilityCheckBox;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
    private SimpleDateFormat mTimeFormat= new SimpleDateFormat("h:mm a");

    public static ProductFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_ID, productId);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setHasOptionsMenu(true);

        UUID productId = (UUID) getArguments().getSerializable(ARG_PRODUCT_ID);
        mProduct = ProductLab.get(getActivity()).getProduct(productId);
        mPhotoFile = ProductLab.get(getActivity()).getPhotoFile(mProduct);
    }

    @Override
    public void onPause() {
        super.onPause();

        ProductLab.get(getActivity()).updateProduct(mProduct);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_product, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Product product = new Product();
                if (mNameField.getText().toString().isEmpty()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setMessage("You have not insert the product name field.\nAre you sure you want to leave now?\n*All Data will not be saved.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Product product = new Product();
                                    deleteProduct();
                                    Intent intent = ProductListFragment.newIntent(getActivity(), product.getProductId());
                                    startActivity(intent);
                                }
                            });
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }else{
                    Intent intent = ProductListFragment.newIntent(getActivity(), product.getProductId());
                    startActivity(intent);
                }
                return true;
            case R.id.delete_product:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.confirm_del_prod)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Product product = new Product();
                                        deleteProduct();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product, container, false);

        mNameField = (EditText) v.findViewById(R.id.product_name);
        mNameField.setText(mProduct.getName());

        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProduct.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNameField.getText().toString().isEmpty()) {
                    mNameField.setError("The product name cannot be empty.");
                    return;
                }
            }
        });

        mDateButton = (Button) v.findViewById(R.id.product_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mProduct.getDate());
                dialog.setTargetFragment(ProductFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button)v.findViewById(R.id.product_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mProduct.getDate());
                dialog.setTargetFragment(ProductFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mAvailabilityCheckBox = (CheckBox) v.findViewById(R.id.product_availability);
        mAvailabilityCheckBox.setChecked(mProduct.isAvailability());
        mAvailabilityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mProduct.setAvailability(isChecked);
            }
        });

        mQuantity = (EditText) v.findViewById(R.id.product_quantity);
        mQuantity.setText(mProduct.getQuantity());
        mQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        mQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mProduct.setQuantity(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                //This space intentionally left blank
            }
        });

        mOrderButton = (Button) v.findViewById(R.id.product_restock);
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getProductOrder());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.product_restock_order));
                i = Intent.createChooser(i, getString(R.string.send_order));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mBrandButton = (Button) v.findViewById(R.id.product_brand);
        mBrandButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mProduct.getBrand() != null) {
            mBrandButton.setText(mProduct.getBrand());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mBrandButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.product_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.product.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.product_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mProduct.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                String brand = c.getString(0);
                mProduct.setBrand(brand);
                mBrandButton.setText(brand);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.product.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }else if (requestCode == REQUEST_TIME){
            Date time = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mProduct.setDate(time);
            updateTime();
        }
    }

    private void updateDate() {
        mDateButton.setText(mDateFormat.format(mProduct.getDate()));
    }

    private void updateTime() {
        mTimeButton.setText(mTimeFormat.format(mProduct.getDate()));
    }

    private String getProductOrder() {
        String solvedString = null;
        if (mProduct.isAvailability()) {
            solvedString = getString(R.string.product_available);
        } else {
            solvedString = getString(R.string.product_not_available);
        }

        String dateFormat = "EEE, MM dd";
        String dateString = DateFormat.format(dateFormat, mProduct.getDate()).toString();

        String brand = mProduct.getBrand();
        if (brand == null) {
            brand = getString(R.string.request_order);
        } else {
            brand = getString(R.string.product_restock_brand);
        }

        String order = getString(R.string.product_restock, mProduct.getName(), dateString, solvedString, brand);

        return order;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void deleteProduct() {
        ProductLab productLab = ProductLab.get(getActivity());
        productLab.deleteProduct(mProduct);

        // Delete the image file associated with product if exists
        File file = new File(mPhotoFile.getPath());
        if (file.exists()) {
            file.delete();
            Log.i("ProductFragment", "deleteProduct photo called");
        }
    }


}
