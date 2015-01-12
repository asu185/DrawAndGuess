package com.game.drawandguess;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.drawandguess.classes.GameController;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class SessionSettingsActivity extends Activity {

	private NumberPicker npDrawing;
	private NumberPicker npRounds;
	private Button btnStartSession;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_settings);
		
		npDrawing = (NumberPicker) findViewById(R.id.npDrawing);
		npRounds = (NumberPicker) findViewById(R.id.npRounds);
		btnStartSession = (Button) findViewById(R.id.btnStartSessionWithSettings);
		
		npDrawing.setMinValue(20);
		npDrawing.setMaxValue(120);
		npDrawing.setWrapSelectorWheel(true);
		npDrawing.setOnValueChangedListener(new OnValueChangeListener(){

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				picker.setValue((newVal < oldVal)?oldVal-5:oldVal+5);
			}
			
		});
		
		npRounds.setMinValue(2);
		npRounds.setMaxValue(20);
		npRounds.setWrapSelectorWheel(true);
		npRounds.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				picker.setValue((newVal < oldVal)?oldVal-2:oldVal+2);
			}
		});
		
		btnStartSession.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int drawingTime = npDrawing.getValue();
				int rounds = npRounds.getValue();
				
				
				//TODO:move this code to settings screen
				try {
					final ParseObject createGameSession = GameController.getInstance().createGameSession(GameController.getInstance().getCurrentGameRoom(), rounds, drawingTime);
					
					createGameSession.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							if (arg0!=null){
								Log.e("mojeLogi", arg0.getMessage());
							}else{
								String gameSessionId = createGameSession.getObjectId();
								GameController.getInstance().setCurrentGameSessionId(gameSessionId);
								
								Intent drawingGame = new Intent(getApplicationContext(), DrawingActivity.class);
								
								JSONObject msg = new JSONObject();
								
								//notify players
								try {
									msg.put("message", "Admin started the game");
									msg.put("sessionId", gameSessionId);
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
								GameController.getInstance().sendNotificationToMyRoomInBackground("sessionCreated", msg, new SendCallback() {
									
									@Override
									public void done(ParseException ex) {
										if (ex == null){
											
										}else{
											Log.e("DAG", ex.getMessage());
										}
									}
								});
								
								startActivity(drawingGame);
							}
							
						}
					});
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.session_settings, menu);
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
}
