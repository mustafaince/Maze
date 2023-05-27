package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import enigma.core.Enigma;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import enigma.console.TextAttributes;

public class Organization 
{
	long startTime = System.currentTimeMillis();
	
	TextAttributes attrs = new TextAttributes(Color.BLUE, Color.WHITE);
	private static TextAttributes blueMenu = new TextAttributes(Color.BLUE, Color.BLACK);
	private static TextAttributes blackMenu = new TextAttributes(Color.WHITE, Color.BLACK);

	private boolean flag = false;//For Add Best Score
	
	Node [][] maze = new Node[21][55];
	Node temp = new Node(' ', 0);
	Node peekTemp= new Node(' ', 0);
	
	public KeyListener klis;
	public boolean is_press;
	public int key;
	
	private int rowh;
	private int colh;
	private int rowc;
	private int colc;
	
	CircularQueue input = new CircularQueue(100);
	CircularQueue path = new CircularQueue(100);
	
	int energyh= 100;
	int energyc= 100;
	
	Stack packvalue = new Stack(5);
	Stack packtime = new Stack(5);
	Stack points= new Stack(1000000);
	Stack temppoints= new Stack(1000000);
	
	int th=0, tc=0;

	private double time;
	
	
	
	enigma.console.Console cn = Enigma.getConsole("Energy Maze", 180, 50);

	public Organization() throws Exception
	{
		printInitionalMaze();
		assignInitionalValueToPoints();
		createHuman();
		createComputer();
		listenKey();

		attrs = new TextAttributes(Color.black, Color.green);
		cn.setTextAttributes(attrs);
		cn.getTextWindow().setCursorPosition(57, 18);
		System.out.println("H");
		attrs = new TextAttributes(Color.black, Color.red);
		cn.setTextAttributes(attrs);
		cn.getTextWindow().setCursorPosition(57, 19);
		System.out.println("C");

		while(!(rowc == rowh && colc==colh))
		{
			
			long endTime = System.currentTimeMillis();
			long estimatedTime = endTime - startTime; // Geçen süreyi milisaniye cinsinden elde ediyoruz
			double seconds = (double)estimatedTime/1000; // saniyeye çevirmek için 1000'e bölüyoruz.
			time = seconds;
			
			cn.setTextAttributes(blackMenu);
			cn.getTextWindow().setCursorPosition(69, 0);
			System.out.print("___Time___");
			
			cn.getTextWindow().setCursorPosition(71, 1);
			System.out.print(seconds);
			
			
			printMaze();			
			printBackpack();
			controlExplosion();
			printEnergy();

			if(energyh >0 || th>1)
			{
				if(is_press==true)
					commands();
				
				if(rowc == rowh && colc==colh) 
				{
					peekTemp.setValue('C');
					break;
				}
				th=0;
			}
			if(energyh==0) th++;
			
			if(energyc >0 || tc>1)
			{
				computerMovement();
				tc=0;
			}
			if(energyc==0) tc++;	

			Thread.sleep(120);
			controlTime(140);

			if(input.size() < 10)
			{
				fillInput();
				printInput();
			}

			if(!isMazeFull())
			{
				fillMaze();
			}
		}

		printMaze();

		playSound("DiePac");
		
		flag = true;
		
		if(flag == true)
		{
			cn.setTextAttributes(blackMenu);
			cn.getTextWindow().setCursorPosition(69, 4);
			System.out.print("___Score___");
			
			cn.setTextAttributes(blackMenu);
			cn.getTextWindow().setCursorPosition(71, 5);
			System.out.print(energyh);
			
			clearScreen();
			
			cn.setTextAttributes(blackMenu);
			cn.getTextWindow().setCursorPosition(0, 0);
			System.out.print("Please Enter Your Name : ");
			
			
			String str;         
            
			str=cn.readLine();
			
			 String scoreANDname;
			 
			 scoreANDname = "Name : " + str + "       " + "Time : " + time + "       " + "Score : " + energyh + "\n" + "\n" ;

	        File file = new File("scores.txt");
	        if (!file.exists()) {
	            file.createNewFile();
	        }

	        FileWriter fileWriter = new FileWriter(file, true);
	        BufferedWriter bWriter = new BufferedWriter(fileWriter);
	        bWriter.write(scoreANDname);
	        bWriter.close();
	        
	        clearScreen2();
	        
	        Menu againplay = new Menu();
			
		}
		
		
	}
	
