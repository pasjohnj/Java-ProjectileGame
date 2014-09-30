package projectileGame;
import gameNet.GameNet_CoreGame;
import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;


enum Command {INITIALIZE, FIRE, RESET, EXIT};

class QuitWindow extends JFrame implements QuitWindowInterface{ //used to exit the game
	private JButton yes = new JButton("Yes");
	private JButton no = new JButton("No");
	private JLabel message = new JLabel("");

	String[] quitMessages = { //quit messages from game Wolfenstein 3D
			"<html>Dost thou wish to leave<br>with such hasty abandon?</html>",
			"<html>Chickening out...already?</html>",
			"<html>Press No for more carnage.<br>Press Yes to be a weenie.</html>",
			"<html>So, you think you can<br>quit this easily, huh?</html>",
			"<html>Press No to save the world.<br>Press Yes to abandon it in its hour of need.</html>",
			"<html>Press No if you are brave.<br>Press Yes to cower in shame.</html>",
			"<html>Heroes, press No.<br>Wimps, press Yes.</html>",
			"<html>You are at an intersection.<br>A sign says, 'Press Yes to quit.'</html>",
			"<html>For guns and glory, press No.<br>For work and worry, press Yes.</html>"
	};
	
	public QuitWindow(){
		super("Quitting?");
		setSize(250, 175);
		setResizable(false);
		setLayout(new GridLayout(2,1));
		message.setText(quitMessages[randInt(8,0)]);
		add(message);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		buttons.add(no);
		buttons.add(yes);
		add(buttons);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	public void addActionListener(ActionListener listener){
		yes.addActionListener(listener);
		no.addActionListener(listener);
	}
	
	public void setVisibility(boolean visibility){
		if(visibility){
			setVisible(true);
		} else {
			setVisible(false);
		}
	}
	
	private int randInt(int max, int min) { //random number generator, for selecting the quit message
		   Random rand = new Random();
		   int randomNum = rand.nextInt((max - min) + 1) + min; //nextInt is normally exclusive of the top value, so add 1 to make it inclusive
	
		   return randomNum;
	}
}

public class MyUserInterface extends JFrame implements  GameNet_UserInterface, ActionListener {
	
	//JComponent variables
	private JLabel gameUpdatesBanner = new JLabel();
	private JLabel systemUpdatesBanner = new JLabel();
	private JLabel playerNameBanner = new JLabel();
	private JLabel scoreCountBanner = new JLabel();
	private JTextField velocityField = new JTextField(2);
	private JTextField arcAngleField = new JTextField(2);
	private JButton launch, quit;
	private QuitWindowInterface quitWindow = new QuitWindow();
	
	//game player and game status variables
	private GamePlayer myGamePlayer;
	private int playerNum = 10; //phony number to facilitate setting its real value down the line
	private int numPlayer1Wins, numPlayer2Wins;
	private String playerName, otherPlayerName;
	
	//game board variables
	private GameBoard gameBoard;
	private GameBoardLogic gbLogic;
	private boolean gameBoardexists = false;
	
	private Command command;

	
	
	public MyUserInterface() {
		super("User Input Window");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); //to keep a player from exiting via closing a window
		
		quitWindow.addActionListener(new ActionListener() { //adding this to the constructor seems inelegant, but it's what a response on StackOverload recommended 
            public void actionPerformed(ActionEvent arg) {
            	String buttonCommand = arg.getActionCommand();
            	if(buttonCommand.equals("No")){
            		quitWindow.setVisibility(false);
            	} else {
            		myGamePlayer.sendMessage(new MyGameInput(playerNum, 0, 0, command.EXIT, playerName));
            		quitWindow.setVisibility(false);
            	}
            		
            }
        });
	}
	
	//*****START set user interaction window methods*****\\
	private void doInputWindowLayout(){ 
		setSize(250, 175);
	    setResizable(false);
	    setLayout(new BorderLayout());
	    
	    inputWindowAddNorthPanel();
		inputWindowAddCenterPanel();
		inputWindowAddSouthPanel();
		
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	private void inputWindowAddNorthPanel(){
		JPanel northPanel = new JPanel();
	    northPanel.setLayout(new GridLayout(2,1));
	    scoreCountBanner.setText("Java Gorillas");
	    northPanel.add(scoreCountBanner);
	    
	    playerNameBanner.setText(playerName + "'s Screen");
		northPanel.add(playerNameBanner);
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	private void inputWindowAddCenterPanel(){
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		
		centerPanel.setLayout(new BorderLayout());
		JPanel inputAreas = new JPanel();
		inputAreas.setLayout(new GridLayout(2, 2));
		inputAreas.add(new JLabel("Angle: "));
		inputAreas.add(arcAngleField);
		inputAreas.add(new JLabel("Velocity: "));
		inputAreas.add(velocityField);
		
		
		southPanel.setLayout(new GridLayout(2, 1));
		southPanel.add(systemUpdatesBanner);
		southPanel.add(gameUpdatesBanner);
		
		centerPanel.add(inputAreas, BorderLayout.NORTH);
		centerPanel.add(southPanel, BorderLayout.SOUTH);
		
		add(centerPanel, BorderLayout.CENTER);
	}	
	
	private void inputWindowAddSouthPanel(){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2));
		launch = new JButton("Launch");
		launch.addActionListener(this);
		buttonPanel.add(launch);
		quit = new JButton("Quit...");
		quit.addActionListener(this);
		buttonPanel.add(quit);

		add(buttonPanel, BorderLayout.SOUTH);
	}
	//*****END set user interaction window methods*****\\
	//*****START game status update methods*****\\
	public void updateGameStatusBanner(String message){ //added mostly to be a more descriptive method name than "setText"
		gameUpdatesBanner.setText(message);
		
	}
	
