package com.game.drawandguess.classes;

import java.util.ArrayList;
import java.util.Iterator;

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
	public static final String GAME_STATE_NEW = "new";
	private String roomId;
	private String roomName;
	private int playersAmount;
	private String administratorId;
	private String gameState;
	private final int maxPeerTeams = 5;
	
	private JSONObject playerToTeam;
	
	public GameRoom(String roomName, String administrator) {
		super();
		this.roomName = roomName;
		this.administratorId = administrator;
		playersAmount = 1;
		gameState = GAME_STATE_NEW;

		
		playerToTeam = new JSONObject();
		try {
			playerToTeam.put(administratorId, 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean assignPlayerToTeam(String playerName, int team) {
		if (playerToTeam.has(playerName)){
			playerToTeam.remove(playerName);
			playersAmount--;
		}
		
		if (playersAmount < 10) {
			try {
				int team1 = getAmountOfPlayersInTeam(1);
				int team2 = getAmountOfPlayersInTeam(2);

				if (team == 1 && team1 < maxPeerTeams) {
					playerToTeam.put(playerName, 1);
				} else if (team == 2 && team2 < maxPeerTeams) {
					playerToTeam.put(playerName, 2);
				} else if (team == 1 && team2 < maxPeerTeams) {
					playerToTeam.put(playerName, 2);
				} else if (team == 2 && team1 < maxPeerTeams) {
					playerToTeam.put(playerName, 1);
				}

				playersAmount++;
				
				return true;

			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
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
		JSONArray names = playerToTeam.names();
		ArrayList<String> namesList = new ArrayList<String>();
		
		for (int i=0; i<namesList.size();i++) {
			try {
				namesList.add(names.getString(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return namesList;
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
		newGameObj.put("playersToTeam", playerToTeam);
		newGameObj.put("gameState", gameState);
		
		return newGameObj;
	}
	
	public void setPlayerToTeam(JSONObject playerToTeam) {
		this.playerToTeam = playerToTeam;
	}

	public static GameRoom parseObjectToGameRoom(ParseObject parseRoom){
		GameRoom tmpGameRoom = new GameRoom(parseRoom.getString("roomName"), parseRoom.getString("administratorId"));
		
		tmpGameRoom.setPlayesAmount(parseRoom.getInt("playersAmount"));
		tmpGameRoom.setRoomId(parseRoom.getObjectId());
		
		tmpGameRoom.setPlayerToTeam(parseRoom.getJSONObject("playersToTeam"));
		
		return tmpGameRoom;
	}
	
	public int getAmountOfPlayersInTeam(int number){
		int counter = 0;
		
		for (Iterator<String> iterator = playerToTeam.keys(); iterator.hasNext();) {
			String player = (String) iterator.next();
			
			try {
				if (playerToTeam.getInt(player) == number) counter++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return counter;	
	}

	public JSONObject getPlayerToTeam() {
		return playerToTeam;
	}
}
