// Copyright (c) 2016 yuriwani@GitHub
// This software is released under an MIT license.
// See LICENSE for full details.

import javax.swing.*;   
import java.awt.event.*;

public class PuzzleSnap
{
	public static void main (String[] args)
	{
		MainWindow win = new MainWindow("Puzzle Snap");
	}
}

class MainWindow extends JFrame 
{

	MainWindow (String strWindowtitle)
	{
		super(strWindowtitle);
		setSize(600, 600);
		setVisible(true);
		
		// register Events
		addWindowListener(new WindowEventHandlers());
		
	}

	// Event handlers
	class WindowEventHandlers extends WindowAdapter
	{
		public void windowClosing(WindowEvent e) 
		{
			System.exit(0);
		}
	}
}
