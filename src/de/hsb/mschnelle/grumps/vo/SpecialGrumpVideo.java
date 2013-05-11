package de.hsb.mschnelle.grumps.vo;

import java.util.Map;

import android.content.res.Resources;

/**
 * Class for special videos, such as announcements
 * @author Marcel
 *
 */
public class SpecialGrumpVideo extends OneOffGrumpVideo {

	public SpecialGrumpVideo(String id, Map<String, String> titles, String thumbnailUrl, Resources res) {
		super(id, titles, thumbnailUrl, res);
	}
	
	@Override
	public String getContentTitle() {
		return "New Game Grumps!";
	}
}
