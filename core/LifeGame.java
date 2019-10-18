package com.gmail.egorovsonalexey.lifegame.core;

import java.util.*;
import java.io.*;

public class LifeGame
{
	private GameField mGameField;
	private int mMaxAge;
	private boolean[] mSurviveNeighbours;
	private boolean[] mBornNeighbours;
	private int mStepCount;
	private HashMap<Integer, Integer> mStartPosition;

	private final String MAX_AGE_PREFIX = "#D MA ";
	private final String CELL_SHAPE_PREFIX = "#D CH ";
	private final String RULE_PREFIX = "#R ";
	private final String HEXAGON_CELL_CODE = "HEX";
	private final String RLE_LIVE_CELL_CODE = "o";
	private final String RLE_DEAD_CELL_CODE = "b";
	private final String RLE_LINE_TERMINATOR = "$";
	private final String RLE_FILE_TERMINATOR = "!";
	private final char SLASH = '/';


	public LifeGame() { }

	public LifeGame(GameField gameField)
	{
		mGameField = gameField;
		mSurviveNeighbours = new boolean[gameField.getMaxNeighbours() + 1];
		mBornNeighbours = new boolean[gameField.getMaxNeighbours() + 1];
	}

	public LifeGame(GameField gameField, int startItemCount)
	{
		this(gameField);
		if(startItemCount > 0)
		{
			randomFillGameField(startItemCount);
		}
	}

	public void saveToLife106Format(OutputStreamWriter writer) throws IOException
	{
		if (!(mGameField instanceof RectangleGameField))
		{
			throw new ClassCastException("Only RectangleGameField supported");
		}

		RectangleGameField gameField = (RectangleGameField)mGameField;

		writer.write("#Life 1.06\r\n");
		if (mGameField instanceof HexagonCellTorGameField)
		{
			writer.write(String.format("%s%s\r\n", CELL_SHAPE_PREFIX, HEXAGON_CELL_CODE));
		}
		writer.write(String.format("%s%d\r\n", MAX_AGE_PREFIX, mMaxAge));
		writer.write(RULE_PREFIX);
		for(int i = 0; i < mSurviveNeighbours.length; i++)
		{
			if(mSurviveNeighbours[i])
			{
				writer.write(Integer.toString(i));
			}
		}
		writer.write(SLASH);
		for(int i = 0; i < mBornNeighbours.length; i++)
		{
			if(mBornNeighbours[i])
			{
				writer.write(Integer.toString(i));
			}
		}
		writer.write(String.format("\r\n"));
		int width = gameField.getGameWidth();
		for(int x = 0; x < width; x++)
		{
			for(int y = 0; y < gameField.getGameHeight(); y++)
			{
				Cell cell = mGameField.get(y * width + x);
				if(cell.getItem() != null)
				{
					writer.write(String.format("%d %d\r\n", x, y));
				} 
			}
		}		
	}
	
