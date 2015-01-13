package com.trinetix.app.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Позволяет хранить продукты в базе данных.
 * Created by fess on 5/19/14.
 */
public class ProductDBHelper extends SQLiteOpenHelper
{
	// If you change the database schema, you must increment the database version.
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Products.db";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
		"CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
			ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY," +
			ProductContract.ProductEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
			ProductContract.ProductEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
			ProductContract.ProductEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
			ProductContract.ProductEntry.COLUMN_NAME_PRICE + TEXT_TYPE + COMMA_SEP +
			ProductContract.ProductEntry.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES =
		"DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

	public ProductDBHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(SQL_CREATE_ENTRIES);
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// This database is only a cache for online data, so its upgrade policy is
		// to simply to discard the data and start over
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	//удаляем базу и пересоздаём заново
	public void clearDatabase(SQLiteDatabase db)
	{
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}
}