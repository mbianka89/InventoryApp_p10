package com.example.android.inventoryapp_p10;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bianka Matyas on 20/07/2017.
 */

public class ItemCursorAdapter extends CursorRecyclerAdapter<ItemCursorAdapter.ViewHolder> {

    private MainActivity activity = new MainActivity();

    public ItemCursorAdapter(MainActivity context, Cursor c) {
        super(context, c);
        this.activity = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView priceTextView;
        protected TextView quantityTextView;
        protected ImageView buy;
        protected ImageView productPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.list_item_product_name);
            priceTextView = (TextView) itemView.findViewById(R.id.list_item_product_price);
            quantityTextView = (TextView) itemView.findViewById(R.id.list_item_product_quantity);
            buy = (ImageView) itemView.findViewById(R.id.shop);
            productPicture = (ImageView) itemView.findViewById(R.id.list_item_product_image_view);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        final long id;
        final int mQuantity;

        id =cursor.getLong(cursor.getColumnIndex(ItemContract.ItemEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PICTURE);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String imageUriString = cursor.getString(imageColumnIndex);
        Uri imageUri = Uri.parse(imageUriString);

        mQuantity = quantity;

        viewHolder.nameTextView.setText(productName);
        viewHolder.priceTextView.setText(productPrice);
        viewHolder.quantityTextView.setText(String.valueOf(quantity));
        viewHolder.productPicture.setImageURI(imageUri);
        viewHolder.productPicture.invalidate();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onItemClick(id);
            }
        });

        viewHolder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mQuantity >0 ) {
                    activity.onBuyNowClick(id, mQuantity);
                } else {
                    Toast.makeText(activity, R.string.soldOut, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
