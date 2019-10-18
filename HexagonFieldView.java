package com.gmail.egorovsonalexey.hexlifegame;

import android.content.*;
import android.graphics.*;
import android.util.*;
//import android.support.v4.view.*;

public class HexagonFieldView extends LifeGameFieldView {

    private float mCosOfPiDiv6 = (float)Math.cos(Math.PI / 6);
    private Paint mTextPaint;

    public HexagonFieldView(Context context)
    {
        super(context);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GRAY);
    }

    public HexagonFieldView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GRAY);
    }

    @Override
    protected void setItemSize(float itemSize) {
        mCellWidth = itemSize + 2 * mItemMargin;
        mCellSize = mCellWidth / (2 * mCosOfPiDiv6);
        mCellHeight = 3 * mCellSize / 2;
        mGridPaint.setPathEffect(new DashPathEffect(new float[] {mCellSize, 2 * mCellSize}, 0));
    }

    private void addLine(float x1, float y1, float x2, float y2, Path p) {
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
    }

    @Override
    protected void drawGrid(Canvas canvas) {
        //setItemSize(mItemSize * mScaleFactor);
        Path path = new Path();

        float x0 = mCurrentViewport.left > 0 ? -mCurrentViewport.left % mCellWidth : -mCurrentViewport.left;
        float y0 = mCurrentViewport.top > 0 ? -mCurrentViewport.top % (mCellHeight * 2) : -mCurrentViewport.top;
        int cellOffsetX = (int)(Math.max(0, mCurrentViewport.left) / mCellWidth);
        int cellOffsetY = (int)(Math.max(0, mCurrentViewport.top) / mCellHeight);

        int gameWidth = Math.min(mGameField.getGameWidth() - cellOffsetX, (int)(mCurrentViewport.width() / mCellWidth));
        int gameHeight = Math.min(mGameField.getGameHeight() - cellOffsetY, (int)(mCurrentViewport.height() / mCellHeight));

        float gameFieldWidth = gameWidth * mCellWidth;
        float gameFieldHeight = gameHeight * mCellHeight;

        for(int i = 0; i <= 2 * gameWidth; i++) {
            float x1 = i * mCellWidth / 2 + x0;
            float y1 = y0;
            float x2 = x1;
            float y2 = gameFieldHeight + y0;

            if(i % 2 == 0)
                y1 += mCellHeight;

            addLine(x1, y1, x2, y2, path);
        }

        float h = gameFieldWidth / (mCosOfPiDiv6 * 2);
        int maxIndex = (int)(3 * gameHeight / 2 + gameWidth);
        for(int i = 0; i <= maxIndex; i++) {
            float y = i * mCellSize + mCellSize / 2;
            float x1 = Math.max(0, 2 * (y - gameFieldHeight) * mCosOfPiDiv6) + x0;
            float y1 = Math.min(y, gameFieldHeight) + y0;
            float x2 = Math.min(2 * y * mCosOfPiDiv6, gameFieldWidth) + x0;
            float y2 = Math.max(0, y - h) + y0;

            if(i < 3 * gameHeight / 2) {
                if(i % 3 == 2) {
                    x1 += mCellWidth;
                    y1 -= mCellSize;
                }

                if(i % 3 == 0) {
                    x1 += mCellWidth / 2;
                    y1 -= mCellSize / 2;
                }
            }

            addLine(x1, y1, x2, y2, path);

            float center = gameFieldWidth / 2 + x0;
            float x1s = symmetricTransformX(x1, center);
            float x2s = symmetricTransformX(x2, center);

            addLine(x1s, y1, x2s, y2, path);
        }

        canvas.drawPath(path, mGridPaint);
    }

    private void drawCellIndexes(Canvas canvas) {
        for(int i = 0; i < mGameField.getCells().length; i++) {
            PointF cellLocation = getCellLocationByIndex(i);
            float x = cellLocation.x - mCurrentViewport.left;
            float y = cellLocation.y - mCurrentViewport.top;
            canvas.drawText(Integer.toString(i), x, y, mTextPaint);
        }
    }

    private float symmetricTransformX(float x, float center) {
        return 2 * center - x;
    }

    @Override
    protected PointF getCellLocationByIndex(int index) {

        int cellX = index % mGameField.getGameWidth();
        int cellY = index / mGameField.getGameWidth();

        float x = cellX * mCellWidth + mCellWidth / 2;
        float y = cellY * mCellHeight + mCellSize / 2;

        if(cellY % 2 == 0) {
            x += mCellWidth / 2;
        }

        return new PointF(x, y);
    }

    @Override
    protected int getCellIndexByLocation(PointF location) {
        for(int i = 0; i < mGameField.getCells().length; i++) {
            PointF cellLocation = getCellLocationByIndex(i);
            float dist = PointF.length(location.x - cellLocation.x, location.y - cellLocation.y);
            if(dist <= mCellWidth / 2) {
                return i;
            }
        }

        return -1;
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        drawCellIndexes(canvas);
//    }
}
