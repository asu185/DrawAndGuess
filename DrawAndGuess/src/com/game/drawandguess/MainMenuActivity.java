package com.game.drawandguess;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.game.drawandguess.classes.GameController;
import com.game.drawandguess.classes.GameRoom;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MainMenuActivity extends Activity {

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GameController.getInstance().init(getApplicationContext(), "myName");
		
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
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_menu,
					container, false);
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);

			final EditText newRoomName = (EditText) view.findViewById(R.id.roomNameInput);
			
			Button addRoomBtn = (Button) view.findViewById(R.id.addRoomBtn);
			addRoomBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final GameRoom newRoom = new GameRoom(newRoomName.getText().toString(), GameController.getInstance().getPlayerName());
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
	}
	
	
	
}
