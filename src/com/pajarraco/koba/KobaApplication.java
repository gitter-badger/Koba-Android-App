package com.pajarraco.koba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class KobaApplication extends Application implements
		OnSharedPreferenceChangeListener {

	private static final String TAB = KobaApplication.class.getSimpleName();
	SharedPreferences prefs;
	private String server;
	private Twitter twitter;
	StatusData statusdata;

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		statusdata = new StatusData(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		statusdata.close();
	}

	/**
	 * get variable from preferences
	 * 
	 * @return String for Server name
	 */
	public synchronized String getServer() {
		if (server == null) {
			// get server name
			String newServer = prefs
					.getString("server",
							"http://blog.pajarraco.com/engine/shards/xmlcache/blog.rss");
			server = newServer;
			Log.d(TAB, String.format("Variables Blog: %s", newServer));
		}
		return server;

	}

	public synchronized Twitter getTwitter() {
		if (twitter == null) {
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String serverTwitter = prefs.getString("serverTwitter",
					"http://api.twitter.com");

			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(serverTwitter);

			Log.d(TAB, String.format("Variables Twitter: %s/%s @ %s", username,
					password, serverTwitter));
		}
		return twitter;
	}

	// call when preferences change
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		server = null;
		twitter = null;
	}

}
