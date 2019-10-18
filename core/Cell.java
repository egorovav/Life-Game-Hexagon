package com.gmail.egorovsonalexey.lifegame.core;

import java.util.ArrayList;
import java.io.*;

public class Cell implements Serializable
{

	private Item mItem;
	private ArrayList<Cell> mNeighbours = new ArrayList<>();

	public Cell() { }

	public Cell(Item item)
	{
		mItem = item;
	} 

	public Item getItem() { return mItem; }
	public void setItem(Item item) { mItem = item; }

	public ArrayList<Cell> getNeighbours() { return mNeighbours; }

	public void fill(boolean isStarted)
	{
		mItem = new Item(isStarted);
	}

	public void clear()
	{
		mItem = null;
	}

	@Override
	public String toString()
	{
		return String.format("Item - %s, neghbour count - %d", mItem, mNeighbours.size());
	}
}