	public void assignPointsOfPath()
	{
		int size = points.size();
		
		for (int i = 1; i < size; i++) 
		{
			temppoints.push(points.peek());
			((Node)points.pop()).setValue('.');
		}
		
		while(!points.isEmpty())
			points.pop();
	}
	
	public void deleteValueOfPoints()
	{
		while(!temppoints.isEmpty()) 
		{
			((Node)temppoints.pop()).setValue(' ');
		}
	}

	public void computerMovement()
	{
		initialValues();
		
		temp= maze[rowc][colc];
		temp.setParent(null);
		path.enqueue((Object)temp);

		while(!path.isEmpty())
		{
			temp= (Node)path.dequeue();

			
			
			if(temp.getLeft().getState() == 0 && temp.getLeft().getValue() !='1' && temp.getLeft().getValue() !='2' && temp.getLeft().getValue() !='3' && temp.getLeft().getValue() !='4' && temp.getLeft().getValue() !='#')
			{
				temp.getLeft().setState(1);
				temp.getLeft().setParent(temp);
				path.enqueue(temp.getLeft());
				
				if (temp.getLeft().getValue() == 'H' && energyc > 30) 
				{
					points.push(temp.getLeft());
					break;	
				}		
				else if (temp.getLeft().getValue() == '*' && energyc <= 30) 
				{
					points.push(temp.getLeft());
					break;	
				}
			}
			else 
				temp.getLeft().setState(1);
			
			
			if(temp.getRight().getState() == 0 && temp.getRight().getValue() !='1' && temp.getRight().getValue() !='2' && temp.getRight().getValue() !='3' && temp.getRight().getValue() !='4' && temp.getRight().getValue() !='#')
			{
				temp.getRight().setState(1);
				temp.getRight().setParent(temp);
				path.enqueue(temp.getRight());
				
				if (temp.getRight().getValue() == 'H' && energyc>30) 
				{
					points.push(temp.getRight());
					break;	
				}		
				else if (temp.getRight().getValue() == '*' && energyc <= 30) 
				{
					points.push(temp.getRight());
					break;	
				}
			}
			else
				temp.getRight().setState(1);
			
			if(temp.getUp().getState() == 0 && temp.getUp().getValue() !='1' && temp.getUp().getValue() !='2' && temp.getUp().getValue() !='3' && temp.getUp().getValue() !='4' && temp.getUp().getValue() !='#')
			{
				temp.getUp().setState(1);
				temp.getUp().setParent(temp);
				path.enqueue(temp.getUp());
				
				if (temp.getUp().getValue() == 'H' && energyc>30) 
				{
					points.push(temp.getUp());
					break;	
				}		
				else if (temp.getUp().getValue() == '*' && energyc<=30) 
				{
					points.push(temp.getUp());
					break;	
				}
			}
			else 
				temp.getUp().setState(1);
			
			if(temp.getDown().getState() == 0 && temp.getDown().getValue() !='1' && temp.getDown().getValue() !='2' && temp.getDown().getValue() !='3' && temp.getDown().getValue() !='4' && temp.getDown().getValue() !='#')
			{
				temp.getDown().setState(1);
				temp.getDown().setParent(temp);
				path.enqueue(temp.getDown());

				if (temp.getDown().getValue() =='H' && energyc>30) 
				{
					points.push(temp.getDown());
					break;	
				}		
				else if (temp.getDown().getValue() =='*' && energyc<=30) 
				{
					points.push(temp.getDown());
					break;	
				}
			}
			else
				temp.getDown().setState(1);
			
			temp.setState(1);			
		}
		
		while (!path.isEmpty()) 
			path.dequeue();
		
		while(true)
		{
			if(temp.getValue() == 'C')
			{
				peekTemp = ((Node)(points.peek()));
				
				if (temp.getLeft() == peekTemp) 
					colc -= 1;
				else if (temp.getRight() == peekTemp)
					colc += 1;
				else if (temp.getUp() == peekTemp)
					rowc -= 1;
				else if (temp.getDown() == peekTemp)
					rowc += 1;
				
				if(maze[rowc][colc].getValue() == '*')
					energyc+= 50;
				
				assignPointsOfPath();
				printMaze();
				deleteValueOfPoints();
				temp.setValue(' ');
				peekTemp.setValue('C');
				
				break;
			}

			points.push(temp);
			temp=temp.getParent();
		}
		
		energyc--;
	}

