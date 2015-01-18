import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;


public class Plotter extends JPanel 
{
	public static final int X = 0;
	public static final int Y = 1;
	
	public static final Color COLOR_SRED = Color.red;
	public static final Color COLOR_SGREEN = Color.green;
	public static final Color COLOR_SBLUE = Color.blue;
	public static final Color COLOR_LINRED = new Color(255, 192, 192);
	public static final Color COLOR_LINGREEN = new Color(192, 255, 192);
	public static final Color COLOR_LINBLUE = new Color(192, 192, 255);
	public static final Color COLOR_X = Color.magenta;
	public static final Color COLOR_Y = Color.yellow;
	public static final Color COLOR_Z = Color.cyan;
	public static final Color COLOR_LAM_XYZ = new Color(192, 128, 255);
	public static final Color COLOR_LAM_xy = new Color(192, 192, 192);
	public static final Color COLOR_LAM_RGB = new Color(192, 255, 192);
	public static final Color COLOR_LAM_SAT_EXTRAP = Color.white;
		
	public static final Color[] colors = {Color.black, Color.black, Color.black, COLOR_SRED,
									      COLOR_SGREEN, COLOR_SBLUE, COLOR_LINRED,
									      COLOR_LINGREEN, COLOR_LINBLUE, COLOR_X,
									      COLOR_Y, COLOR_Z, COLOR_LAM_XYZ,
									      COLOR_LAM_xy, COLOR_LAM_SAT_EXTRAP, COLOR_LAM_RGB};
	
	public static double maxRGB = 1;
	public static double minRGB = 0;
	public static double maxXYZ = 1.79;
	public static double minXYZ = 0;
	public static double maxLambda = 830;
	public static double minLambda = 360;
	
	public GUI parent;
			
	/**[dataset][magnitude]
	 * First dataset are the t-values
	 * Second dataset is empty
	 * The rest is as specified by the constants in GUI */
	public double[][] data = new double[GUI.profilerParams.length][1];		
	public Color color;
	
	public Point p1 = new Point(0, 0);
	public Point p2 = new Point(0, 0);
	
	public boolean extrapolate = true; // Make sure this matches with the button
	public boolean slopeLock = false; // initialization in GUI.getProfilerPanel()
	public boolean editLock = false;
	
	public double pixelsPerSample = 1;
	
	public int preferredHeight = 250;
	
	private double[] r = {0, 0};
	private double[] m = {0, 0};
	
	public double hStep = 1;	
	
	public double[] vStep = new double[GUI.profilerParams.length]; // In screen pixels
	public double[] vOffset = new double[GUI.profilerParams.length]; // In screen pixels
	public boolean[] plotEnabled = new boolean[GUI.profilerParams.length]; // In screen pixels
	public Insets insets = new Insets(2,4,2,4);	
	
	public Plotter(GUI parent) 
	{
		this.parent = parent;
		setBackground(Color.black);
		setPreferredSize(new Dimension(1, preferredHeight));
		addComponentListener(new MyListener());
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.preConcatenate(AffineTransform.getTranslateInstance(0, getHeight()));
		g2d.transform(at);
		
		if (!editLock)
			plotData(g2d);
	}
	
	public void refresh()
	{
		repaint();
	}
		
	public void setEndPoints(Point p1, Point p2, boolean sample)
	{	
		// Set Points
		
		if (extrapolate)
			if (parent.ip.img != null)
				extrapolate(p1, p2, parent.ip.img.getWidth(), parent.ip.img.getHeight());
		this.p1.setLocation(p1);
		this.p2.setLocation(p2);
		
		// Find and store the new origin and direction vector
		
		r = new double[] {p1.x, p1.y};
		m = new double[] {p2.x, p2.y};
		m = Calc.add(m, Calc.scale(r, -1));
		m = Calc.unit(m);	
		
		// Sample if necessary
		
		if (sample)
			sample();
	}
	
	public int getDeltaX()
	{
		return p2.x - p1.x;
	}
	
	public int getDeltaY()
	{
		return p2.y - p1.y;
	}
	
	public void hide()
	{
		parent.paneCenter.setDividerLocation(0);
		parent.ip.translateOffset(0, preferredHeight);
	}
	
