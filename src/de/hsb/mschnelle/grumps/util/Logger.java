package de.hsb.mschnelle.grumps.util;

import android.util.Log;

/**
 * 
 * @author marcel
 * 
 */
public final class Logger {

	private static final String TAG = "game-grumps-notifier";

	private static final boolean ENABLED = true;

	private Logger() {
	}

	public static final void d(String message) {
		if (ENABLED)
			Log.d(TAG, message);
	}

	public static final void v(String message) {
		if (ENABLED)
			Log.v(TAG, message);
	}

	public static final void e(String message) {
		if (ENABLED)
			Log.e(TAG, message);
	}

	public static final void d(String message, Throwable ex) {
		if (ENABLED)
			Log.d(TAG, message, ex);
	}

	public static final void v(String message, Throwable ex) {
		if (ENABLED)
			Log.d(TAG, message, ex);
	}

	public static final void e(String message, Throwable ex) {
		if (ENABLED)
			Log.d(TAG, message, ex);
	}
}
