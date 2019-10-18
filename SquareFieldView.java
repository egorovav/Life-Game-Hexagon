package com.gmail.egorovsonalexey.hexlifegame;

import android.graphics.*;
import android.content.*;
import android.util.*;

public class SquareFieldView extends LifeGameFieldView {

    public SquareFieldView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public SquareFieldView(Context context) {
        super(context);
    }

    @Override
    protected void setItemSize(float itemSize) {
        mCellWidth = itemSize + 2 * mItemMargin;
        mCellSize = mCellWidth;
        mCellHeight = mCellWidth;
    }

    @Override
    protected PointF getCellLocationByIndex(int index) {
        int cellX = index % mGameField.getGameWidth();
        int cellY = index / mGameField.getGameWidth();
        float x = cellX * mCellSize + mCellSize / 2;
        float y = cellY * mCellSize + mCellSize / 2;

        return new PointF(x, y);
    }

    @Override
    protected int getCellIndexByLocation(PointF location) {
        int cellX = (int)Math.floor(location.x / mCellSize);
        int cellY = (int)Math.floor(location.y / mCellSize);
        return cellY * mGameField.getGameWidth() + cellX;
    }

    private void addLine(float x1, float y1, float x2, float y2, Path p) {
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
    }

    @Override
    protected void drawGrid(Canvas canvas) {
        //setItemSize(mItemSize * mScaleFactor);

        float x0 = mCurrentViewport.left > 0 ? -mCurrentViewport.left % mCellWidth : -mCurrentViewport.left;
        float y0 = mCurrentViewport.top > 0 ? -mCurrentViewport.top % mCellHeight : -mCurrentViewport.top;
        int cellOffsetX = (int)(Math.max(0, mCurrentViewport.left) / mCellWidth);
        int cellOffsetY = (int)(Math.max(0, mCurrentViewport.top) / mCellHeight);

        int gameWidth = Math.min(mGameField.getGameWidth() - cellOffsetX, (int)(mCurrentViewport.width() / mCellWidth));
        int gameHeight = Math.min(mGameField.getGameHeight() - cellOffsetY, (int)(mCurrentViewport.height() / mCellHeight));

        float gameFieldWidth = gameWidth * mCellWidth;
        float gameFieldHeight = gameHeight * mCellHeight;

        Path path = new Path();

        for(int i = 0; i <= gameWidth; i++) {
            float x1 = i * mCellWidth + x0;
            float y1 = y0;
            float x2 = x1;
            float y2 = gameFieldHeight + y0;

            addLine(x1, y1, x2, y2, path);
        }

        for(int i = 0; i <= gameHeight; i++) {
            float x1 = x0;
            float y1 = i * mCellHeight + y0;
            float x2 = gameFieldWidth + x0;
            float y2 = y1;

            addLine(x1, y1, x2, y2, path);
        }

        canvas.drawPath(path, mGridPaint);
    }
}
