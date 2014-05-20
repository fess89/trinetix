package com.trinetix.app.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trinetix.app.Helpers.MyDragShadowBuilder;
import com.trinetix.app.Product;
import com.trinetix.app.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Адаптер для показа продуктов в каталоге.
 * Created by fess on 5/19/14.
 */
public class ProductsListAdapter extends ArrayAdapter<Product>
{
	private String logKey;

	private List<Product> productList;
	private int resource;
	private LayoutInflater inflater;

	private Bitmap[] bitmaps;
	private Boolean[] loadingBitmap;

	public ProductsListAdapter(Context context, int resource, List<Product> productList)
	{
		super(context, resource, productList);
		this.productList = productList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resource = resource;
		this.bitmaps = new Bitmap[productList.size()];

		//initially, all bitmaps are not loading
		this.loadingBitmap = new Boolean[productList.size()];
		for (int i = 0; i < loadingBitmap.length ; i++)
		{
			loadingBitmap[i] = false;
		}

		this.logKey = context.getResources().getString(R.string.trinetix);
	}

	//храним использованные вьюшки здесь, чтобы не создавать layout с нуля
	private static class ViewHolderItem
	{
		public TextView titleTextView;
		public TextView descriptionTextView;
		public TextView priceTextView;
		public TextView residueTextView;
		public ImageView imageView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolderItem viewHolder;
		final int pos = position;

		if (null == convertView)
		{
			convertView = inflater.inflate(resource, parent, false);

			viewHolder = new ViewHolderItem();
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.product_title_textview);
			viewHolder.descriptionTextView = (TextView) convertView.findViewById(R.id.product_description_textview);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.product_price_textview);
			viewHolder.residueTextView = (TextView) convertView.findViewById(R.id.product_residue_textview);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.product_imageview);

			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolderItem) convertView.getTag();
		}

		final Product product = productList.get(pos);
		viewHolder.titleTextView.setText(product.title);
		viewHolder.descriptionTextView.setText(product.description);
		viewHolder.priceTextView.setText(product.price);
		viewHolder.residueTextView.setText(product.residue);

		//устанавливаем на каждую картинку тэг - позицию этого товара в списке
		viewHolder.imageView.setTag(String.valueOf(pos));

		//loading image
		if (bitmaps[pos] == null)
		{
			viewHolder.imageView.setImageDrawable(new ColorDrawable(Color.WHITE));
			if (!loadingBitmap[pos])
			{
				Log.e(logKey, "Bitmap #" + String.valueOf(pos) + " is not loading");
				if (null == product.image)
				{
					Log.e(logKey, "Product image URL is null, not loading image");
				}
				else
				{
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							Log.e(logKey, "Loading bitmap from URL: " + product.image);

							//actually loading the bitmap
							Bitmap loadedBitmap = null;
							try
							{
								InputStream in = new URL(product.image).openStream();
								loadedBitmap = BitmapFactory.decodeStream(in);
							}
							catch (Exception e)
							{
								Log.e(logKey, "An exception occurred while loading a bitmap from URL: " + product.image);
							}
							Log.e(logKey, "Finished loading bitmap from URL: " + product.image);

							//saving the bitmap
							bitmaps[pos] = loadedBitmap;

							//changing image on background thread
							viewHolder.imageView.post(new Runnable()
							{
								@Override
								public void run()
								{
									//проверяем, в ту ли позицию мы устанавливаем картинку.
									//это важно, иначе некоторое время показываются неправильные картинки
									if ( (bitmaps[pos] != null) && (String.valueOf(pos).equals(viewHolder.imageView.getTag())) )
									{
										Log.e(logKey, "Setting loaded image from URL " + product.image + " to item number " + String.valueOf(pos));
										viewHolder.imageView.setImageBitmap(bitmaps[pos]);
										viewHolder.imageView.invalidate();
									}
								}
							});
						}
					}).start();

					loadingBitmap[pos] = true;
					Log.e(logKey, "Bitmap #" + String.valueOf(pos) + " loading...");
				}

			}
		}
		else
		{
			Log.e(logKey, "Setting pre-loaded image from URL " + product.image + " to item number " + String.valueOf(pos));
			viewHolder.imageView.setImageBitmap(bitmaps[pos]);
			viewHolder.imageView.invalidate();
		}

		//setting up drag and drop
		viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener()
		{
			// Defines the one method for the interface, which is called when the View is long-clicked
			public boolean onLongClick(View v)
			{
				// Create a new ClipData.
				ClipData dragData = ClipData.newPlainText((String) v.getTag(), (String) v.getTag());

				// Instantiates the drag shadow builder.
				View.DragShadowBuilder myShadow = new MyDragShadowBuilder(viewHolder.imageView);

				// Starts the drag
				v.startDrag(dragData, myShadow, null, 0);

				return true;
			}
		});
		return convertView;
	}

}
