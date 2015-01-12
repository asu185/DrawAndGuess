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
import android.widget.EditText;


public class MainActivity extends Activity {

    private EditText nameInput;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GameController.getInstance().init(getApplicationContext(), "default");
        
        Button btn = (Button) findViewById(R.id.btnStartGame);
        nameInput = (EditText) findViewById(R.id.etNameInput);
        
        SharedPreferences appData = getApplicationContext().getSharedPreferences(getString(R.string.applicationSharedPreferencesKey), Context.MODE_PRIVATE);
        String playerName = appData.getString("playerName", "");

        nameInput.setText(playerName);

		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("allNotify");
        
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
				
				String name = nameInput.getText().toString();
				
				SharedPreferences appData = getApplicationContext().getSharedPreferences(getString(R.string.applicationSharedPreferencesKey), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = appData.edit();
				
				if (name.isEmpty()) {
					editor.putString("playerName", "default");
					editor.commit();
				}else{
					editor.putString("playerName", name);
					editor.commit();
				}
				
				GameController.getInstance().setPlayerName(name);
				
				startActivity(intent);
				
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
