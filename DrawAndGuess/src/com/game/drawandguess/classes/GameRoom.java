package com.game.drawandguess.classes;

import java.util.ArrayList;

/**
 * Represent the room in a game.
 * This object should be available only on a server device.
 *  
 * @author Marcin Peck <marcinpeck@gmail.com>
 *
 */
public class GameRoom {
	private String roomId;
	private int playesAmount;
	private ArrayList<String> playerList;
	
	
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public int getPlayesAmount() {
		return playesAmount;
	}
	public void setPlayesAmount(int playesAmount) {
		this.playesAmount = playesAmount;
	}
	public ArrayList<String> getPlayerList() {
		return playerList;
	}
	public void setPlayerList(ArrayList<String> playerList) {
		this.playerList = playerList;
	}
	
}
