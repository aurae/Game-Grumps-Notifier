package de.hsb.mschnelle.grumps.service.ytapiv2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.SharedPreferences;
import android.util.Log;
import de.hsb.mschnelle.grumps.abstractclasses.HTTPServiceExecutor;
import de.hsb.mschnelle.grumps.service.GrumpCheckService;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;

/**
 * Grump Check Service V2
 * This service calls YouTube's Data API v2 and handles its responses
 * using XML parsing. At the moment, this version is superior due to
 * issues with the experimental JSON API v3 - new uploads don't show up
 * in those JSON feeds until an hour after they were actually uploaded.
 * @author Marcel
 *
 */
public class GrumpCheckExecutorYTAPIV2 extends HTTPServiceExecutor {
	
	/**
	 * Constructor
	 */
	public GrumpCheckExecutorYTAPIV2(GrumpCheckService service, SharedPreferences preferences) {
		this.service = service;
		this.preferences = preferences;
	}

	/**
	 * Build the API request
	 * @throws UnsupportedEncodingException
	 */
	public String buildRequest() throws UnsupportedEncodingException {
		StringBuilder urlBuilder = new StringBuilder();
		String chars = "UTF-8";
		
		// API URL
		urlBuilder.append("https://gdata.youtube.com/feeds/api/users/GameGrumps/uploads");
		
		// Attach API key
		urlBuilder.append("?key=");
		urlBuilder.append(GrumpConstants.API_KEY);
		
		// Attach max results
		urlBuilder.append("&max-results=1");
		
		// Attach fields (we're only interested in the video ID and title)
		urlBuilder.append("&fields=");
		urlBuilder.append(URLEncoder.encode("entry(id,title)", chars));

		// Build the request and return it
		String request = urlBuilder.toString();
		return request;
	}

	/**
	 * Check the result that was gathered from the API request
	 * @param result
	 * @return true if a new video was found, false otherwise
	 * @throws JSONException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void checkResult(String result) throws JSONException,
			MalformedURLException, IOException, XmlPullParserException {
		
		// Initialize the parser for the result XML and parse its contents
		GrumpXmlParser parser = new GrumpXmlParser();
        Map<String, String> map = parser.parse(result);
        
		// Get previously saved video ID from preferences
		String lastVideoId = preferences.getString(GrumpConstants.PREF_LASTVIDEO, GrumpConstants.EMPTY_VIDEO);
		
		// Get supposedly "new" video's id & title from the XML map
		String videoId = map.get("id");
		String title = map.get("title");
		
		// When there is no saved video ID so far, just save this one
		// to have a reference when something new pops up
		if (lastVideoId.equals(GrumpConstants.EMPTY_VIDEO)) {
			service.saveVideoId(videoId);
		}
		// Usual check: Compare the saved video ID with the now-newest one
		else if (!lastVideoId.equals(videoId)) {
			
			Log.d(GrumpConstants.LOG_TAG, "New upload found: " + title);

			// Compose thumbnail URL & notify the user. Finally save the new video ID
			String thumbnailUrl = GrumpConstants.YT_THUMBNAIL_URL_1 + videoId + GrumpConstants.YT_THUMBNAIL_URL_2;

			service.postNotification(title, thumbnailUrl, videoId);
		} else
			// Nothing new
			Log.d(GrumpConstants.LOG_TAG, "Nothing new (latest ID: " + lastVideoId + ")");
	}
	
	public String getName() {
		return "YT API V2";
	}
	
}
