
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

class ObjectWrapper implements Serializable { //wrapper class used to send data back and forth (good enough for building windows)
	private int x, y, width, height;
	private Color color;
	
	
	ObjectWrapper(int x, int y, int width, int height, Color color){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public Color getColor(){
		return color;
	}
}
//*******************************************************************************************\\
class BuildingWrapper extends ObjectWrapper implements Serializable {
	private ArrayList<ObjectWrapper> windows;
	
	BuildingWrapper(int x, int y, int width, int height, Color color, ArrayList<ObjectWrapper> windows){
		super(x, y, width, height, color);
		this.windows = windows;
	}
	
	public ArrayList<ObjectWrapper> getWindows(){
		return windows;
	}
}
//*******************************************************************************************\\
public class GameBoardLogic implements Serializable{ //this class provides the coordinates of buildings and players needed to play and visualize the game
	//game board dimensions
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	//building dimensions
	private static final int BUILDING_WIDTH = 167;
	private static final int PADDING = 5;
	
	//wrapper classes to pass to GUI
	private BuildingWrapper[] buildingsWrapper = new BuildingWrapper[7];
	private ObjectWrapper[] playersWrapper = new ObjectWrapper[2];
	
	//player starting coordinates
	private int xPlayer1, xPlayer2, yPlayer1, yPlayer2;
	
	GameBoardLogic(){
		addBuildings();
		addPlayers();
	}
	
	//*****START building logic methods*****\\
	private void addBuildings(){ //loop to set up the coordinates that will generate the virtual game board and GUI game board buildings
		for(int i = 0; i < buildingsWrapper.length; i++){
			int startingX, startingY, endingY;
			startingY = randInt(700, 300); //random height, COULD BE ALTERED FOR A MORE CUSTOMIZABLE GAME
			endingY = HEIGHT;
			
			if(i == 0){ //first building is a special case
				startingX = 0;
			} else { //all other buildings
				startingX = (BUILDING_WIDTH * i) + (PADDING * i);
			}
			
			Color randColor = new Color(returnRandomColorValue(randInt(4,1)));
			ArrayList<ObjectWrapper> windows = addWindows(startingX, startingY);
			buildingsWrapper[i] = new BuildingWrapper(startingX, startingY, BUILDING_WIDTH, endingY, randColor, windows);
		}
	}
	
