

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

class GameLogic  implements Serializable{ //a single instance of this class is called.  It's where the "brains" of the game live and where the game is played and messages are sent. 
	
	//virtual gameboard objects
	private int width, height;
	private GameBoardLogic gameBoardLogic;
	
	//used to play game
	private Rectangle[] buildings = new Rectangle[7]; 
	private Rectangle[] players = new Rectangle[2];
	
	//used to pass to GUI
	private BuildingWrapper[] buildingsToGUI;
	private ObjectWrapper[] playersToGUI;
	private int xPlayer1, xPlayer2, yPlayer1, yPlayer2;	//player starting coordinates
	
	//projectile related variables
	private double gravity = 2; //hard coded for now
	ArrayList<Double> xProjectile = new ArrayList<Double>();
	ArrayList<Double> yProjectile = new ArrayList<Double>();
	
	//game status related variables
	private boolean player1Active;
	private String player1Name, player2Name;
	String scoreBanner, generalBanner, specificBanner;
	public int player1Score = 0; public int player2Score = 0;
	boolean roundWon = false;
	
	//wrappers to transport data
	public DataToGUI dataToGUI;
	public DataToUserInterface dataToUserInterface;

	//*****START constructor type methods*****\\
	GameLogic(String player1Name, String player2Name, GameBoardLogic gameBoardLogic){
		this.player1Name = player1Name;
		this.player2Name = player2Name;
		this.gameBoardLogic = gameBoardLogic;
		buildingsToGUI = gameBoardLogic.getBuildings();
		playersToGUI = gameBoardLogic.getPlayers();
		
		//set active player (1 or 2), set up the gameboard with the game logic, set initial banners
		setActivePlayer();
		setVariables();
		setBanners();
		
		//send out initial data packages
		dataToUserInterface = new DataToUserInterface(scoreBanner, generalBanner, specificBanner);
		dataToGUI = new DataToGUI(width, height, buildingsToGUI, playersToGUI, xProjectile, yProjectile);
	}
	
	public void newRound(GameBoardLogic gameBoardLogic){ //an attenuated version of the constructor, designed to start a round, but keep the object intact
		this.gameBoardLogic = gameBoardLogic;
		buildingsToGUI = gameBoardLogic.getBuildings();
		playersToGUI = gameBoardLogic.getPlayers();
		
		setActivePlayer();
		setVariables();
		setBanners();
		
		dataToUserInterface = new DataToUserInterface(scoreBanner, generalBanner, specificBanner);
		dataToGUI = new DataToGUI(width, height, buildingsToGUI, playersToGUI, xProjectile, yProjectile);
		
		xProjectile.clear();
		yProjectile.clear();
		roundWon = false;
	}
	//*****END constructor type methods*****\\
	
	//*****START initialization methods*****\\
	private void setActivePlayer(){ //randomly sets active player for beginning of every new round
		if(randInt(1, 0) == 0){ 
			player1Active = true;
		} else {
			player1Active = false;
		}
	}
	
	private void setVariables(){ //set at the start of every new round
		width = gameBoardLogic.getWidth();
		height = gameBoardLogic.getHeight();
		setBuildings(gameBoardLogic.getBuildings());
		setPlayers(gameBoardLogic.getPlayers());
		
		xPlayer1 = gameBoardLogic.getPlayerX(0);
		xPlayer2 = gameBoardLogic.getPlayerX(1);
		yPlayer1 = gameBoardLogic.getPlayerY(0);
		yPlayer2 = gameBoardLogic.getPlayerY(1);
		
		xProjectile.clear();
		yProjectile.clear();
	}
	
	private void setBanners(){ //set at the start of every new round
		if(player1Score == 0 && player2Score == 0){
			scoreBanner = "No score yet.";
		}
		
		specificBanner = "Ready to play.";
		
		if(player1Active) {
			generalBanner = player1Name + "'s turn.";
		} else {
			generalBanner = player2Name + "'s turn.";
		}
	}
	//*****END initialization methods*****\\
	
	//*****START set buildings and players methods*****\\
	private void setBuildings(BuildingWrapper[] buildingsWrapper){ //set up buildings for play
		for(int i = 0; i < buildings.length; i++){
			buildings[i] = new Rectangle(buildingsWrapper[i].getX(), buildingsWrapper[i].getY(), buildingsWrapper[i].getWidth(), buildingsWrapper[i].getHeight());
		}
	}
		
	private void setPlayers(ObjectWrapper[] playersWrapper){ //set up players for play
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
	private boolean hitBuildings(double x, double y, int arcAngle, int velocity){ //hit detection: test if current coordinates are in a building
			
		for(int i = 0; i < buildings.length; i++){
			if(buildings[i].contains(x, y)){
				specificBanner = "Arc: " + arcAngle + " Velocity: " + velocity + ". Building " + (i + 1) + " hit.";
				return true;
			}
		}
			
		return false;
	}
		
	private boolean hitPlayer(double x, double y, int arcAngle, int velocity){ //hit detection: test if current coordinates are in a player rectangle.  Tests for opposite player from active player
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
			if(player1Active){
				generalBanner = player1Name + " WINS THE ROUND!";
			} else {
				generalBanner = player2Name + " WINS THE ROUND!";
			}
			
				specificBanner = "Arc: " + arcAngle + " Velocity: " + velocity + ". " + playerKilled.toUpperCase() + " HIT!";
				return true;
			}		
				
		return false;
	}
		
	private boolean offBoard(double x){ //check for out of bounds 
		if(x > width || x < 0){
			specificBanner = "Projectile launched out of bounds.";
			return true;
		}
			
		return false;
	}
	//*****END hit detection logic methods*****\\
		
	//*****START game play methods*****\\
	public boolean launchProjectiles(int arcAngle, int velocity){ //method to launch projectiles.  Returns true if a player is hit  
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
			time = time + .01; //increment by .01 of a second...this can be changed for a more accurate arc 
		}
		
		return hit;		
			
	}
	
	public boolean cheating(int arcAngle, int velocity){ //added a way to cheat to test new round activities
		
		if(arcAngle == 5 && velocity == 5){ //cheating 
			if(player1Active){
				generalBanner = player1Name + " CHEATED!";
				specificBanner = player1Name + " WINS THE ROUND!";
			} else {
				generalBanner = player2Name + " CHEATED!";
				specificBanner = player2Name + " WINS THE ROUND!";
			}
			return true;
		}
		
		return false;
	}
	
	public void playTurn(DataToGame dataToGame){ //play a turn, get results, and update the banner messages as appropriate
		boolean hit;
		
		if(cheating(dataToGame.arcAngle, dataToGame.velocity)){ //cheating 
			hit = true;
		} else {
			hit = launchProjectiles(dataToGame.arcAngle, dataToGame.velocity); // launch projectile and check where projectile landed
		}
		
		
		
		if(hit){ //if projectile hit a player, indicate so
			if(player1Active){
				player1Score++;
			} else {
				player2Score++;
			}
			scoreBanner = player1Name + ": " + player1Score + " " + player2Name + ": " + player2Score;
			roundWon = true;
		} else {
			if(player1Active){
				generalBanner = player1Name + " missed. " + player2Name + "'s turn.";
			} else {
				generalBanner = player2Name + " missed. " + player1Name + "'s turn.";
			}
		}
			
		if(player1Active){ //switch active player
			player1Active = false;
		} else {
			player1Active = true;
		}
		
		//update banner messages
		dataToUserInterface.generalBanner = generalBanner;
		dataToUserInterface.specificBanner = specificBanner;
		dataToUserInterface.scoreBanner = scoreBanner;
	}
	//*****END game play methods*****\\

}