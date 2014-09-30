

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class GUI extends JFrame {
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	GamePanel gamePanel;
	
	public GUI (DataToGUI dataToGUI){
		super("Java Gorillas");	
		setVisible(true);
	    setResizable(false);
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    setSize(1200,800);
	    //setAlwaysOnTop(true);
	    gamePanel = new GamePanel(dataToGUI);
	    setContentPane(gamePanel);
	}
	
	public void updateGameBoard(ArrayList<Double> xProjectile, ArrayList<Double> yProjectile){  
		gamePanel.updateGameBoard(xProjectile, yProjectile);
	}
	
	public void newRound(DataToGUI dataToGUI){
		gamePanel.newRound(dataToGUI);
	}
}

class GamePanel extends JPanel {
	
	private BuildingWrapper[] buildings;
	private ObjectWrapper[] players;
	private ArrayList<Double> xProjectile, yProjectile;
	private String fileName = "gorilla.jpg";
	private Image gorilla;
	
	public GamePanel(DataToGUI dataToGUI) {
		this.buildings = dataToGUI.buildings;
        this.players = dataToGUI.players;
        
        gorilla = loadImage(fileName);
	}
	
	public void paint(Graphics g){
		Color sky = new Color(Integer.parseInt("0200A6", 16));
		
		g.setColor(sky); //fill in the background sky
		g.fillRect(0, 0, 1200, 800);
		
		
		
		for(int i = 0; i < buildings.length; i++){ //fill in the buildings and their windows
			g.setColor(buildings[i].getColor());
			g.fillRect(buildings[i].getX(), buildings[i].getY(), buildings[i].getWidth(), buildings[i].getHeight());
			drawWindows(buildings[i].getWindows(), g);
		}
		
		if(xProjectile != null){
			g.setColor(Color.WHITE); //draw the arc of the projectile as single point lines (could be animated in the future)
        	for(int i = 0; i < xProjectile.size(); i++){
        		int x = xProjectile.get(i).intValue();
        		int y = yProjectile.get(i).intValue();
        		g.drawLine(x, y, x, y);
        	}
		}
		
		g.drawImage(gorilla, players[0].getX(), players[0].getY(), players[0].getWidth(), players[0].getHeight(), null);
		g.drawImage(gorilla, players[1].getX(), players[1].getY(), players[1].getWidth(), players[1].getHeight(), null);
       
	}
	
	private void drawWindows(ArrayList<ObjectWrapper> windows, Graphics g){ //helper method to draw building windows
		for(int i = 0; i < windows.size(); i++){
			g.setColor(windows.get(i).getColor());
			g.fillRect(windows.get(i).getX(), windows.get(i).getY(), windows.get(i).getWidth(), windows.get(i).getHeight());
		}
	}
	
	public void updateGameBoard(ArrayList<Double> xProjectile, ArrayList<Double> yProjectile){  //for when projectiles are launched
		 this.xProjectile = xProjectile;
		 this.yProjectile = yProjectile;
		 repaint();
	}
	
	public void newRound(DataToGUI dataToGUI){ //redo the GUI after each round 
		this.buildings = dataToGUI.buildings;
        this.players = dataToGUI.players;
        xProjectile = null;
        yProjectile = null;
        repaint();
	}
	
	 private Image loadImage(String fileName) { //load gorilla images
		 return new ImageIcon(fileName).getImage();
     }
}


/*public class GUI extends JFrame{

	private int width, height;
	private BuildingWrapper[] buildings;
	private ObjectWrapper[] players;
	private ArrayList<Double> xProjectile, yProjectile;
	private String fileName = "gorilla.jpg";
	private Image gorilla;
	
	GUI(DataToGUI dataToGUI){
		super("Java Gorillas");
		
		this.width = dataToGUI.width;
        this.height = dataToGUI.height;
        this.buildings = dataToGUI.buildings;
        this.players = dataToGUI.players;
        
        gorilla = loadImage(fileName);
        
        setSize(width,height);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	public void paint(Graphics g){
		Color sky = new Color(Integer.parseInt("0200A6", 16));
		
		g.setColor(sky); //fill in the background sky
		g.fillRect(0, 0, width, height);
		
		
		
		for(int i = 0; i < buildings.length; i++){ //fill in the buildings and their windows
			g.setColor(buildings[i].getColor());
			g.fillRect(buildings[i].getX(), buildings[i].getY(), buildings[i].getWidth(), buildings[i].getHeight());
			drawWindows(buildings[i].getWindows(), g);
		}
		
		if(xProjectile != null){
			g.setColor(Color.WHITE); //draw the arc of the projectile as single point lines (could be animated in the future)
        	for(int i = 0; i < xProjectile.size(); i++){
        		int x = xProjectile.get(i).intValue();
        		int y = yProjectile.get(i).intValue();
        		g.drawLine(x, y, x, y);
        	}
		}
		
		g.drawImage(gorilla, players[0].getX(), players[0].getY(), players[0].getWidth(), players[0].getHeight(), null);
		g.drawImage(gorilla, players[1].getX(), players[1].getY(), players[1].getWidth(), players[1].getHeight(), null);
       
	}
	
	private void drawWindows(ArrayList<ObjectWrapper> windows, Graphics g){ //helper method to draw building windows
		for(int i = 0; i < windows.size(); i++){
			g.setColor(windows.get(i).getColor());
			g.fillRect(windows.get(i).getX(), windows.get(i).getY(), windows.get(i).getWidth(), windows.get(i).getHeight());
		}
	}
	
	public void updateGameBoard(ArrayList<Double> xProjectile, ArrayList<Double> yProjectile){  //for when projectiles are launched
		 this.xProjectile = xProjectile;
		 this.yProjectile = yProjectile;
		 repaint();
	}
	
	public void newRound(DataToGUI dataToGUI){ //redo the GUI after each round 
		this.buildings = dataToGUI.buildings;
        this.players = dataToGUI.players;
        xProjectile = null;
        yProjectile = null;
        repaint();
	}
	
	 private Image loadImage(String fileName) { //load gorilla images
		 return new ImageIcon(fileName).getImage();
     }
}*/
