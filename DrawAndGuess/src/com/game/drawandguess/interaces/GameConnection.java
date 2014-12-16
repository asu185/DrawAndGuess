package com.game.drawandguess.interaces;

import java.util.ArrayList;

public interface GameConnection {
	/**
	 * Methods
	 */
	public boolean createConnection();
	public void closeConnection();
	public ArrayList<String> getGameRooms();
	public boolean joinToRoom(String roomId);
	
}
