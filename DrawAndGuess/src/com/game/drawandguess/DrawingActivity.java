package com.game.drawandguess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bolts.Task;

import com.game.drawandguess.classes.GameController;
import com.game.drawandguess.classes.GameRoom;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class DrawingActivity extends Activity implements OnClickListener {

    private AndroidDrawingScreen drawingScreen;
    private DrawingView drawView;
    private ImageButton newBtn, refreshBtn;
	private int teamNumber;
	private ImageView roleImg;
	private TextView tvTimeCounter;
	private LinearLayout drawerPanel;
	private ImageButton btnSendPicture;
	private TextView tvQuestion;
	private AsyncTask<Integer, Integer, Boolean> drawingAsyncTimer;
	private AsyncTask<Integer, Integer, Boolean> guessAsyncTimer;
	private LinearLayout guessPanel;
	private ImageButton btnSendAnswer;
	private EditText answerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        
        drawView = (DrawingView)findViewById(R.id.drawing);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        refreshBtn = (ImageButton)findViewById(R.id.refresh_btn);
        roleImg = (ImageView) findViewById(R.id.ivRoleImg);
        tvTimeCounter = (TextView) findViewById(R.id.tvTimeCounter);
        drawerPanel = (LinearLayout) findViewById(R.id.drawerPanel);
        btnSendPicture = (ImageButton) findViewById(R.id.btnSendPicture);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        guessPanel = (LinearLayout) findViewById(R.id.guessPanel);
        btnSendAnswer = (ImageButton) findViewById(R.id.btnSendAnswer);
        answerInput = (EditText) findViewById(R.id.etAnswerInput);
        
        newBtn.setOnClickListener(this);
        btnSendPicture.setOnClickListener(this);
        btnSendAnswer.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
        
        drawingScreen = AndroidDrawingScreen.getInstance();
        drawingScreen.init(getApplicationContext());

        ParseQuery<ParseObject> querySession = ParseQuery.getQuery("GameSession");
        
        try {
			ParseObject currentSession = querySession.get(GameController.getInstance().getCurrentGameSessionId());
			setViewToRole(currentSession);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
    }    

    private void setViewToRole(ParseObject currentSession) throws JSONException, ParseException {
    	GameRoom currentGameRoom = GameController.getInstance().getCurrentGameRoom();
		JSONObject playerToTeam = currentGameRoom.getPlayerToTeam();
		
    	JSONArray myTeam = null;
		String playerRole = null;
		int playerNumberOnList = 0;
		
		teamNumber = playerToTeam.getInt(GameController.getInstance().getPlayerName());
		JSONArray queue = currentSession.getJSONArray("queue");
		
		//download screen
		ParseFile screenFile = currentSession.getParseFile("screen");
		
		//download my team
		if (teamNumber == 1){
			myTeam = currentSession.getJSONArray("team1");
		}else{
			myTeam = currentSession.getJSONArray("team2");
		}
		
		//find my role and number in team
		for (int i=0; i<myTeam.length(); i++){
			JSONObject player = myTeam.getJSONObject(i);
			
			if (player.getString("playerName").contains(GameController.getInstance().getPlayerName())){
				playerRole = player.getString("playerRole");
				playerNumberOnList = i;
			}
		}
		
		//update screen image
		byte[] screenByteArray = screenFile.getData();	
		//AndroidDrawingScreen.getInstance().createScreen(drawView);
		AndroidDrawingScreen.getInstance().updateScreen(drawView, screenByteArray);
		
    	int round = currentSession.getInt("round");
		
		//gra team1
		if ((round%2) == 1){
			if(teamNumber == 2){
				if (currentSession.getString("state").contains("J")){
					playerRole = "J";
				}else{
					playerRole = "O";
				}	
			}
			//ja jestem w 1
			else{
				//gram ale teraz jest ocenianie odp
				if (currentSession.getString("state").contains("J")){
					playerRole = "O";
				}
				//gram i jestem zgadujacym - czy moja kolej? jak nie to obserwuje
				else if (playerRole.contains("G")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue != playerNumberOnList){
							playerRole = "O";
						}
					}
				}
				//jestem rysujacym ale juz nie moja kolej
				else if (playerRole.contains("D")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue != playerNumberOnList){
							playerRole = "O";
						}
					}
				}
				//jestem obserwujacym ale jak teraz ja kolejce to znaczy ze mam rysowac
				else if (playerRole.contains("O")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue == playerNumberOnList){
							playerRole = "D";
						}
					}
				}
			}
		}else{
			if(teamNumber == 1){
				if (currentSession.getString("state").contains("J")){
					playerRole = "J";
				}else{
					playerRole = "O";
				}	
			}else{
				if (currentSession.getString("state").contains("J")){
					playerRole = "O";
				}
				else if (playerRole.contains("G")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue != playerNumberOnList){
							playerRole = "O";
						}
					}
				}else if (playerRole.contains("D")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue != playerNumberOnList){
							playerRole = "O";
						}
					}
				}else if (playerRole.contains("O")){
					if (!queue.isNull(0)){
						int currenInQueue = queue.getInt(0);
						if (currenInQueue == playerNumberOnList){
							playerRole = "D";
						}
					}
				}
			}
		}
		
		//manipulate view
		
    	if (playerRole.contains("D")){
    		roleImg.setImageResource(R.drawable.brush_60);
    		tvQuestion.setText(currentSession.getString("question"));
    		
    		guessPanel.setVisibility(View.GONE);
    		drawerPanel.setVisibility(View.VISIBLE);
    		
    		
			int drawingTime = currentSession.getInt("drawTime");
			
			tvTimeCounter.setVisibility(View.VISIBLE);
			tvTimeCounter.setText(Integer.toString(drawingTime));
			
			drawingAsyncTimer = new DrawingCounterTask().execute(drawingTime);
			
			
		} else if (playerRole.contains("G")){
			roleImg.setImageResource(R.drawable.cartoon_60_guess);
			
			guessPanel.setVisibility(View.VISIBLE);
			drawerPanel.setVisibility(View.GONE);
		
			tvTimeCounter.setVisibility(View.VISIBLE);
			
			guessAsyncTimer = new GuessCounterTask().execute(GameController.getInstance().guessTime);
			
		} else if (playerRole.contains("O")){
			roleImg.setImageResource(R.drawable.brush_60_binoculars);
			
			guessPanel.setVisibility(View.GONE);
			drawerPanel.setVisibility(View.GONE);
			
			tvTimeCounter.setVisibility(View.INVISIBLE);

		}
		
    	currentGameRoom = null;
    	currentSession = null;
	}

    private class DrawingCounterTask extends AsyncTask<Integer, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Integer... params) {
			Integer time = params[0];

			for (int i=time; i>=0; i--){
				publishProgress(i);
				SystemClock.sleep(1000);
			}
			
			
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			tvTimeCounter.setText(Integer.toString(values[0]));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			btnSendPicture.performClick();
		}
    	
    }
    
    private class GuessCounterTask extends AsyncTask<Integer, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Integer... params) {
			Integer time = params[0];

			for (int i=time; i>=0; i--){
				publishProgress(i);
				SystemClock.sleep(1000);
			}
			
			
			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			tvTimeCounter.setText(Integer.toString(values[0]));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			btnSendAnswer.performClick();
		}
    	
    }

	@Override
    public void onClick(View view){
        //respond to clicks
        if(view.getId() == R.id.refresh_btn){ ///* updateScreen
            byte[] imgByte = drawingScreen.getPictureByteArray(drawView);         
            final ParseFile fileScreen = new ParseFile(imgByte);
            
            ParseQuery<ParseObject> updateGameQuery = ParseQuery.getQuery("GameSession");
            
            updateGameQuery.getInBackground(GameController.getInstance().getCurrentGameSessionId(), new GetCallback<ParseObject>() {
				
				@Override
				public void done(ParseObject session, ParseException ex) {
					if (ex == null){
						session.put("screen", fileScreen);
						session.put("lastEditor", "PLAYER");
						
						JSONArray queueArray = session.getJSONArray("queue");
						JSONArray newQueue = new JSONArray();
						
						for (int i=1;i<queueArray.length();i++){
							try {
								newQueue.put(queueArray.getInt(i));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						if (newQueue.length() == 0){
							session.put("state", "J");
						}
						
						session.put("queue", newQueue);
						
						session.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException ex2) {
								if (ex2 != null){
									Log.e("DAG", ex2.getMessage());
								}else{
									//TODO:progress bar dismiss
								}
								
							}
						});
					}else{
						Log.e("DAG", ex.getMessage());
					}
					
				}
			});
            
//            Log.i("test", "imgByte: " + imgByte);
//            drawingScreen.updateScreen(drawView, imgByte);
        }
        else if(view.getId()==R.id.new_btn){
            //new button
            //drawingMgr.createScreen();
            drawingScreen.createScreen(drawView);
        }else if (view.getId() == R.id.btnSendPicture){
        	 
        	 //cancel the timer task
        	 Status status = drawingAsyncTimer.getStatus();
        	 if (status == AsyncTask.Status.RUNNING){
        		 drawingAsyncTimer.cancel(true);
        	 }
        	
        	 byte[] imgByte = drawingScreen.getPictureByteArray(drawView);         
             final ParseFile fileScreen = new ParseFile(imgByte);
             
             ParseQuery<ParseObject> updateGameQuery = ParseQuery.getQuery("GameSession");
             updateGameQuery.getInBackground(GameController.getInstance().getCurrentGameSessionId(), new GetCallback<ParseObject>() {
 				
 				@Override
 				public void done(ParseObject session, ParseException ex) {
 					if (ex == null){
 						session.put("screen", fileScreen);
 						session.put("lastEditor", "PLAYER");
 						
 						JSONArray queueArray = session.getJSONArray("queue");
 						JSONArray newQueue = new JSONArray();
 						
 						for (int i=1;i<queueArray.length();i++){
 							try {
 								newQueue.put(queueArray.getInt(i));
 							} catch (JSONException e) {
 								e.printStackTrace();
 							}
 						}
 						
 						if (newQueue.length() == 0){
 							session.put("state", "J");
 						}
 						
 						session.put("queue", newQueue);
 						
 						session.saveInBackground(new SaveCallback() {
 							
 							@Override
 							public void done(ParseException ex2) {
 								if (ex2 != null){
 									Log.e("DAG", ex2.getMessage());
 								}else{
 									//TODO:progress bar dismiss
 								}
 								
 							}
 						});
 					}else{
 						Log.e("DAG", ex.getMessage());
 					}
 					
 				}
 			});
        }else if (view.getId() == R.id.btnSendAnswer){
			Status status = guessAsyncTimer.getStatus();
			
			if (status == AsyncTask.Status.RUNNING) {
				guessAsyncTimer.cancel(true);
			}
			
			final String answer = answerInput.getText().toString();
        	
        	ParseQuery<ParseObject> updateGameQuery = ParseQuery.getQuery("GameSession");
            updateGameQuery.getInBackground(GameController.getInstance().getCurrentGameSessionId(), new GetCallback<ParseObject>() {

				@Override
				public void done(ParseObject currentSession, ParseException ex) {
					if (ex == null){
						currentSession.put("answer", answer);
						currentSession.put("lastEditor", "PLAYER");
 						
 						JSONArray queueArray = currentSession.getJSONArray("queue");
 						JSONArray newQueue = new JSONArray();
 						
 						for (int i=1;i<queueArray.length();i++){
 							try {
 								newQueue.put(queueArray.getInt(i));
 							} catch (JSONException e) {
 								e.printStackTrace();
 							}
 						}
 						
 						if (newQueue.length() == 0){
 							currentSession.put("state", "J");
 						}
 						
 						currentSession.put("queue", newQueue);
 						
 						currentSession.saveInBackground(new SaveCallback() {
 							
 							@Override
 							public void done(ParseException ex2) {
 								if (ex2 != null){
 									Log.e("DAG", ex2.getMessage());
 								}else{
 									//TODO:progress bar dismiss
 								}
 								
 							}
 						});
 						
						try {
							currentSession.save();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
            	
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawing, menu);
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
        }else if (id == R.id.action_judge){
        	ParseQuery<ParseObject> querySession = ParseQuery.getQuery("GameSession");
        	ParseObject currentSession;
        	
			try {
				currentSession = querySession.get(GameController.getInstance().getCurrentGameSessionId());
				
				if (currentSession.getString("state").contains("J")){
					//obsluga zgadywania + dodac pobranie odpowiedzi do sprawdzenia
					currentSession.put("judge", "T");
					currentSession.put("state", "P");
					currentSession.save();
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				currentSession = null;
			}
        	
        	
        	return true;
        }
        return super.onOptionsItemSelected(item);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		Bundle extras = intent.getExtras();
		
		String action = extras.getString("pushNotification",null);
		
		if (action.contains("refreshSession")){
        	ParseQuery<ParseObject> querySession = ParseQuery.getQuery("GameSession");
        	ParseObject currentSession;
        	
			try {
				currentSession = querySession.get(GameController.getInstance().getCurrentGameSessionId());
				setViewToRole(currentSession);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}else if (action.contains("nextRound")){
			drawingScreen.createScreen(drawView);
		}
		
	}
}

