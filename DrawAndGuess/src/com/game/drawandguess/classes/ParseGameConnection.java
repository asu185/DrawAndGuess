package com.game.drawandguess.classes;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;

import com.game.drawandguess.interaces.GameConnection;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ParseGameConnection implements GameConnection {

	@Override
	public boolean createConnection(Context ctx) {
		//parse init
		Parse.enableLocalDatastore(ctx);
		Parse.initialize(ctx, "RdX8vekKxiz61K7Pwo8bchKxzIYUFrnfLYpyOyXV", "cuYsUoONHPdi8KhMIIpDKWD13jXJaPpkarDoXKPF");
		
		return true;
	}

	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getGameRooms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean joinToRoom(String roomId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createConnection() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
