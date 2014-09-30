

import java.util.Scanner;

public class Gorillas { //a simple class to tie the game together
	
	String player1Name = "";
	String player2Name = "";
	GameBoardLogic gameBoardLogic;
	GameLogic gameLogic;
	GUI gui;
	UserInterface userInterface;
	
	public void getNames(){ //get the names of the two opponents
		Scanner keyboard = new Scanner(System.in);
		
		if(player1Name.equals("")){
			System.out.println("Please enter Player 1's name:");
		} else {
			System.out.println("Please enter Player 2's name:");
		}
		
		String name = keyboard.nextLine();
		
		if(name.equals("")){
			if(player1Name.equals("")){ //default to "Player 1/2"
				name = "Player 1";
				player1Name = name;
			} else {
				name = "Player 2";
				player2Name = name;
			}	
		} else if (player1Name.equals("")){
			player1Name = name;	
		} else {
			player2Name = name;
		}
		System.out.println(name + " enters...");
		return;
	}
	
	public void intializeGame() { //calls up everything initially needed to play the first round, in the order necessary
		gameBoardLogic = new GameBoardLogic();
		gameLogic = new GameLogic(player1Name, player2Name, gameBoardLogic);
		gui = new GUI(gameLogic.dataToGUI);
		userInterface = new UserInterface(gameLogic.dataToUserInterface, this);
	}
	
	public void talkToGame(DataToGame dataToGame){ //sends information to be processed by the game logic, and updates the GUI and user interface
		gameLogic.playTurn(dataToGame);
		gui.updateGameBoard(gameLogic.xProjectile, gameLogic.yProjectile);
		talkToInterface(gameLogic.dataToUserInterface);
		
		if(gameLogic.roundWon){ //checks if the round was won, and initializes a new gameboard if so
			userInterface.newRoundForUserInterface();
		}
	}

	private void talkToInterface(DataToUserInterface dataToUserInterface){ //update the relevant banners in the interface
		userInterface.setScoreBanner(dataToUserInterface.scoreBanner);
		userInterface.setGeneralBanner(dataToUserInterface.generalBanner);
		userInterface.setSpecificBanner(dataToUserInterface.specificBanner);
		
	}
	
	public void newRound(){ //resets the gameboard and game logic whenever a player gets a hit
		gameBoardLogic = new GameBoardLogic();
		gameLogic.newRound(gameBoardLogic);
		gui.newRound(gameLogic.dataToGUI);
		userInterface.setGeneralBanner(gameLogic.generalBanner); //I'm cheating by not using the dataToUserInterface class!
		userInterface.setSpecificBanner(gameLogic.specificBanner); //I'm cheating by not using the dataToUserInterface class!
	}
	
	public static void main(String[] args) {
		Gorillas gorilla = new Gorillas();
		
		while(gorilla.player2Name.equals("")){gorilla.getNames();}
		
		gorilla.intializeGame();
	}

}
