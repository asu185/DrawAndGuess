package com.game.drawandguess.classes;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.game.drawandguess.R;
import com.parse.ParseObject;

/**
 * Represent the room in a game.
 * This object should be available only on a server device.
 * 
 * @author Marcin Peck <marcinpeck@gmail.com>
 *
 */
public class GameRoom {
	private static final String GAME_STATE_NEW = "new";
	private String roomId;
	private String roomName;
	private int playersAmount;
	private String administratorId;
	
	private ArrayList<String> playerNameList;
	private JSONObject playerToTeam;
	
	public GameRoom(String roomName, String administrator) {
		super();
		this.roomName = roomName;
		this.administratorId = administrator;
		playersAmount = 1;
		
		playerNameList = new ArrayList<String>();
		playerNameList.add(administratorId);
		
		playerToTeam = new JSONObject();
		try {
			playerToTeam.put(administratorId, 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public void assignPlayerToTeam(String playerName, int team) {
		try {
			playerToTeam.put(playerName, team);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public int getPlayesAmount() {
		return playersAmount;
	}
	public void setPlayesAmount(int playesAmount) {
		this.playersAmount = playesAmount;
	}
	public ArrayList<String> getPlayerNameList() {
		return playerNameList;
	}
	public void setPlayerNameList(ArrayList<String> playerList) {
		this.playerNameList = playerList;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public int getPlayersAmount() {
		return playersAmount;
	}

	public String getAdministratorId() {
		return administratorId;
	}
	
	public ParseObject getParseObject(){
		ParseObject newGameObj = new ParseObject(GameController.getInstance().GAME_ROOM_TABLE_NAME);
		
		newGameObj.put("roomName", roomName);
		newGameObj.put("playersAmount", playersAmount);
		newGameObj.put("administratorId", administratorId);
		newGameObj.put("playerNameList", playerNameList);
		newGameObj.put("playersToTeam", playerToTeam);
		newGameObj.put("gameState", GAME_STATE_NEW);
		
		return newGameObj;
	}
}