	private ArrayList<ObjectWrapper> addWindows(int startingX, int startingY){ //add windows to buildings (for GUI purposes: adds visual appeal)

		ArrayList<ObjectWrapper> windows = new ArrayList<ObjectWrapper>();
		ObjectWrapper window;
		Color randColor;
		
		//these could be set more dynamically or generically in order to make a more flexible game
		int windowWidth = 25;
		int windowHeight = 30;
		int windowPaddingX = 23;
		int windowPaddingY = 10;
		int windowY = startingY;
		int windowX = startingX;
		int windowCounter = 0;
		
		while (windowY < 1200){ //really ugly, might try and fix
			if(windowCounter == 0){
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + windowPaddingX), (windowY + windowPaddingY), windowWidth, windowHeight, randColor);
				windows.add(window);
				
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + (windowPaddingX * 2 + windowWidth)), (windowY + windowPaddingY), windowWidth, windowHeight, randColor);
				windows.add(window);
				
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + (windowPaddingX * 3 + windowWidth * 2)), (windowY + windowPaddingY), windowWidth, windowHeight, randColor);
				windows.add(window);
			} else {
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + windowPaddingX), (windowY + ((windowPaddingY * windowCounter) + (windowHeight * windowCounter))), windowWidth, windowHeight, randColor);
				windows.add(window);
				
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + (windowPaddingX * 2 + windowWidth)), (windowY + ((windowPaddingY * windowCounter) + (windowHeight * windowCounter))), windowWidth, windowHeight, randColor);
				windows.add(window);
				
				randColor = new Color(returnRandomColorValue(randInt(6,5)));
				window = new ObjectWrapper((windowX + (windowPaddingX * 3 + windowWidth * 2)), (windowY + ((windowPaddingY * windowCounter) + (windowHeight * windowCounter))), windowWidth, windowHeight, randColor);
				windows.add(window);
			}
			
			windowCounter++;
			windowY = windowY + (windowPaddingY + windowHeight);
		}
		
		return windows;
	}
	//*****END building logic methods*****\\

	//*****START player logic methods*****\\
	private void addPlayers(){ //create the player locations
		
		//set the starting point 20 less than center, to expand 20 in either direction for player rectangle
		int randomBuild1 = randInt(2, 0);
		int randomBuild2 = randInt(6, 4);
		int playerWidth = 40;
		int playerHeight = 45;
		
		//set the middle coordinate from which the player rectangle will be built and projectiles will be launched 
		xPlayer1 = getPlayerCenterPointX(buildingsWrapper[randomBuild1].getX());
		xPlayer2 = getPlayerCenterPointX(buildingsWrapper[randomBuild2].getX());
		yPlayer1 = getPlayerCenterPointY(randomBuild1, playerHeight);
		yPlayer2 = getPlayerCenterPointY(randomBuild2, playerHeight);
		
		//actually set the coordinates for the rectangles
		int xPlayer1Start = xPlayer1 - (playerWidth / 2);
		int xPlayer2Start = xPlayer2 - (playerWidth / 2);
		int yPlayer1Start = (buildingsWrapper[randomBuild1].getY() - playerHeight);
		int yPlayer2Start = (buildingsWrapper[randomBuild2].getY() - playerHeight);
		
		//set the player coordinates
		playersWrapper[0] = new ObjectWrapper(xPlayer1Start, yPlayer1Start, playerWidth, playerHeight, Color.BLACK);
		playersWrapper[1] = new ObjectWrapper(xPlayer2Start, yPlayer2Start, playerWidth, playerHeight, Color.BLACK);
		
	}
	
	private int getPlayerCenterPointX(int buildingStartX){ //the xCenter of the player rectangle (where projectiles originate from)
		 int buildingEndX = buildingStartX + BUILDING_WIDTH;    
		 return (buildingStartX + buildingEndX) / 2;
		 
	}
	
	private int getPlayerCenterPointY(int buildingStartY, int playerHeight){ //the yCenter of the player rectangle (where projectiles originate from)
		return (buildingsWrapper[buildingStartY].getY() - (playerHeight / 2));
	}
	//*****END player logic methods*****\\
	
	//*****START randomizer methods*****\\
	private int randInt(int max, int min) { //random number generator, for building height and color, adapted from Stack Overload
		   Random rand = new Random();
		   int randomNum = rand.nextInt((max - min) + 1) + min; //nextInt is normally exclusive of the top value, so add 1 to make it inclusive
	
		   return randomNum;
	}
	
	
	private int returnRandomColorValue(int i){ //for setting building and window colors
		
		switch(i){
		case(1):
			return Integer.parseInt("580000", 16); //dark red
		case(2):
			return Integer.parseInt("C0C0C0", 16); //light gray
		case(3):
			return Integer.parseInt("996600", 16); //burnt orange
		case(4):
			return Integer.parseInt("585858", 16); //dark gray
		case(5):
			return Integer.parseInt("FFFF00", 16); //yellow reserved for windows
		case(6):
			return Integer.parseInt("000000", 16); //black reserved for windows
		} 
		
		return -1; //shouldn't get here
	}
	//*****END randomizer methods*****\\
	
	//*****START "get" methods*****\\
	public int getWidth(){
		return WIDTH;
	}
	
	public int getHeight(){
		return HEIGHT;
	}
	
	public int getBuildingWidth(){
		return BUILDING_WIDTH;
	}
	
	public int getPadding(){
		return PADDING;
	}
	
	public BuildingWrapper[] getBuildings(){
		return buildingsWrapper;
	}
	
	public ObjectWrapper[] getPlayers(){
		return playersWrapper;
	}
	
	public int getPlayerX(int playerNumber){
		if(playerNumber == 0){
			return xPlayer1;
		} else if(playerNumber == 1){
			return xPlayer2;
		} else {
			return -1; //shouldn't get here
		}
	}
	
	public int getPlayerY(int playerNumber){
		if(playerNumber == 0){
			return yPlayer1;
		} else if(playerNumber == 1){
			return yPlayer2;
		} else {
			return -1; //shouldn't get here
		}
	}
}
