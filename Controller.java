//Controller
//Description: This class creates a Controller for the interactive components in the GUI.
//Created By: Wentan Su
//Last Modified: Jan 22, 2023
import java.awt.event.*;
import javax.swing.*;

public class Controller implements ActionListener
{
	private Model model;
	private JTextField roundNum; //Textfield to input the number of rounds for the game
	
	public Controller(Model model, JTextField roundNum)
	{
		this.model = model;
		this.roundNum = roundNum;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		//Determine which component is used
		switch (Integer.parseInt(e.getActionCommand()))
		{
			case 11:
				//Input the number of rounds of play
				int maxRound = 0;
				
				//Validate the input
				try
				{
					maxRound = Integer.parseInt(this.roundNum.getText());
					this.model.setMaxRound(maxRound);
				}
				catch (NumberFormatException ex)
				{
					this.roundNum.selectAll();
				}
				
				break;
			case 12:
				this.model.newGame();
				break;
			case 13:
				this.model.exit();
				break;
			case 14:
				this.model.endGame();
				break;
			case 15:
				this.model.nextRound();
				break;
			case 16:
				this.model.setLevel(1);
				break;
			case 17:
				this.model.setLevel(2);
				break;
			case 18:
				this.model.setLevel(3);
				break;
			default:
				this.model.place(Integer.parseInt(e.getActionCommand())); //Place a checker for the player
		}
	}
} //End of class