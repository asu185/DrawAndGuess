package com.game.drawandguess.interaces;

import java.util.ArrayList;

import android.content.Context;

public interface GameConnection {
	/**
	 * Methods
	 */
	public boolean createConnection();
	public boolean createConnection(Context ctx);
	
	public void closeConnection();
	public ArrayList<String> getGameRooms();
	public boolean joinToRoom(String roomId);
	
	
}
