

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

class QuitWindow extends JFrame implements QuitWindowInterface{ //used to exit the game cleanly
	private JButton yes = new JButton("Yes");
	private JButton no = new JButton("No");
	private JLabel message = new JLabel("");

	String[] quitMessages = { //quit messages from game Wolfenstein 3D
			"<html>Dost thou wish to leave<br>with such hasty abandon?</html>",
			"<html>Chickening out...already?</html>",
			"<html>Click No for more carnage.<br>Click Yes to be a weenie.</html>",
			"<html>So, you think you can<br>quit this easily, huh?</html>",
			"<html>Click No to save the world.<br>Click Yes to abandon it in its hour of need.</html>",
			"<html>Click No if you are brave.<br>Click Yes to cower in shame.</html>",
			"<html>Heroes, click No.<br>Wimps, click Yes.</html>",
			"<html>You are at an intersection.<br>A sign says, 'Click Yes to quit.'</html>",
			"<html>For guns and glory, click No.<br>For work and worry, click Yes.</html>"
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
//*************************\\

public class UserInterface extends JFrame implements ActionListener, Runnable {
	
	//JComponent variables
	private JLabel inputBanner = new JLabel("Player Input"); //stays static
	private JLabel scoreBanner = new JLabel(); //keeps count of score
	private JLabel generalBanner = new JLabel(); //gives general results of a turn
	private JLabel specificBanner = new JLabel(); //gives specific results (ie building # hit, if a player is hit)
	
	
	private JTextField velocityField = new JTextField(2);
	private JTextField arcAngleField = new JTextField(2);
	//private JButton launch, quit;
	JButton launch, quit; //view set to default for now, until I can figure out how to make the roundWon method work properly 
	private QuitWindowInterface quitWindow = new QuitWindow();
	private Gorillas gorilla;

	public UserInterface(DataToUserInterface dataUserInterface, Gorillas gorilla) {
		super("Java Gorillas");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); //to keep a player from exiting via closing a window
		generalBanner.setText(dataUserInterface.generalBanner);
		specificBanner.setText(dataUserInterface.specificBanner);
		scoreBanner.setText(dataUserInterface.scoreBanner);
		this.gorilla = gorilla; //the creating main (main is used to talk back and forth to the game and interface)
		
		quitWindow.addActionListener(new ActionListener() { //adding this to the constructor seems inelegant, but it's what a response on StackOverload recommended 
            public void actionPerformed(ActionEvent arg) {
            	String buttonCommand = arg.getActionCommand();
            	if(buttonCommand.equals("No")){
            		quitWindow.setVisibility(false);
            	} else {
            		System.exit(0);
            		quitWindow.setVisibility(false);
            	}
            		
            }
        });
		
		doInputWindowLayout();
	}
	
	//*****START set banner message methods*****\\	
	void setScoreBanner(String scoreBannerMessage){
		scoreBanner.setText(scoreBannerMessage);
	}
	
	void setGeneralBanner(String generalBannerMessage){
		generalBanner.setText(generalBannerMessage);
	}
	
	void setSpecificBanner(String specificBannerMessage){
		specificBanner.setText(specificBannerMessage);
	}
	//*****end set banner message methods*****\\
	
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
	
	private void inputWindowAddNorthPanel(){ //two topmost banners: score and input (which is static)
		JPanel northPanel = new JPanel();
	    northPanel.setLayout(new GridLayout(2,1));
	    northPanel.add(inputBanner);
		northPanel.add(scoreBanner);
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	private void inputWindowAddCenterPanel(){ //middle two panels: where the user enters angle and velocity
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
		southPanel.add(specificBanner);
		southPanel.add(generalBanner);
		
		centerPanel.add(inputAreas, BorderLayout.NORTH);
		centerPanel.add(southPanel, BorderLayout.SOUTH);
		
		add(centerPanel, BorderLayout.CENTER);
	}	
	
	private void inputWindowAddSouthPanel(){ //two buttons: launch and quit
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
	
	@Override
	public void actionPerformed(ActionEvent arg) { //what happens when the launch button is pressed 
		String buttonCommand = arg.getActionCommand();
		
		if(buttonCommand.equals("Launch")){ //launch projectile
			try{
				int velocity = Integer.parseInt(velocityField.getText());
				int arcAngle = Integer.parseInt(arcAngleField.getText()); 
				if(arcAngle < 1){ //check for angle
					setSpecificBanner("ANGLE TOO LOW!");
					velocityField.setText("");
					arcAngleField.setText("");
					return;
				} else if (arcAngle > 90){
					setSpecificBanner("ANGLE TOO HIGH!");
					velocityField.setText("");
					arcAngleField.setText("");
					return;
				}
			gorilla.talkToGame(new DataToGame(arcAngle, velocity));	
				
			} catch (NumberFormatException e){ //ensures both entries are numbers
				setSpecificBanner("INPUT MUST BE IN INTEGERS!");
			} finally {
				velocityField.setText("");
				arcAngleField.setText("");
			}
		} else if(buttonCommand.equals("Quit...")){
			quitWindow.setVisibility(true);
		}
	}
	
	public void newRoundForUserInterface(){ //method used to pause the game between rounds
		Thread newRoundThread = new Thread(this);
		newRoundThread.start();
	}

	@Override
	public void run() { //threading to pause the game between rounds
		try {
			launch.setVisible(false);
			Thread.sleep(1500);
			setSpecificBanner("Starting a new round...");
			Thread.sleep(1500);
			launch.setVisible(true);
			gorilla.newRound();
		} catch (InterruptedException e) {
			System.out.println("INTERRUPTED THE SLEEPING THREAD!");
		}
		
	}
}