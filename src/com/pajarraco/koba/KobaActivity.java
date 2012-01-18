package com.pajarraco.koba;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class KobaActivity extends Activity implements OnClickListener {
	EditText editText1;
	Button button1;
	static final int DIALOG_ID = 40;
	KobaApplication koba;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		koba = (KobaApplication) getApplication();

		// Debug.startMethodTracing("Koba.trace");

		setContentView(R.layout.main);

		editText1 = (EditText) findViewById(R.id.editText1);
		button1 = (Button) findViewById(R.id.btStatusUpdate);

		button1.setOnClickListener(this);

	}

	@Override
	protected void onStop() {
		super.onStop();

		// Debug.stopMethodTracing();

	}

	// ////// Button click
	public void onClick(View arg0) {

		String updateStatus = editText1.getText().toString();
		Log.d("Nombre", "Mi nombre: " + updateStatus);
		new StatusUpdate().execute(updateStatus);

	}

	// /////// Dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		switch (id) {
		case DIALOG_ID:
			dialog.setMessage(KobaActivity.this.getString(R.string.msgDialog));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
		}
		return dialog;
	}

	// /////// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class));
			break;
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		case R.id.itemServiceStar:
			startService(new Intent(this, UpdaterService.class));
			break;
		case R.id.itemStopService:
			stopService(new Intent(this, UpdaterService.class));
			break;
		case R.id.itemPurge:
			koba.statusdata.delete();
			Toast.makeText(this, R.string.msgAllDataPurge, Toast.LENGTH_LONG)
					.show();
			break;
		case R.id.itemBlog:
			startActivity(new Intent(this, BlogActivity.class));
			break;
		}
		return true;
	}

	// /////// Status Update
	private class StatusUpdate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_ID);
		}

		@Override
		protected String doInBackground(String... status) {
			String result = "";

			try {
				koba.getTwitter().setStatus(status[0]);

				result = KobaActivity.this.getString(R.string.statusUpdateSuss);
			} catch (Exception e) {
				result = KobaActivity.this.getString(R.string.statusUpdateFail);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dismissDialog(DIALOG_ID);

			Toast.makeText(KobaActivity.this, result, Toast.LENGTH_LONG).show();
		}

	}

}
