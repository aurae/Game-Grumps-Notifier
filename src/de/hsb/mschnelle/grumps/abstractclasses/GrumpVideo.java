package de.hsb.mschnelle.grumps.abstractclasses;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import de.hsb.mschnelle.grumps.R;
import de.hsb.mschnelle.grumps.vo.GrumpConstants;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Data structure for the different components of a Grump video
 * 
 * @author Marcel
 * 
 */
public abstract class GrumpVideo {

	// Video ID
	protected String id;
	// Title of the game this video is about
	protected String game;
	// Title of the episode's name (multi-part only)
	protected String episode;
	// Title of the part number (multi-part only)
	protected String part;
	// Title of the show this video is part of
	protected String show;
	// Thumbnail image
	protected Bitmap thumbnail;

	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param thumbnailUrl
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public GrumpVideo(String id, Map<String, String> titles, String thumbnailUrl, Resources res) {
		
		// Video ID
		this.id = id;
		
		// Components of YouTube title
		this.game 		= titles.get(GrumpConstants.KEY_GAME);
		this.episode 	= titles.get(GrumpConstants.KEY_EPISODE);
		this.part 		= titles.get(GrumpConstants.KEY_PART);
		this.show 		= titles.get(GrumpConstants.KEY_SHOW);
		
		// Thumbnail image
		try {
			this.thumbnail = BitmapFactory.decodeStream(new URL(thumbnailUrl)
					.openConnection().getInputStream());
		} catch (Exception e) {
			// If we can't load the image for whatever reason, use the default one from res
			this.thumbnail = BitmapFactory.decodeResource(res, R.drawable.thumbnail_default);
		}
	}
	
	/**
	 * Get bitmap of thumbnail
	 * @return
	 */
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	/**
	 * Return title message for ticker and notification
	 * @return
	 */
	public String getContentTitle() {
		return String.format("New %s!", show);
	}
	
	/**
	 * Return content of notification
	 * @return
	 */
	public abstract String getContentText();
	
	/**
	 * Return ticker text
	 * @return
	 */
	public abstract String getTickerText();
}
