// Copyright (c) 2016 yuriwani@GitHub
// This software is released under an MIT license.
// See LICENSE for full details.

import javax.swing.*;   
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Random;
import java.io.*;

enum Difficulty
{
	EASY,
	MEDIUM,
	HARD
}
	
public class PuzzleSnap
{	
	public static void main (String[] args)
	{
		MainWindow win = new MainWindow("Puzzle Snap");
	}
}

class MainWindow extends JFrame 
{
	private class StatStruct implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public int nPlayed = 0;
		public double dAverage = 0;
		public long lHighscore = 0;
	}
	
	private JButton btnStart = new JButton("Start Game");
	private JRadioButton rLevelEasy = new JRadioButton("EASY", true);
	private JRadioButton rLevelMedium = new JRadioButton("MEDIUM", false);
	private JRadioButton rLevelHard = new JRadioButton("HARD", false);
	private JLabel textStat = new JLabel("", SwingConstants.CENTER);

	private StatStruct stat[] = {new StatStruct(), new StatStruct(), new StatStruct()};
	private final String savefilename = "stats.data";
	
	public void callback(long time_sec, Difficulty level, boolean bCleared)
	{
		if(bCleared)
		{
			// Reflect the result to statistics data
			int nIndex;
			if(level == Difficulty.EASY)
				nIndex = 0;
			else if(level == Difficulty.MEDIUM)
				nIndex = 1;
			else
				nIndex = 2;
			
			stat[nIndex].nPlayed++;
			stat[nIndex].dAverage = (stat[nIndex].dAverage + time_sec)/stat[nIndex].nPlayed;
			if(time_sec < stat[nIndex].lHighscore || stat[nIndex].lHighscore == 0)
				stat[nIndex].lHighscore = time_sec;
			
			UpdateStatShow();
		}
		
		btnStart.setEnabled(true);
	}

	private void UpdateStatShow()
	{
		int nIndex;
		if(rLevelEasy.isSelected())
			nIndex = 0;
		else if(rLevelMedium.isSelected())
			nIndex = 1;
		else
			nIndex = 2;
		
		textStat.setText(String.format("<html>High Score: %d<br>Average Score: %f<br>Played: %d", 
							stat[nIndex].lHighscore, stat[nIndex].dAverage, stat[nIndex].nPlayed) );	
	}
	
	MainWindow (String strWindowtitle)
	{
		// create main window object
		super(strWindowtitle);
		setSize(300, 300);
		setVisible(true);
		addWindowListener(new WindowEventHandlers());
			
		// read saved statistics data
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefilename));
            stat[0] = (StatStruct) ois.readObject();
			stat[1] = (StatStruct) ois.readObject();
			stat[2] = (StatStruct) ois.readObject();
        } 
		catch(FileNotFoundException nofile)
		{
			System.out.println("saved data file not found");
		}
		catch (IOException | ClassNotFoundException err) 
		{
            System.out.println(err.getMessage());
        }
		
		Container container = getContentPane();
		//container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		// create and add items to the main window
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		JLabel label1 = new JLabel("[Level]");
		panel1.add(label1);
		centerPanel.add(panel1);
			
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
	
		rLevelEasy.addActionListener(new ActionHandlers());
		rLevelMedium.addActionListener(new ActionHandlers());
		rLevelHard.addActionListener(new ActionHandlers());
		ButtonGroup group = new ButtonGroup();
		group.add(rLevelEasy);
		group.add(rLevelMedium);
		group.add(rLevelHard);
		panel2.add(rLevelEasy);
		panel2.add(rLevelMedium);
		panel2.add(rLevelHard);
		centerPanel.add(panel2);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));	
		JLabel label2 = new JLabel("[Statistics]");
		panel3.add(label2);	
		centerPanel.add(panel3);
		
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));			
		panel4.add(textStat);
		
		UpdateStatShow();
		
		centerPanel.add(panel4);
		
		//container.add(new JLabel("Welcome to Puzzle Snap!"), BorderLayout.NORTH);
		container.add(centerPanel, BorderLayout.CENTER);
		container.add(btnStart, BorderLayout.SOUTH);
		btnStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Difficulty level;
				if(rLevelEasy.isSelected())
					level = Difficulty.EASY;
				else if(rLevelMedium.isSelected())
					level = Difficulty.MEDIUM;
				else
					level = Difficulty.HARD;
				PuzzleWindow puzzle = new PuzzleWindow("Snap", level, MainWindow.this);
				btnStart.setEnabled(false);
			}
		});	
	}

	// Event handlers
	class WindowEventHandlers extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			// save statistics data
			try
			{
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefilename));
				oos.writeObject(stat[0]);
				oos.writeObject(stat[1]);
				oos.writeObject(stat[2]);
			} 
			catch (IOException err) 
			{
				System.out.println(err.getMessage());
			}
			
			System.exit(0);
		}
	}
	
	class ActionHandlers implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UpdateStatShow();
		}
	}
}

class PuzzleWindow extends JFrame
{
	private class DataStruct
	{
		public int number = -1;
		public boolean bEnabled = true;
		public boolean bNowCompared = false;
	}
	
