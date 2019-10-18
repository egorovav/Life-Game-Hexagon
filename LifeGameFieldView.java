package com.gmail.egorovsonalexey.hexlifegame;

import android.graphics.*;
import androidx.core.view.ViewCompat;
import android.view.*;
import android.util.*;
import android.content.*;

import com.gmail.egorovsonalexey.lifegame.core.*;

public abstract class LifeGameFieldView extends View {

    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    // The current viewport. This rectangle represents the currently visible chart domain and range.
    protected RectF mCurrentViewport = new RectF(0, 0, 0, 0);
    protected Paint mPaint = new Paint();
    protected Paint mGridPaint = new Paint();

    protected float mScaleFactor = 1.f;

    protected float mItemMargin = 1;
    protected float mItemSize = 20;
    protected float mCellWidth = mItemSize + 2 * mItemMargin;
    protected float mCellSize;
    protected float mCellHeight;
    protected RectangleGameField mGameField;

    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;

    public LifeGameFieldView(Context context, AttributeSet attributes){
        super(context, attributes);

        init(context);
    }

    public LifeGameFieldView(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        mPaint.setColor(Color.GREEN);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new TouchListener());

        mGridPaint.setColor(Color.LTGRAY);
        mGridPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mGridPaint.setPathEffect(new DashPathEffect(new float[] {mCellSize, 2 * mCellSize}, 0));
        mGridPaint.setStrokeWidth(1);
    }

    void setViewport(RectF viewport) {
        mCurrentViewport = viewport;
        mIsViewportShifted = true;
    }

    RectF getViewport() {
        return mCurrentViewport;
    }

    void setScale(float scale) {
        mScaleFactor = scale;
    }

    float getScale() {
        return mScaleFactor;
    }

    void setGameField(RectangleGameField gameField) {
        mGameField = gameField;
        setItemSize(mItemSize);
    }

    protected void offsetViewport(float offsetX, float offsetY) {
        mCurrentViewport.left += offsetX;
        mCurrentViewport.right += offsetX;
        mCurrentViewport.top += offsetY;
        mCurrentViewport.bottom += offsetY;

        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {

        mCurrentViewport.right = mCurrentViewport.left + w;
        mCurrentViewport.bottom = mCurrentViewport.top + h;
    }

    protected void drawItems(Canvas canvas) {
        for(int i = 0; i < mGameField.getCells().length; i++) {
            if(mGameField.get(i).getItem() != null) {
                PointF cellLocation = getCellLocationByIndex(i);
                float x = cellLocation.x - mCurrentViewport.left;
                float y = cellLocation.y - mCurrentViewport.top;
                float r = (mItemSize / 2 - mItemMargin) * mScaleFactor;
                // Sometimes throw null reference exception here on getAge(). Item is null.
                int age = mGameField.get(i).getItem().getAge();
                int green = age > 25 ? 0 : 255 - age * 10;
                int color = Color.argb(255, 0, green, 0);
                if(mScaleFactor > 0.3) {
                    mPaint.setShader(new RadialGradient(x - r * 0.2f, y - r * 0.2f, r, Color.WHITE, color, Shader.TileMode.CLAMP));
                }
                canvas.drawCircle(x, y, r, mPaint);
            }
        }
    }

    private boolean mIsViewportShifted = false;
    @Override
    protected void onDraw(Canvas canvas) {
        if(!mIsViewportShifted) {
            offsetViewport((mCellWidth * mGameField.getGameWidth() - getWidth()) / 2,
                    (mCellHeight * mGameField.getGameHeight() - getHeight()) / 2);
            mIsViewportShifted = true;
        }

        //canvas.save();
        //canvas.scale(mScaleFactor, mScaleFactor, mScaleFocusX, mScaleFocusY);
        setItemSize(mItemSize * mScaleFactor);
        if(mScaleFactor > 0.2) {
            drawGrid(canvas);
        }
        //drawCellIndexes(canvas);
        try {
            drawItems(canvas);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        //canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean retVal = mScaleDetector.onTouchEvent(ev);
        retVal = mGestureDetector.onTouchEvent(ev) || retVal;

        return retVal || super.onTouchEvent(ev);
    }

    protected abstract PointF getCellLocationByIndex(int index);
    protected abstract int getCellIndexByLocation(PointF location);
    protected abstract void drawGrid(Canvas canvas);
    protected abstract void setItemSize(float itemSize);

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {

            return super.onScaleBegin(scaleGestureDetector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();

            float newScaleFactor = mScaleFactor * scaleFactor;
            if(newScaleFactor > 5.0f || newScaleFactor < 0.1f) {
                return true;
            }

            mScaleFactor = newScaleFactor;

            // don't let the object get too small or too large
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            float contentX = focusX + mCurrentViewport.left;
            float contentY = focusY + mCurrentViewport.top;
            float offsetX = (scaleFactor - 1f) * contentX;
            float offsetY = (scaleFactor - 1f) * contentY;

            offsetViewport(offsetX, offsetY);

            invalidate();

            return true;
        }
    }

    class TouchListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if(mScaleDetector.isInProgress()){
                return true;
            }

            offsetViewport(distanceX, distanceY);

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            float x = event.getX() + mCurrentViewport.left;
            float y = event.getY() + mCurrentViewport.top;
            PointF touchLocation = new PointF(x, y);
            int cellIndex = getCellIndexByLocation(touchLocation);
            if (cellIndex >= 0 && cellIndex < mGameField.getCells().length) {
                if(mGameField.get(cellIndex).getItem() == null) {
                    mGameField.get(cellIndex).fill(true);
                }
                else {
                    mGameField.get(cellIndex).clear();
                }
                invalidate();
            }
            return true;
        }
    }
}
