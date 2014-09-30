package projectileGame;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import gameNet.GameControl;
import gameNet.GameNet_CoreGame;

/*This Game is inspired by and based off of:
 *Q B a s i c   G o r i l l a s
 *Copyright (C) Microsoft Corporation 1990
 */


public class MyGame  extends GameNet_CoreGame implements Serializable{
	//virtual gameboard objects
	private int width, height;
	private GameBoardLogic gbLogic;
	private Rectangle[] buildings = new Rectangle[7];
	private Rectangle[] players = new Rectangle[2];
	private int xPlayer1, xPlayer2, yPlayer1, yPlayer2;	//player starting coordinates
	
	//projectile related variables
	private double gravity;
	private ArrayList<Double> xProjectile = new ArrayList<Double>();
	private ArrayList<Double> yProjectile = new ArrayList<Double>();
	
	//game status related variables
	private int numPlayers;
	private boolean player1Active;
	private String gameStatus = "";
	private String systemStatus = "";
	private String player1Name, player2Name;

	MyGame(){
		setVariables();
		gravity = 2; //hard coded for now
	}
	
	private void setVariables(){ //set at the start of every new round
		gbLogic = new GameBoardLogic();
		width = gbLogic.getWidth();
		height = gbLogic.getHeight();
		setBuildings(gbLogic.getBuildings());
		setPlayers(gbLogic.getPlayers());
		
		xPlayer1 = gbLogic.getPlayerX(0);
		xPlayer2 = gbLogic.getPlayerX(1);
		yPlayer1 = gbLogic.getPlayerY(0);
		yPlayer2 = gbLogic.getPlayerY(1);
		
		xProjectile.clear();
		yProjectile.clear();
	}
	
	//*****START set buildings and players methods*****\\
	private void setBuildings(BuildingWrapper[] buildingsWrapper){
		for(int i = 0; i < buildings.length; i++){
			buildings[i] = new Rectangle(buildingsWrapper[i].getX(), buildingsWrapper[i].getY(), buildingsWrapper[i].getWidth(), buildingsWrapper[i].getHeight());
		}
	}
	
	private void setPlayers(ObjectWrapper[] playersWrapper){
		for(int i = 0; i < players.length; i++){
			players[i] = new Rectangle(playersWrapper[i].getX(), playersWrapper[i].getY(), playersWrapper[i].getWidth(), playersWrapper[i].getHeight());
		}
	}
	//*****END set buildings and players methods*****\\
	
	//*****START randomizer method*****\\
	private int randInt(int max, int min) { //random number generator, for building height and color, adapted from Stack Overload
		   Random rand = new Random();
		   int randomNum = rand.nextInt((max - min) + 1) + min; //nextInt is normally exclusive of the top value, so add 1 to make it inclusive
	
		   return randomNum;
	}
	//*****END randomizer method*****\\
	
