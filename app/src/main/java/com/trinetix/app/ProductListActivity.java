package com.trinetix.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trinetix.app.Adapters.ProductsListAdapter;
import com.trinetix.app.Database.ProductContract;
import com.trinetix.app.Database.ProductDBHelper;
import com.trinetix.app.Helpers.HighlightHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Показываем список продуктов.
 * Created by fess on 5/19/14.
 */
public class ProductListActivity extends Activity
{
	private ListView productsListView;

	private static long mCartCount = 0;

	private ProductDBHelper mDbHelper;

	private String productsString;
	private String gsonErrorString;
	private String dataConversionErrorString;

	private ProductsListAdapter adapter;

	private static final PorterDuffColorFilter greenFilter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
	private static final PorterDuffColorFilter blueFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.products_list_activity);

		//loading views
		productsListView = (ListView) findViewById(R.id.products_listview);

		//loading string resources
		gsonErrorString = getResources().getString(R.string.gson_error);
		dataConversionErrorString = getResources().getString(R.string.data_conversion_error);

		//getting extras
		productsString = getIntent().getStringExtra("products");

		//initializing database access
		mDbHelper = new ProductDBHelper(this);

		//showing the products in a ListView
		showProducts();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		//узнаём, сколько в корзине позиций
		new AsyncTask<Void, Void, Long>()
		{
			@Override
			protected Long doInBackground(Void... params)
			{
				SQLiteDatabase db = mDbHelper.getReadableDatabase();
				return DatabaseUtils.queryNumEntries(db, ProductContract.ProductEntry.TABLE_NAME, null, null);
			}

			@Override
			protected void onPostExecute(Long result)
			{
				//вряд ли у нас будет больше Integer позиций
				setCartCount(result);
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.products_list_menu, menu);

		//находим кнопку в меню для использования в дальнейшем
		View count = menu.findItem(R.id.action_cart).getActionView();
		Button cartCount = (Button) count.findViewById(R.id.notif_count);
		cartCount.setText(String.valueOf(mCartCount));
		cartCount.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
				startActivity(intent);
			}
		});

		//при нажатии на кнопку подсвечиваем её
		HighlightHelper.setShadow(cartCount);

		//на кнопку "корзина" можно перетащить товары
		MyDragEventListener dragListener = new MyDragEventListener();
		cartCount.setOnDragListener(dragListener);

		return super.onCreateOptionsMenu(menu);
	}

	private void setCartCount(Long count)
	{
		mCartCount = count;
		invalidateOptionsMenu();
	}

	private void showProducts()
	{
		Product[] products;
		Gson gson = new Gson();
		try
		{
			products = gson.fromJson(productsString, Product[].class);
		}
		catch (Exception ex)
		{
			showError(gsonErrorString);
			return;
		}

		List<Product> productList = Arrays.asList(products);
		adapter = new ProductsListAdapter(this, productList);
		productsListView.setAdapter(adapter);
	}

	private void showError(String s)
	{
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}

	private class MyDragEventListener implements View.OnDragListener
	{
		public boolean onDrag(View v, DragEvent event)
		{
			final int action = event.getAction();
			switch (action)
			{
				case DragEvent.ACTION_DRAG_STARTED:
				{
					// Determines if this View can accept the dragged data
					if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
					{
						// Applies a blue color tint to the View to indicate that it can accept data.
						v.getBackground().setColorFilter(blueFilter);
						v.invalidate();
						// returns true to indicate that the View can accept the dragged data.
						return true;
					}
					else
					{
						// During the current drag and drop operation, this View will
						// not receive events again until ACTION_DRAG_ENDED is sent.
						return false;
					}
				}

				case DragEvent.ACTION_DRAG_ENTERED:
				{
					// Applies a green tint to the View. Return true; the return value is ignored.
					v.getBackground().setColorFilter(greenFilter);
					// Invalidate the view to force a redraw in the new tint
					v.invalidate();
					return true;
				}

				case DragEvent.ACTION_DRAG_LOCATION:
				{
					// Ignore the event
					return true;
				}

				case DragEvent.ACTION_DRAG_EXITED:
				{
					// Re-sets the color tint to blue. Returns true; the return value is ignored.
					v.getBackground().setColorFilter(blueFilter);
					v.invalidate();
					return true;
				}

				case DragEvent.ACTION_DROP:
				{
					// Gets the item containing the dragged data
					ClipData.Item item = event.getClipData().getItemAt(0);
					// Gets the text data from the item.
					String dragData = item.getText().toString();

					//save the item to cart
					saveItemToCart(dragData);

					// Turns off any color tints
					v.getBackground().clearColorFilter();
					v.invalidate();
					return true;
				}

				case DragEvent.ACTION_DRAG_ENDED:
				{
					// Turns off any color tinting
					v.getBackground().clearColorFilter();
					// Invalidates the view to force a redraw
					v.invalidate();
					// returns true; the value is ignored.
					return true;
				}

				// An unknown action type was received.
				default:
				{
					Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
					return true;
				}
			}
		}
	}

	private void saveItemToCart(String data)
	{
		Integer itemPosition;
		try
		{
			itemPosition = Integer.valueOf(data);
		}
		catch (Exception e)
		{
			showError(dataConversionErrorString);
			return;
		}
		if (null == itemPosition)
		{
			return;
		}

		//loading the product which was dragged to the cart
		Product product = adapter.getItem(itemPosition);
		final String id = String.valueOf(product.id);
		final String title = product.title;
		final String description = product.description;
		final String price = product.price;
		final String image = product.image;

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// Gets the data repository in write mode
				SQLiteDatabase db = mDbHelper.getWritableDatabase();

				// Create a new map of values, where column names are the keys
				ContentValues values = new ContentValues();
				values.put(ProductContract.ProductEntry.COLUMN_NAME_ID, id);
				values.put(ProductContract.ProductEntry.COLUMN_NAME_TITLE, title);
				values.put(ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION, description);
				values.put(ProductContract.ProductEntry.COLUMN_NAME_PRICE, price);
				values.put(ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL, image);

				// Insert the new row, returning the primary key value of the new row
				db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

				mCartCount += 1;
				setCartCount(mCartCount);
			}
		}).start();
	}
}
