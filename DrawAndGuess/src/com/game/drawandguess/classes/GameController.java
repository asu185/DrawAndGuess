package com.game.drawandguess.classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.util.Log;

import com.game.drawandguess.interaces.GameConnection;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class GameController {
	
	public static final String GAME_ROOM_TABLE_NAME = "GameRoom";
	private static GameController instance;
	private String playerName;
	private GameConnection connection;
	private GameRoom currentGameRoom;
	
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

}
