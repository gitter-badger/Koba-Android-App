package com.pajarraco.koba;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class BlogActivity extends Activity implements OnClickListener {
	Button button2;

	static final int DIALOG_ID = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blog);

		button2 = (Button) findViewById(R.id.btBlog);

		button2.setOnClickListener(this);

	}

	// ////// Button click
	public void onClick(View arg0) {

		new UpdateBlog().execute();

	}

	// /////// Dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		switch (id) {
		case DIALOG_ID:
			dialog.setMessage(BlogActivity.this.getString(R.string.msgDialog));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
		}
		return dialog;
	}

	// /////// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_blog, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemStatusUpdate:
			startActivity(new Intent(this, KobaActivity.class));
			break;
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
		}
		return true;
	}

	// /// Update Blog

	private class UpdateBlog extends AsyncTask<String, integer, String> {

		List<String> titles = new ArrayList<String>(20);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			showDialog(DIALOG_ID);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String result = "";

			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();

				DefaultHandler handler = new DefaultHandler() {

					boolean bfname = false;
					boolean blname = false;
					boolean bnname = false;
					boolean bsalary = false;

					public void startElement(String uri, String localName,
							String qName, Attributes attributes)
							throws SAXException {

						System.out.println("Tag :" + qName);

						if (qName.equalsIgnoreCase("title")) {
							bfname = true;
						}
						if (qName.equalsIgnoreCase("link")) {
							blname = true;
						}
						if (qName.equalsIgnoreCase("description")) {
							bnname = true;
						}
						if (qName.equalsIgnoreCase("pubDate")) {
							bsalary = true;
						}
					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {
						// System.out.println("End Element :" + qName);
					}

					public void characters(char ch[], int start, int length)
							throws SAXException {

						if (bfname) {
							System.out.println("Title : "
									+ new String(ch, start, length));
							titles.add("Title : "
									+ new String(ch, start, length));
							bfname = false;
						}
						if (blname) {
							System.out.println("Link : "
									+ new String(ch, start, length));
							titles.add("Link : "
									+ new String(ch, start, length));
							blname = false;
						}
						if (bnname) {
							System.out.println("Description : "
									+ new String(ch, start, length));
							titles.add("Description : "
									+ new String(ch, start, length));
							bnname = false;
						}
						if (bsalary) {
							System.out.println("PubDate : "
									+ new String(ch, start, length));
							titles.add("PubDate : "
									+ new String(ch, start, length));
							bsalary = false;
						}
					}
				};

				String url = ((KobaApplication) BlogActivity.this
						.getApplication()).getServer();

				saxParser.parse(url, handler);

				result = BlogActivity.this.getString(R.string.statusUpdateSuss);

			} catch (Exception e) {
				result = BlogActivity.this.getString(R.string.statusUpdateFail);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ListView lv1 = (ListView) findViewById(R.id.listView1);
			lv1.setAdapter(new ArrayAdapter<String>(BlogActivity.this,
					R.layout.row, titles));

			dismissDialog(DIALOG_ID);

			Toast.makeText(BlogActivity.this, result, Toast.LENGTH_LONG).show();
		}

	}

}
