package com.example.android.inventoryapp_p10;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Bianka Matyas on 19/07/2017.
 */

public class EditorActivity  extends AppCompatActivity implements LoaderCallbacks<Object> {

    private static final int EXISTING_ITEM_LOADER = 0;
    Uri imageUri;

    private ImageView mImage;
    private EditText mEditTextName;
    private EditText mEditTextPrice;
    private EditText mEditTextSupplierName;
    private EditText mEditTextSupplierEmail;
    private TextView mQuantityTextView;
    private Button mPlusButton;
    private Button mMinusButton;
    private Button mOrderButton;
    private TextView mImageText;
    private int mQuantity;
    private Uri mCurrentProductUri;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);


        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        mImage = (ImageView) findViewById(R.id.item_image);
        mEditTextName = (EditText) findViewById(R.id.edit_item_name);
        mEditTextPrice = (EditText) findViewById(R.id.edit_item_price);
        mEditTextSupplierName = (EditText) findViewById(R.id.supplier_name);
        mEditTextSupplierEmail = (EditText) findViewById(R.id.supplier_email);

        mQuantityTextView = (TextView) findViewById(R.id.edit_quantity_text_view);
        mPlusButton = (Button) findViewById(R.id.button_plus);
        mMinusButton = (Button) findViewById(R.id.button_minus);
        mImageText = (TextView) findViewById(R.id.add_photo_text);
        mOrderButton = (Button) findViewById(R.id.button_order);

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_item_title));
            mImageText.setText(getString(R.string.add_image_tv));
            mEditTextSupplierName.setEnabled(true);
            mEditTextSupplierEmail.setEnabled(true);
            mOrderButton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product_title));
            mImageText.setText(getString(R.string.change_photo));
            mEditTextSupplierName.setEnabled(false);
            mEditTextSupplierEmail.setEnabled(false);
            mOrderButton.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mEditTextName.setOnTouchListener(mOnTouchListener);
        mEditTextPrice.setOnTouchListener(mOnTouchListener);
        mEditTextSupplierName.setOnTouchListener(mOnTouchListener);
        mEditTextSupplierEmail.setOnTouchListener(mOnTouchListener);
        mMinusButton.setOnTouchListener(mOnTouchListener);
        mPlusButton.setOnTouchListener(mOnTouchListener);
        mOrderButton.setOnTouchListener(mOnTouchListener);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mProductHasChanged = true;
            }
        });
    }
    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }
    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intentType));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPicture)), 0);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
    public void increment(View view) {
        mQuantity++;
        displayQuantity();
    }

    public void decrement(View view) {
        if (mQuantity == 0) {
            Toast.makeText(this, R.string.notLessQuantity, Toast.LENGTH_SHORT).show();
        } else {
            mQuantity--;
            displayQuantity();
        }
    }

    public void displayQuantity() {
        mQuantityTextView.setText(String.valueOf(mQuantity));
    }
}

