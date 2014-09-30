package projectileGame;

import java.io.Serializable;
import java.util.ArrayList;

public class MyGameOutput implements Serializable {
	private int playerNum;
	private ArrayList<Double> xProjectile, yProjectile;
	private boolean player1Wins, player2Wins;
	private String gameStatus, systemStatus;
	private GameBoardLogic gbLogic;
	private Command command;
	private String player1Name, player2Name;
	
	MyGameOutput(int playerNum, ArrayList<Double> xProjectile, ArrayList<Double> yProjectile, boolean player1Wins, boolean player2Wins, String gameStatus, GameBoardLogic gbLogic, Command command, String systemStatus, String player1Name, String player2Name){

		this.playerNum = playerNum;
		this.xProjectile = xProjectile;
		this.yProjectile = yProjectile;
		this.player1Wins = player1Wins;
		this.player2Wins = player2Wins;
		this.gameStatus = gameStatus;
		this.gbLogic = gbLogic;
		this.command = command;
		this.systemStatus = systemStatus;
		this.player1Name = player1Name;
		this.player2Name = player2Name;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
  	public ArrayList<Double> getXProjectile(){
		return xProjectile;
	}
	
	public ArrayList<Double> getYProjectile(){
		return yProjectile;
	}

	public boolean getPlayer1Wins(){
		return player1Wins;
	}
	
	public boolean getPlayer2Wins(){
		return player2Wins;
	}
	
	public String getGameStatus(){
		return gameStatus;
	}
	
	public GameBoardLogic getGameBoardLogic(){
		return gbLogic;
	}
	
	public Command getCommand(){
		return command;
	}
	
	public String getSystemStatus(){
		return systemStatus;
	}
	
	public String getPlayer1Name(){
		return player1Name;
	}
	
	public String getPlayer2Name(){
		return player2Name;
	}

}
