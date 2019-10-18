package com.gmail.egorovsonalexey.lifegame.core;

public class GameCreator
{
	public static LifeGame RectangleTorGame(int width, int height, int itemsCount)
	{
		SquareCellTorGameField sctgf = new SquareCellTorGameField(width, height);
		return new LifeGame(sctgf, itemsCount);
	}

	public static LifeGame HexagonalTorGame(int width, int height, int itemsCount)
	{
		HexagonCellTorGameField hctgf = new HexagonCellTorGameField(width, height);
		return new LifeGame(hctgf, itemsCount);
	}
}