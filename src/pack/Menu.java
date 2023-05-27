package pack;


import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enigma.console.Console;
import enigma.console.TextAttributes;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class Menu {
	public enigma.console.Console cn = Enigma.getConsole("MENU", 180, 50);
	public TextMouseListener tmlis; 
	public KeyListener klis; 

	// ------ Standard variables for mouse and keyboard ------
	public int mousepr;          // mouse pressed or not
	public int mousex, mousey;   // mouse text coords.
	public int keypr;   // key pressed?
	public int rkey;    // key   (for press/release)
	// ----------------------------------------------------
	
	private static TextAttributes blackSelect = new TextAttributes(Color.WHITE, Color.BLACK);
	private static TextAttributes yellowSelect = new TextAttributes(Color.YELLOW, Color.BLACK);
	private static TextAttributes redMenu = new TextAttributes(Color.RED, Color.BLACK);
	private static TextAttributes greenMenu = new TextAttributes(Color.GREEN, Color.BLACK);
	private static TextAttributes blueMenu = new TextAttributes(Color.BLUE, Color.BLACK);

	Menu() throws Exception {   // --- Contructor

		// ------ Standard code for mouse and keyboard ------ Do not change
		tmlis=new TextMouseListener() {
			public void mouseClicked(TextMouseEvent arg0) {}
			public void mousePressed(TextMouseEvent arg0) {
				if(mousepr==0) {
					mousepr=1;
					mousex=arg0.getX();
					mousey=arg0.getY();
				}
			}
			public void mouseReleased(TextMouseEvent arg0) {}
		};
		cn.getTextWindow().addTextMouseListener(tmlis);

		klis=new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(keypr==0) {
					keypr=1;
					rkey=e.getKeyCode();
				}
			}
			public void keyReleased(KeyEvent e) {}
		};
		cn.getTextWindow().addKeyListener(klis);
		////////////////////////////////////////////////////////////
		menu();

		while(true){

			if(mousepr==1){
				int px;int py;
				px=mousex; py= mousey;
				
				if( py==25 && px >= 18 && px < 23){
					clearScreen();
					Organization play = new Organization();
					mousepr=0;

				}
				
				if(py == 26 && px>28 && px<33){
					clearScreen();
					cn.setTextAttributes(blackSelect);
					readScore("scores.txt");
					cn.getTextWindow().setCursorPosition(101, 40);
					cn.getTextWindow().output(" BACK " , new TextAttributes(Color.BLACK , Color.WHITE));
					
					cn.getTextWindow().setCursorPosition(80, 40);
					cn.getTextWindow().output(" Reset Scores " , new TextAttributes(Color.BLACK , Color.WHITE));
					
					mousepr=0;
				}
				
				if(py==27 && px>=38 && px<43){
					
					clearScreen();
					howtoPlay();
					cn.getTextWindow().setCursorPosition(101, 40);
					cn.getTextWindow().output(" BACK " , new TextAttributes(Color.BLACK , Color.WHITE));

				}
				
				if(py==28 && px>53)
				{
					clearScreen();
					cn.setTextAttributes(blackSelect);
					readScore("developers.txt");
					cn.getTextWindow().setCursorPosition(101, 40);
					cn.getTextWindow().output(" BACK " , new TextAttributes(Color.BLACK , Color.WHITE));
					mousepr=0;
					
				}
				
				if(py==29 && px>=53 && px<68)
				{
					System.exit(1);
					mousepr=0;
				}
				
				if(py==40 && px>=80 && px<90)
				{
					clearScreen();
					resetScore();
					menu();
					mousepr=0;
				}
				
				if(py==40 && px>=101 && px<110)
				{
					clearScreen();
					menu();
					mousepr=0;
				}
				

			}
			mousepr=0;
			Thread.sleep(20);
		}


	}

	public void menu() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		playSound("Menuu");
		
		cn.setTextAttributes(blackSelect);
		readScore("menu2.txt");
		
		cn.setTextAttributes(yellowSelect);
		cn.getTextWindow().setCursorPosition(18, 25);
		System.out.println("Play");
		
		cn.getTextWindow().setCursorPosition(25, 26);
		System.out.println("Best Scores");
		
		cn.getTextWindow().setCursorPosition(38, 27);
		System.out.println("How to play");
		
		cn.getTextWindow().setCursorPosition(52, 28);
		System.out.println("Developers");
		
		cn.getTextWindow().setCursorPosition(65, 29);
		System.out.println("Exit");
		

	}
	public void howtoPlay() throws Exception{
	
		
		cn.getTextWindow().setCursorPosition(10, 5);
		System.out.println("To change your way: Arrow Buttons (^,v,>,<)");
		
		cn.getTextWindow().setCursorPosition(10, 7);
		System.out.println("Put your back : 'W - A - S - D' button");
		
		cn.getTextWindow().setCursorPosition(10, 9);
		System.out.println("Put on board: 'I - J - K - L' button");
		
		cn.getTextWindow().setCursorPosition(101, 40);
		cn.getTextWindow().output(" BACK " , new TextAttributes(Color.BLACK , Color.BLUE)); 
		

	}
	
	
	public void clearScreen()
	{
		int px=0;
		int py=0;
		
		for (int i = 0; i < 50; i++) {


			cn.getTextWindow().setCursorPosition(px, py);
			cn.getTextWindow().output("                                                                                "
					+ "                                                                                               ");
			py++;
		}
		cn.getTextWindow().setCursorPosition(0, 0);
	}
	
	public void playSound(String fileName) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(fileName +".wav").getAbsoluteFile());
		
		Clip clip = AudioSystem.getClip();
		
		clip.open(audioIn);
		
		clip.start();
		
	}
	
	public void readScore(String name) throws IOException
	{
		
		String line;				

		File file = new File(name);

		FileReader fr = new FileReader(file);

		BufferedReader br = new BufferedReader(fr);

		while ((line = br.readLine()) != null) 
		{
			System.out.println(line);
		}

		br.close();
		
	}
	
	public void resetScore() throws IOException{
		
		File file = new File("scores.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file, false);
        BufferedWriter bWriter = new BufferedWriter(fileWriter);
        bWriter.close();
		
	}
	
}
