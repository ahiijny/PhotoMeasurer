import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	
	private class MyListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e) 
		{
	        refresh();           
	    }
	}
	
}