	public void updateSystemStatusBanner(String message){
		systemUpdatesBanner.setText(message);
	}
	
	public void updateSystemStatusBanner(boolean player1Wins){
		if((player1Wins && playerNum == 0) || (!player1Wins && playerNum != 0)){
			systemUpdatesBanner.setText("YOU WIN!");
		} else {
			systemUpdatesBanner.setText("WOW! YOU LOSE!");
		}
	}
	
	public void updateScoreCountBanner(boolean player1Wins){
		if(player1Wins){
			numPlayer1Wins++;
		} else {
			numPlayer2Wins++;
		}
		
		if(playerNum == 0){
			scoreCountBanner.setText(playerName + ": " + numPlayer1Wins + " " + otherPlayerName + ": " + numPlayer2Wins);
		} else {
			scoreCountBanner.setText(otherPlayerName + ": " + numPlayer1Wins + " " + playerName + ": " + numPlayer2Wins);
		}
		
		
	}
	//*****END game status update methods*****\\
	
	
	@Override
	public void actionPerformed(ActionEvent arg) {
		String buttonCommand = arg.getActionCommand();
		
		if(buttonCommand.equals("Launch")){
			try{
				int velocity = Integer.parseInt(velocityField.getText());
				int arcAngle = Integer.parseInt(arcAngleField.getText()); 
				if(arcAngle < 1){
					updateGameStatusBanner("ANGLE TOO LOW!");
					velocityField.setText("");
					arcAngleField.setText("");
					return;
				} else if (arcAngle > 90){
					updateGameStatusBanner("ANGLE TOO HIGH!");
					velocityField.setText("");
					arcAngleField.setText("");
					return;
				}
				myGamePlayer.sendMessage(new MyGameInput(playerNum, arcAngle, velocity, command.FIRE, playerName));
				
			} catch (NumberFormatException e){
				updateGameStatusBanner("INPUT MUST BE IN INTEGERS!");
			} finally {
				velocityField.setText("");
				arcAngleField.setText("");
			}
		} else if(buttonCommand.equals("Quit...")){
			quitWindow.setVisibility(true);
		}
	}
	
	//*****START interface-required methods*****\\
	@Override
	public void startUserInterface(GamePlayer player) {
		myGamePlayer = player;
		playerName = player.getPlayerName();
		myGamePlayer.sendMessage(new MyGameInput(0, 0, 0, Command.INITIALIZE, playerName));
		
	}
	
	public void receivedMessage(Object ob) {
		MyGameOutput gameOutput = (MyGameOutput)ob;
		
		switch(gameOutput.getCommand()){
		case INITIALIZE:
			if(playerNum == 10){ //assign the playerNum value on initialization.  Because players sign on one before the other, the MyGame assigns playerNums sequentially
				playerNum = gameOutput.getPlayerNum();
			}
			
			if(playerNum < 2){ //locks out a player with a playerNum greater than 1 (prevents more than 2 to join)
				if(!gameBoardexists){
					gbLogic = gameOutput.getGameBoardLogic();
					gameBoard = new GameBoard(gbLogic.getBuildings(), gbLogic.getPlayers(), gbLogic.getWidth(), gbLogic.getHeight());
					doInputWindowLayout();
					gameBoardexists = true;
					
				}
			}
			
			if(playerNum == 0){ //gets the other player's name for banner update purposes
				otherPlayerName = gameOutput.getPlayer2Name();
			} else if(playerNum == 1){
				otherPlayerName = gameOutput.getPlayer1Name(); 
			}
			
			updateGameStatusBanner(gameOutput.getGameStatus());
			updateSystemStatusBanner(gameOutput.getSystemStatus());
			return;
			
		case FIRE:
			gameBoard.updateGameBoard(gameOutput.getXProjectile(), gameOutput.getYProjectile());
			updateGameStatusBanner(gameOutput.getGameStatus());
			updateSystemStatusBanner(gameOutput.getSystemStatus());
			
			if(gameOutput.getPlayer1Wins() || gameOutput.getPlayer2Wins()){ //logic for when a player is hit
				updateScoreCountBanner(gameOutput.getPlayer1Wins());
				updateSystemStatusBanner(gameOutput.getPlayer1Wins());
				launch.setVisible(false);
				try{ //start a new game automatically with threaded timers
					Thread.sleep(2000);
					updateSystemStatusBanner("Starting a new game.");
					Thread.sleep(2000);
					launch.setVisible(true);
					if(playerNum == 0){ //this avoids having to calls to reset the game
						MyGameInput resetInput = new MyGameInput(0, 0, 0, Command.RESET, "");
						myGamePlayer.sendMessage(resetInput);
					}
				} catch (InterruptedException e){
					System.out.println("INTERRUPTED THE SLEEPING THREAD!");
				}
			}
			return;
		
		case RESET: //resets the game board and updates banners
			gbLogic = gameOutput.getGameBoardLogic();
			gameBoard.resetGameBoard(gbLogic.getBuildings(), gbLogic.getPlayers(), gbLogic.getWidth(), gbLogic.getHeight());
			updateSystemStatusBanner(gameOutput.getSystemStatus());
			return;
			
		case EXIT: //logic for a clean, controlled exit
			updateGameStatusBanner(gameOutput.getGameStatus());
			try {
				Thread.sleep(2000);
				updateSystemStatusBanner("Now exiting Java Gorillas.");
				Thread.sleep(2000);
				scoreCountBanner.setText("k bye");
				System.exit(0);
			} catch (InterruptedException e) {
				System.out.println("INTERRUPTED THE SLEEPING THREAD!");
			}
			
		
		}
	}
	
}