

import java.util.ArrayList;

class DataToGUI { //a clean wrapper to send data from the game logic to the GUI to be visualized

	int width, height;
	BuildingWrapper[] buildings;
	ObjectWrapper[] players;
	ArrayList<Double> xProjectile, yProjectile;
	
	DataToGUI(int width, int height, BuildingWrapper[] buildings, ObjectWrapper[] players, ArrayList<Double> xProjectile, ArrayList<Double> yProjectile){
		this.width = width;
		this.height = height;
		this.buildings = buildings;
		this.players = players;
		this.xProjectile = xProjectile;
		this.yProjectile = yProjectile;
	}
	
	
}
