package com.pajarraco.koba;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends Activity {
	public static final String TAG = TimelineActivity.class.getSimpleName();
	ListView listStatus;
	KobaApplication Koba;
	Cursor cursor;
	SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Koba = (KobaApplication) getApplication();

		// Setup UI
		setContentView(R.layout.timeline);
		listStatus = (ListView) findViewById(R.id.lsitStatus);

		// Get the Data
		cursor = Koba.statusdata.query();
		startManagingCursor(cursor);

		// setup adapter
		String[] from = { StatusData.C_USER, StatusData.C_TEXT,
				StatusData.C_CREATED_AT };
		int[] to = { R.id.textStatusUser, R.id.textStatusText,
				R.id.textStatusCreatedAt };
		adapter = new SimpleCursorAdapter(this, R.layout.row_status, cursor,
				from, to);
		adapter.setViewBinder(VIEW_BINDER);
		listStatus.setAdapter(adapter);

		Log.d(TAG, "On Created");

	}

	/**
	 * Our custom binder to bind createAt column to to it view and change data
	 * from timestamp to relative time
	 */
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (cursor.getColumnIndex(StatusData.C_CREATED_AT) != columnIndex) {
				// we are not processing anything other that createdAt column
				// here
				return false;
			} else {
				Long timestamp = cursor.getLong(cursor
						.getColumnIndex(StatusData.C_CREATED_AT));
				CharSequence relTime = DateUtils
						.getRelativeTimeSpanString(timestamp);
				((TextView) view).setText(relTime);
				return true;
			}
		}
	};
}
