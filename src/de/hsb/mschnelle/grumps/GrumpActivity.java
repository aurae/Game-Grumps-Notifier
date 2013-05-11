package de.hsb.mschnelle.grumps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import de.hsb.mschnelle.grumps.service.GrumpAlarmListener;
import de.hsb.mschnelle.grumps.util.GrumpUtils;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;

/**
 * Main activity for the boopin' GrumpNotifier
 * @author Marcel
 *
 */
public class GrumpActivity extends Activity {

	// AlarmListener that schedules the service
	private GrumpAlarmListener alarm;
	
	// Preferences
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize alarm listener
		alarm = new GrumpAlarmListener();
		
		// Retrieve preferences
		preferences = getSharedPreferences(GrumpConstants.PREFERENCES, 0);
		
		// Initialize button listener
		Button button = (Button) findViewById(R.id.buttonToggleService);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start or stop the schedule
				if (preferences.getBoolean(GrumpConstants.PREF_RUNNING, false))
					stopService();
				else
					startService();
			}
		});
		
		// Initialize radio group
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.check(preferences.getInt(GrumpConstants.PREF_SELECTEDSOUND, R.id.radioDefault));
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// Set the sound that plays when a new notification pops up
				SharedPreferences.Editor editor = preferences.edit();
				switch (checkedId) {
				case R.id.radioNoJon:
					editor.putString(GrumpConstants.PREF_SOUND, GrumpConstants.SOUND_NOJON);
					break;
				case R.id.radioGrumps:
					editor.putString(GrumpConstants.PREF_SOUND, GrumpConstants.SOUND_GRUMPS);
					break;
				default:
					editor.putString(GrumpConstants.PREF_SOUND, GrumpConstants.SOUND_DEFAULT);
					break;
				}
				editor.putInt(GrumpConstants.PREF_SELECTEDSOUND, checkedId);
				editor.commit();
			}
		});

		// Initialize seek bar
		SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
		sb.setMax(119);
		sb.setProgress(preferences.getInt(GrumpConstants.PREF_CHECKINTERVAL, 9));
		alarm.setCurrentRate(sb.getProgress() + 1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onProgressChanged(SeekBar sb, int progress, boolean isUser) {
				// Update the alarm listener's rate
				alarm.setCurrentRate(progress + 1);
				updateUI();
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) { }

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// Save the progress bar's current progress
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(GrumpConstants.PREF_CHECKINTERVAL, alarm.getCurrentRate() - 1);
				editor.commit();
				
				// Now, if the service was running before, re-start it with the updated value
				if (preferences.getBoolean(GrumpConstants.PREF_RUNNING, false)) {
					stopService();
					startService();
				}
			}
		});

		// Finally, update the UI
		updateUI();
	}

	/**
	 * Starts the Grump Check service
	 */
	private void startService() {
		// Check if there is a network connection first
		if (GrumpUtils.isNetworkAvailable(this)) {
			// Save the state
			savePrefRunning(true);
			// When there is a connection, schedule the Grump checker
			WakefulIntentService.scheduleAlarms(alarm, GrumpActivity.this, false);
		} else
			// If there is no connection, post a Toast message
			Toast.makeText(this, getString(R.string.noConnectionAvailable), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Stops the Grump Check service
	 */
	private void stopService() {
		// Cancel schedule
		WakefulIntentService.cancelAlarms(GrumpActivity.this);
		// Save the state
		savePrefRunning(false);
	}

	/**
	 * Saves the "service running" preference
	 * @param b
	 */
	private void savePrefRunning(boolean b) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(GrumpConstants.PREF_RUNNING, b);
		editor.commit();
		// Finally, update the UI
		updateUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}

	/**
	 * Updates the UI components
	 */
	public void updateUI() {
		
		// Update status TextView
		TextView textView = (TextView) findViewById(R.id.textViewServiceStatus);
		String text = (alarm != null && preferences.getBoolean(GrumpConstants.PREF_RUNNING, false))
				? getString(R.string.serviceRunning)
				: getString(R.string.serviceNotRunning);
		// Set
		textView.setText(text);

		// Update rate TextView
		TextView textViewCurrentRate = (TextView) findViewById(R.id.textViewCurrentCheckRate);
		// Calculate more readable display of time that the scheduler runs at
		int rate = alarm.getCurrentRate();
		int hours = rate / 60;
		int minutes = rate % 60;
		// Convert to Strings
		String sHours = hours > 0 ? hours + "h " : "";
		String sMinutes = minutes != 0 ? minutes + "min" : "";
		// Set
		textViewCurrentRate.setText(sHours + sMinutes);
	}
}
