package de.hsb.mschnelle.grumps.vo;

import de.hsb.mschnelle.grumps.R;


public enum GrumpTheme {

	GAME_GRUMPS(R.drawable.bg_gg),
	
	STEAM_TRAIN(R.drawable.bg_st);
	
	private int bg;
	
	private GrumpTheme(int bgDrawableRes) {
		this.bg = bgDrawableRes;
	}
	
	public int getBackgroundRes() {
		return bg;
	}
	
	public static GrumpTheme fromString(String str) {
		GrumpTheme[] values = values();
		for (GrumpTheme theme : values)
			if (theme.toString().equals(str))
				return theme;
		return GAME_GRUMPS;
	}
}
