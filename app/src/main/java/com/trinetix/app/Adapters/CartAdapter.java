package com.trinetix.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.trinetix.app.Product;
import com.trinetix.app.R;

import java.util.List;

/**
 * Адаптер для краткого показа продуктов в корзине.
 * Created by fess on 5/19/14.
 */
public class CartAdapter extends ArrayAdapter<Product> {

    private final List<Product> productList;
    private final LayoutInflater inflater;

    public CartAdapter(Context context, List<Product> productList) {
        super(context, 0, productList);
        this.productList = productList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //храним использованные вьюшки здесь, чтобы не создавать layout с нуля
    private static class ViewHolderItem {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView priceTextView;
        public TextView residueTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolderItem viewHolder;

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.cart_list_item, parent, false);

            viewHolder = new ViewHolderItem();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.product_title_textview);
            viewHolder.descriptionTextView = (TextView) convertView.findViewById(R.id.product_description_textview);
            viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.product_price_textview);
            viewHolder.residueTextView = (TextView) convertView.findViewById(R.id.product_residue_textview);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        Product product = productList.get(position);
        viewHolder.titleTextView.setText(product.title);
        viewHolder.descriptionTextView.setText(product.description);
        viewHolder.priceTextView.setText(product.price);
        viewHolder.residueTextView.setText(product.residue);

        return convertView;
    }
}
