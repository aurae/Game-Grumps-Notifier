package de.hsb.mschnelle.grumps.service;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import de.hsb.mschnelle.grumps.R;
import de.hsb.mschnelle.grumps.abstractclasses.GrumpVideo;
import de.hsb.mschnelle.grumps.abstractclasses.ServiceExecutor;
import de.hsb.mschnelle.grumps.service.ytapiv3.GrumpCheckExecutorYTAPIV3;
import de.hsb.mschnelle.grumps.util.GrumpUtils;
import de.hsb.mschnelle.grumps.util.Logger;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;

public class GrumpCheckService extends WakefulIntentService {

	// Number of notifications active
	private static int notificationCount = 0;
	
	private ServiceExecutor delegate;
	private SharedPreferences preferences;
	
	public GrumpCheckService() {
		super("GrumpCheckService");
	}

	@Override
	/**
	 * Work method of the service
	 * @param pi
	 */
	protected void doWakefulWork(Intent pi) {

		// Get preference reference (hehe)
		preferences = getSharedPreferences(GrumpConstants.PREFERENCES, 0);
		
		// Switch the executor object here
		delegate = new GrumpCheckExecutorYTAPIV3(this, preferences);
		
		Logger.d("==============GRUMP CHECK SERVICE (" + delegate.getName() + ")==============");
		
		try {
			// Build API request
			String request = delegate.buildRequest();
			Logger.d("Request:");
			Logger.d(request);
			// Execute API request
			String result = delegate.executeRequest(request);
			Logger.d("Response:");
			Logger.d(result);
			Logger.d("(" + result.getBytes().length + " bytes)");
			// Process request result
			delegate.checkResult(result);
		} catch (Exception e) {
			Logger.d("Exception during GrumpService execution: " + e.getClass());
			e.printStackTrace();
		}
		
		// Stop this service
		stopSelf();
	}
	
	/**
	 * Saves the given video ID in preferences
	 * @param videoId
	 */
	public void saveVideoId(String videoId) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(GrumpConstants.PREF_LASTVIDEO, videoId);
		editor.commit();
	}
	
	public void postNotification(String title, String thumbnailUrl, String videoId) {
		this.notification(title, thumbnailUrl, videoId);
//		this.saveVideoId(videoId);
	}
	
	/**
	 * Notifies the Android system of the new Grump video
	 * @param title			Non-format title of the video (according to YT video title)
	 * @param thumbnail		String URL of the thumbnail image
	 * @param videoId		YT video ID
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void notification(String title, String thumbnailUrl, String videoId) {

		// Create a GrumpVideo instance using the parameters
		GrumpVideo video = GrumpUtils.createGrumpVideo(videoId, title, thumbnailUrl, getResources());
		
		// Get Notification manager service
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Init sound to play
		Uri alarmSound = getNotificationSound();

		// Init intent for the Video View action
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(GrumpConstants.YT_VIEW_URL + videoId));
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Build notification
		Notification notification = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.statusbar_icon)
				.setLargeIcon(video.getThumbnail())
				.setContentTitle(video.getContentTitle())
				.setContentText(video.getContentText())
				.setAutoCancel(true)
				.setSound(alarmSound)
				.setTicker(video.getTickerText())
				.setLights(Color.BLUE, 300, 200).setContentIntent(pIntent)
				.setOnlyAlertOnce(true).build();

		// Notify Android
		mNotificationManager.notify(++notificationCount, notification);
	}

	/**
	 * Returns the sound to play for the notification using preferences
	 * @return
	 */
	private Uri getNotificationSound() {
		// Get current preference
		String sound = preferences.getString(GrumpConstants.PREF_SOUND, GrumpConstants.SOUND_DEFAULT);
		
		// Switch-case-like statement to determine the sound to play
		if (sound.equals(GrumpConstants.SOUND_NOJON)) {
			return Uri.parse("android.resource://de.hsb.mschnelle.grumps/" + R.raw.notification_nojon);
		} else if (sound.equals(GrumpConstants.SOUND_GRUMPS)) {
			return Uri.parse("android.resource://de.hsb.mschnelle.grumps/" + R.raw.notification_grumps);
		} else
			// default
			return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	}

}
