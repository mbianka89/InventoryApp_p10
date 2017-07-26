package com.example.android.inventoryapp_p10;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp_p10.Data.ItemContract;

import java.io.ByteArrayOutputStream;

/**
 * Created by Bianka Matyas on 19/07/2017.
 */

public class EditorActivity extends AppCompatActivity
        implements LoaderCallbacks<Cursor> {

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
    private Uri mCurrentItemUri;

    private boolean mItemHasChanged = false;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);


        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

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

        if (mCurrentItemUri == null) {
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
                mItemHasChanged = true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                mImage.setImageURI(imageUri);
                mImage.invalidate();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // In case of unsaved changes a dialog box will show, where the user can confirm what he/she wants
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // in case of discard
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_and_quit);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // In case of Keep editing, dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Save
            case R.id.action_save:
                // Insert item
                if (saveItem()) {
                    // Go back to MainActivity
                    finish();
                }
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // In case of unsaved changes a dialog box will show, where the user can confirm what he/she wants
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //order in email
    public void order(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + mEditTextSupplierEmail.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order of " + mEditTextName.getText().toString().trim());
        String message = "Hello " + mEditTextSupplierName.getText().toString().trim() + ", \n I wish to order more of your product: " + mEditTextName.getText().toString().trim() + ", please find the details below:";
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    private boolean saveItem() {

        boolean allFilledOut = false;

        String nameString = mEditTextName.getText().toString().trim();
        String priceString = mEditTextPrice.getText().toString().trim();
        String supplierNameString = mEditTextSupplierName.getText().toString().trim();
        String supplierEmailString = mEditTextSupplierEmail.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString();

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierEmailString) &&
                TextUtils.isEmpty(quantityString) &&
                imageUri == null) {
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            allFilledOut = true;
            return allFilledOut;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.itemNameReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, nameString);

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.itemPriceReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, priceString);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, getString(R.string.supplierNameReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, getString(R.string.supplierEmailReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        values.put(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL, supplierEmailString);

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.quantityReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);

        if (imageUri == null) {
            Toast.makeText(this, getString(R.string.itemPicReq), Toast.LENGTH_SHORT).show();
            return allFilledOut;
        }

        BitmapDrawable drawable = (BitmapDrawable) mImage.getDrawable();
        byte[] imageBytes = getBytes(drawable.getBitmap());
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE, imageBytes);

        // Determine if this is a new or existing item by checking if mCurrentPruductUri is null or not
        if (mCurrentItemUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_saving_item),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.item_saved),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // In case of an EXISTING item, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_saving_item),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                if (mItemHasChanged) {
                    Toast.makeText(this, getString(R.string.item_saved),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        allFilledOut = true;
        return allFilledOut;
    }

    private static byte[] getBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_item_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content URI already identifies the item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_done),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of item attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_NAME);
            int sEmailColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL);

            int imageColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE);
            byte[] imageBytes = cursor.getBlob(imageColumnIndex);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            mImage.setImageBitmap(imageBitmap);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            imageUri = Uri.EMPTY;
            String price = cursor.getString(priceColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sEmail = cursor.getString(sEmailColumnIndex);
            mQuantity = cursor.getInt(quantityColumnIndex);
            // Update the views on the screen with the values from the database
            mEditTextName.setText(name);
            mEditTextPrice.setText(price);
            mEditTextSupplierName.setText(sName);
            mEditTextSupplierEmail.setText(sEmail);
            mQuantityTextView.setText(Integer.toString(mQuantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEditTextName.setText("");
        mEditTextPrice.setText("");
        mEditTextSupplierName.setText("");
        mEditTextSupplierEmail.setText("");
        mQuantityTextView.setText("");
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