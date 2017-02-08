// Copyright (c) 2016 yuriwani@GitHub
// This software is released under an MIT license.
// See LICENSE for full details.

import javax.swing.*;   
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.Random;

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
	private JButton btnStart = new JButton("Start Game");;
	
	public void callback(int time_sec, Difficulty level, boolean bCleared)
	{
		btnStart.setEnabled(true);
	}

	MainWindow (String strWindowtitle)
	{
		super(strWindowtitle);
		setSize(300, 300);
		setVisible(true);
		// register Events
		addWindowListener(new WindowEventHandlers());
		
		
		getContentPane().add(btnStart, BorderLayout.PAGE_END);
		btnStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				PuzzleWindow puzzle = new PuzzleWindow("Snap", Difficulty.EASY, MainWindow.this);
				btnStart.setEnabled(false);
			}
		});	
	}

	// Event handlers
	class WindowEventHandlers extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}
	
	class ActionHandlers implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			
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
				row_num = col_num = 6;
				CreateData(6,6);
				timerShow = new Timer(1000, new TimerHandler());
				break;
			case MEDIUM:
		
			case HARD:
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
				btns[i][j] = new JButton("test");
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
			mainWnd.callback(0, level, false);
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
			timerShow.start();
			
			for(int i=0; i<row_num; i++)
			{
				for(int j=0; j<col_num; j++)
				{
					btns[i][j].setEnabled(false);
					
					if( e.getSource() == btns[i][j])
					{
						btns[i][j].setText(Integer.toString(data[i][j].number));
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
			}
			else
			{
				timerShow.stop();
				for(int i=0; i<row_num; i++)
				{
					for(int j=0; j<col_num; j++)
					{
						btns[i][j].setText("");
						btns[i][j].setEnabled(true);
					}
				}
			}
		}
	}
}
