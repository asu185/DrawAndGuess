package com.game.drawandguess.classes;

import org.json.JSONException;
import org.json.JSONObject;

import com.game.drawandguess.DrawingActivity;
import com.game.drawandguess.TeamSelectActivity;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

public class ParsePushDrawAndGuessReceiver extends ParsePushBroadcastReceiver {

	@Override
	protected Class<? extends Activity> getActivity(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		return super.getActivity(arg0, arg1);
	}

	@Override
	protected Bitmap getLargeIcon(Context context, Intent intent) {
		// TODO Auto-generated method stub
		return super.getLargeIcon(context, intent);
	}

	@Override
	protected Notification getNotification(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("mojeLogi", context.toString());
		Log.d("mojeLogi", intent.toString());
		
		return super.getNotification(context, intent);
	}

	@Override
	protected int getSmallIconId(Context context, Intent intent) {
		// TODO Auto-generated method stub
		return super.getSmallIconId(context, intent);
	}

	@Override
	protected void onPushDismiss(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onPushDismiss(context, intent);
	}

	@Override
	protected void onPushOpen(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		super.onPushOpen(arg0, arg1);
	}

	@Override
	protected void onPushReceive(Context ctx, Intent intent) {
		Log.d("mojeLogi", ctx.toString());
		Log.d("mojeLogi", intent.toString());

		Bundle extras = intent.getExtras();
		String message = extras.getString("com.parse.Data");
		
		JSONObject ob = null;
		JSONObject teams = null;
		
		try {
			ob = new JSONObject(message);
			
			String action = ob.getString("action");
			String sender = ob.getString("sender");
			
			if (!sender.contains(GameController.getInstance().getPlayerName())) {

				if (action.contains("refreshTeamList")) {

					Intent intent2open = new Intent(ctx,
							TeamSelectActivity.class);
					intent2open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					if (ob.has("teams")) {
						teams = ob.getJSONObject("teams");
						intent2open.putExtra("teams", teams.toString());
					}
					intent2open.putExtra("pushNotification", action);
					ctx.startActivity(intent2open);

				}else if (action.contains("refreshSession")){
					Intent intent2open = new Intent(ctx,
							DrawingActivity.class);
					
					intent2open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					intent2open.putExtra("pushNotification", action);
					ctx.startActivity(intent2open);
				}else if (action.contains("sessionCreated")){
					Intent intent2open = new Intent(ctx,
							TeamSelectActivity.class);
					
					intent2open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					
					intent2open.putExtra("pushNotification", action);
					intent2open.putExtra("sessionId", ob.getString("sessionId"));
					
					ctx.startActivity(intent2open);
				}
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		super.onPushReceive(ctx, intent);
	}

}
