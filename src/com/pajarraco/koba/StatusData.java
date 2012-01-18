package com.pajarraco.koba;

import winterwell.jtwitter.Twitter.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData {
	public static final String TAG = StatusData.class.getSimpleName();

	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "koba_createdAt";
	public static final String C_USER = "koba_user";
	public static final String C_TEXT = "koba_text";

	Context context;
	DbHelper dbHelper;

	public StatusData(Context context) {
		this.context = context;
		dbHelper = new DbHelper();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Insert into database
	 * 
	 * @param values
	 */
	public void insert(ContentValues values) {
		// Open Database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// insert values into database
		db.insertWithOnConflict(DbHelper.TABLE, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);

		// close database
		db.close();
	}

	/**
	 * Insert into database
	 * 
	 * @param status
	 *            Status data as provided by online service
	 */
	public void insert(Status status) {

		ContentValues values = new ContentValues();

		// create content values
		values.put(StatusData.C_ID, status.id);
		values.put(StatusData.C_CREATED_AT, status.createdAt.getTime());
		values.put(StatusData.C_USER, status.user.name);
		values.put(StatusData.C_TEXT, status.text);

		this.insert(values);
	}

	/**
	 * Delete all the data
	 */
	public void delete() {
		// Open Database
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// delete the data
		db.delete(DbHelper.TABLE, null, null);

		// close database
		db.close();
	}

	/**
	 * Query Select database
	 * 
	 * @return Cursor like record set
	 */
	public Cursor query() {
		// Open Database
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// get the data
		return db.query(DbHelper.TABLE, null, null, null, null, null,
				C_CREATED_AT + " DESC");

	}

	/**
	 * Class to help open/create/upgrade database
	 */
	private class DbHelper extends SQLiteOpenHelper {
		public static final String DB_NAME = "timeline.db";
		public static final int DB_VERSION = 3;
		public static final String TABLE = "statuses";

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String
					.format("create table %s (%s int primary key, %s int, %s text, %s text)",
							TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
			Log.d(TAG, "On Create SQL: " + sql);

			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TABLE);
			Log.d(TAG, "onUpgrade dropped table : " + TABLE);
			this.onCreate(db);
		}

	}

}
