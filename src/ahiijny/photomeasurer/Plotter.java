package ahiijny.photomeasurer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	
	public static double[] defaultHistogramStep = {1/255.0, 1/255.0, 1.79/255.0, 1};
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
	public double[][] profile_data = new double[GUI.labelsProfilerParams.length][1];
	public double[][] area_data = new double[GUI.labelsAreaParams.length][1];
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
	
	public double[] histogramStep = new double[4]; 
	public double[] mins = new double[GUI.labelsAreaParams.length];
	public double[] maxes = new double[GUI.labelsAreaParams.length];
	public int[] histogramWidth = new int[GUI.labelsAreaParams.length];
	
	public double[] hSteps = new double[GUI.labelsAreaParams.length]; // In the relevant units
	public double[] vSteps = new double[GUI.labelsProfilerParams.length]; // In screen pixels
	public double[] vOffset = new double[GUI.labelsProfilerParams.length]; // In screen pixels
	public boolean[] profilePlotEnabled = new boolean[GUI.labelsProfilerParams.length];
	public boolean[] areaPlotEnabled = new boolean[GUI.labelsAreaParams.length]; // In screen pixels
	public Insets insets = new Insets(2,4,2,4);
	public Dimension plotSize = new Dimension(0, 0);
	
	private int areaPointCount = 0;
	
	public Plotter(GUI parent) 
	{
		this.parent = parent;
		setBackground(Color.black);
		setPreferredSize(new Dimension(1, preferredHeight));
		addComponentListener(new MyListener());
		setDefaultExtrema();
		setDefaultHistogramStep();
	}	
		
	public void refresh()
	{
		if (parent.mode == GUI.PROFILER)
			sampleProfile();
		else if (parent.mode == GUI.AREA)
			sampleArea(parent.ip.getAreaSelection());
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
			sampleProfile();
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
	
	public void setProfilePlotEnabled(int param, boolean enabled)
	{
		profilePlotEnabled[param] = enabled;
	}
	
	public void setAreaPlotEnabled(int param, boolean enabled) 
	{
		areaPlotEnabled[param] = enabled;
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
	
	public void sampleArea(Shape shape)
	{
		if (shape != null)
		{
			// Write status
			
			parent.setTitle("Working...");
			editLock = true;
			parent.refresh();
			
			// Initialize bounds of checking area
	
			Rectangle bounds = shape.getBounds();
			int left = Math.max(bounds.x, 0);
			int down = Math.max(bounds.y, 0);
			int right = bounds.x + bounds.width;
			int up = bounds.y + bounds.height;	
			
			// Create tester
			
			BufferedImage tester = new BufferedImage(bounds.x + bounds.width, bounds.y + bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)tester.getGraphics();
			g.setColor(new Color(0));
			g.draw(shape);
									
			// Iterate through grid selection and store point, if within shape
			
			ArrayList<Point> points = new ArrayList<Point>(bounds.width * bounds.height);
	
			for (int x = left; x < right; x++)
				for (int y = down; y < up; y++)	
					if (tester.getRGB(x, y) == 0)	
						points.add(new Point(x, y));
			// Set up data array
			
			recalcHistogramSteps();			
			area_data = new double[GUI.labelsAreaParams.length][];
			
			for (int i = 0; i < area_data.length; i++)
			{
				area_data[i] = new double[histogramWidth[i]];
				
				// Init histogram counters to 0
				for (int j = 0; j < area_data[i].length; j++)
					area_data[i][j] = 0;
			}
			
			// Sample data
			
			int n = points.size();
			areaPointCount = n;
			for (int i = 0; i < n; i++)
			{	
				Point pt = points.get(i);
				double[] thisPoint = {pt.x, pt.y};
				
				double[] rgb = sample_RGB(thisPoint);
				area_data[GUI.AR_SRED][getHistogramIndex(GUI.AR_SRED, rgb[0])]++;
				area_data[GUI.AR_SGREEN][getHistogramIndex(GUI.AR_SGREEN, rgb[1])]++;
				area_data[GUI.AR_SBLUE][getHistogramIndex(GUI.AR_SBLUE, rgb[2])]++;
				
				double[] rgb_lin = sample_RGB_lin(thisPoint, rgb);
				area_data[GUI.AR_LINRED][getHistogramIndex(GUI.AR_LINRED, rgb_lin[0])]++;
				area_data[GUI.AR_LINGREEN][getHistogramIndex(GUI.AR_LINGREEN, rgb_lin[1])]++;
				area_data[GUI.AR_LINBLUE][getHistogramIndex(GUI.AR_LINBLUE, rgb_lin[2])]++;
				
				double[] XYZ = sample_XYZ(thisPoint, rgb);
				area_data[GUI.AR_X][getHistogramIndex(GUI.AR_X, XYZ[0])]++;
				area_data[GUI.AR_Y][getHistogramIndex(GUI.AR_Y, XYZ[1])]++;
				area_data[GUI.AR_Z][getHistogramIndex(GUI.AR_Z, XYZ[2])]++;
				
				double lambda1 = sample_lambda_XYZ(thisPoint, XYZ);
				area_data[GUI.AR_LAM_XYZ][getHistogramIndex(GUI.AR_LAM_XYZ, lambda1)]++;
				
				double lambda2= sample_lambda_xy(thisPoint, XYZ);
				area_data[GUI.AR_LAM_xy][getHistogramIndex(GUI.AR_LAM_xy, lambda2)]++;
				
				double lambda3 = sample_lambda_RGB(thisPoint);
				area_data[GUI.AR_LAM_RGB][getHistogramIndex(GUI.AR_LAM_RGB, lambda3)]++;
				
				double lambda4 = sample_lambda_sat_extrap(thisPoint, XYZ);
				area_data[GUI.AR_LAM_SAT_EXTRAP][getHistogramIndex(GUI.AR_LAM_SAT_EXTRAP, lambda4)]++;
			}
			parent.resetTitle();
			editLock = false;
		}				
	}
	
	/** For histogram area calculations
	 */
	private int getHistogramIndex(int param, double value)
	{
		int bin = (int)(value / hSteps[param] - mins[param]+ 0.5);
		if (bin < 0)
			bin = 0;
		if (bin >= histogramWidth[param])
			bin = histogramWidth[param] - 1;
		return bin;
	}
	
	public void sampleProfile()
	{
		// Set up
		
		editLock = true;
		double T = getLength();
		int n = getPointCount();		
		double dt = T/n;
		double[][] points = new double[n][2];
		profile_data = new double[GUI.labelsProfilerParams.length][n];
		
		recalcHStep(n);
		
		// Compute LUT for points in linear profile
		
		for (int i = 0; i < n; i++)
		{
			points[i] = Calc.add(r, Calc.scale(m, i * dt));
			profile_data[GUI.PF_X_COORD][i] = points[i][0];
			profile_data[GUI.PF_Y_COORD][i] = points[i][1];
			profile_data[GUI.PF_T_COORD][i] = 1.0 * i * pixelsPerSample;
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
			profile_data[GUI.PF_SRED][i] = rgb[0];
			profile_data[GUI.PF_SGREEN][i] = rgb[1];
			profile_data[GUI.PF_SBLUE][i] = rgb[2];
			
			double[] rgb_lin = sample_RGB_lin(points[i], rgb);
			profile_data[GUI.PF_LINRED][i] = rgb_lin[0];
			profile_data[GUI.PF_LINGREEN][i] = rgb_lin[1];
			profile_data[GUI.PF_LINBLUE][i] = rgb_lin[2];
			
			double[] XYZ = sample_XYZ(points[i], rgb);
			profile_data[GUI.PF_X][i] = XYZ[0];
			profile_data[GUI.PF_Y][i] = XYZ[1];
			profile_data[GUI.PF_Z][i] = XYZ[2];
			
			double lambda1 = sample_lambda_XYZ(points[i], XYZ);
			profile_data[GUI.PF_LAM_XYZ][i] = lambda1;
			
			double lambda2= sample_lambda_xy(points[i], XYZ);
			profile_data[GUI.PF_LAM_xy][i] = lambda2;
			
			double lambda3 = sample_lambda_RGB(points[i]);
			profile_data[GUI.PF_LAM_RGB][i] = lambda3;
			
			double lambda4 = sample_lambda_sat_extrap(points[i], XYZ);
			profile_data[GUI.PF_LAM_SAT_EXTRAP][i] = lambda4;
		}
		editLock = false;
	}
	
	public void recalcPlotSize()
	{
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		plotSize = new Dimension(width, height);
	}
	
	public void recalcHStep(int n)
	{
		double length = plotSize.width;
		hStep = length / n;
	}
	
	public void recalcHistogramSteps()
	{		
		hSteps[GUI.AR_SRED] = histogramStep[GUI.AR_SRGB_STEP];
		hSteps[GUI.AR_SGREEN] = histogramStep[GUI.AR_SRGB_STEP];
		hSteps[GUI.AR_SBLUE] = histogramStep[GUI.AR_SRGB_STEP];		
		hSteps[GUI.AR_LINRED] = histogramStep[GUI.AR_LINRGB_STEP];
		hSteps[GUI.AR_LINGREEN] = histogramStep[GUI.AR_LINRGB_STEP];
		hSteps[GUI.AR_LINBLUE] = histogramStep[GUI.AR_LINRGB_STEP];		
		hSteps[GUI.AR_X] = histogramStep[GUI.AR_XYZ_STEP];
		hSteps[GUI.AR_Y] = histogramStep[GUI.AR_XYZ_STEP];
		hSteps[GUI.AR_Z] = histogramStep[GUI.AR_XYZ_STEP];		
		hSteps[GUI.AR_LAM_XYZ] = histogramStep[GUI.AR_LAMBDA_STEP];
		hSteps[GUI.AR_LAM_xy] = histogramStep[GUI.AR_LAMBDA_STEP];
		hSteps[GUI.AR_LAM_RGB] = histogramStep[GUI.AR_LAMBDA_STEP];
		hSteps[GUI.AR_LAM_SAT_EXTRAP] = histogramStep[GUI.AR_LAMBDA_STEP];
		
		histogramWidth[GUI.AR_SRED] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_SRGB_STEP] + 1.5);
		histogramWidth[GUI.AR_SGREEN] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_SRGB_STEP]+ 1.5);
		histogramWidth[GUI.AR_SBLUE] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_SRGB_STEP]+ 1.5);		
		histogramWidth[GUI.AR_LINRED] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_LINRGB_STEP]+ 1.5);
		histogramWidth[GUI.AR_LINGREEN] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_LINRGB_STEP]+ 1.5);
		histogramWidth[GUI.AR_LINBLUE] = (int)((maxRGB - minRGB)/histogramStep[GUI.AR_LINRGB_STEP]+ 1.5);		
		histogramWidth[GUI.AR_X] = (int)((maxXYZ - minXYZ)/histogramStep[GUI.AR_XYZ_STEP]+ 1.5);
		histogramWidth[GUI.AR_Y] = (int)((maxXYZ - minXYZ)/histogramStep[GUI.AR_XYZ_STEP]+ 1.5);
		histogramWidth[GUI.AR_Z] = (int)((maxXYZ - minXYZ)/histogramStep[GUI.AR_XYZ_STEP]+ 1.5);		
		histogramWidth[GUI.AR_LAM_XYZ] = (int)((maxLambda - minLambda)/histogramStep[GUI.AR_LAMBDA_STEP]+ 1.5);
		histogramWidth[GUI.AR_LAM_xy] = (int)((maxLambda - minLambda)/histogramStep[GUI.AR_LAMBDA_STEP]+ 1.5);
		histogramWidth[GUI.AR_LAM_RGB] = (int)((maxLambda - minLambda)/histogramStep[GUI.AR_LAMBDA_STEP]+ 1.5);
		histogramWidth[GUI.AR_LAM_SAT_EXTRAP] = (int)((maxLambda - minLambda)/histogramStep[GUI.AR_LAMBDA_STEP]+ 1.5);
	}
	
	public void recalcVStep()
	{
		double height = plotSize.height;
		
		if (parent.mode == GUI.PROFILER)
		{			
			double stepRGB = height / (maxRGB - minRGB);
			double stepXYZ = height / (maxXYZ - minXYZ);
			double stepLambda = height / (maxLambda - minLambda);
							
			vSteps[GUI.PF_SRED] = stepRGB;
			vSteps[GUI.PF_SGREEN] = stepRGB;
			vSteps[GUI.PF_SBLUE] = stepRGB;
			vSteps[GUI.PF_LINRED] = stepRGB;
			vSteps[GUI.PF_LINGREEN] = stepRGB;
			vSteps[GUI.PF_LINBLUE] = stepRGB;
			vSteps[GUI.PF_X] = stepXYZ;
			vSteps[GUI.PF_Y] = stepXYZ;
			vSteps[GUI.PF_Z] = stepXYZ;
			vSteps[GUI.PF_LAM_XYZ] = stepLambda;
			vSteps[GUI.PF_LAM_xy] = stepLambda;
			vSteps[GUI.PF_LAM_SAT_EXTRAP] = stepLambda;
			vSteps[GUI.PF_LAM_RGB] = stepLambda;
			
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
		else if (parent.mode == GUI.AREA)
		{
			for (int i = 0; i < GUI.labelsAreaParams.length; i++)
			{
				// Find max value
				int max = Integer.MIN_VALUE;
				for (int j = 0; j < area_data[i].length; j++)
					if (area_data[i][j] > max)
						max = (int)area_data[i][j];
				vSteps[i] = height / max;
				vOffset[i] = -mins[i] * vSteps[i] + insets.bottom;				
			}
		}
	}
	
	public int getAreaPointCount()
	{
		return areaPointCount;
	}
	
	public double getHistogramValue(int mode, int index)
	{
		double result = 0;
		if (parent.mode == GUI.AREA)
			result = mins[mode] + hSteps[mode] * index;
		return result;
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
	
	public double[] sample_RGB_lin(double[] location, double[] rgb)
	{	
		return Calc.sRGBreverseGamma(rgb);
	}
	
	public double[] sample_XYZ(double[] location, double[] rgb)
	{
		return Calc.RGBtoXYZ(rgb);
	}
	
	public double sample_lambda_XYZ (double[] location, double[] XYZ)
	{
		double[] results = Calc.getPrimaryWavelengthFitXYZ(XYZ);
		return results[0];		
	}
	
	public double sample_lambda_xy (double[] location, double[] XYZ)
	{
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
	
	public double sample_lambda_sat_extrap (double[] location, double[] XYZ)
	{
		double[] results = Calc.getPrimaryWavelengthSatExtrap(XYZ);
		return results[0];		
	}
	
	public void setDefaultExtrema()
	{
		mins[GUI.AR_SRED] = minRGB;
		mins[GUI.AR_SGREEN] = minRGB;
		mins[GUI.AR_SBLUE] = minRGB;
		mins[GUI.AR_LINRED] = minRGB;
		mins[GUI.AR_LINGREEN] = minRGB;
		mins[GUI.AR_LINBLUE] = minRGB;
		mins[GUI.AR_X] = minXYZ;
		mins[GUI.AR_Y] = minXYZ;
		mins[GUI.AR_Z] = minXYZ;
		mins[GUI.AR_LAM_XYZ] = minLambda;
		mins[GUI.AR_LAM_xy] = minLambda;
		mins[GUI.AR_LAM_RGB] = minLambda;
		mins[GUI.AR_LAM_SAT_EXTRAP] = minLambda;
		
		maxes[GUI.AR_SRED] = maxRGB;
		maxes[GUI.AR_SGREEN] = maxRGB;
		maxes[GUI.AR_SBLUE] = maxRGB;
		maxes[GUI.AR_LINRED] = maxRGB;
		maxes[GUI.AR_LINGREEN] = maxRGB;
		maxes[GUI.AR_LINBLUE] = maxRGB;
		maxes[GUI.AR_X] = maxXYZ;
		maxes[GUI.AR_Y] = maxXYZ;
		maxes[GUI.AR_Z] = maxXYZ;
		maxes[GUI.AR_LAM_XYZ] = maxLambda;
		maxes[GUI.AR_LAM_xy] = maxLambda;
		maxes[GUI.AR_LAM_RGB] = maxLambda;
		maxes[GUI.AR_LAM_SAT_EXTRAP] = maxLambda;
	}
	
	public void setDefaultHistogramStep()
	{
		for (int i = 0; i < histogramStep.length; i++)
			histogramStep[i] = defaultHistogramStep[i];
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);;
		
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform at = AffineTransform.getScaleInstance(1, -1);
		at.preConcatenate(AffineTransform.getTranslateInstance(0, getHeight()));
		g2d.transform(at);
		
		if (!editLock)
		{
			if (parent.mode == GUI.PROFILER)
				plotProfile(g2d);
			else if (parent.mode == GUI.AREA)
				plotHistogram(g2d);
		}
	}	
	
	public void plotProfile (Graphics2D g)
	{
		int n = profile_data[0].length;
		
		if (n != 0)
		{
			// Calculate x-coordinates ahead of time
			int[] x = new int[n];
			for (int t = 0; t < n; t++)
				x[t] = (int)(insets.left + (hStep * t) + 0.5);
			
			// Plot points
			
			for (int i = 0; i < profile_data.length; i++)
			{
				if (profilePlotEnabled[i])
				{						
					g.setColor(colors[i]);
					int lastx = x[0];
					int lasty = (int)(vSteps[i] * profile_data[i][0] + vOffset[i] + 0.5);
					
					for (int t = 0; t < n; t++)
					{
						int x1 = x[t];
						int y1 = (int)(vSteps[i] * profile_data[i][t] + vOffset[i] + 0.5);	
						g.fillRect(x1, y1, 1, 1);
						g.drawLine(x1, y1, lastx, lasty);
						lastx = x1;
						lasty = y1;
					}
				}
			}
		}
	}
	
	public void plotHistogram(Graphics2D g)
	{
		recalcVStep();
		
		// Plot points
		
		for (int i = 0; i < area_data.length; i++)
		{
			if (areaPlotEnabled[i])
			{	
				g.setColor(colors[i+3]);
				int lastx = (int)(insets.left + 0.5);
				int lasty = (int)(vSteps[i] * area_data[i][0] + vOffset[i] + 0.5);
				recalcHStep(area_data[i].length);
				
				for (int t = 0; t < area_data[i].length; t++)
				{
					int x1 = (int)(insets.left + (hStep * t) + 0.5);
					int y1 = (int)(vSteps[i] * area_data[i][t] + vOffset[i] + 0.5);
										
					g.fillRect(x1, y1, 1, 1);
					g.drawLine(x1, y1, lastx, lasty);
					lastx = x1;
					lasty = y1;
				}			
			}
		}
	}
	
	public double[] getSummedXYZ()
	{
		double[] XYZ = {0, 0, 0};
		int n = area_data[GUI.AR_X].length;
		for (int i = 0; i < n; i++)
		{
			double magnitude = getHistogramValue(GUI.AR_X, i);
			XYZ[0] += magnitude * area_data[GUI.AR_X][i];
			XYZ[1] += magnitude * area_data[GUI.AR_Y][i];
			XYZ[2] += magnitude * area_data[GUI.AR_Z][i];
		}
		return XYZ;
	}
	
	public boolean isAllSelected()
	{
		boolean allSelected = true;
		for (int i = GUI.PF_SRED; i < GUI.labelsProfilerParams.length; i++)
			allSelected = allSelected && profilePlotEnabled[i];
		return allSelected;
	}
	
	private class MyListener extends ComponentAdapter
	{
		public void componentResized(ComponentEvent e) 
		{
			recalcPlotSize();
			recalcHStep(profile_data[0].length);
			recalcVStep();
	        repaint();           
	    }
	}		
}

