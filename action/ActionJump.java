// Copyright (c) 2016 yuriwani@GitHub
// This software is released under an MIT license.
// See LICENSE for full details.

import javax.swing.*;   
import java.awt.event.*;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Graphics;



enum Difficulty
{
	EASY,
	MEDIUM,
	HARD
}
	
enum Status
{
	START,
	GAMEMAIN,
	CLEAR,
	OVER,
	EXIT
}

public class ActionJump
{	
	public static void main (String[] args)
	{
		MainWindow win = new MainWindow("Action Jump");
	}
}

class MainWindow extends JFrame implements Runnable
{
	private final String savefilename = "stats.data";
	StartPanel panelStart;
	GameMainPanel panelGame;
	GameClearPanel panelClear;
	GameOverPanel panelOver;
	Thread thread;
	Status status;
	boolean bInGame = false;
		
	MainWindow (String strWindowtitle)
	{
		// create main window object
		super(strWindowtitle);
		setBounds(50, 50, 800, 600);
		setVisible(true);
		setResizable(false);
		addWindowListener(new WindowEventHandlers());
			
		Container container = getContentPane();
		container.setLayout(new GridLayout(1, 1, 0, 0));
		
		panelStart = new StartPanel(this);
		container.add(panelStart);
		status = Status.START;
	
		thread = new Thread(this);
		thread.start();	
	}

	private JPanel getPanel(Status _status)
	{
		if(_status == Status.START)
			return (JPanel)panelStart;
		else if(_status == Status.GAMEMAIN)
			return (JPanel)panelGame;
		else if(_status == Status.CLEAR)
			return (JPanel)panelClear;
		else if(_status == Status.OVER)
			return (JPanel)panelOver;
		else
			return null;
	}
	
	public void callbackStatusChange(Status new_status)
	{
		Container container = getContentPane();
		
		// remove previous status
		JPanel oldPanel = getPanel(status);
		container.remove(oldPanel);
		oldPanel = null;  // remove the reference to make it garbage-collected
		
		// create and add new panel
		switch(new_status)
		{
			case START:
				panelStart = new StartPanel(this);
				container.add(panelStart);
				break;
			case GAMEMAIN:
				panelGame = new GameMainPanel(this);
				container.add(panelGame);
				break;
			case CLEAR:
				panelClear = new GameClearPanel(this);
				container.add(panelClear);
				break;
			case OVER:
				panelOver = new GameOverPanel(this);
				container.add(panelOver);
				break;
			case EXIT:
				dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				return;
		}
		
		status = new_status;
		validate();
	}
	
	public void run()
	{
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
