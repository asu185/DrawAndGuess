package com.game.drawandguess;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;

import com.game.drawandguess.interaces.DrawingScreen;

/**
 * Created by wensnoopy on 15/1/9.
 */
public class AndroidDrawingScreen implements DrawingScreen {

    private static AndroidDrawingScreen instance;
    private Context context;

    public void init(Context ctx){
           this.context = ctx;
    }

    public static AndroidDrawingScreen getInstance(){
        if (instance == null){
            instance = new AndroidDrawingScreen();
            return instance;
        }else{
            return instance;
        }
    }

    @Override
    public void createScreen(DrawingView drawView) {
        drawView.startNew();
        drawView.setBackgroundColor(0xffffffff);
    }

    @Override
    public void updateScreen(DrawingView drawView, final byte[] imgByte) {
        @SuppressWarnings("deprecation")
		Drawable image =  new BitmapDrawable(BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length));
        
        drawView.setBackgroundColor(Color.WHITE);
        drawView.setBackground(image);
        drawView.destroyDrawingCache();
    }

    @Override
    public byte[] getPictureByteArray(DrawingView drawView) {
        drawView.setDrawingCacheEnabled(true);
        
        Bitmap bitmapTmp = drawView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        bitmapTmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        
        byte[] byteArray = stream.toByteArray();
        
        return byteArray;
    }
}
