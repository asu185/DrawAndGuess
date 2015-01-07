package com.game.drawandguess;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.game.drawandguess.classes.GameController;
import com.game.drawandguess.classes.GameRoom;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.SaveCallback;

public class MainMenuActivity extends Activity {

	public static ArrayAdapter<GameRoom> roomListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("test");
		
		setContentView(R.layout.activity_main_menu);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
		}else if (id == R.id.room_refresh){
			
			//TODO: progress dialog
			roomListAdapter.clear();
			roomListAdapter.addAll(GameController.getInstance().getGameRooms());
			roomListAdapter.notifyDataSetChanged();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private ListView roomListView;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_menu,
					container, false);
			
			//room list prepare
			roomListAdapter = new ArrayAdapter<GameRoom>(getActivity().getApplicationContext(), R.layout.list_item_room, GameController.getInstance().getGameRooms()){

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					
					LayoutInflater inflater = getActivity().getLayoutInflater();
					
					View view = inflater.inflate(R.layout.list_item_room, null,true);
					
					GameRoom room = getItem(position);
					
					TextView roomName = (TextView) view.findViewById(R.id.tvRoomName);
					TextView playersAmount = (TextView) view.findViewById(R.id.tvPlayersAmount);
					
					roomName.setText(room.getRoomName());
					playersAmount.setText(Integer.toString(room.getPlayersAmount()));
					
					return view;
				}
			};
			
			roomListView = (ListView) rootView.findViewById(R.id.roomListView);
			roomListView.setAdapter(roomListAdapter);
			
			
			
			
			//action joining to room
			roomListView.setOnItemClickListener(new RoomListOnItemClickListener());

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);

			Button addRoomBtn = (Button) view.findViewById(R.id.addRoomBtn);
			addRoomBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final GameRoom newRoom = new GameRoom("nazwa pokoju", GameController.getInstance().getPlayerName());
					final ParseObject newGameObj = newRoom.getParseObject();
					
					newGameObj.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							if (arg0 == null){
								String objectId = newGameObj.getObjectId();
								newRoom.setRoomId(objectId);
								GameController.getInstance().setCurrentGameRoom(newRoom);
							}else{
								Log.e("DAG", arg0.getMessage());
							}
							
						}
					});
				}
			});
		}
		
		public class addRoomCallback extends SaveCallback{

			@Override
			public void done(ParseException arg0) {
				Log.d("DAG", "saved");
				
			}
			
		}
		
		/**
		 * Join to room from list action
		 */
		public class RoomListOnItemClickListener implements OnItemClickListener{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				final ProgressDialog prg = ProgressDialog.show(getActivity(), "", "Joining to the game...");
				
				final GameRoom selectedRoom = roomListAdapter.getItem(position);
				
				//assign player to free team
				boolean assignResult = selectedRoom.assignPlayerToTeam(GameController.getInstance().getPlayerName(), 1);
				
				final ParseObject parseObject = selectedRoom.getParseObject();
				
				//update game room
				if (assignResult){

					ParseQuery<ParseObject> query = ParseQuery.getQuery(GameController.GAME_ROOM_TABLE_NAME);

					query.getInBackground(selectedRoom.getRoomId(), new GetCallback<ParseObject>() {
					  public void done(ParseObject gameRoom, ParseException e) {
					    if (e == null) {
					    	gameRoom.put("playersAmount", parseObject.get("playersAmount"));
					    	gameRoom.put("playersToTeam", parseObject.get("playersToTeam"));
					    	
					    	gameRoom.saveInBackground(new SaveCallback() {
								
								@Override
								public void done(ParseException ex) {
									if (ex == null){
										prg.dismiss();
										GameController.getInstance().setCurrentGameRoom(selectedRoom);
										
										Intent teamSelectIntent = new Intent(getActivity().getApplicationContext(), TeamSelectActivity.class);
										startActivity(teamSelectIntent);
										//TODO: go to the next screen
										//send push notify
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
			}

			
		}
	}
	
	
	
}
