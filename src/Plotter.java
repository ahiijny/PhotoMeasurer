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
	
	public boolean extrapolate = false;
	public boolean slopeLock = false;
	public boolean editLock = false;
	
	public Plotter(GUI parent) 
	{
		this.parent = parent;
		size = new Dimension(0, 0);
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
		if (extrapolate)
			if (parent.ip.img != null)
				extrapolate(p1, p2, parent.ip.img.getWidth(), parent.ip.img.getHeight());
		this.p1.setLocation(p1);
		this.p2.setLocation(p2);
	}
	
	public int getDeltaX()
	{
		return p2.x - p1.x;
	}
	
	public int getDeltaY()
	{
		return p2.y - p1.y;
	}
	
	public void extrapolate(Point p1, Point p2, int imgWidth, int imgHeight)
	{
		System.out.println("[" + imgWidth + "," + imgHeight + "]");
		System.out.println(p1 + " ->" + p2);
		
		// Find the vector equations of all of the edges
		
		double[] r = {p1.x, p1.y};
		double[] m = {p2.x, p2.y};
		m = Calc.add(m, Calc.scale(r, -1));
		m = Calc.unit(m);
					
		double[] r_top = {0, 0}; // top edge
		double[] m_top = {1, 0};
		
		double[] r_right = {imgWidth, 0}; // right edge
		double[] m_right = {0, 1};
		
		double[] r_bottom = {0, imgHeight}; // bottom edge
		double[] m_bottom = {1, 0};
		
		double[] r_left = {0, 0}; // left edge
		double[] m_left  = {0, 1};
		
		// Convert vector form to standard form
		
		double[] line = Matrix.vectorToStandardForm(r, m);
		double[] top = Matrix.vectorToStandardForm(r_top, m_top);
		double[] right = Matrix.vectorToStandardForm(r_right, m_right);
		double[] bottom = Matrix.vectorToStandardForm(r_bottom, m_bottom);
		double[] left = Matrix.vectorToStandardForm(r_left, m_left);
		
		// Find the solutions of the line to all four edges
		
		double[] extrapLeft = Matrix.solveLines(line[0], line[1], line[2], left[0], left[1], left[2]);
		double[] extrapTop = Matrix.solveLines(line[0], line[1], line[2], top[0], top[1], top[2]);
		double[] extrapRight = Matrix.solveLines(line[0], line[1], line[2], right[0], right[1], right[2]);
		double[] extrapBottom = Matrix.solveLines(line[0], line[1], line[2], bottom[0], bottom[1], bottom[2]);
		
		// Check bounds
		
		boolean leftGood = extrapLeft[1] >= 0 && extrapLeft[1] <= imgHeight; 
		boolean topGood = extrapTop[0] >= 0 && extrapTop[0] <= imgWidth;
		boolean rightGood = extrapRight[1] >= 0 && extrapRight[1] <= imgHeight;
		boolean bottomGood = extrapBottom[0] >= 0 && extrapBottom[0] <= imgWidth;
		
		System.out.println("Left = " + leftGood);
		Calc.println(extrapLeft);
		System.out.println("Top = " + topGood);
		Calc.println(extrapTop);
		System.out.println("Right = " + rightGood);
		Calc.println(extrapRight);
		System.out.println("Bottom = " + bottomGood);
		Calc.println(extrapBottom);
		System.out.println();
		
		// Correct points if possible
		
		if (rightGood) // right priority
		{
			if (topGood)
			{
				p1.setLocation(extrapTop[0], extrapTop[1]);
				p2.setLocation(extrapRight[0], extrapRight[1]);
			}
			else if (leftGood)
			{
				p1.setLocation(extrapLeft[0], extrapLeft[1]);
				p2.setLocation(extrapRight[0], extrapRight[1]);
			}
			else if (bottomGood)
			{
				p1.setLocation(extrapBottom[0], extrapBottom[1]);
				p2.setLocation(extrapRight[0], extrapRight[1]);
			}
		}
		else if (leftGood) // left priority
		{
			if (topGood)
			{
				p1.setLocation(extrapLeft[0], extrapLeft[1]);
				p2.setLocation(extrapTop[0], extrapTop[1]);
			}
			else if (bottomGood)
			{
				p1.setLocation(extrapLeft[0], extrapLeft[1]);
				p2.setLocation(extrapBottom[0], extrapBottom[1]);
			}
		}
		else if (bottomGood) // bottom priority
		{
			if (topGood)
			{
				if (p1.y < p2.y)
				{
					p1.setLocation(extrapBottom[0], extrapBottom[1]);
					p2.setLocation(extrapTop[0], extrapTop[1]);
				}
				else
				{
					p2.setLocation(extrapBottom[0], extrapBottom[1]);
					p1.setLocation(extrapTop[0], extrapTop[1]);
				}
			}
		}	
	}
	
	private class MyListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e) 
		{
	        refresh();           
	    }
	}
	
}

