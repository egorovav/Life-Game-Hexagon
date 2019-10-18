package com.gmail.egorovsonalexey.lifegame.core;

import java.util.*;
import java.io.*;

public class GameField implements Serializable, List<Cell>
{
	protected Cell[] mCells = new Cell[0];

	public GameField()
	{
	}

	public GameField(Object data)
	{
		int count = (int)data;
		mCells = new Cell[count];
	}

	public Cell[] getCells() { return mCells; }

	public void createField()
	{
		for (int i = 0; i < mCells.length; i++)
		{
			mCells[i] = new Cell();
		}
	}

	public void clearCells()
	{
		for (Cell cell : mCells)
		{
			cell.clear();
		}
	}

	public String getFieldKind() 
	{
		return "Base";
	}

	public int getMaxNeighbours()
	{
		return 0;
	}

	@Override 
	public boolean add(Cell cell) { throw new UnsupportedOperationException(); }

	@Override 
	public void add(int index, Cell cell) { throw new UnsupportedOperationException(); }

	@Override
	public boolean addAll(Collection<? extends Cell> cells) { throw new UnsupportedOperationException(); }

	@Override
	public boolean addAll(int index, Collection<? extends Cell> cells) { throw new UnsupportedOperationException(); }

	@Override
	public void clear()
	{
		mCells = new Cell[mCells.length];
	}

	@Override
	public boolean contains(Object cell)
	{
		if (cell == null || !(cell instanceof Cell))
		{
			return false;
		}

		return Arrays.binarySearch(mCells, (Cell)cell, null) >= 0;
	}

	@Override
	public boolean containsAll(Collection<?> cells) { throw new UnsupportedOperationException(); }

	@Override
	public boolean equals(Object o) { throw new UnsupportedOperationException(); }

	@Override
	public Cell get(int index)
	{
		if (index < 0 || index > mCells.length)
		{
			throw new IndexOutOfBoundsException(String.format("Game field contains %d cells, passed index is %d.", mCells.length, index));
		}

		return mCells[index];
	}

	@Override
	public int hashCode()
	{
		return mCells.hashCode();
	}

	@Override
	public int indexOf(Object cell)
	{
		if(!(cell instanceof Cell))
		{
			throw new ClassCastException();
		}

		return Arrays.binarySearch(mCells, (Cell)cell, null);
	}

	@Override
	public boolean isEmpty()
	{
		return mCells.length == 0;
	}

	@Override
	public Iterator<Cell> iterator()
	{
		return Arrays.asList(mCells).iterator();
	}

	@Override
	public int lastIndexOf(Object o) { throw new UnsupportedOperationException(); }

	@Override
	public ListIterator<Cell> listIterator()
	{
		return Arrays.asList(mCells).listIterator();
	}

	@Override
	public ListIterator<Cell> listIterator(int index) { throw new UnsupportedOperationException(); }

	@Override
	public Cell remove(int index) { throw new UnsupportedOperationException(); }

	@Override
	public boolean remove(Object o) { throw new UnsupportedOperationException(); }

	@Override
	public boolean removeAll(Collection<?> cells) { throw new UnsupportedOperationException(); }

	@Override
	public boolean retainAll(Collection<?> cells) { throw new UnsupportedOperationException(); }

	@Override
	public Cell set(int index, Cell cell)
	{
		if (index < 0 || index >= mCells.length)
		{
			throw new IndexOutOfBoundsException(String.format("Game field contains %d cells, passed index is %d.", mCells.length, index));
		}

		Cell oldCell = get(index);
		mCells[index] = cell;
		return oldCell;
	}

	@Override
	public int size()
	{
		return mCells.length;
	}

	@Override
	public List<Cell> subList(int from, int to) { throw new UnsupportedOperationException(); }

	@Override
	public Object[] toArray()
	{
		return mCells;
	}

	@Override
	public <T> T[] toArray(T[] arr) { throw new UnsupportedOperationException(); }
}