	private JButton btns[][];
	private JLabel label;
	private int row_num=1, col_num=1;
	private Timer timerCnt, timerShow;
	private DataStruct data[][];
	private Difficulty level;
	private MainWindow mainWnd;
	private long start_time = 0, clear_time_sec = 0;
	private boolean bCleared = false;
	
	private void CreateData(int row_num, int col_num)
	{
		data = new DataStruct[row_num][col_num];
		for(int i=0; i<row_num; i++)
		{
			for(int j=0; j<col_num; j++)
				data[i][j] = new DataStruct();
		}
		
		Random rnd = new Random();

        int tmp;
		for(int i=row_num*col_num/2; i>0; i--)
		{
			while(true)
			{
				tmp = rnd.nextInt(row_num*col_num);
				//System.out.println("tmp="+tmp+", row="+(tmp/col_num)+", col="+(tmp%col_num));
				if (data[tmp/col_num][tmp%col_num].number < 0)
				{
					data[tmp/col_num][tmp%col_num].number = i;
					while(true)
					{
						tmp = rnd.nextInt(row_num*col_num);
						//System.out.println("tmp="+tmp+", row="+(tmp/col_num)+", col="+(tmp%col_num));
						if (data[tmp/col_num][tmp%col_num].number < 0)
						{
							data[tmp/col_num][tmp%col_num].number = i;
							break;
						}
					}
					break;
				}
			}
		}
		
	}
	
	PuzzleWindow(String strWindowtitle, Difficulty level, MainWindow mainWnd)
	{
		super(strWindowtitle);
		setSize(600,600);
		setVisible(true);
		
		addWindowListener(new WindowEventHandlers());
		
		this.level = level;
		this.mainWnd = mainWnd;
		timerCnt = new Timer(500, new TimerHandler());
		
		switch(level)
		{
			case EASY:
				row_num = col_num = 4;
				CreateData(4,4);
				timerShow = new Timer(1000, new TimerHandler());
				break;
			case MEDIUM:
				row_num = col_num = 6;
				CreateData(6,6);
				timerShow = new Timer(1000, new TimerHandler());
				break;
			case HARD:
				row_num = col_num = 8;
				CreateData(8,8);
				timerShow = new Timer(800, new TimerHandler());
				break;
			default:
				System.out.println("fell in default. "+level);
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(row_num, col_num));
	
		btns = new JButton[row_num][col_num];
		for(int i=0; i<row_num; i++)
		{
			for(int j=0; j<col_num; j++)
			{	
				btns[i][j] = new JButton();
				btns[i][j].addActionListener(new BtnHandler());
				panel.add(btns[i][j]);
			}
		}
		
		getContentPane().add(panel, BorderLayout.CENTER);
		label = new JLabel("Find matching pairs!");
		getContentPane().add(label, BorderLayout.PAGE_END);
	}
	
	class WindowEventHandlers extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) 
		{
			timerCnt.stop();
			timerShow.stop();
			mainWnd.callback(clear_time_sec, level, bCleared);
			setVisible(false);
			dispose();
		}
	}
	
	private void SetEnableAllButton(boolean bEnabled)
	{
		for(int i=0; i<row_num; i++)
		{
			for(int j=0; j<col_num; j++)
			{
				btns[i][j].setEnabled(false);	
			}
		}
	}
	
	class BtnHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(start_time == 0)
			{
				start_time = System.currentTimeMillis();
				timerCnt.start();
			}
			timerShow.start();
			
			for(int i=0; i<row_num; i++)
			{
				for(int j=0; j<col_num; j++)
				{
					if(data[i][j].bEnabled)
					{
						btns[i][j].setEnabled(false);
						
						if( e.getSource() == btns[i][j])
						{
							int nEnabledCnt = 0;
							for(int k=0; k<row_num; k++)
							{
								for(int l=0; l<col_num; l++)	
								{
									if(i==k && j==l)
										continue;
									
									if(data[k][l].bEnabled)
										nEnabledCnt++;
									
									if(data[k][l].number == data[i][j].number)
									{
										if(data[k][l].bNowCompared)
										{
											data[k][l].bEnabled = false;
											data[i][j].bEnabled = false;
											nEnabledCnt--;
										}
										else
										{
											data[i][j].bNowCompared = true;
										}	
									}
									else
										data[k][l].bNowCompared = false;
								}
							}
							
							// all cleared
							if(nEnabledCnt == 0)
							{
								bCleared = true;
								timerCnt.stop();
								clear_time_sec = (System.currentTimeMillis() - start_time)/1000;
								label.setText(String.format("CONGRATUATION!!! Time = %d sec.", clear_time_sec));
							}
							
							btns[i][j].setText(Integer.toString(data[i][j].number));
						}
					}
				}
			}
		}
	}
	
	class TimerHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == timerCnt)
			{
				label.setText(String.format("Elapsed time = %d sec", (System.currentTimeMillis()-start_time)/1000));
			}
			else
			{
				timerShow.stop();
				for(int i=0; i<row_num; i++)
				{
					for(int j=0; j<col_num; j++)
					{
						btns[i][j].setText("");
						btns[i][j].setEnabled(data[i][j].bEnabled);
					}
				}
			}
		}
	}
}
