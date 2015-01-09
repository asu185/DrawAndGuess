package com.game.drawandguess.classes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.game.drawandguess.interaces.GameConnection;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.parse.SendCallback;

public class GameController {
	
	public static final String GAME_SESSION_TABLE_NAME = "GameSession";
	public static final String GAME_ROOM_TABLE_NAME = "GameRoom";
	private static GameController instance;
	private String playerName;
	private GameConnection connection;
	private GameRoom currentGameRoom;
	private String playerColor;
	
	public static GameController getInstance(){
		if (instance == null){
			instance = new GameController();
			return instance;
		}else{
			return instance;
		}
	}
	
	public void init(Context ctx, String playerName){
		this.playerName = playerName;

		this.connection = new ParseGameConnection();
		this.connection.createConnection(ctx);
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public GameRoom getCurrentGameRoom() {
		return currentGameRoom;
	}

	public void setCurrentGameRoom(GameRoom currentGameRoom) {
		this.currentGameRoom = currentGameRoom;
	}

	public ArrayList<GameRoom> getGameRooms() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(GAME_ROOM_TABLE_NAME);
		query.whereEqualTo("gameState", GameRoom.GAME_STATE_NEW);
		
		ArrayList<GameRoom> roomList = new ArrayList<GameRoom>();
		
		try {
			List<ParseObject> findList = query.find();
			
			for (ParseObject parseObject : findList) {
				roomList.add(GameRoom.parseObjectToGameRoom(parseObject));
			}
			
		} catch (ParseException e) {
			Log.e("DAG", e.getMessage());
		}
		
		return roomList;
	}

	public String getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(String playerColor) {
		this.playerColor = playerColor;
	}
	
	public void sendNotificationToMyRoomInBackground(String action, JSONObject values, SendCallback sendCallback){
		ParsePush push = new ParsePush();
		
		push.setChannel("A_" + currentGameRoom.getRoomId());
		try {
			values.put("action", action);
			values.put("sender", this.playerName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		push.setData(values);
		
		push.sendInBackground(sendCallback);
		
	}
	
	public ParseObject createGameSession(GameRoom room, int rounds, int drawTime) throws JSONException{
		ParseObject newSession = new ParseObject(GAME_SESSION_TABLE_NAME);
		
		newSession.put("roomId", "A_" + room.getRoomId());
		newSession.put("round", 0);
		newSession.put("maxRounds", rounds);
		newSession.put("drawTime", drawTime);
		
		JSONArray team1 = new JSONArray();
		JSONArray team2 = new JSONArray();
		
		JSONObject playerToTeam = room.getPlayerToTeam();
		
		int c1 = 0;
		int c2 = 0;
		
		for (Iterator<String> iterator = playerToTeam.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
	
			if (playerToTeam.getInt(key) == 1){
				c1++;
				
				JSONObject tmpPlayer = new JSONObject();
				tmpPlayer.put("playerName", key);
				
				if (c1 == 1){
					tmpPlayer.put("playerRole", "G");
				}else if (c1 == 2){
					tmpPlayer.put("playerRole", "D");
				}else{
					tmpPlayer.put("playerRole", "O");
				}
				
				team1.put(tmpPlayer);
			}else{
				c2++;
				
				JSONObject tmpPlayer = new JSONObject();
				tmpPlayer.put("playerName", key);
				
				if (c2 == 1){
					tmpPlayer.put("playerRole", "G");
				}else if (c2 == 2){
					tmpPlayer.put("playerRole", "D");
				}else{
					tmpPlayer.put("playerRole", "O");
				}
				
				team2.put(tmpPlayer);
			}
		}
		
		newSession.put("team1",team1);
		newSession.put("team2",team2);
		
		return newSession;
	}

}