	public void show()
	{
		parent.paneCenter.setDividerLocation(-1);
		parent.ip.translateOffset(0, -preferredHeight);
	}
	
	public void setPlotEnabled(int param, boolean enabled)
	{
		plotEnabled[param] = enabled;
	}
	
	public void extrapolate(Point p1, Point p2, int imgWidth, int imgHeight)
	{		
		// Find the vector equations of all of the edges
		
		r = new double[] {p1.x, p1.y};
		m = new double[] {p2.x, p2.y};
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
	
	public double getLength()
	{
		double[] q1 = {p1.x, p1.y};
		double[] q2 = {p2.x, p2.y};
		double[] diff = Calc.add(q2, Calc.scale(q1, -1));
		double length = Calc.mag(diff);
		return length;
	}
	
	public int getPointCount()
	{
		double length = getLength();
		return (int)(length / pixelsPerSample + 0.5);
	}
	
	public void sample()
	{
		// Set up
		
		editLock = true;
		double T = getLength();
		int n = getPointCount();		
		double dt = T/n;
		double[][] points = new double[n][2];
		data = new double[GUI.profilerParams.length][n];
		
		recalcHStep(n);
		
		// Compute LUT for points in linear profile
		
		for (int i = 0; i < n; i++)
		{
			points[i] = Calc.add(r, Calc.scale(m, i * dt));
			data[GUI.PF_X_COORD][i] = points[i][0];
			data[GUI.PF_Y_COORD][i] = points[i][1];
			data[GUI.PF_T_COORD][i] = 1.0 * i * pixelsPerSample;
		}
		
		System.out.print ("r = ");
		Calc.println(r);
		System.out.print ("m = ");
		Calc.println(m);
		System.out.println("dt = " + dt);
		
		// Sample datasets
		for (int i = 0; i < n; i++)
		{
			double[] rgb = sample_RGB(points[i]);
			data[GUI.PF_SRED][i] = rgb[0];
			data[GUI.PF_SGREEN][i] = rgb[1];
			data[GUI.PF_SBLUE][i] = rgb[2];
			
			double[] rgb_lin = sample_RGB_lin(points[i]);
			data[GUI.PF_LINRED][i] = rgb_lin[0];
			data[GUI.PF_LINGREEN][i] = rgb_lin[1];
			data[GUI.PF_LINBLUE][i] = rgb_lin[2];
			
			double[] XYZ = sample_XYZ(points[i]);
			data[GUI.PF_X][i] = XYZ[0];
			data[GUI.PF_Y][i] = XYZ[1];
			data[GUI.PF_Z][i] = XYZ[2];
			
			double lambda1 = sample_lambda_XYZ(points[i]);
			data[GUI.PF_LAM_XYZ][i] = lambda1;
			
			double lambda2= sample_lambda_xy(points[i]);
			data[GUI.PF_LAM_xy][i] = lambda2;
			
			double lambda3 = sample_lambda_RGB(points[i]);
			data[GUI.PF_LAM_RGB][i] = lambda3;
			
			double lambda4 = sample_lambda_sat_extrap(points[i]);
			data[GUI.PF_LAM_SAT_EXTRAP][i] = lambda4;
		}
		editLock = false;
	}
	
	public void recalcHStep(int n)
	{
		double length = getWidth() - insets.left - insets.right;
		hStep = length / n;		
	}
	
	public void recalcVStep()
	{
		double height = getHeight() - insets.top - insets.bottom;
		double stepRGB = height / (maxRGB - minRGB);
		double stepXYZ = height / (maxXYZ - minXYZ);
		double stepLambda = height / (maxLambda - minLambda);
						
		vStep[GUI.PF_SRED] = stepRGB;
		vStep[GUI.PF_SGREEN] = stepRGB;
		vStep[GUI.PF_SBLUE] = stepRGB;
		vStep[GUI.PF_LINRED] = stepRGB;
		vStep[GUI.PF_LINGREEN] = stepRGB;
		vStep[GUI.PF_LINBLUE] = stepRGB;
		vStep[GUI.PF_X] = stepXYZ;
		vStep[GUI.PF_Y] = stepXYZ;
		vStep[GUI.PF_Z] = stepXYZ;
		vStep[GUI.PF_LAM_XYZ] = stepLambda;
		vStep[GUI.PF_LAM_xy] = stepLambda;
		vStep[GUI.PF_LAM_SAT_EXTRAP] = stepLambda;
		vStep[GUI.PF_LAM_RGB] = stepLambda;
		
		vOffset[GUI.PF_SRED] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_SGREEN] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_SBLUE] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_LINRED] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_LINGREEN] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_LINBLUE] = -minRGB * stepRGB + insets.bottom;
		vOffset[GUI.PF_X] = -minXYZ * stepXYZ + insets.bottom;
		vOffset[GUI.PF_Y] = -minXYZ * stepXYZ + insets.bottom;
		vOffset[GUI.PF_Z] = -minXYZ * stepXYZ + insets.bottom;
		vOffset[GUI.PF_LAM_XYZ] = -minLambda * stepLambda + insets.bottom;
		vOffset[GUI.PF_LAM_xy] = -minLambda * stepLambda + insets.bottom;
		vOffset[GUI.PF_LAM_SAT_EXTRAP] = -minLambda * stepLambda + insets.bottom;
		vOffset[GUI.PF_LAM_RGB] = -minLambda * stepLambda + insets.bottom;	
	}
	
	public double[] sample_RGB(double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] rgb = new double[] {rgb255[0]/255.0, rgb255[1]/255.0, rgb255[2]/255.0};
		return rgb;
	}
	
	public double[] sample_RGB_lin(double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] rgb = new double[] {rgb255[0]/255.0, rgb255[1]/255.0, rgb255[2]/255.0};		
		return Calc.sRGBreverseGamma(rgb);
	}
	
	public double[] sample_XYZ(double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		return Calc.RGBtoXYZ(rgb255);
	}
	
	public double sample_lambda_XYZ (double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] XYZ = Calc.RGBtoXYZ(rgb255);
		double[] results = Calc.getPrimaryWavelengthFitXYZ(XYZ);
		return results[0];		
	}
	
	public double sample_lambda_xy (double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] XYZ = Calc.RGBtoXYZ(rgb255);
		double[] results = Calc.getPrimaryWavelengthFitxy(XYZ);
		return results[0];		
	}
	
	public double sample_lambda_RGB (double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] results = Calc.getPrimaryWavelengthInverseTrunc(rgb255);
		return results[0];		
	}
	
	public double sample_lambda_sat_extrap (double[] location)
	{
		Point point = new Point();
		point.setLocation(location[0], location[1]);
		Color c = parent.ip.getPixel(point);
		int[] rgb255 = Calc.getIntRGB(c);
		double[] XYZ = Calc.RGBtoXYZ(rgb255);
		double[] results = Calc.getPrimaryWavelengthSatExtrap(XYZ);
		return results[0];		
	}
	
	public void plotData (Graphics2D g)
	{
		int n = data[0].length;
		
		if (n != 0)
		{
			// Calculate x-coordinates ahead of time
			int[] x = new int[n];
			for (int t = 0; t < n; t++)
				x[t] = (int)(insets.left + (hStep * t) + 0.5);
			
			// Plot points
			
			for (int i = 0; i < data.length; i++)
			{
				if (plotEnabled[i])
				{						
					g.setColor(colors[i]);
					int lastx = x[0];
					int lasty = (int)(vStep[i] * data[i][0] + vOffset[i] + 0.5);
					
					for (int t = 0; t < n; t++)
					{
						int x1 = x[t];
						int y1 = (int)(vStep[i] * data[i][t] + vOffset[i] + 0.5);	
						g.fillRect(x1, y1, 1, 1);
						g.drawLine(x1, y1, lastx, lasty);
						lastx = x1;
						lasty = y1;
					}
				}
			}
		}
	}
	
	public boolean isAllSelected()
	{
		boolean allSelected = true;
		for (int i = GUI.PF_SRED; i < GUI.profilerParams.length; i++)
			allSelected = allSelected && plotEnabled[i];
		return allSelected;
	}
	
	private class MyListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e) 
		{
			recalcHStep(data[0].length);
			recalcVStep();
	        refresh();           
	    }
	}
	
}