	//*****START hit detection logic methods*****\\
	private boolean hitBuildings(double x, double y, int arcAngle, int velocity){ //hit detection: test if the coordinates are in a building
		
		for(int i = 0; i < buildings.length; i++){
			if(buildings[i].contains(x, y)){
				gameStatus = "Arc: " + arcAngle + " Velocity: " + velocity + ". Building " + (i + 1) + " hit.";
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hitPlayer(double x, double y, int arcAngle, int velocity){ //needs to test for opposite player
		int testingPlayer;	
		String playerKilled;
		
		if (player1Active){ //flip playerNumber for testing
				testingPlayer = 1;
				playerKilled = player2Name;
			} else {
				testingPlayer = 0;
				playerKilled = player1Name;
			}
		
			if(players[testingPlayer].contains(x, y)){
				systemStatus = "winningMessage";
				gameStatus = "Arc: " + arcAngle + " Velocity: " + velocity + ". " + playerKilled.toUpperCase() + " HIT!";
				return true;
			}		
			
			return false;
	}
	
	private boolean offBoard(double x){
		if(x > width || x < 0){
			gameStatus = "Projectile launched out of bounds.";
			return true;
		}
		
		return false;
	}
	//*****END hit detection logic methods*****\\
	
	public boolean launchProjectiles(int arcAngle, int velocity){
		xProjectile.clear();
		yProjectile.clear();
		
		double radians = Math.toRadians(arcAngle);
		
		double initVx = velocity * Math.cos(radians);
		double initVy = velocity * Math.sin(radians);

		double positionX = initVx;
		double positionY = initVy;

		double time = .1; //change this to adjust the granularity of the drawn arcs
		
		boolean hit = false;
		
		if(player1Active){ //start the projectile's launch at the player's center coordinates
			xProjectile.add((double) xPlayer1); 
			yProjectile.add((double) yPlayer1);
		} else {
			xProjectile.add((double) xPlayer2);
			yProjectile.add((double) yPlayer2);
		}
				
		while(positionY < width){ //send the projectile left or right, based on active player
			if(player1Active){
				positionX = (initVx * time) + xPlayer1;
				positionY = (yPlayer1 - initVy * time + gravity * time * time/2);
			} else {
				positionX = xPlayer2- (initVx * time);
				positionY = (yPlayer2 - initVy * time + gravity * time * time/2);
			}
			
			if(hitBuildings(positionX, positionY, arcAngle, velocity)){ //check if hit a building
				break;
			} else if(hitPlayer(positionX, positionY, arcAngle, velocity)){ //check if hit a player
				hit = true;
				break;
			} else if(offBoard(positionX)){ //check if out of bounds
				break;
			}
			
			xProjectile.add(positionX);
			yProjectile.add(positionY);
			time = time + .01;
		}
		
		
		return hit;
		
		
	}
	
	@Override
	public MyGameOutput process(Object ob) {
		MyGameInput gameInput = (MyGameInput) ob;
		MyGameOutput gameOutput;
		
		int arcAngle = gameInput.getArcAngle();
		int velocity = gameInput.getVelocity();
		int playerNum = gameInput.getPlayerNum();
		
		boolean player1Wins = false;
		boolean player2Wins = false;
		
		switch(gameInput.getCommand()){
		case INITIALIZE:
			if(numPlayers == 2){ //locking out any players beyond 2 players
				System.out.println("2 players already. CAN'T JOIN GAME!");
				systemStatus = "Player blocked from entering!";
				playerNum = 2;
				break;
			} else {
				if(numPlayers == 0){ //sets a boolean flag (player1Active) to lock out input from other player when it's not his/her turn
					player1Active = true;
					playerNum = 0; //this is a roundabout way to set the playerNumber in the GUI--on initialization, the GUI passes in a 0 and gets passed back a 0 or 1, depending on # of active players.  This number doesn't get set to anything else.  It is mostly used to control who sees what in their status window.
					player1Name = gameInput.getPlayerName();
					player2Name = "Kirk Johnson"; //placeholder until it's set next time
					gameStatus = "Waiting for 2nd player.";
				} else if (numPlayers == 1){
					playerNum = 1;
					player2Name = gameInput.getPlayerName();
					gameStatus = "Ready to play. " + player1Name + " active";
				}
				systemStatus = gameInput.getPlayerName() + " enters...";
				numPlayers++;
				break;	
			}
			
		case FIRE:
			if((player1Active && playerNum != 0) ||(!player1Active && playerNum != 1)){ //checks if you're the active player
				gameStatus = "WAIT YOUR TURN " + gameInput.getPlayerName().toUpperCase() + "!";
				break;
			}
			
			if(numPlayers < 2){ //locking out player from launching projectiles if he/she is the only one online
				gameStatus = "WAITING FOR 2ND PLAYER!";
				break;
			}
			
			boolean hit = launchProjectiles(arcAngle, velocity); // launch projectile and check where projectile landed
			
			if(hit){ //if projectile hit a player, indicate so
				if(playerNum == 0){
					player1Wins = true;
				} else {
					player2Wins = true;
				}
			} else {
				if(playerNum == 0){
					systemStatus = player1Name + " missed. " + player2Name + "'s turn.";
				} else {
					systemStatus = player2Name + " missed. " + player1Name + "'s turn.";
				}
			}
				
			if(player1Active){ //switch active player
				player1Active = false;
			} else {
				player1Active = true;
			}
			
			
			break;
			
		case RESET: //resets the playing field for another round 
			setVariables();
			player1Active = true;
			systemStatus = "New game started. " + player1Name + "'s turn.";
			break;
			
		case EXIT: 
			gameStatus = gameInput.getPlayerName() + " is quitting.";
			break;
		}
		
		gameOutput = new MyGameOutput(playerNum, xProjectile, yProjectile, player1Wins, player2Wins, gameStatus, gbLogic, gameInput.getCommand(), systemStatus, player1Name, player2Name);
		
		return gameOutput;
	}
}
