package de.hsb.mschnelle.grumps.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import de.hsb.mschnelle.grumps.abstractclasses.GrumpVideo;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;
import de.hsb.mschnelle.grumps.vo.MultiPartGrumpVideo;
import de.hsb.mschnelle.grumps.vo.OneOffGrumpVideo;
import de.hsb.mschnelle.grumps.vo.SpecialGrumpVideo;

/**
 * Utility methods for the Grump Notifier
 * 
 * @author Marcel
 * 
 */
public class GrumpUtils {

	public static GrumpVideo createGrumpVideo(String id, String title,
			String thumbnailUrl, Resources res) {
		Map<String, String> titles = GrumpUtils.parseTitle(title);
		
		if (titles.get(GrumpConstants.KEY_EPISODE) != null
				&& titles.get(GrumpConstants.KEY_PART) != null) {
			// Multi-part episode
			return new MultiPartGrumpVideo(id, titles, thumbnailUrl, res);
		} else if (titles.get(GrumpConstants.KEY_SHOW) == null) {
			// Special/Announcement video
			return new SpecialGrumpVideo(id, titles, thumbnailUrl, res);
		} else {
			// "Ordinary" video (I know, when it comes to the Grumps, what is
			// ordinary, right?)
			return new OneOffGrumpVideo(id, titles, thumbnailUrl, res);
		}
	}

	/**
	 * Parses the title of a GrumpVideo and splits it up, returning a nice Map
	 * 
	 * @param title
	 * @return
	 */
	public static Map<String, String> parseTitle(String title) {
		// Split the full video title up using a regex
		String[] split = title.split("(\\s+)(-)(\\s+)");

		// Describe elements
		String gameName, episodeName, partName, showName;

		// Get first element and save it
		gameName = split[0];

		// It MIGHT be that there is only one element, i.e. if the Grumps
		// uploaded a special video (such as the Grep shirt announcement).
		// In this case, return only the "gameName" variable containing
		// the full video title
		if (split.length == 1) {
			episodeName = null;
			partName = null;
			showName = null;
		} else {
			// Get second element and check its value
			String secondToken = split[1];
			// If the next check returns true, the second element is "PART x",
			// therefore it's a multi-part series
			if (GrumpUtils.isMultiPartSeries(secondToken)) {
				// Get the episode name out of the gameName variable (Format:
				// "Game: Episode", e.g. "Sonic '06: Back to Happy")
				StringTokenizer colonTokenizer = new StringTokenizer(gameName, ":");
				
				gameName = colonTokenizer.nextToken();
				episodeName = (colonTokenizer.hasMoreTokens() ? colonTokenizer.nextToken() : null);
				partName = secondToken;
				showName = split[2];
			} else {
				// If not, it's a one-off, and the second token is just the show
				// name
				// ("Game Grumps VS" etc.)
				episodeName = null;
				partName = null;
				showName = secondToken;
			}
		}

		// Build the result Map and return it
		Map<String, String> map = new HashMap<String, String>();
		map.put(GrumpConstants.KEY_GAME, gameName);
		map.put(GrumpConstants.KEY_EPISODE, episodeName);
		map.put(GrumpConstants.KEY_PART, partName);
		map.put(GrumpConstants.KEY_SHOW, showName);
		
		return map;
	}

	/**
	 * Checks if a network is currently enabled on the device
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Regex matcher for PART X pattern in multi-part videos
	 * http://txt2re.com/index-java.php3
	 * 
	 * @param token
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static boolean isMultiPartSeries(String token) {
		String re1 = "(PART)"; // Word 1
		String re2 = "( )"; // White Space 1
		String re3 = "(\\d+)"; // Integer Number 1

		Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = p.matcher(token.toUpperCase());

		return m.find();
	}
}