	public void loadFromLife106Format(BufferedReader reader) throws NumberFormatException, IOException
	{
		if (!(mGameField instanceof RectangleGameField))
		{
			throw new ClassCastException("Only RectangleGameField supported");
		}

		RectangleGameField gameField = (RectangleGameField)mGameField;

		String line = reader.readLine();
		while(line.charAt(0) == '#')
		{
			if(line.startsWith(CELL_SHAPE_PREFIX))
			{
				String chStr = line.substring(CELL_SHAPE_PREFIX.length(), line.length());
				if(chStr.equals(HEXAGON_CELL_CODE) && !(mGameField instanceof HexagonCellTorGameField))
				{
					mGameField = new HexagonCellTorGameField(gameField.getGameWidth(), gameField.getGameHeight());
				}
			}
			
			if(line.startsWith(MAX_AGE_PREFIX))
			{
				String maStr = line.substring(MAX_AGE_PREFIX.length(), line.length());
				mMaxAge = Integer.parseInt(maStr);
			}

			if(line.startsWith(RULE_PREFIX))
			{
				String rStr = line.substring(RULE_PREFIX.length());
				int slashIndex = rStr.indexOf("/");
				for(int i = 0; i < slashIndex; i++)
				{
					int index = Integer.parseInt(rStr.substring(i, i + 1));
					mSurviveNeighbours[index] = true;
				}
				for(int i = slashIndex + 1; i < rStr.length(); i++)
				{
					int index = Integer.parseInt(rStr.substring(i, i + 1));
					mBornNeighbours[index] = true;
				}
			}

			line = reader.readLine();		
		}
		int width = gameField.getGameWidth();
		while (line != null)
		{
			String[] parts = line.split(" ");
			if(parts.length != 2)
			{
				throw new NumberFormatException("Invalid file format");
			}

			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);

			initialCell(y * width + x);	
			line = reader.readLine();		
		}
		mStepCount = 0;
	}

	private String appendStringToLine(String line, String str, OutputStreamWriter writer) throws IOException
	{
		if(line.length() + str.length() > 70)
		{
			writer.write(line + "\r\n");
			line = "";
		}
		line = line + str;
		if(str.equals(RLE_FILE_TERMINATOR))
		{
			writer.write(line + "\r\n");
		}
		return line;
	}

	public void saveToRleFormat(OutputStreamWriter writer) throws IOException
	{
		if (!(mGameField instanceof RectangleGameField))
		{
			throw new ClassCastException("Only RectangleGameField supported");
		}

		RectangleGameField gameField = (RectangleGameField)mGameField;
		int minX = gameField.getGameWidth();
		int minY = gameField.getGameHeight();
		int maxX = 0;
		int maxY = 0;
		for(int i = 0; i < gameField.size(); i++)
		{
			if(gameField.get(i).getItem() != null)
			{
				int x = i % gameField.getGameWidth();
				int y = i / gameField.getGameHeight();
				if (x > maxX) { maxX = x; }
				if (x < minX) { minX = x; }
				if (y > maxY) { maxY = y; }
				if (y < minY) { minY = y; }
			}
		}

		String bornString = "";
		for(int i = 0; i < mBornNeighbours.length; i++)
		{
			if(mBornNeighbours[i])
			{
				bornString += Integer.toString(i);
			}
		}

		String surviveString = "";
		for(int i = 0; i < mSurviveNeighbours.length; i++)
		{
			if(mSurviveNeighbours[i])
			{
				surviveString += Integer.toString(i);
			}
		}
		String ruleString = String.format("b%s/s%s", bornString, surviveString);
		if(gameField instanceof HexagonCellTorGameField)
		{
			ruleString += "H";
		}

		// start with even row
		if(minY % 2 == 1)
		{
			minY -=1;	
		}

		writer.write(String.format("x = %d, y = %d, rule = %s\r\n", maxX - minX + 1, maxY - minY + 1, ruleString));
		String line = "";
		int liveCount = 0;
		int deadCount = 0;
		for(int y = minY; y <= maxY; y++)
		{
			for(int x = minX; x <= maxX; x++)
			{
				if(gameField.get(y * gameField.getGameWidth() + x).getItem() != null)
				{
					if(liveCount == 0)
					{
						if(deadCount > 1)
						{
							line = appendStringToLine(line, Integer.toString(deadCount), writer);
						}
						if(deadCount > 0)
						{
							line = appendStringToLine(line, RLE_DEAD_CELL_CODE, writer);
						}
						deadCount = 0;					
					}
					liveCount++;		
				}
				else
				{
					if(deadCount == 0)
					{
						if(liveCount > 1)
						{
							line = appendStringToLine(line, Integer.toString(liveCount), writer);
						}
						if(liveCount > 0)
						{
							line = appendStringToLine(line, RLE_LIVE_CELL_CODE, writer);
						}
						liveCount = 0;
					}
					deadCount++;
				}				
			}
			if(liveCount > 1)
			{
				line = appendStringToLine(line, Integer.toString(liveCount), writer);
			}
			if(liveCount > 0)
			{
				line = appendStringToLine(line, RLE_LIVE_CELL_CODE, writer);
			}
			liveCount = 0;
			deadCount = 0;
			if(y < maxY)
			{
				line = appendStringToLine(line, RLE_LINE_TERMINATOR, writer);		
			}
		}
		
		appendStringToLine(line, RLE_FILE_TERMINATOR, writer);
	}

    	public void loadFromRleFormat(BufferedReader reader) throws IOException, PatternIsTooLargeException
	{
		if (!(mGameField instanceof RectangleGameField))
		{
			throw new ClassCastException("Only RectangleGameField supported");
		}

		RectangleGameField gameField = (RectangleGameField)mGameField;
		mBornNeighbours = new boolean[mBornNeighbours.length];
		mSurviveNeighbours = new boolean[mSurviveNeighbours.length];
		
		String line = reader.readLine();
		while(line.charAt(0) == '#')
		{
			line = reader.readLine();
		}
		
		//int slashIndex = line.indexOf("/");
		String numString = "";
		int width = -1;
		int height = -1;

		// i suppose there are two formats of rule string. first is {b.../s...}, where born condition
		// plased before slash; and second format is {.../...}, where born condition plased after slash.

		boolean isBornCondition = false;
		for(int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);
			if (Character.isDigit(c))
			{
				if(width < 0 || height < 0)
				{
					numString += c;
				}
				else
				{
					int n = Integer.parseInt(String.valueOf(c));
					if(isBornCondition)
					{
						mBornNeighbours[n] = true;
					}
					else
					{
						mSurviveNeighbours[n] = true;
					}
				} 
			}
			else
			{
				if(!numString.equals(""))
				{
					if(width < 0)
					{
						width = Integer.parseInt(numString);
						if (width >= gameField.getGameWidth()) 
						{
							throw new PatternIsTooLargeException(gameField.getGameWidth(), width);
						}
					}
					else if (height < 0)
					{
						height = Integer.parseInt(numString);
						if(height >= gameField.getGameHeight())
						{
							throw new PatternIsTooLargeException(gameField.getGameWidth(), width);
						}
					}
					
					numString = "";
				}

				if(c == 'H'&& !(mGameField instanceof HexagonCellTorGameField))
				{
					mGameField = new HexagonCellTorGameField(gameField.getGameWidth(), gameField.getGameHeight());
				}

				if(Character.toLowerCase(c) == 'b')
				{
					isBornCondition = true;
				}

				if(c == SLASH)
				{
					isBornCondition = !isBornCondition;
				}

				if(Character.toLowerCase(c) == 't')
				{
					break;
				}
			}
		}
		line = reader.readLine();
		int x0 = Math.max(0, (gameField.getGameWidth() - width) / 2);
		int y0 = Math.max(0, (gameField.getGameHeight() - height) / 2);
		// start with even row
		if(y0 % 2 == 1)
		{
			y0 -= 1;
		}
		int x = x0;
		int y = y0;
		while(line != null)
		{
			for(int i = 0; i < line.length(); i++)
			{
				char c = line.charAt(i);
				if(Character.isDigit(c))
				{
					numString += c;
				}
				else
				{
					int n = 1;
					if(!numString.equals(""))
					{
						n = Integer.parseInt(numString);
						numString = "";
					}
					
					switch(String.valueOf(c))
					{
						case RLE_DEAD_CELL_CODE:
						{
							x += n;
							break;
						}
						case RLE_LIVE_CELL_CODE:
						{
							for(int j = 0; j < n; j++)
							{
								int index = gameField.getGameWidth() * y + x;
								if(index < gameField.size())
								{
									initialCell(index);
								}
								x++;
							}
							break;
						}
						case RLE_LINE_TERMINATOR:
						{
							y += n;
							x = x0;
							break;
						}
						case RLE_FILE_TERMINATOR:
						{
							break;
						}
					}
				}
			}
			line = reader.readLine();
		}		
	}

	public int getMaxAge() { return mMaxAge; }
	public void setMaxAge(int age) { mMaxAge = age; }

	public void setSurviveNeighbours(int[] neighbours)
	{
		mSurviveNeighbours = new boolean[mSurviveNeighbours.length];
		for(int i = 0; i < neighbours.length; i++)
		{
			if(neighbours[i] >= 0 && neighbours[i] < mSurviveNeighbours.length)
			{
				mSurviveNeighbours[neighbours[i]] = true;
			}
		}
	}

	public ArrayList<Integer> getSurviveNeighbours()
	{
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		for(int i = 0; i < mSurviveNeighbours.length; i++)
		{
			if(mSurviveNeighbours[i])
			{
				neighbours.add(i);
			}
		}
		return neighbours;
	}

	public void setBornNeighbours(int[] neighbours)
	{
		mBornNeighbours = new boolean[mBornNeighbours.length];
		for(int i = 0; i < neighbours.length; i++)
		{
			if(neighbours[i] >= 0 && neighbours[i] < mBornNeighbours.length)
			{
				mBornNeighbours[neighbours[i]] = true;
			}
		}
	}

	public ArrayList<Integer> getBornNeighbours()
	{
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		for(int i = 0; i < mBornNeighbours.length; i++)
		{
			if(mBornNeighbours[i])
			{
				neighbours.add(i);
			}
		}
		return neighbours;
	}

	public int getStepCount() { return mStepCount; }
	public void setStepCount(int stepCount) { mStepCount = stepCount; }

	public int getItemCount()
	{
		if(mGameField == null)
		{
			return 0;
		}

		int count = 0;
		for(Cell cell : mGameField)
		{
			if(cell.getItem() != null)
			{
				count++;
			}
		}
		return count;
	}

	public GameField getGameField() { return mGameField; }
	public void setGameField(GameField field) { mGameField = field; }

	public HashMap<Integer, Integer> getCurrentPosition()
	{
		HashMap<Integer, Integer> position = new HashMap<Integer, Integer>();
		for(int i = 0; i < mGameField.size(); i++)
		{
			Item item = mGameField.get(i).getItem();
			if(item != null && !item.getIsDie())
			{
				position.put(i, item.getAge());
			}
		}
		return position;
	}

	public void step()
	{
		if(mStepCount == 0)
		{
			mStartPosition = getCurrentPosition();
		}

		for(Cell cell : mGameField)
		{
			int neighboursCount = 0;
			for(Cell neighbour : cell.getNeighbours())
			{
				if(neighbour.getItem() != null && neighbour.getItem().getAge() != 0)
				{
					neighboursCount++;
				}
			}

			if(cell.getItem() != null)
			{
				if(!mSurviveNeighbours[neighboursCount] ||
					(mMaxAge != 0 && cell.getItem().getAge() > mMaxAge))
				{
					cell.getItem().setIsDie(true);
				}
			}
			else if(mBornNeighbours[neighboursCount])
			{
				cell.fill(false);
			}
		}

		for(Cell cell : mGameField)
		{
			if(cell.getItem() != null)
			{
				if(cell.getItem().getIsDie())
				{
					cell.clear();
				}
				else
				{
					cell.getItem().incrementAge();
				}
			}
		}
		mStepCount++;
	}

	public void randomFillGameField(int startItemCount)
	{
		int fieldSize = mGameField.size();
		if(fieldSize < startItemCount)
		{
			throw new IllegalArgumentException(String.format(
				"Can not put %d items in %d places. Parameter startItemCount must be less than cells count.",
					startItemCount, fieldSize));
		}
		Random random = new Random();
		for(int i = 0; i < startItemCount; i++)
		{
			int cellIndex = 0;
			do
			{
				cellIndex = random.nextInt(fieldSize);
			}
			while(mGameField.get(cellIndex).getItem() != null);
			
			initialCell(cellIndex);
		}
	}

	public void initialCell(int cellIndex)
	{
		mGameField.get(cellIndex).fill(true);
	}

	public void reset()
	{
		if(mStartPosition != null)
		{
			setPosition(mStartPosition);
			mStepCount = 0;
		}
	}

	public void setPosition(HashMap<Integer, Integer> position)
	{
		mGameField.clearCells();
		for(Integer index : position.keySet())
		{
			if(index >= 0 && index < mGameField.size())
			{
				mGameField.get(index).fill(true);
				mGameField.get(index).getItem().setAge(position.get(index));
			}
		}
	}

	public class PatternIsTooLargeException extends Exception
	{
		private int mPatternWidth;
		private int mGameFieldWidth;

		public PatternIsTooLargeException(int gameFieldWidth, int patternWidth)
		{
			mPatternWidth = patternWidth;
			mGameFieldWidth = gameFieldWidth;
		}

		@Override
		public String getMessage()
		{
			return String.format("Pattern width is %d, it's too match for field width is %d", mPatternWidth, mGameFieldWidth); 
		}
	}
}