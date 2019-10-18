package com.gmail.egorovsonalexey.lifegame.core;

import java.util.ArrayList;

public class RectangleGameField extends GameField
{
	protected int mGameWidth;
	protected int mGameHeight;

	public RectangleGameField() { }

	public RectangleGameField(int width, int height)
	{
		mGameWidth = width;
		mGameHeight = height;
		mCells = new Cell[width * height];
		createField();
	}

	public int getGameWidth() { return mGameWidth; }
	public void setGameWidth(int width) { mGameWidth = width; }

	public int getGameHeight() { return mGameHeight; }
	public void setGameHeiht(int height) { mGameHeight = height; }

	@Override
	public int getMaxNeighbours()
	{
		return 0;
	}

	@Override
	public Cell[] getCells()
	{
		if(mCells == null)
		{
			mCells = new Cell[mGameWidth * mGameHeight];
		}
		return mCells;
	}

	protected int incrementCoordinate(int coordinate, int size)
	{
		return (coordinate + 1) % size;
	}

	protected int decrementCoordinate(int coordinate, int size)
	{
		return (coordinate + size - 1) % size;
	}

	public String getFieldKind() 
	{
		return "Rectangle";
	}
}