	public void initialValues()
	{
		for (int i = 0; i < 21; i++) 
		{
			for (int j = 0; j < 55; j++) 
			{
				maze[i][j].setState(0);

				if (j==0) 
					maze[i][j].setLeft(null);
				else
					maze[i][j].setLeft(maze[i][j-1]);					
				if (j==54) 
					maze[i][j].setRight(null);
				else 
					maze[i][j].setRight(maze[i][j+1]);
				if (i==0) 
					maze[i][j].setUp(null);
				else 
					maze[i][j].setUp(maze[i-1][j]);				
				if (i==20) 
					maze[i][j].setDown(null);
				else 
					maze[i][j].setDown(maze[i+1][j]);				
			}
		}
	}

	public void createComputer()
	{
		boolean flag=false;

		while(flag==false)
		{
			rowc = (int)(Math.random()*21);
			colc = (int)(Math.random()*55);

			if(maze[rowc][colc].getValue() != '#' && maze[rowc][colc].getValue() != 'H')
			{
				maze[rowc][colc].setValue('C');
				flag=true;
			}	
		}
	}

	public void commands() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		if(key==KeyEvent.VK_LEFT && maze[rowh][colh-1].getValue() !='#')
		{
			playSound("Movee");
			playerMovement(rowh, colh-1, rowh, colh-2, 4);
		}
		else if(key==KeyEvent.VK_RIGHT && maze[rowh][colh+1].getValue() !='#')
		{
			playSound("Movee");
			playerMovement(rowh, colh+1, rowh, colh+2, 3);
		}
		else if(key==KeyEvent.VK_UP && maze[rowh-1][colh].getValue() !='#')
		{
			playSound("Movee");
			playerMovement(rowh-1, colh, rowh-2, colh, 2);
		}
		else if(key==KeyEvent.VK_DOWN && maze[rowh+1][colh].getValue() !='#')
		{
			playSound("Movee");
			playerMovement(rowh+1, colh, rowh+2, colh, 1);
		}		

		//put an item into the backpack
		else if(key == KeyEvent.VK_W && !packvalue.isFull() && maze[rowh-1][colh].getValue() !='C' && maze[rowh-1][colh].getValue() !='#' && maze[rowh-1][colh].getValue() !=' ')
		{
			playSound("Back");
			packvalue.push(maze[rowh-1][colh].getValue());
			maze[rowh-1][colh].setValue(' ');		
			packtime.push(maze[rowh-1][colh].getTime());
			energyh -=100;
		}

		else if(key == KeyEvent.VK_S && !packvalue.isFull() && maze[rowh+1][colh].getValue() !='C' && maze[rowh+1][colh].getValue() !='#' && maze[rowh+1][colh].getValue() !=' ')
		{
			playSound("Back");
			packvalue.push(maze[rowh+1][colh].getValue());
			maze[rowh+1][colh].setValue(' ');
			packtime.push(maze[rowh+1][colh].getTime());
			energyh -=100;
		}

		else if(key == KeyEvent.VK_A && !packvalue.isFull() && maze[rowh][colh-1].getValue() !='C' && maze[rowh][colh-1].getValue() !='#' && maze[rowh][colh-1].getValue() !=' ')
		{
			playSound("Back");
			packvalue.push(maze[rowh][colh-1].getValue());
			maze[rowh][colh-1].setValue(' ');
			packtime.push(maze[rowh][colh-1].getTime());
			energyh -=100;
		}

		else if(key == KeyEvent.VK_D && !packvalue.isFull() && maze[rowh][colh+1].getValue() !='C' && maze[rowh][colh+1].getValue() !='#' && maze[rowh][colh+1].getValue() !=' ')
		{
			playSound("Back");
			packvalue.push(maze[rowh][colh+1].getValue());
			maze[rowh][colh+1].setValue(' ');
			packtime.push(maze[rowh][colh+1].getTime());
			energyh -=100;
		}

