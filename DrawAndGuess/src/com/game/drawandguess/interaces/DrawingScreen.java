package com.game.drawandguess.interaces;

public interface DrawingScreen {
	/**
	 * Methods
	 */
	public DrawingScreen getInstance();
	public void createScreen();
	public void updateScreen();
	public void deleteScreen();
	public <T extends Object> T serializeScreen();
		
}
