import javax.swing.*;   
import java.awt.event.*;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Graphics;
	
public class GameMainPanel extends JPanel
{
	MainWindow mainWnd;
	JButton btn;
	Dimension size;
	
	public GameMainPanel(MainWindow mainWnd)
	{
		this.mainWnd = mainWnd;
		size = mainWnd.getSize();
		
		setLayout(null);
		setBackground(Color.white);
		
		btn = new JButton("test");
		btn.addActionListener(new ButtonHandler());
		add(btn);
		
		addMouseListener(new MouseHandler());
	}
			
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);   
		FontMetrics fm;
		Font font;
		String str;
		int w, h;

		font = new Font("SansSerif", Font.BOLD, 40);
		fm  = g.getFontMetrics(font);
		str = "In Game now...";
		w   = fm.stringWidth(str);
		g.setFont(font);
		g.drawString(str, size.width/2-w/2, size.height/2);

		font = new Font("Serif", Font.PLAIN, 20);
		fm  = g.getFontMetrics(font);
		str = "Click to GameOver, Double-click to GameClear";
		w   = fm.stringWidth(str);
		h   = size.height - fm.getHeight() - 10;
		g.setFont(font);
		g.drawString(str, size.width/2-w/2, h);
	}
	
	class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == btn) 
			{
				
			}
		}
	}
	
	class MouseHandler extends MouseAdapter 
	{
		public void mouseClicked(MouseEvent e) 
		{
			if (e.getClickCount() == 2)
				mainWnd.callbackStatusChange(Status.CLEAR);
			else if (e.getClickCount() == 1)
				mainWnd.callbackStatusChange(Status.OVER);
		}
	}
}