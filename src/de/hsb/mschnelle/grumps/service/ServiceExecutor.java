package de.hsb.mschnelle.grumps.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;

/**
 * Abstract class for executor methods used by the WakefulIntentService.
 * Basically implementations of this class can be seen as different
 * APIs and connection methods to get grumpy data.
 * @author Marcel
 *
 */
public abstract class ServiceExecutor {
	
	/** Preferences */
	protected SharedPreferences preferences;
	/** Calling service */
	protected GrumpCheckService service;
	
	/**
	 * Abstract constructor
	 * @param service
	 * @param preferences
	 */
	protected ServiceExecutor(GrumpCheckService service, SharedPreferences preferences) {
		this.preferences = preferences;
		this.service = service;
	}
	
	/**
	 * Build request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public abstract String buildRequest() throws UnsupportedEncodingException;

	/**
	 * Execute request
	 * @param request
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public abstract String executeRequest(String request) throws ClientProtocolException, IOException;
	
	/**
	 * Check result of request
	 * @param response
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public abstract void checkResult(String response) throws JSONException,
			MalformedURLException, IOException, XmlPullParserException;

	/**
	 * Get descriptive name of this executor
	 * @return
	 */
	public abstract String getName();
}
