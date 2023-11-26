//Board
//Description: This class draws a game board for the GUI.
//Created By: Wentan Su
//Last Modified: Jan 22, 2023
import javax.swing.*;
import java.awt.*;

public class Board extends JComponent
{
	private Model model;
	
	public Board(Model model)
	{
		super();
		this.setPreferredSize(new Dimension(500, 500));
		this.model = model;
	}
	
	public void paintComponent(Graphics g)
	{
		//Set scale
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(this.getWidth() / 22, this.getHeight() / 19);
		g2.setStroke(new BasicStroke(50.0F / this.getWidth()));
		
		this.drawBoard(g2); //Draw the empty board
		
		//Draw any checkers present on the board
		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 7; j++)
			{
				if (this.model.getGrid()[i][j] != 0)
				{
					this.drawChecker(g2, j + 1, i + 1);
				}
			}
		}
		
		//Determine if the round is over and it is not a draw
		if (this.model.getRoundOver() && !this.model.getWinner().equals("Draw"))
		{
			//Set the start and end points of the four checks that are connected
			int row1, column1, row2, column2;
			row1 = this.model.getEndPoints()[0][0] + 1;
			column1 = this.model.getEndPoints()[0][1] + 1;
			row2 = this.model.getEndPoints()[1][0] + 1;
			column2 = this.model.getEndPoints()[1][1] + 1;
			
			//Draw a line that connects the four checkers
			g2.setColor(Color.BLACK);
			g2.drawLine(column1 * 3 - 1, row1 * 3 - 1, column2 * 3 - 1, row2 * 3 - 1);
		}
	}
	
	//Draw an empty board
	private void drawBoard(Graphics2D g2)
	{
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, 22, 19);
		
		g2.setColor(Color.WHITE);
		
		//Draw the holes
		for (int i = 1; i <= 19; i += 3)
		{
			for (int j = 1; j <= 16; j += 3)
			{
				g2.fillOval(i, j, 2, 2);
			}
		}
	}
	
	//Draw a checker on the specified row and column
	private void drawChecker(Graphics2D g2, int column, int row)
	{
		//Determine whether the checker is the player's or computer's
		if (this.model.getGrid()[row - 1][column - 1] == 1)
		{
			g2.setColor(Color.RED);
		}
		else
		{
			g2.setColor(Color.YELLOW);
		}
		
		g2.fillOval(column * 3 - 2, row * 3 - 2, 2, 2);
	}
} //End of class