package com.game.drawandguess;

import com.game.drawandguess.classes.GameController;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GameController.getInstance().init(getApplicationContext(), "default");
        
        Button btn = (Button) findViewById(R.id.addRoomBtn);
        
        //TODO: move it to the first screen 
        SharedPreferences appData = getApplicationContext().getSharedPreferences(getString(R.string.applicationSharedPreferencesKey), Context.MODE_PRIVATE);
        String playerName = appData.getString("playerName", "default");

		if (playerName.contains("default")) {
			SharedPreferences.Editor editor = appData.edit();
			editor.putString("playerName", "Marta");
			editor.commit();
		}
        
        GameController.getInstance().setPlayerName(playerName);

		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("allNotify");
        
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
				//Intent int2 = new Intent(getApplicationContext(), DrawingActivity.class);
				
				startActivity(intent);
				
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
