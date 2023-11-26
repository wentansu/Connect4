//Model
//Description: This class creates the Model for the Connect 4 game.
//Created By: Wentan Su
//Last Modified: Jan 22, 2023
import java.util.*;
import java.io.*;

public class Model extends Object
{
	private View view;
	private int[][] grid = new int[6][7]; //The grid which represents the game board
	private int[][] endPoints = new int[2][2]; //The start and end points of the four connected checkers
	private int playerPoints;
	private int computerPoints;
	private int round;
	private int maxRound; //Number of rounds of play
	private int level; //Computer level
	private int gameNum;
	private boolean newGame;
	private boolean roundOver;
	private boolean gameOver;
	private String winner;
	private PrintWriter output; //Output to file

	public Model()
	{
		//Initialize the grid, 0, 1 and 2 represent empty, player and computer respectively
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				this.grid[i][j] = 0;
			}
		}

		//Initialize attributes
		this.endPoints[0][0] = 0;
		this.endPoints[0][1] = 0;
		this.endPoints[1][0] = 0;
		this.endPoints[1][1] = 0;
		this.playerPoints = 0;
		this.computerPoints = 0;
		this.round = 1;
		this.maxRound = 0;
		this.level = 0;
		this.gameNum = 1;
		this.newGame = true;
		this.roundOver = false;
		this.gameOver = false;
		this.winner = "None";
		this.writeFile();
	}

	//Set the instance of the View
	public void setGUI(View view)
	{
		this.view = view;
	}

	//Call the update method in View
	private void updateView()
	{
		this.view.update();
	}

	//Place a checker for the player
	public void place(int column)
	{
		this.grid[this.firstVacantIndex(column)][column] = 1;

		//Determine if the player has connected four checkers
		if (this.isRoundOver(column))
		{
			this.winner = "Player won";
			this.playerPoints++;
			this.roundOver = true;
			this.writeRound();
		}
		else
		{
			this.roundOver = false;
			this.computerPlace();
		}

		this.updateView();
	}

	//Place a checker for the computer depending on its level
	private void computerPlace()
	{
		ArrayList<Integer> vacantColumns = new ArrayList<Integer>(); //List of columns that computer can place a checker in
		boolean connected = false; //If the computer has connected and won the game
		
		//Check each column
		for (int i = 0; i < 7; i++)
		{
			//Determine if the column is not full
			if (this.grid[0][i] == 0)
			{
				vacantColumns.add(i);
			}
		}
		
		int number = 0; //Random number for level 1 computer
		
		//Determine computer level
		if (this.level == 1)
		{
			number = (int) (Math.random() * 3); //Generate a random integer from 0 to 2
		}
		
		//Determine if the random number is 0, which has a 1/3 chance for the level 1 computer
		if (number == 0)
		{
			connected = this.connect();
			
			//Determine if the computer can connect four
			if (!connected)
			{
				//Determine if the computer can block the player from connecting four
				if (!this.block())
				{
					//Determine computer level
					if (this.level != 3)
					{
						//Randomly place a checker
						int index = (int) (Math.random() * vacantColumns.size());
						this.grid[this.firstVacantIndex(vacantColumns.get(index))][vacantColumns.get(index)] = 2;
					}
					else
					{
						vacantColumns.clear();
						
						//Find all columns that have at least two empty holes
						for (int i = 0; i < 7; i++)
						{
							if (this.grid[1][i] == 0)
							{
								vacantColumns.add(i);
							}
						}
						
						//Check through all available columns
						for (int i = 0; i < vacantColumns.size(); i++)
						{
							//Suppose the computer places a checker at the current column, determine if the player can win by placing their checker in the same column for their turn
							this.grid[this.firstVacantIndex(vacantColumns.get(i)) - 1][vacantColumns.get(i)] = 1;
							boolean playerWins = this.isRoundOver(vacantColumns.get(i));
							
							//Suppose the computer places a checker at the current column, determine if the player can block the computer from winning for their turn
							this.grid[this.firstVacantIndex(vacantColumns.get(i)) + 1][vacantColumns.get(i)] = 2;
							boolean computerWins = this.isRoundOver(vacantColumns.get(i));
							
							this.grid[this.firstVacantIndex(vacantColumns.get(i)) + 1][vacantColumns.get(i)] = 0; //Reset the hole in the current column
							
							//Determine if placing a checker at the current column gives the player a chance to win or block, in this way the computer thinks a move ahead considering the player's turn
							if (playerWins || computerWins)
							{
								vacantColumns.remove(i); //Remove the column from the list so the computer will not consider this column for placing its checker
							}
						}
						
						//Add all columns that have only one empty hole to the list of available columns
						for (int i = 0; i < 7; i++)
						{
							if (this.grid[0][i] == 0 && this.grid[1][i] != 0)
							{
								vacantColumns.add(i);
							}
						}
						
						//Randomly place a checker in one of the columns that remain in the list
						int index = (int) (Math.random() * vacantColumns.size());
						this.grid[this.firstVacantIndex(vacantColumns.get(index))][vacantColumns.get(index)] = 2;
					}
				}
			}
		}
		else
		{
			//Randomly place a checker in an available column
			int index = (int) (Math.random() * vacantColumns.size());
			this.grid[this.firstVacantIndex(vacantColumns.get(index))][vacantColumns.get(index)] = 2;
			connected = this.isRoundOver(vacantColumns.get(index)); //Determine if the computer has won
			
			if (connected)
			{
				this.winner = "Computer won";
				this.computerPoints++;
				this.roundOver = true;
				this.writeRound();
			}
		}
		
		vacantColumns.clear();
		
		//Check again for the columns that are not full
		for (int i = 0; i < 7; i++)
		{
			if (this.grid[0][i] == 0)
			{
				vacantColumns.add(i);
			}
		}
		
		//Determine if none of the columns are available, which indicates a draw if the computer hasn't won by placing its checker at the last available hole
		if (vacantColumns.size() == 0 && !connected)
		{
			this.winner = "Draw";
			this.playerPoints++;
			this.computerPoints++;
			this.roundOver = true;
			this.writeRound();
		}
	}

	//Block the player from connecting four, return true if the computer is able to block
	private boolean block()
	{
		//Check through every column
		for (int i = 0; i < 7; i++)
		{
			//Determine if the column is full
			if (this.firstVacantIndex(i) == -1)
			{
				continue;
			}

			this.grid[this.firstVacantIndex(i)][i] = 1; //Place a checker for the player in the current column

			//Determine if the player wins
			if (this.isRoundOver(i))
			{
				this.grid[this.firstVacantIndex(i) + 1][i] = 2; //Place a checker for the computer instead to prevent the player from connecting
				return true;
			}
			else
			{
				this.grid[this.firstVacantIndex(i) + 1][i] = 0; //Reset the hole
			}
		}

		return false;
	}

	//Connect four of the computer's checkers to win the game, return true if the computer is able to connect
	private boolean connect()
	{
		//Check through every column
		for (int i = 0; i < 7; i++)
		{
			//Determine if the column is full
			if (this.firstVacantIndex(i) == -1)
			{
				continue;
			}

			this.grid[this.firstVacantIndex(i)][i] = 2; //Place a checker for the computer at the current column

			//Determine if the computer has won
			if (this.isRoundOver(i))
			{
				this.winner = "Computer won";
				this.computerPoints++;
				this.roundOver = true;
				this.writeRound();
				return true;
			}
			else
			{
				this.grid[this.firstVacantIndex(i) + 1][i] = 0; //Reset the hole
			}
		}

		return false;
	}

	//Determine if the round is over, which is when either the player or computer has connected four of their checkers horizontally, vertically, or diagonally
	private boolean isRoundOver(int column)
	{
		int row = this.firstVacantIndex(column) + 1; //Index of the row of the checker that has just been placed

		//Check vertically
		//Determine if the checker is placed above row 2, which means it's possible to connect four vertically
		if (row <= 2)
		{
			//Check the three rows below
			for (int i = 1; i <= 3; i++)
			{
				//Determine if the hole has the same checker as the one that has just been placed
				if (this.grid[row][column] != this.grid[row + i][column])
				{
					break;
				}

				//Determine if all three holes that have been checked are the same
				if (i == 3)
				{
					//Set the start and end points of the four connected checkers
					this.endPoints[0][0] = row;
					this.endPoints[0][1] = column;
					this.endPoints[1][0] = row + i;
					this.endPoints[1][1] = column;
					return true;
				}
			}
		}

		//Check horizontally
		int index = 1;
		int count = 1; //Number of consecutive checkers that are the same
		this.endPoints[0][0] = row;
		this.endPoints[0][1] = column;

		//Check to the left if the column is not 0
		if (column != 0)
		{
			while (true)
			{
				//Determine if the next hole is the same as the checker that has just been placed
				if (this.grid[row][column] == this.grid[row][column - index])
				{
					//Set the start points to the left most hole which has the same checker
					this.endPoints[0][0] = row;
					this.endPoints[0][1] = column - index;
					count++;
					
					//Determine if four consecutive checkers are found
					if (count == 4)
					{
						//Set the end points to the checker that has just been placed
						this.endPoints[1][0] = row;
						this.endPoints[1][1] = column;

						return true;
					}
				}
				else
				{
					break;
				}

				index++;

				//Determine if end of grid is reached
				if (index > column)
				{
					break;
				}
			}
		}

		index = 1;

		//Check to the right if the column is not 6
		if (column != 6)
		{
			while (true)
			{
				//Determine if the next hole is the same as the checker that has just been placed
				if (this.grid[row][column] == this.grid[row][column + index])
				{
					count++;

					//Determine if four consecutive checkers are found
					if (count == 4)
					{
						//Set the end points to the right most checker of the consecutive four checkers
						this.endPoints[1][0] = row;
						this.endPoints[1][1] = column + index;
						return true;
					}
				}
				else
				{
					break;
				}

				index++;

				//Determine if end of grid is reached
				if (column + index > 6)
				{
					break;
				}
			}
		}

		//Check the diagonal from top left to bottom right
		if (this.checkDiagonal(row, column, 1))
		{
			return true;
		}
		
		//Check the diagonal from bottom left to top right
		return this.checkDiagonal(row, column, 2);
	}
	
	//Check the two diagonals passing through a hole on the grid, return true if four consecutive checkers are found
	private boolean checkDiagonal(int row, int column, int direction)
	{
		//Indexes for checking through the grid
		int index1 = 0;
		int index2 = 0;
		
		//Restrictions to the row and column for which each check algorithm is applicable
		int restriction1 = 0;
		int restriction2 = 0;
		int count = 1; //Number of consecutive checkers found
		
		//Determine the direction to check in, 1 means top left to bottom right, 2 means bottom left to top right
		switch (direction)
		{
			case 1:
				index1 = -1;
				index2 = -1;
				restriction1 = 0;
				restriction2 = 0;
				break;
			case 2:
				index1 = 1;
				index2 = -1;
				restriction1 = 5;
				restriction2 = 0;
				break;
		}
		
		//Increment of the indexes
		int increment1 = index1;
		int increment2 = index2;

		//Determine if the location of the hole is within the restriction
		if (row != restriction1 && column != restriction2)
		{
			while (true)
			{
				//Determine if the next hole is the same
				if (this.grid[row][column] == this.grid[row + index1][column + index2])
				{
					//Set the start points
					this.endPoints[0][0] = row + index1;
					this.endPoints[0][1] = column + index2;
					count++;

					//Determine if four consecutive checkers are found
					if (count == 4)
					{
						//Set the end points
						this.endPoints[1][0] = row;
						this.endPoints[1][1] = column;

						return true;
					}
				}
				else
				{ 
					break;
				}

				//Update the indexes
				index1 += increment1;
				index2 += increment2;

				//Determine if the next hole being checked is out of bounds
				if (this.hasReachedEnd(row + index1, column + index2))
				{
					break;
				}
			}
		}

		//Update the variables for the next check depending on the direction
		switch (direction)
		{
			case 1:
				index1 = 1;
				index2 = 1;
				restriction1 = 5;
				restriction2 = 6;
				break;
			case 2:
				index1 = -1;
				index2 = 1;
				restriction1 = 0;
				restriction2 = 6;
				break;
		}
		
		increment1 = index1;
		increment2 = index2;

		//Determine if the location of the hole is within the restriction
		if (row != restriction1 && column != restriction2)
		{
			while (true)
			{
				//Determine if the next hole is the same
				if (this.grid[row][column] == this.grid[row + index1][column + index2])
				{
					count++;

					//Determine if four consecutive checkers are found
					if (count == 4)
					{
						//Set the end points
						this.endPoints[1][0] = row + index1;
						this.endPoints[1][1] = column + index2;

						return true;
					}
				}
				else
				{
					return false;
				}

				//Update the indexes
				index1 += increment1;
				index2 += increment2;

				//Determine if the next hole being checked is out of bounds
				if (this.hasReachedEnd(row + index1, column + index2))
				{
					return false;
				}
			}
		}
		
		return false;
	}
	
	//Determine if a set of index on the grid is out of bounds
	private boolean hasReachedEnd(int row, int column)
	{
		//Determine if the row is out of bounds
		if (row < 0 || row > 5)
		{
			return true;
		}
		
		//Determine if the column is out of bounds
		if (column < 0 || column > 6)
		{
			return true;
		}
		
		return false;
	}

	//Find the index of the first empty hole of the column
	private int firstVacantIndex(int column)
	{
		//Determine if the column is full
		if (this.grid[0][column] != 0)
		{
			return -1;
		}

		int index = 5;

		//Check from the first row
		for (int i = 0; i < 6; i++)
		{
			//Determine if the row is occupied
			if (this.grid[i][column] != 0)
			{
				index = i - 1;
				break;
			}
		}

		return index;
	}

	//Start a new game
	public void newGame()
	{
		this.newGame = true;
		this.gameNum++;
		this.roundOver = false;
		this.gameOver = false;

		//Initialize board
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				this.grid[i][j] = 0;
			}
		}

		//Initialize attributes
		this.endPoints[0][0] = 0;
		this.endPoints[0][1] = 0;
		this.endPoints[1][0] = 0;
		this.endPoints[1][1] = 0;
		this.playerPoints = 0;
		this.computerPoints = 0;
		this.round = 1;
		this.maxRound = 0;
		this.level = 0;
		this.winner = "None";
		this.writeFile(); //Open a new file
		this.updateView();
	}

	//Exit the program
	public void exit()
	{
		this.output.close();
		System.exit(0);
	}

	//End current game
	public void endGame()
	{
		this.gameOver = true;
		this.determineWinner();
		this.updateView();
		this.writeResult();
	}

	//Continue to next round
	public void nextRound()
	{
		this.round++;
		this.roundOver = false;

		//Initialize the board
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				this.grid[i][j] = 0;
			}
		}

		//Initialize end points
		this.endPoints[0][0] = 0;
		this.endPoints[0][1] = 0;
		this.endPoints[1][0] = 0;
		this.endPoints[1][1] = 0;
		this.winner = "None";
		
		//Determine if game is over
		if (this.round > this.maxRound)
		{
			this.gameOver = true;
			this.round--;
			this.determineWinner();
			this.writeResult();
		}
		
		this.updateView();
	}
	
	//Determine the game winner
	private void determineWinner()
	{
		//Determine if the game is a draw
		if (this.playerPoints == this.computerPoints)
		{
			this.winner = "Draw";
		}
		else
		{
			this.winner = this.playerPoints > this.computerPoints ? "Player won" : "Computer won";
		}
	}
	
	//Open a new file
	private void writeFile()
	{
		String file = "Game " + this.gameNum + ".txt"; //File name
		
		try
		{
			this.output = new PrintWriter(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		this.output.println("Game " + this.gameNum + " Results"); //Title
	}
	
	//Output current round's results
	private void writeRound()
	{
		this.output.println("Round " + this.round + " - " + this.winner);
	}
	
	//Output the overall game results to file
	private void writeResult()
	{
		this.output.println("\nOverall Scores");
		this.output.println("Player - " + this.playerPoints);
		this.output.println("Computer - " + this.computerPoints);
		this.output.println("Overall Game Result - " + this.winner);
		this.output.close();
	}

	//Accessor methods to set or get values of instance variables
	//Set number of rounds of play
	public void setMaxRound(int maxRound)
	{
		this.maxRound = maxRound;
		this.newGame = false;
		this.updateView();
	}
	
	//Set computer level
	public void setLevel(int level)
	{
		this.level = level;
		this.updateView();
	}
	
	public int[][] getGrid()
	{
		return this.grid;
	}

	public int[][] getEndPoints()
	{
		return this.endPoints;
	}

	public int getPlayerPoints()
	{
		return this.playerPoints;
	}

	public int getComputerPoints()
	{
		return this.computerPoints;
	}

	public int getRound()
	{
		return this.round;
	}
	
	public int getLevel()
	{
		return this.level;
	}

	public int getMaxRound()
	{
		return this.maxRound;
	}

	public boolean getNewGame()
	{
		return this.newGame;
	}

	public boolean getRoundOver()
	{
		return this.roundOver;
	}

	public boolean getGameOver()
	{
		return this.gameOver;
	}

	public String getWinner()
	{
		return this.winner;
	}
} //End of class