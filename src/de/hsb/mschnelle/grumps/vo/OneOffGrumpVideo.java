package de.hsb.mschnelle.grumps.vo;

import java.util.Map;

import de.hsb.mschnelle.grumps.abstractclasses.GrumpVideo;

import android.content.res.Resources;

/**
 * Class for one-off grump videos without PART tags or episode names
 * @author Marcel
 *
 */
public class OneOffGrumpVideo extends GrumpVideo {

	public OneOffGrumpVideo(String id, Map<String, String> titles, String thumbnailUrl, Resources res) {
		super(id, titles, thumbnailUrl, res);
	}

	@Override
	public String getContentText() {
		return game;
	}

	@Override
	public String getTickerText() {
		return String.format("%s\n%s", getContentTitle(), game);
	}

}
