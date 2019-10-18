package com.gmail.egorovsonalexey.lifegame.core;

import java.util.ArrayList;

public class HexagonCellTorGameField extends RectangleGameField
{
	public HexagonCellTorGameField() { }
	
	public HexagonCellTorGameField(int width, int height)
	{
		super(width, height);
	}

	@Override
	public int getMaxNeighbours()
	{
		return 6;
	}

	@Override
	public void createField()
	{
		super.createField();
		
		for (int i = 0; i < mCells.length; i++)
		{
			int x = i % mGameWidth;
			int y = i / mGameWidth;

			int incrX = incrementCoordinate(x, mGameWidth);
			int decrX = decrementCoordinate(x, mGameWidth);
			int incrY = incrementCoordinate(y, mGameHeight);
			int decrY = decrementCoordinate(y, mGameHeight);

			ArrayList<Cell> neighbours = get(i).getNeighbours();
			neighbours.add(get(y * mGameWidth + incrX));
			neighbours.add(get(y * mGameWidth + decrX));
			neighbours.add(get(incrY * mGameWidth + x));
			neighbours.add(get(decrY * mGameWidth + x));

			if(y % 2 == 0)
			{
				neighbours.add(get(incrY * mGameWidth + incrX));
				neighbours.add(get(decrY * mGameWidth + incrX));
			}
			else
			{
				neighbours.add(get(incrY * mGameWidth + decrX));
				neighbours.add(get(decrY * mGameWidth + decrX));
			}
		}
	}

	@Override
	public String getFieldKind()
	{
		return "Hexagon Cell Tor";
	}
}