package com.gmail.egorovsonalexey.lifegame.core;

import java.io.*;

public class Item implements Serializable, Cloneable
{
	private int mAge;
	private boolean mIsDie;

	public Item() { }

	public Item(boolean isStarted)
	{
		if(isStarted) 
		{
			mAge = 1;
		}
	}

	public int getAge() { return mAge; }
	public void setAge(int age) { mAge = age; }

	public boolean getIsDie() { return mIsDie; }
	public void setIsDie(boolean isDie) { mIsDie = isDie; }

	public void incrementAge() { mAge++; }

	@Override
	public Item clone()
	{
		Item item = new Item();
		item.setAge(mAge);
		item.setIsDie(mIsDie);
		return item;
	}

	@Override
	public String toString()
	{
		return String.format("Age - %d, is die - %s", mAge, mIsDie);
	}	
}