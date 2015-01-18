import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;


public class Plotter extends JPanel 
{
	public static final int X = 0;
	public static final int Y = 1;
	public GUI parent;
	public Dimension size;
	
	public double vScalar = 1;
	public double hScalar = 1;
	
	public double[][] data; //[coordinate][magnitude]
							// First coordinate is the horizontal parameter;
							// Second coordinate is the magnitude at that location
	public Color color;
	
	public Point p1 = new Point(0, 0);
	public Point p2 = new Point(0, 0);
	
	public Plotter(GUI parent) 
	{
		this.parent = parent;
		size = new Dimension (0, 0);
	}
	
	public void paintComponent(Graphics g)
	{
		
	}
	
	public void refresh()
	{
		size = getSize();
		repaint();
	}
	
	public void recalculateScale()
	{
		
	}
	
	public void setEndPoints(Point p1, Point p2)
	{
		this.p1.setLocation(p1);
		this.p2.setLocation(p2);
	}
	
	private class MyListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e) 
		{
	        refresh();           
	    }
	}
	
}
