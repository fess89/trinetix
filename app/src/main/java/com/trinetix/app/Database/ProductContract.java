package com.trinetix.app.Database;

import android.provider.BaseColumns;

/**
 * Контракт для хранения продуктов в базе данных.
 * Created by fess on 5/19/14.
 */
public final class ProductContract
{
	public ProductContract() {}

	/* Inner class that defines the table contents */
	public static abstract class ProductEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "products";
		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COLUMN_NAME_PRICE = "price";
		public static final String COLUMN_NAME_IMAGE_URL = "image";
	}
}
