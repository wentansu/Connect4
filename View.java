//View
//Description: This class creates the GUI for the Connect 4 game.
//Created By: Wentan Su
//Last Modified: Jan 22, 2023
import javax.swing.*;
import java.awt.*;

public class View extends JPanel
{
	private Model model;
	private JButton[] columns = new JButton[7]; //Buttons for each column
	private JButton newGame = new JButton("New Game");
	private JButton exit = new JButton("Exit");
	private JButton endGame = new JButton("End Game");
	private JButton nextRound = new JButton("Next Round");
	private JTextField roundNum = new JTextField("Enter the number of rounds here"); //Textfield to input number of rounds and display it
	private JLabel instruction = new JLabel(); //Instruction for player
	private JTextArea results = new JTextArea();
	private JLabel playerPoints = new JLabel("Player: 0 Points");
	private JLabel computerPoints = new JLabel("Computer: 0 Points");
	private JRadioButton level1 = new JRadioButton("1 - Computer might block or connect.");
	private JRadioButton level2 = new JRadioButton("2 - Computer always block or connect.");
	private JRadioButton level3 = new JRadioButton("3 - Computer sees one move ahead.");
	private Board board; //Game board

	public View(Model model)
	{
		super();
		this.model = model;
		this.model.setGUI(this);
		this.board = new Board(this.model);
		this.createLayout();
		this.registerControllers();
		this.update();
	}

	//Initialize the layout
	private void createLayout()
	{
		JPanel game = new JPanel(); //Game board section
		JPanel topArea = new JPanel(); //Round number and instruction
		JPanel top = new JPanel(); //Top area and buttons
		JPanel buttons = new JPanel(); //Buttons for each column
		JPanel bottom = new JPanel(); //New game, exit, end game buttons

		top.add(this.roundNum);
		top.add(this.instruction);

		//Initialize and add buttons for each column
		for (int i = 0; i < 7; i++)
		{
			this.columns[i] = new JButton(String.valueOf(i + 1));
			buttons.add(this.columns[i]);
		}

		topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
		topArea.add(top);
		topArea.add(buttons);

		bottom.add(this.newGame);
		bottom.add(this.exit);
		bottom.add(this.endGame);

		game.setLayout(new BorderLayout());
		game.add(topArea, BorderLayout.NORTH);
		game.add(this.board, BorderLayout.CENTER);
		game.add(bottom, BorderLayout.SOUTH);

		JPanel information = new JPanel(); //Information section
		JPanel resultsPanel = new JPanel(); //Results
		JPanel scores = new JPanel(); //Scores for player and computer
		JPanel computerLevel = new JPanel(); //Radio buttons to select computer level

		this.results.setEditable(false);
		resultsPanel.add(this.results);

		scores.setLayout(new BoxLayout(scores, BoxLayout.Y_AXIS));
		scores.add(this.playerPoints);
		scores.add(this.computerPoints);

		computerLevel.setLayout(new BoxLayout(computerLevel, BoxLayout.Y_AXIS));
		computerLevel.add(this.level1);
		computerLevel.add(this.level2);
		computerLevel.add(this.level3);
		computerLevel.setPreferredSize(new Dimension(400, 100));

		//Add borders to panels
		resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		scores.setBorder(BorderFactory.createTitledBorder("Scores"));
		computerLevel.setBorder(BorderFactory.createTitledBorder("Computer Level"));

		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		information.add(resultsPanel);
		information.add(scores);
		information.add(computerLevel);
		information.add(this.nextRound);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(game);
		this.add(information);
	}

	//Register controllers for components
	private void registerControllers()
	{
		Controller controller = new Controller(this.model, this.roundNum);

		//Add action listener and set action command for each component
		this.roundNum.addActionListener(controller);
		this.roundNum.setActionCommand("11");
		this.newGame.addActionListener(controller);
		this.newGame.setActionCommand("12");
		this.exit.addActionListener(controller);
		this.exit.setActionCommand("13");
		this.endGame.addActionListener(controller);
		this.endGame.setActionCommand("14");
		this.nextRound.addActionListener(controller);
		this.nextRound.setActionCommand("15");
		this.level1.addActionListener(controller);
		this.level1.setActionCommand("16");
		this.level2.addActionListener(controller);
		this.level2.setActionCommand("17");
		this.level3.addActionListener(controller);
		this.level3.setActionCommand("18");

		for (int i = 0; i < 7; i++)
		{
			this.columns[i].addActionListener(controller);
			this.columns[i].setActionCommand(String.valueOf(i));
		}
	}

	//Update the GUI after player's action
	public void update()
	{
		this.repaint();

		//Update scores display
		this.playerPoints.setText("Player: " + this.model.getPlayerPoints() + " Points");
		this.computerPoints.setText("Computer: " + this.model.getComputerPoints() + " Points");

		//Disable buttons if their column is full
		for (int i = 0; i < 7; i++)
		{
			this.columns[i].setEnabled(this.model.getGrid()[0][i] == 0);
		}
		
		//Disable buttons for each column if a round or game is over
		for (int i = 0; i < 7; i++)
		{
			this.columns[i].setEnabled(!(this.model.getRoundOver() || this.model.getGameOver()));
		}

		//Determine if player has entered number of rounds
		if (this.model.getMaxRound() != 0)
		{
			this.roundNum.setEditable(false);
			this.roundNum.setText("Round " + this.model.getRound() + "/" + this.model.getMaxRound());
			this.instruction.setText("Place your checker with the buttons below");
		}

		//Determine if a new game is starting
		if (this.model.getNewGame())
		{
			this.roundNum.setText("Enter the number of rounds here");
			this.roundNum.selectAll();
			this.roundNum.setEditable(true);
			this.instruction.setText("Select a computer level");
			this.results.setText("");
		}

		//Determine if the round is over
		if (this.model.getRoundOver())
		{
			this.results.setText(this.results.getText().concat("Round " + this.model.getRound() + " - " + this.model.getWinner() + "\n"));
			this.instruction.setText(this.model.getWinner());
		}
		
		this.nextRound.setVisible(this.model.getRoundOver()); //Allow player to move to next round
		
		//Determine if the game is over
		if (this.model.getGameOver())
		{
			this.instruction.setText("Game over - " + this.model.getWinner());
			this.nextRound.setVisible(false);
			this.results.setText(this.results.getText().concat("Overall Game Result - " + this.model.getWinner()));
		}
		
		//Determine if a computer level has been selected
		if (this.model.getLevel() == 0)
		{
			this.level1.setSelected(false);
			this.level2.setSelected(false);
			this.level3.setSelected(false);
		}
		
		//Disable radio buttons if a computer level has been selected
		this.level1.setEnabled(this.model.getLevel() == 0);
		this.level2.setEnabled(this.model.getLevel() == 0);
		this.level3.setEnabled(this.model.getLevel() == 0);
	}
} //End of class