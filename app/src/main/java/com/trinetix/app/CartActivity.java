package com.trinetix.app;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.trinetix.app.Adapters.CartAdapter;
import com.trinetix.app.Database.ProductContract;
import com.trinetix.app.Database.ProductDBHelper;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends Activity {

    private ProductDBHelper mDbHelper;

    private ListView cartListView;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        loadCartItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_clear_cart: {
                //очищаем корзину
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                mDbHelper.clearDatabase(db);

                //очищаем адаптер
                adapter.clear();
                adapter.notifyDataSetInvalidated();
                break;
            }
            //это надо сделать, иначе при возврате к ProductListActivity будет вызываться onCreate() :(
            case android.R.id.home: {
                break;
            }
            default: {
                break;
            }
        }
        return true;
    }

    private void loadCartItems() {
        new AsyncTask<Void, Void, List<Product>>() {
            @Override
            protected List<Product> doInBackground(Void... params) {
                //initializing database access
                mDbHelper = new ProductDBHelper(CartActivity.this);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();

                String[] projection =
                        {
                                ProductContract.ProductEntry._ID,
                                ProductContract.ProductEntry.COLUMN_NAME_ID,
                                ProductContract.ProductEntry.COLUMN_NAME_TITLE,
                                ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION,
                                ProductContract.ProductEntry.COLUMN_NAME_PRICE,
                                ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL
                        };

                // How you want the results sorted in the resulting Cursor
                String sortOrder = ProductContract.ProductEntry.COLUMN_NAME_ID + " DESC";

                Cursor cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,    // The table to query
                        projection,                                 // The columns to return
                        null,                                       // The columns for the WHERE clause
                        null,                                       // The values for the WHERE clause
                        null,                                       // don't group the rows
                        null,                                       // don't filter by row groups
                        sortOrder                                   // The sort order
                );

                List<Product> productList = new ArrayList<>();

                //creating a list of products
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String id = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_ID));
                        String title = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_TITLE));
                        String description = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION));
                        String price = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_PRICE));
                        String image = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL));

                        //считаем, что мы каждый раз добавляем по одному товару в корзину
                        String residue = "1";

                        Product product = new Product();
                        product.setId(id);
                        product.setTitle(title);
                        product.setDescription(description);
                        product.setPrice(price);
                        product.setResidue(residue);
                        product.setImage(image);

                        productList.add(product);
                        cursor.moveToNext();
                    }
                }

                return productList;
            }

            @Override
            protected void onPostExecute(List<Product> productList) {
                cartListView = (ListView) findViewById(R.id.cart_listview);
                adapter = new CartAdapter(CartActivity.this, productList);
                cartListView.setAdapter(adapter);
            }
        }.execute();
    }
}
