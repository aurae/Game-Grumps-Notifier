package de.hsb.mschnelle.grumps.vo;

import java.util.Map;

import android.content.res.Resources;

/**
 * Special grump video for multi-part series
 * (i.e. videos with PART tags and episode names)
 * @author Marcel
 *
 */
public class MultiPartGrumpVideo extends GrumpVideo {

	public MultiPartGrumpVideo(String id, Map<String, String> titles, String thumbnailUrl, Resources res) {
		super(id, titles, thumbnailUrl, res);
	}

	@Override
	public String getContentText() {
		return String.format("%s - %s", game, part);
	}

	@Override
	public String getTickerText() {
		return String.format("%s\n%s - %s", getContentTitle(), game, part);
	}

}
