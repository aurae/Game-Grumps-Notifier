package de.hsb.mschnelle.grumps.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class GrumpAlarmListener implements WakefulIntentService.AlarmListener {

	private int rate;
	
	@Override
	public long getMaxAge() {
		return 5000;
	}

	@Override
	public void scheduleAlarms(AlarmManager am, PendingIntent pi, Context c) {
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 1000, rate * 60000, pi);
	}

	@Override
	public void sendWakefulWork(Context ctxt) {
		WakefulIntentService.sendWakefulWork(ctxt, GrumpCheckService.class);
	}
	
	public void setCurrentRate(int rate) {
		this.rate = rate;
	}
	
	public int getCurrentRate() {
		return rate;
	}
}
