package projectileGame;
import java.io.*;
import java.util.*;


import gameNet.*;



// ***************************************************
public class MyMain extends GameCreator{   
 
  public GameNet_CoreGame createGame()
  {
	  return new MyGame();
  }
  

  public static void main(String[] args) throws IOException 
  {   
  	MyMain myMain = new MyMain();
  	GameNet_UserInterface myUserInterface = new MyUserInterface();
    
  	myMain.enterGame(myUserInterface); 
  }// end of main
}// end of class
