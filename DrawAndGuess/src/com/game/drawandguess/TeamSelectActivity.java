package com.game.drawandguess;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.drawandguess.classes.GameController;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class TeamSelectActivity extends Activity {

	private ListView team1ListView;
	private ListView team2ListView;
	private ArrayAdapter<String> team1Adapter;
	private ArrayAdapter<String> team2Adapter;
	private ImageButton switchTeamBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_select);
		
		team1ListView = (ListView) findViewById(R.id.team1ListView);
		team2ListView = (ListView) findViewById(R.id.team2ListView);
		
		team1Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		team2Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		refreshTeamList();
		
		team1ListView.setAdapter(team1Adapter);
		team2ListView.setAdapter(team2Adapter);
		
		switchTeamBtn = (ImageButton) findViewById(R.id.btnSwitchTeam);
		
		switchTeamBtn.setOnClickListener(new SwitchBtnOnClickListener());
		
		ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
		
		ParsePush push = new ParsePush();
		push.setChannel("test");
		push.setMessage("test");
		push.sendInBackground();
		
	}

	/*
	 * Refreshing team lists in adapters from GameRoom object
	 */
	private void refreshTeamList() {
		JSONObject playerToTeam = GameController.getInstance().getCurrentGameRoom().getPlayerToTeam();

		team1Adapter.clear();
		team2Adapter.clear();

		for (Iterator<String> it = playerToTeam.keys(); it.hasNext();) {
			String key = (String) it.next();

			try {
				if (playerToTeam.getInt(key) == 1) {
					team1Adapter.add(key);
				} else {
					team2Adapter.add(key);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.team_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public class SwitchBtnOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			String playerName = GameController.getInstance().getPlayerName();
			JSONObject playerToTeam = GameController.getInstance().getCurrentGameRoom().getPlayerToTeam();
			int amountOfPlayersInTeam = 0;
			boolean switchResult = false;
			
			final ProgressDialog prg = ProgressDialog.show(arg0.getContext(), "", "Changing team...");
			
			try {
				int currentTeam = playerToTeam.getInt(playerName);
				
				if (currentTeam == 1){
					amountOfPlayersInTeam = GameController.getInstance().getCurrentGameRoom().getAmountOfPlayersInTeam(2);
					if (amountOfPlayersInTeam < 5){
						GameController.getInstance().getCurrentGameRoom().assignPlayerToTeam(playerName, 2);
						switchResult = true;
					}
				}else{
					amountOfPlayersInTeam = GameController.getInstance().getCurrentGameRoom().getAmountOfPlayersInTeam(1);
					if (amountOfPlayersInTeam < 5){
						GameController.getInstance().getCurrentGameRoom().assignPlayerToTeam(playerName, 1);
						switchResult = true;
					}
				}
				
				if(switchResult){
					ParseQuery<ParseObject> query = ParseQuery.getQuery(GameController.GAME_ROOM_TABLE_NAME);

					query.getInBackground(GameController.getInstance().getCurrentGameRoom().getRoomId(), new GetCallback<ParseObject>() {
					  public void done(ParseObject gameRoom, ParseException e) {
					    if (e == null) {
					    	gameRoom.put("playersToTeam", GameController.getInstance().getCurrentGameRoom().getParseObject().get("playersToTeam"));
					    	
					    	gameRoom.saveInBackground(new SaveCallback() {
								
								@Override
								public void done(ParseException ex) {
									if (ex == null){
										refreshTeamList();
										team1Adapter.notifyDataSetChanged();
										team2Adapter.notifyDataSetChanged();
										
										prg.dismiss();
										//TODO: send push notify
									}else{
										Log.e("DAG", ex.getMessage());
										prg.dismiss();
									}
									
								}
							});	
					    }else{
					    	Log.e("DAG", e.getMessage());
					    	prg.dismiss();
					    }
					  }
					});
				}else{
					prg.dismiss();
				}
				
				
			} catch (JSONException e) {
				prg.dismiss();
				e.printStackTrace();
			}

		}

	}

}