		// remove item from the backpack
		else if(key == KeyEvent.VK_I && maze[rowh-1][colh].getValue() ==' ' && !packvalue.isEmpty())
		{
			playSound("Back");
			maze[rowh-1][colh].setValue((char)packvalue.pop());                 
			maze[rowh-1][colh].setTime((int)packtime.pop());
		}

		else if(key == KeyEvent.VK_K && maze[rowh+1][colh].getValue() ==' ' && !packvalue.isEmpty())
		{
			playSound("Back");
			maze[rowh+1][colh].setValue((char) packvalue.pop());   
			maze[rowh+1][colh].setTime((int)packtime.pop());
		}

		else if(key == KeyEvent.VK_J && maze[rowh][colh-1].getValue()==' ' && !packvalue.isEmpty())
		{
			playSound("Back");
			maze[rowh][colh-1].setValue((char) packvalue.pop());       
			maze[rowh][colh-1].setTime((int)packtime.pop());
		}

		else if(key == KeyEvent.VK_L && maze[rowh][colh+1].getValue() ==' ' && !packvalue.isEmpty())
		{
			playSound("Back");
			maze[rowh][colh+1].setValue((char) packvalue.pop());       
			maze[rowh][colh+1].setTime((int)packtime.pop());
		}

		is_press=false;
	}

	public void playerMovement(int i, int j, int k, int l, int cont) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		if((maze[i][j].getValue() =='1' || maze[i][j].getValue() =='2' || maze[i][j].getValue() =='3' || maze[i][j].getValue() =='4') 
				&& (maze[k][l].getValue() !='#' && maze[k][l].getValue() !='*' && maze[k][l].getValue() !='1' && maze[k][l].getValue() !='2' &&
				maze[k][l].getValue() !='3' && maze[k][l].getValue() !='4'))
		{
			maze[rowh][colh].setValue(' ');
			maze[k][l].setValue(maze[i][j].getValue());
			maze[k][l].setTime(maze[i][j].getTime());

			if(cont==1) rowh++;
			if(cont==2) rowh--;
			if(cont==3) colh++;
			if(cont==4) colh--;

			maze[rowh][colh].setValue('H');
			if(energyh>0)
				energyh--;
		}
		else if(!(maze[i][j].getValue() =='1' || maze[i][j].getValue() =='2' || maze[i][j].getValue() =='3' || maze[i][j].getValue() =='4'))
		{
			maze[rowh][colh].setValue(' ');
			if(maze[i][j].getValue() =='*')
			{
				playSound("Eat");
				energyh+=25;
			}

			if(cont==1) rowh++;
			if(cont==2) rowh--;
			if(cont==3) colh++;
			if(cont==4) colh--;

			maze[rowh][colh].setValue('H');
			if(energyh>0)
				energyh--;
		}
	}

	public void controlExplosion() throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		for (int i = 0; i < 21; i++) 
		{
			for (int j = 0; j < 55; j++) 
			{
				if(maze[i][j].getValue() =='1' || maze[i][j].getValue() =='2' || maze[i][j].getValue() =='3' || maze[i][j].getValue() =='4')
				{
					mainExplosion(maze[i][j].getValue(), i, j);
				}
			}
		}
	}

	public void mainExplosion(char value, int i, int j) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		int count=0;

		if(maze[i-1][j].getValue()  == value)
		{
			count+= explosion(value, i-1, j);
			maze[i][j].setValue(value);
		}
		if(maze[i+1][j].getValue()  == value)
		{
			count+= explosion(value, i+1, j);
			maze[i][j].setValue(value);
		}
		if(maze[i][j-1].getValue()  ==value)
		{
			count+= explosion(value, i, j-1);
			maze[i][j].setValue(value);
		}
		if(maze[i][j+1].getValue() == value)
		{
			count+= explosion(value, i, j+1);
			maze[i][j].setValue(value);
		}
		if(count>0)
		{
			count++;
			maze[i][j].setValue(' ');
		}
		if(count==2) 
		{
			playSound("Boom");
			energyh+=100;
		}
		if(count==3) {
			playSound("Boom");
			energyh+=200;
		}
		if(count==4) {
			playSound("Boom");
			energyh+=400;
		}
	}

	public int explosion(char value, int i, int j)
	{
		int count=0;

		if(maze[i-1][j].getValue()  == value)
		{
			maze[i][j].setValue(' ');
			maze[i-1][j].setValue(' ');
			count++;
		}
		if(maze[i+1][j].getValue()  == value)
		{
			maze[i][j].setValue(' ');
			maze[i+1][j].setValue(' ');
			count++;
		}
		if(maze[i][j-1].getValue()  ==value)
		{
			maze[i][j].setValue(' ');
			maze[i][j-1].setValue(' ');
			count++;
		}
		if(maze[i][j+1].getValue()  ==value)
		{
			maze[i][j].setValue(' ');
			maze[i][j+1].setValue(' ');
			count++;
		}

		return count;
	}

	public void printEnergy()
	{
		if(energyh <0) energyh =0;
		cn.getTextWindow().setCursorPosition(61, 18);
		System.out.print("             ");
		cn.getTextWindow().setCursorPosition(61, 18);
		System.out.print(energyh);

		if(energyc <0) energyc =0;
		cn.getTextWindow().setCursorPosition(61, 19);
		System.out.print("             ");
		cn.getTextWindow().setCursorPosition(61, 19);
		System.out.print(energyc);
	}

	public void printBackpack()
	{
		int size=packvalue.size();
		Stack temp = new Stack(5);

		while(!packvalue.isEmpty())
		{
			temp.push(packvalue.pop());
		}
		for(int i=1; i<=5; i++)
		{
			cn.getTextWindow().setCursorPosition(59, 14-i);
			System.out.println(" ");
		}
		for(int i=1; i<=size; i++)
		{
			cn.getTextWindow().setCursorPosition(59, 14-i);
			System.out.println(temp.peek());
			
			
			
			
			
			
			packvalue.push(temp.pop());
		}
	}

	public void controlTime(int time)
	{
		for (int i = 0; i < 21; i++) 
		{
			for (int j = 0; j < 55; j++) 
			{
				maze[i][j].setTime(+time);

				if((maze[i][j].getValue() =='*' || maze[i][j].getValue() =='1' || maze[i][j].getValue() =='2' || maze[i][j].getValue() =='3' || maze[i][j].getValue() =='4') && maze[i][j].getTime()>100000)
				{
					maze[i][j].setTime(' ');	
				}
			}	
		}
	}

	public void printInput()
	{
		cn.getTextWindow().setCursorPosition(57, 3);
		for (int i = 0; i < 10; i++) 
		{	
			if((char)(input.peek()) =='*')
			{
				attrs = new TextAttributes(Color.BLUE, Color.WHITE);
				cn.setTextAttributes(attrs);
			}
			else if((char)(input.peek()) =='1')
			{
				attrs = new TextAttributes(Color.BLACK, Color.PINK);
				cn.setTextAttributes(attrs);
			}
			else if((char)(input.peek()) =='2')
			{
				attrs = new TextAttributes(Color.BLACK, Color.ORANGE);
				cn.setTextAttributes(attrs);
			}
			else if((char)(input.peek()) =='3')
			{
				attrs = new TextAttributes(Color.BLACK, Color.MAGENTA);
				cn.setTextAttributes(attrs);
			}
			else if((char)(input.peek()) =='4')
			{
				attrs = new TextAttributes(Color.BLACK, Color.CYAN);
				cn.setTextAttributes(attrs);
			}
			System.out.print((char)(input.peek()));
			input.enqueue(input.dequeue());
		}
	}

	public void printMaze()
	{
		for (int i = 0; i < 21; i++) 
		{
			for (int j = 0; j < 55; j++) 
			{
				if(maze[i][j].getValue() =='#')
				{
					attrs = new TextAttributes(Color.white, Color.BLACK);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='H')
				{
					attrs = new TextAttributes(Color.black, Color.GREEN);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='C')
				{
					attrs = new TextAttributes(Color.BLACK, Color.RED);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='*')
				{
					attrs = new TextAttributes(Color.BLUE, Color.WHITE);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='1')
				{
					attrs = new TextAttributes(Color.BLACK, Color.PINK);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='2')
				{
					attrs = new TextAttributes(Color.BLACK, Color.ORANGE);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='3')
				{
					attrs = new TextAttributes(Color.BLACK, Color.MAGENTA);
					cn.setTextAttributes(attrs);
				}
				else if(maze[i][j].getValue() =='4')
				{
					attrs = new TextAttributes(Color.BLACK, Color.CYAN);
					cn.setTextAttributes(attrs);
				}
				
				else if(maze[i][j].getValue() == '.')
				{
					attrs = new TextAttributes(Color.RED, Color.white);
					cn.setTextAttributes(attrs);
					
				}
				
				else
				{
					attrs = new TextAttributes(Color.BLACK, Color.WHITE);
					cn.setTextAttributes(attrs);
				}
				
				cn.getTextWindow().setCursorPosition(j, i);
				System.out.println(maze[i][j].getValue());
			}
		}
	}

	public void fillMaze()
	{
		while(!isMazeFull() && input.size() !=0)
		{
			int rand1=(int)((Math.random()*19)+1);
			int rand2=(int)((Math.random()*53)+1);

			if(maze[rand1][rand2].getValue() !='#' && maze[rand1][rand2].getValue() !='1' && maze[rand1][rand2].getValue() !='2' && 
					maze[rand1][rand2].getValue() !='3' && maze[rand1][rand2].getValue() !='4' && maze[rand1][rand2].getValue() !='*' &&
					maze[rand1][rand2].getValue() !='H' && maze[rand1][rand2].getValue() !='C')
			{
				maze[rand1][rand2] = new Node((char) input.dequeue(), 0);
			}
		}
	}

	public boolean isMazeFull()
	{
		int count=0;

		for (int i = 1; i < 20; i++) 
		{
			for (int j = 1; j < 54; j++) 
			{
				if(maze[i][j].getValue() =='*' || maze[i][j].getValue() =='1' || maze[i][j].getValue() =='2' || maze[i][j].getValue() =='3' || maze[i][j].getValue() =='4')
					count++;
			}
		}
		count += packvalue.size();

		if(count>19)		
			return true;
		else
			return false;
	}

	public void fillInput()
	{				
		while(input.size()<10)
		{
			int rand= (int)((Math.random()*8)+1);

			if(rand==1)
				input.enqueue('1');
			else if(rand==2)
				input.enqueue('2');
			else if(rand==3)
				input.enqueue('3');
			else if(rand==4)
				input.enqueue('4');
			else
				input.enqueue('*');
		}
	}

	public void listenKey() throws InterruptedException
	{	
		klis=new KeyListener() 
		{
			public void keyTyped(KeyEvent e) {}			
			public void keyPressed(KeyEvent e) 
			{
				if(is_press==false) 
				{
					is_press=true;
					key=e.getKeyCode();
				}
			}			
			public void keyReleased(KeyEvent e) {}
		};
		cn.getTextWindow().addKeyListener(klis);				
	}

	public void createHuman()
	{
		boolean flag=false;

		while(flag==false)
		{
			rowh = (int)(Math.random()*21);
			colh = (int)(Math.random()*55);

			if(maze[rowh][colh].getValue() != '#')
			{
				maze[rowh][colh].setValue('H');
				flag=true;
			}	
		}
	}

	public void assignInitionalValueToPoints()
	{
		BufferedReader br = null;
		FileReader fr = null;
		String filename = "maze2.txt";
		try 
		{
			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filename));
			int rowh = 0;

			while ((sCurrentLine = br.readLine()) != null) 
			{
				char[] temp = sCurrentLine.toCharArray();

				for (int i = 0; i < temp.length; i++) 
				{
					maze[rowh][i] = new Node(temp[i], 100000);
				}
				rowh++;
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}				
	}

	public void printInitionalMaze() throws IOException
	{
		String line;				

		File file = new File("maze.txt");

		FileReader fr = new FileReader(file);

		BufferedReader br = new BufferedReader(fr);

		while ((line = br.readLine()) != null) 
		{
			System.out.println(line);
		}

		br.close();
	}
	
	public void clearScreen()
	{
		int px=0;
		int py=0;
		for (int i = 0; i < 50; i++) {


			cn.getTextWindow().setCursorPosition(px, py);
			cn.getTextWindow().output("                                                                     ");
			py++;
		}
		cn.getTextWindow().setCursorPosition(0, 0);
	}
	
	public void clearScreen2()
	{
		int px=0;
		int py=0;
		for (int i = 0; i < 50; i++) {


			cn.getTextWindow().setCursorPosition(px, py);
			cn.getTextWindow().output("                                                                                ");
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
	
}