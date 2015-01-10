package com.game.drawandguess.interaces;

import com.game.drawandguess.DrawingView;

/**
 * Created by wensnoopy on 15/1/7.
 */
public interface DrawingScreen {

    public void createScreen(DrawingView view);
    public void updateScreen(DrawingView view, byte[] imgByte);
    public byte[] getPictureByteArray(DrawingView view);

}
