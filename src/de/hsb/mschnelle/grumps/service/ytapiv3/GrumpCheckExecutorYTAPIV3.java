package de.hsb.mschnelle.grumps.service.ytapiv3;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;
import de.hsb.mschnelle.grumps.abstractclasses.HTTPServiceExecutor;
import de.hsb.mschnelle.grumps.service.GrumpCheckService;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;

/**
 * Grump Check Service V3
 * This service calls YouTube's Data API v3 and handles its responses
 * using XML parsing. At the moment, this version is inferior due to
 * issues with the experimental JSON API v3 - new uploads don't show up
 * in those JSON feeds until an hour after they were actually uploaded.
 * I'm using YTAPIV2 for the time being...
 * @author Marcel
 *
 */
public class GrumpCheckExecutorYTAPIV3 extends HTTPServiceExecutor {
	
	/**
	 * Constructor
	 */
	public GrumpCheckExecutorYTAPIV3(GrumpCheckService service, SharedPreferences preferences) {
		this.service = service;
		this.preferences = preferences;
	}

	public String buildRequest() throws UnsupportedEncodingException {
		StringBuilder urlBuilder = new StringBuilder();
		String chars = "UTF-8";
		
		urlBuilder.append("https://www.googleapis.com/youtube/v3/playlistItems?part=");
		urlBuilder.append(URLEncoder.encode("snippet,contentDetails", chars));
		urlBuilder.append("&playlistId=");
		urlBuilder.append(GrumpConstants.GRUMPS_UPLOADSID);
		urlBuilder.append("&maxResults=1");
		urlBuilder.append("&fields=");
		urlBuilder.append(URLEncoder.encode("items(snippet/thumbnails/default/url,contentDetails/videoId,snippet/title)", chars));
		urlBuilder.append("&key=");
		urlBuilder.append(GrumpConstants.API_KEY);

		String request = urlBuilder.toString();
		return request;
	}

	public void checkResult(String result) throws JSONException,
			MalformedURLException, IOException {

		JSONObject data = new JSONObject(result).getJSONArray("items").getJSONObject(0);
		JSONObject snippet = data.getJSONObject("snippet");
		JSONObject contentDetails = data.getJSONObject("contentDetails");
		
		// Get previously saved video id
		String lastVideoId = preferences.getString(GrumpConstants.PREF_LASTVIDEO, GrumpConstants.EMPTY_VIDEO);
		
		// Get supposedly "new" video's id
		String videoId = contentDetails.getString("videoId");
		
		// For the first check, just set the video id
		if (lastVideoId.equals(GrumpConstants.EMPTY_VIDEO))
			service.saveVideoId(videoId);
		// Ordinary check: Compare the saved video ID with the now-newest one
		else if (!lastVideoId.equals(videoId)) {
			// New video was found
			String title = snippet.getString("title");

			Log.d(GrumpConstants.LOG_TAG, "New upload found: " + title);

			String thumbnailUrl = snippet.getJSONObject("thumbnails")
					.getJSONObject("default").getString("url");
			
			service.postNotification(title, thumbnailUrl, videoId);
		} else
			Log.d(GrumpConstants.LOG_TAG, "Nothing new (latest ID: " + lastVideoId + ")");
	}
	
	public String getName() {
		return "YT API V3";
	}
}
