package projectileGame;

import java.io.Serializable;

public class MyGameInput implements Serializable {

	private String playerName;
	private int playerNum, arcAngle, velocity;
	private Command command;
	
	MyGameInput(int playerNum, int arcAngle, int velocity, Command command, String playerName){
		this.playerNum = playerNum;
		this.arcAngle = arcAngle;
		this.velocity = velocity;
		this.command = command;
		this.playerName = playerName;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
	public int getArcAngle(){
		return arcAngle;
	}
	
	public int getVelocity(){
		return velocity;
	}
	
	public Command getCommand(){
		return command;
	}
	
	public String getPlayerName(){
		return playerName;
	}
}
