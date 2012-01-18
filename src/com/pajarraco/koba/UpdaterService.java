package com.pajarraco.koba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	private static final String TAG = UpdaterService.class.getSimpleName();
	private Updater updater;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		updater = new Updater();

		Log.d(TAG, "On Create");
	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// start updater
		if (!updater.isRunning()) {
			updater.start();
		}

		Log.d(TAG, "On Start");
	}

	@Override
	public synchronized void onDestroy() {
		super.onDestroy();

		// stop updater
		if (updater.isRunning()) {
			updater.interrupt();
		}

		updater = null;

		Log.d(TAG, "On Destroy");
	}

	// ///// updater thread
	class Updater extends Thread {
		static final long DELAY = 60000;
		private Boolean isRunning = false;
		KobaApplication koba;

		public Updater() {
			super("Updater");
			koba = (KobaApplication) getApplication();

		}

		@Override
		public void run() {
			isRunning = true;
			while (isRunning) {
				try {
					// do something
					Log.d(TAG, "Updater running");

					try {
						// get twitter friends statuses
						List<Status> statuses = koba.getTwitter()
								.getFriendsTimeline();
						for (Status status : statuses) {
							// insert data
							koba.statusdata.insert(status);

							Log.d(TAG, String.format("%s : %s - %s",
									status.user.name, status.text,
									status.createdAt.toString()));
						}

					} catch (TwitterException e) {
						Log.e("error", e.getMessage());
					}

					// sleep
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// interrupted
					isRunning = false;
				}
			}// / while
		}

		public boolean isRunning() {
			return this.isRunning;
		}

	}

}
