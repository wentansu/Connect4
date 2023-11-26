//Startup
//Description: This program is a Connect 4 game where the user plays against the computer.
//Created By: Wentan Su
//Last Modified: Jan 13, 2023
import javax.swing.*;

public class Startup
{
	public static void main(String[] args)
	{
		Model model = new Model();
		View view = new View(model);
		
		//Initialize window
		JFrame window = new JFrame("Connect 4");
		window.setLocation(300, 100);
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(view);
		window.pack();
		window.setVisible(true);
	}
} //End of class