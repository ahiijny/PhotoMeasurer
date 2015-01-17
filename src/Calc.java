import java.awt.Color;
import java.awt.Point;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Calc 
{
	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	
	public static final int X = 0;
	public static final int Y = 1;
	
	public static final double[][] XYZtoRGBlin = 
	{
		{3.2406, -1.5372, -0.4986},
		{-0.9689, 1.8758, 0.0415},
		{0.0557, -0.2040, 1.0570}
	}
	;
	
	public static final double[][] RGBlintoXYZ =
	{
		{0.4124, 0.3576, 0.1805},
		{0.2126, 0.7152, 0.0722},
		{0.0193, 0.1192, 0.9505}
	}
	;
	
	public static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss XX");
	public static DecimalFormat large = new DecimalFormat("0.000E0");
	public static DecimalFormat larger = new DecimalFormat("0.000000000000E0");
	public static DecimalFormat small = new DecimalFormat("0.00");
	public static DecimalFormat smaller = new DecimalFormat("0.0000");	
	public static DecimalFormat whole = new DecimalFormat("0");
	public static DecimalFormat precise8 = new DecimalFormat("0.########");
	public static DecimalFormat precise12 = new DecimalFormat("0.############");
	
		
	/** (http://chemistry.about.com/od/workedchemistryproblems/a/scalar-product-vectors-problem.htm) <p>
	 * 
	 * A &middot; B = |A||B|cos &theta; <br>
	 * A &middot; B = (A.x)(B.x) + (A.y)(B.y) <p>
	 * 
	 * Therefore &theta; = acos[ ((A.x)(B.x) + (A.y)(B.y)) / (|A||B|) ]
	 *  
	 * @param A			outer point
	 * @param B			center point
	 * @param C			outer point
	 * @return the angle between the three points at vertex B in degrees
	 */
	public static double findAngle(double[] A, double[] B, double[] C)
	{
		double[] F = new double[2];
		double[] G = new double[2];
		F[X] = A[X] - B[X];
		F[Y] = A[Y] - B[Y];
		G[X] = C[X] - B[X];
		G[Y] = C[Y] - B[Y];
		double radians = Math.acos(((F[X] * G[X]) + (F[Y] * G[Y])) / (mag(F) * mag(G)));
		return Math.toDegrees(radians);
	}
	
	public static double findAngle(Point A, Point B, Point C)
	{
		return findAngle(new double[]{A.x, A.y},
						 new double[]{B.x, B.y},
						 new double[]{C.x, C.y});
	}
	
	/** Converts the specified vector into a scalar.
	 * (http://www.icoachmath.com/math_dictionary/magnitude_of_a_vector.html)
	 *  
	 * @param vector 	an ordered pair
	 * @return the magnitude of the vector
	 */
	public static double scalar(Point vector)
	{
		return Math.sqrt((vector.x * vector.x) + (vector.y * vector.y));
	}
	
	/** Finds the Cartesian distance between two points. 
	 * (http://www.mathwarehouse.com/algebra/distance_formula/index.php)
	 * 
	 * @param A		the first ordered pair
	 * @param B		the second ordered pair
	 * @return a distance scalar
	 */
	public static double findDistance(Point A, Point B)
	{
		return findDistance(A, B, 1);
	}
	
	/** Finds the Cartesian distance between two points, scaled
	 * according to the given conversion ratio. Uses distance formula.
	 * (http://www.mathwarehouse.com/algebra/distance_formula/index.php) 
	 * 
	 * @param A		the first ordered pair
	 * @param B		the second ordered pair
	 * @param pixelsPerUnit		the number of Cartesian coordinate
	 * 							units in one desired output distance unit
	 * @return a distance scalar in the specified units
	 */
	public static double findDistance(Point A, Point B, double pixelsPerUnit)
	{
		double distance = Math.sqrt(Math.pow((A.x - B.x), 2) + Math.pow((A.y - B.y), 2));
		distance /= pixelsPerUnit;
		return distance;
	}
	
	/** Returns the cross product of a and b. Must be in R3.
	 * 
	 * @param a 	the first vector
	 * @param b		the second vector
	 * @return	a x b
	 */
	public static double[] cross(double[] a, double[] b)
	{
		double[] c = new double[3];
		c[0] = a[1]*b[2] - a[2]*b[1];
		c[1] = -(a[0]*b[2] - a[2]*b[0]);
		c[2] = a[0]*b[1] - a[1]*b[0];
		return c;
	}
	
	/** Multiplies the given vector by the given scalar.
	 * 
	 * @param a			the vector
	 * @param scalar	the scalar
	 * @return the scaled vector.
	 */
	public static double[] scale(double[] a, double scalar)
	{
		double c[] = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] * scalar;
		return c;
	}
	
	/** Adds the two vectors together.
	 * 
	 * @param a		the first vector
	 * @param b		the second vector
	 * @return the sum of a and b.
	 */
	public static double[] add(double[] a, double[] b)
	{
		double c[] = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i] + b[i];
		return c;
	}
	
	/** Returns the dot product of a and b. Must be in R3. 
	 * 
	 * @param a		the first vector
	 * @param b		the second vector
	 * @return a &sdot; b
	 */
	public static double dot(double[] a, double[] b)
	{
		return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
	}
	
	/** Returns the magnitude of the given R3 vector.
	 * 
	 * @param a		the vector
	 * @return the magnitude (length) of the vector
	 */
	public static double mag(double[] a)
	{
		double m = 0;
		
		for (int i = 0; i < a.length; i++)
			m += a[i]*a[i];
		m = Math.sqrt(m);
		return m;
	}
	
	/** Returns the square of the magnitude of the given R3 vector.
	 * 
	 * @param a		the vector
	 * @return the vector magnitude squared
	 */
	public static double sqmag(double[] a)
	{
		double sqm = 0;
		for (int i = 0; i < a.length; i++)
			sqm += a[i]*a[i];
		return sqm;
	}
	
	/** Scales the given vector such that it is a unit vector pointing
	 * in the same direction as the given vector. If the given vector
	 * has magnitude zero, does nothing and returns the given vector.
	 * 
	 * @param a		the vector
	 * @return unit vector if magnitude not 0; the given vector otherwise
	 */
	public static double[] unit(double[] a)
	{
		double mag = mag(a);
		if (mag != 0)
			return scale(a, 1/mag);
		else
			return copy(a);
	}
	
	/** Returns a copy of the given vector. The returned
	 * vector is a reference to a new object separate from
	 * the given vector. Changing one will not change the other.
	 * 
	 * @param a		the vector
	 * @return a copy of the given vector
	 */
	public static double[] copy(double[] a)
	{
		double[] c = new double[a.length];
		for (int i = 0; i < a.length; i++)
			c[i] = a[i];
		return c;		
	}
	
	/** Sets all of the components of the given vector to 0.
	 * 
	 * @param a		the vector
	 * @return the given vector, but zeroed
	 */
	public static double[] empty(double[] a)
	{
		for (int i = 0; i < a.length; i++)
			a[i] = 0;
		return a;
	}
	
	/** For debugging. Prints the given vector. And then a line break.
	 * 
	 * @param a		the vector
	 */
	public static void println(double[] a)
	{
		for (int i = 0; i < a.length - 1; i++)
			System.out.print(a[i] + ",");
		System.out.println(a[a.length-1]);
	}
	
	/** Returns the CIE 1931 2-deg XYZ (D65) color coordinates for
	 * the specified wavelength in nanometers.
	 *  
	 * @param nanometers
	 * @return
	 */
	public static double[] lambdaToXYZ(double nanometers)
	{
		double[] XYZ = {0, 0, 0};
		int nm = (int)(nanometers + 0.5);
		
		if (nm >= 360 && nm <= 830)		
			XYZ = copy(cmf_cie31[nm - 360]);
		
		return XYZ;
	}
	
	/** Returns the xyz color coordinates that are normalized
	 * to Y = 1.
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] norm(double[] XYZ)
	{
		double scalar = XYZ[1];
		double[] xyz = Calc.scale(XYZ, 1/scalar);
		return xyz;
	}
	
	/** Accepts an nonlinear integer RGB value scaled to
	 * the gamma specified by sRGB standards. Converts
	 * to XYZ. RGB values are in the range [0..255].
	 * 
	 * @param rgb
	 * @return
	 */
	public static double[] RGBtoXYZ(int[] rgb)
	{
		double[] rgb255 = {rgb[0], rgb[1], rgb[2]};
		double[] rgb_norm = scale(rgb255, 1.0/255);		
		return RGBtoXYZ(rgb_norm);
	}
	
	/** Accepts an nonlinear gamma scaled RGB value as 
	 * specified by sRGB standards. Converts to XYZ. 
	 * RGB values must be in the range [0..1].
	 * 
	 * @param rgb
	 * @return
	 */
	public static double[] RGBtoXYZ(double[] rgb)
	{
		// Undo perceptual uniformity
		
		double[][] rgb_lin = Matrix.getColumnMatrix(sRGBreverseGamma(rgb));
		
		// Transform to XYZ
		
		double[][] XYZ = Matrix.multiply(RGBlintoXYZ, rgb_lin);
				
		return Matrix.getColumnVector(XYZ);
	}
	
	/** Converts from CIE 1931 2-deg XYZ(D65) to gamma-scaled
	 * sRGB. Caution: colors that are out of gamut may return
	 * sRGB values that are negative or greater than 1.
	 * 
	 * XYZ values should be normalized so that Y = 1 for maximum
	 * luminosity.
	 * 
	 * To convert to integer 8-bit RGB, multiply output by
	 * 255 and round.
	 * 
	 * @param xyz
	 * @return
	 */
	public static double[] XYZtoRGB(double[] xyz)
	{
		// Transform to RGB_lin
		
		double[][] XYZ = Matrix.getColumnMatrix(xyz);
		double[] rgb_lin = Matrix.getColumnVector(Matrix.multiply(XYZtoRGBlin, XYZ));
		double[] rgb = {0, 0, 0};
		
		// Transform to perceptual uniformity
		
		rgb = sRGBgamma(rgb_lin);
		
		return rgb;
	}
	
	/** Scales a linear function into an exponential one
	 * to approximate perceptual uniformity as a function
	 * of the bit value. The conversion formula is as
	 * specified by sRGB. The input RGB_lin values should be in
	 * the range [0..1].
	 * 
	 * @param rgb_lin
	 * @return
	 */
	public static double[] sRGBgamma(double[] rgb_lin)
	{
		double[] rgb = {0,0,0};
		
		for (int i = 0; i < 3; i++)
		{
			if (rgb_lin[i] <= 0.0031308)
				rgb[i] = 12.92 * rgb_lin[i];
			else
				rgb[i] = 1.055 * Math.pow(rgb_lin[i], 1/2.4) - 0.055;
		}
		return rgb;
	}
	
	/** Undos the gamma of the given nonlinear RGB values.
	 * This undoes the perceptual uniformity of the brightness
	 * as a function of the bit value. The conversion formula 
	 * is as specified by sRGB. The input RGB values should be in
	 * the range [0..1].
	 * 
	 * @param rgb
	 * @return
	 */
	public static double[] sRGBreverseGamma(double[] rgb)
	{
		double[] rgb_lin = {0,0,0};
		
		for (int i = 0; i < 3; i++)
		{
			if (rgb[i] <= 0.04045)
				rgb_lin[i] = rgb[i] / 12.92;
			else
				rgb_lin[i] = Math.pow((rgb[i] + 0.055) / 1.055, 2.4);
		}
		
		return rgb_lin;
	}
	
	/** Converts XYZ to xyY
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] XYZtoxyY(double[] XYZ)
	{
		double[] xyY = new double[3];
		
		xyY[0] = XYZ[0] / (XYZ[0] + XYZ[1] + XYZ[2]);
		xyY[1] = XYZ[1] / (XYZ[0] + XYZ[1] + XYZ[2]);
		xyY[2] = XYZ[1];
		
		return xyY;
	}
	
	/** Returns the color for the specified RGB values
	 * in the range [0..1]. Values out of range will be
	 * truncated.
	 * 
	 * @param rgb
	 * @return
	 */
	public static Color getRGB(double[] rgb)
	{
		int[] rgb255 = new int [3];
		for (int i = 0; i < 3; i++)
		{
			rgb[i] = rgb[i] < 0 ? 0 : rgb[i]; 		// Truncate to 0 if < 0
			rgb[i] = rgb[i] > 1 ? 1 : rgb[i];		// Truncate to 1 if > 1
			rgb255[i] = (int)(rgb[i] * 255 + 0.5);			
		}
		
		return new Color(rgb255[0], rgb255[1], rgb255[2]);
	}
	
	/** Returns an int array representing the components of the
	 * given colour.
	 * 
	 * @param c
	 * @return
	 */
	public static int[] getIntRGB(Color color)
	{
		int[] rgb = {color.getRed(), color.getGreen(), color.getBlue()};
		return rgb;
	}
	
	/** Attempts the monochromatic wavelength that most closely matches
	 * the given XYZ color. Does this by brute-forcing the CIE CMF table
	 * and choosing the best value. Returns an array containing two elements:
	 * 
	 *  index 0 - the wavelength in nanometers
	 *  index 1 - the minimized standard error of lambda in X fit
	 *  index 2 - the minimized standard error of lambda in Y fit
	 *  index 3 - the minimized standard error of lambda in Z fit
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] getPrimaryWavelengthFitXYZ(double[] XYZ)
	{		
		double min_sqerror = Double.POSITIVE_INFINITY;
		double[] rmse = new double [3];
		int bestLambda = -1;
		
		// Iterate through CMF table and find best match
		
		for (int i = 0; i < cmf_cie31.length; i++)
		{
			// Compute the squared error at this wavelength
			
			double sqerror = 0;			
			
			for (int j = 0; j < 3; j++)
			{
				double error = cmf_cie31[i][j] - XYZ[j];
				sqerror += error * error;
			}
			
			// If it is smaller than the error so far, then
			// store this value.
			
			if (sqerror < min_sqerror)
			{
				min_sqerror = sqerror;
				bestLambda = indexToNM(i);
			}
		}
		
		// Find RMSE of wavelength
		
		double[] slopes = cmf_slope(bestLambda);
		
		for (int j = 0; j < 3; j++)
		{
			double error = cmf_cie31[nmToIndex(bestLambda)][j] - XYZ[j];
			rmse[j] = error / slopes[j];
			if (slopes[j] == 0)
				rmse[j] = Double.POSITIVE_INFINITY;
		}
		
		double[] result = {bestLambda, rmse[0], rmse[1], rmse[2]};
		
		return result;
	}
	

	
	/** The idea is to saturate the given XYZ color until it reaches the spectral
	 * locus. i.e., determine the dominant wavelength by extrapolating outwards
	 * from the white point in xyY color space.
	 * 
	 * index 0 - the wavelength in nanometers
	 * index 1 - the minimized SSE of fit
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] getPrimaryWavelengthFitxy(double[] XYZ)
	{			
		double[] xyY = XYZtoxyY(XYZ);
		double[] xy = {xyY[0], xyY[1]};
		int bestLambda = -1;
		
		double min_sqerror = Double.POSITIVE_INFINITY;
		
		// Iterate through CMF xy table and find best match
		
		for (int i = 0; i < cmf_xy.length; i++)
		{
			// Compute the squared error at this wavelength
			
			double sqerror = 0;			
			
			for (int j = 0; j < 2; j++)
			{
				double error = cmf_xy[i][j] - xy[j];
				sqerror += error * error;
			}
			
			// If it is smaller than the error so far, then
			// store this value.
			
			if (sqerror < min_sqerror)
			{
				min_sqerror = sqerror;
				bestLambda = indexToNM(i);
			}
		}
		
		double[] result = {bestLambda, min_sqerror};
		
		return result;
	}
	
	/** The idea is to saturate the given XYZ color until it reaches the spectral
	 * locus. i.e., determine the dominant wavelength by extrapolating outwards
	 * from the white point in xyY color space.
	 * 
	 * index 0 - the wavelength in nanometers
	 * index 1 - the minimized SSE of fit
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] getPrimaryWavelengthInverseTrunc(int[] rgb)
	{			
		double[] rgb_double = {rgb[0], rgb[1], rgb[2]};
		rgb_double = scale(rgb_double, 1.0/255); // Scale so that values are in the range [0..1]
		double[] rgb_lin = sRGBreverseGamma(rgb_double);
		int bestLambda = -1;
		
		double min_sqerror = Double.POSITIVE_INFINITY;
		
		// Iterate through CMF rgb_lin table and find best match
		
		for (int i = 0; i < cmf_rgb_lin.length; i++)
		{
			// Compute the squared error at this wavelength
			
			double sqerror = 0;			
			
			for (int j = 0; j < 3; j++)
			{
				double error = cmf_rgb_lin[i][j] - rgb_lin[j];
				sqerror += error * error;
			}
			
			// If it is smaller than the error so far, then
			// store this value.
			
			if (sqerror < min_sqerror)
			{
				min_sqerror = sqerror;
				bestLambda = indexToNM(i);
			}
		}
		
		double[] result = {bestLambda, min_sqerror};
		
		return result;
	}
	
	/** The idea is to saturate the given XYZ color until it reaches the spectral
	 * locus. i.e., determine the dominant wavelength by extrapolating outwards
	 * from the white point in xyY color space.
	 * 
	 * index 0 - the wavelength in nanometers
	 * index 1 - the minimized standard error of lambda in theta fit
	 * 
	 * @param XYZ
	 * @return
	 */
	public static double[] getPrimaryWavelengthSatExtrap(double[] XYZ)
	{			
		double[] xyY = XYZtoxyY(XYZ);
		double[] direction = {xyY[0] - 1/3.0, xyY[1] - 1/3.0};
		direction = unit(direction);
		int bestLambda = -1;
		
		double min_sqerror = Double.POSITIVE_INFINITY;
		
		// Iterate through CMF white-to-locus table and find best match
		
		for (int i = 0; i < cmf_whiteToLocus.length; i++)
		{
			// Compute the squared error at this wavelength
			
			double sqerror = 0;			
			
			for (int j = 0; j < 2; j++)
			{
				double error = cmf_whiteToLocus[i][j] - direction[j];
				sqerror += error * error;
			}
			
			// If it is smaller than the error so far, then
			// store this value.
			
			if (sqerror < min_sqerror)
			{
				min_sqerror = sqerror;
				bestLambda = indexToNM(i);
			}
		}
		
		// Find RMSE of wavelength		
				
		double dlambda = Double.POSITIVE_INFINITY;
		if (bestLambda != -1)
		{
			// Find angle between our colour and spectral colour
			
			double[] white = {1/3.0, 1/3.0};
			double degrees = findAngle(cmf_whiteToLocus[nmToIndex(bestLambda)], white, direction);
			dlambda = degrees * cmf_dlambda_dtheta(bestLambda);
		}
		
		double[] result = {bestLambda, dlambda};
		
		return result;
	}
	
	/** Returns the derivative of the cie cmf
	 * at the specified wavelength. A 1 nm wide
	 * approximation.
	 * 
	 * @param nm
	 * @return
	 */
	public static double[] cmf_slope(double nm)
	{
		double slopes[] = {0, 0, 0};
		
		if (nm >= 360 && nm <= 830)
		{
			int index = nmToIndex(nm);
			// Left edge is 0 
			if (index == nmToIndex(360))
				slopes = copy(cmf_cie31[index + 1]);
			
			// Right edge is 0 
			else if (index == nmToIndex(830))
				slopes = scale(copy(cmf_cie31[index - 1]), -1);
			
			// Otherwise, the numerator of the slope is just the next value subtract the previous value
			else
				slopes = add(cmf_cie31[index + 1], scale(cmf_cie31[index - 1], -1));
		}
		
		// Divide by dlambda
		
		slopes = scale(slopes, 0.5);
		
		return slopes;
	}
	
	/** Returns the derivative of the cie cmf
	 * at the specified wavelength wrt theta
	 * with the white point (1/3,1/3) as an origin.
	 * This is at the locus about the gamut of
	 * vision in xy color space. This is a 1 nm 
	 * wide approximation.
	 * 
	 * i.e. we are finding dlambda/dtheta
	 * 
	 * @param nm
	 * @return
	 */
	public static double cmf_dlambda_dtheta(double nm)
	{
		double slope = 0;
		
		if (nm <= 360)
			nm = 361;
		else if (nm >= 830)
			nm = 829;
		
		int index = nmToIndex(nm);
		
		// The numerator is just the next value subtract the previous value... i.e. 2 nm
		
		slope = 2;
		
		// Divide by dtheta:
		
		double[] A = cmf_xy[index + 1];
		double[] B = new double[] {1/3.0, 1/3.0};
		double[] C = cmf_xy[index - 1];
		
		slope /= findAngle(A, B, C);
		
		return slope;
	}
		
	public static int indexToNM(int index)
	{
		return index + 360;	
	}
	
	public static int nmToIndex(double nm)
	{
		return (int)(nm + 0.5) - 360;	
	}		
		
	/** The CIE 1931 2-deg spectrum->XYZ colour-matching function.
	 * The first entry is with wavelength = 360 nm. The increments
	 * are 1 nm. The last entry is 830 nm. There are 471 entries. 
	 * 
	 * Source: http://www.cvrl.org/cmfs.htm 
	 */
	public static final double[][] cmf_cie31 = {
		{0.0001299,0.000003917,0.0006061},
		{0.000145847,0.000004393581,0.0006808792},
		{0.0001638021,0.000004929604,0.0007651456},
		{0.0001840037,0.000005532136,0.0008600124},
		{0.0002066902,0.000006208245,0.0009665928},
		{0.0002321,0.000006965,0.001086},
		{0.000260728,0.000007813219,0.001220586},
		{0.000293075,0.000008767336,0.001372729},
		{0.000329388,0.000009839844,0.001543579},
		{0.000369914,0.00001104323,0.001734286},
		{0.0004149,0.00001239,0.001946},
		{0.0004641587,0.00001388641,0.002177777},
		{0.000518986,0.00001555728,0.002435809},
		{0.000581854,0.00001744296,0.002731953},
		{0.0006552347,0.00001958375,0.003078064},
		{0.0007416,0.00002202,0.003486},
		{0.0008450296,0.00002483965,0.003975227},
		{0.0009645268,0.00002804126,0.00454088},
		{0.001094949,0.00003153104,0.00515832},
		{0.001231154,0.00003521521,0.005802907},
		{0.001368,0.000039,0.006450001},
		{0.00150205,0.0000428264,0.007083216},
		{0.001642328,0.0000469146,0.007745488},
		{0.001802382,0.0000515896,0.008501152},
		{0.001995757,0.0000571764,0.009414544},
		{0.002236,0.000064,0.01054999},
		{0.002535385,0.00007234421,0.0119658},
		{0.002892603,0.00008221224,0.01365587},
		{0.003300829,0.00009350816,0.01558805},
		{0.003753236,0.0001061361,0.01773015},
		{0.004243,0.00012,0.02005001},
		{0.004762389,0.000134984,0.02251136},
		{0.005330048,0.000151492,0.02520288},
		{0.005978712,0.000170208,0.02827972},
		{0.006741117,0.000191816,0.03189704},
		{0.00765,0.000217,0.03621},
		{0.008751373,0.0002469067,0.04143771},
		{0.01002888,0.00028124,0.04750372},
		{0.0114217,0.00031852,0.05411988},
		{0.01286901,0.0003572667,0.06099803},
		{0.01431,0.000396,0.06785001},
		{0.01570443,0.0004337147,0.07448632},
		{0.01714744,0.000473024,0.08136156},
		{0.01878122,0.000517876,0.08915364},
		{0.02074801,0.0005722187,0.09854048},
		{0.02319,0.00064,0.1102},
		{0.02620736,0.00072456,0.1246133},
		{0.02978248,0.0008255,0.1417017},
		{0.03388092,0.00094116,0.1613035},
		{0.03846824,0.00106988,0.1832568},
		{0.04351,0.00121,0.2074},
		{0.0489956,0.001362091,0.2336921},
		{0.0550226,0.001530752,0.2626114},
		{0.0617188,0.001720368,0.2947746},
		{0.069212,0.001935323,0.3307985},
		{0.07763,0.00218,0.3713},
		{0.08695811,0.0024548,0.4162091},
		{0.09717672,0.002764,0.4654642},
		{0.1084063,0.0031178,0.5196948},
		{0.1207672,0.0035264,0.5795303},
		{0.13438,0.004,0.6456},
		{0.1493582,0.00454624,0.7184838},
		{0.1653957,0.00515932,0.7967133},
		{0.1819831,0.00582928,0.8778459},
		{0.198611,0.00654616,0.959439},
		{0.21477,0.0073,1.0390501},
		{0.2301868,0.008086507,1.1153673},
		{0.2448797,0.00890872,1.1884971},
		{0.2587773,0.00976768,1.2581233},
		{0.2718079,0.01066443,1.3239296},
		{0.2839,0.0116,1.3856},
		{0.2949438,0.01257317,1.4426352},
		{0.3048965,0.01358272,1.4948035},
		{0.3137873,0.01462968,1.5421903},
		{0.3216454,0.01571509,1.5848807},
		{0.3285,0.01684,1.62296},
		{0.3343513,0.01800736,1.6564048},
		{0.3392101,0.01921448,1.6852959},
		{0.3431213,0.02045392,1.7098745},
		{0.3461296,0.02171824,1.7303821},
		{0.34828,0.023,1.74706},
		{0.3495999,0.02429461,1.7600446},
		{0.3501474,0.02561024,1.7696233},
		{0.350013,0.02695857,1.7762637},
		{0.349287,0.02835125,1.7804334},
		{0.34806,0.0298,1.7826},
		{0.3463733,0.03131083,1.7829682},
		{0.3442624,0.03288368,1.7816998},
		{0.3418088,0.03452112,1.7791982},
		{0.3390941,0.03622571,1.7758671},
		{0.3362,0.038,1.77211},
		{0.3331977,0.03984667,1.7682589},
		{0.3300411,0.041768,1.764039},
		{0.3266357,0.043766,1.7589438},
		{0.3228868,0.04584267,1.7524663},
		{0.3187,0.048,1.7441},
		{0.3140251,0.05024368,1.7335595},
		{0.308884,0.05257304,1.7208581},
		{0.3032904,0.05498056,1.7059369},
		{0.2972579,0.05745872,1.6887372},
		{0.2908,0.06,1.6692},
		{0.2839701,0.06260197,1.6475287},
		{0.2767214,0.06527752,1.6234127},
		{0.2689178,0.06804208,1.5960223},
		{0.2604227,0.07091109,1.564528},
		{0.2511,0.0739,1.5281},
		{0.2408475,0.077016,1.4861114},
		{0.2298512,0.0802664,1.4395215},
		{0.2184072,0.0836668,1.3898799},
		{0.2068115,0.0872328,1.3387362},
		{0.19536,0.09098,1.28764},
		{0.1842136,0.09491755,1.2374223},
		{0.1733273,0.09904584,1.1878243},
		{0.1626881,0.1033674,1.1387611},
		{0.1522833,0.1078846,1.090148},
		{0.1421,0.1126,1.0419},
		{0.1321786,0.117532,0.9941976},
		{0.1225696,0.1226744,0.9473473},
		{0.1132752,0.1279928,0.9014531},
		{0.1042979,0.1334528,0.8566193},
		{0.09564,0.13902,0.8129501},
		{0.08729955,0.1446764,0.7705173},
		{0.07930804,0.1504693,0.7294448},
		{0.07171776,0.1564619,0.6899136},
		{0.06458099,0.1627177,0.6521049},
		{0.05795001,0.1693,0.6162},
		{0.05186211,0.1762431,0.5823286},
		{0.04628152,0.1835581,0.5504162},
		{0.04115088,0.1912735,0.5203376},
		{0.03641283,0.199418,0.4919673},
		{0.03201,0.20802,0.46518},
		{0.0279172,0.2171199,0.4399246},
		{0.0241444,0.2267345,0.4161836},
		{0.020687,0.2368571,0.3938822},
		{0.0175404,0.2474812,0.3729459},
		{0.0147,0.2586,0.3533},
		{0.01216179,0.2701849,0.3348578},
		{0.00991996,0.2822939,0.3175521},
		{0.00796724,0.2950505,0.3013375},
		{0.006296346,0.308578,0.2861686},
		{0.0049,0.323,0.272},
		{0.003777173,0.3384021,0.2588171},
		{0.00294532,0.3546858,0.2464838},
		{0.00242488,0.3716986,0.2347718},
		{0.002236293,0.3892875,0.2234533},
		{0.0024,0.4073,0.2123},
		{0.00292552,0.4256299,0.2011692},
		{0.00383656,0.4443096,0.1901196},
		{0.00517484,0.4633944,0.1792254},
		{0.00698208,0.4829395,0.1685608},
		{0.0093,0.503,0.1582},
		{0.01214949,0.5235693,0.1481383},
		{0.01553588,0.544512,0.1383758},
		{0.01947752,0.56569,0.1289942},
		{0.02399277,0.5869653,0.1200751},
		{0.0291,0.6082,0.1117},
		{0.03481485,0.6293456,0.1039048},
		{0.04112016,0.6503068,0.09666748},
		{0.04798504,0.6708752,0.08998272},
		{0.05537861,0.6908424,0.08384531},
		{0.06327,0.71,0.07824999},
		{0.07163501,0.7281852,0.07320899},
		{0.08046224,0.7454636,0.06867816},
		{0.08973996,0.7619694,0.06456784},
		{0.09945645,0.7778368,0.06078835},
		{0.1096,0.7932,0.05725001},
		{0.1201674,0.8081104,0.05390435},
		{0.1311145,0.8224962,0.05074664},
		{0.1423679,0.8363068,0.04775276},
		{0.1538542,0.8494916,0.04489859},
		{0.1655,0.862,0.04216},
		{0.1772571,0.8738108,0.03950728},
		{0.18914,0.8849624,0.03693564},
		{0.2011694,0.8954936,0.03445836},
		{0.2133658,0.9054432,0.03208872},
		{0.2257499,0.9148501,0.02984},
		{0.2383209,0.9237348,0.02771181},
		{0.2510668,0.9320924,0.02569444},
		{0.2639922,0.9399226,0.02378716},
		{0.2771017,0.9472252,0.02198925},
		{0.2904,0.954,0.0203},
		{0.3038912,0.9602561,0.01871805},
		{0.3175726,0.9660074,0.01724036},
		{0.3314384,0.9712606,0.01586364},
		{0.3454828,0.9760225,0.01458461},
		{0.3597,0.9803,0.0134},
		{0.3740839,0.9840924,0.01230723},
		{0.3886396,0.9874182,0.01130188},
		{0.4033784,0.9903128,0.01037792},
		{0.4183115,0.9928116,0.009529306},
		{0.4334499,0.9949501,0.008749999},
		{0.4487953,0.9967108,0.0080352},
		{0.464336,0.9980983,0.0073816},
		{0.480064,0.999112,0.0067854},
		{0.4959713,0.9997482,0.0062428},
		{0.5120501,1,0.005749999},
		{0.5282959,0.9998567,0.0053036},
		{0.5446916,0.9993046,0.0048998},
		{0.5612094,0.9983255,0.0045342},
		{0.5778215,0.9968987,0.0042024},
		{0.5945,0.995,0.0039},
		{0.6112209,0.9926005,0.0036232},
		{0.6279758,0.9897426,0.0033706},
		{0.6447602,0.9864444,0.0031414},
		{0.6615697,0.9827241,0.0029348},
		{0.6784,0.9786,0.002749999},
		{0.6952392,0.9740837,0.0025852},
		{0.7120586,0.9691712,0.0024386},
		{0.7288284,0.9638568,0.0023094},
		{0.7455188,0.9581349,0.0021968},
		{0.7621,0.952,0.0021},
		{0.7785432,0.9454504,0.002017733},
		{0.7948256,0.9384992,0.0019482},
		{0.8109264,0.9311628,0.0018898},
		{0.8268248,0.9234576,0.001840933},
		{0.8425,0.9154,0.0018},
		{0.8579325,0.9070064,0.001766267},
		{0.8730816,0.8982772,0.0017378},
		{0.8878944,0.8892048,0.0017112},
		{0.9023181,0.8797816,0.001683067},
		{0.9163,0.87,0.001650001},
		{0.9297995,0.8598613,0.001610133},
		{0.9427984,0.849392,0.0015644},
		{0.9552776,0.838622,0.0015136},
		{0.9672179,0.8275813,0.001458533},
		{0.9786,0.8163,0.0014},
		{0.9893856,0.8047947,0.001336667},
		{0.9995488,0.793082,0.00127},
		{1.0090892,0.781192,0.001205},
		{1.0180064,0.7691547,0.001146667},
		{1.0263,0.757,0.0011},
		{1.0339827,0.7447541,0.0010688},
		{1.040986,0.7324224,0.0010494},
		{1.047188,0.7200036,0.0010356},
		{1.0524667,0.7074965,0.0010212},
		{1.0567,0.6949,0.001},
		{1.0597944,0.6822192,0.00096864},
		{1.0617992,0.6694716,0.00092992},
		{1.0628068,0.6566744,0.00088688},
		{1.0629096,0.6438448,0.00084256},
		{1.0622,0.631,0.0008},
		{1.0607352,0.6181555,0.00076096},
		{1.0584436,0.6053144,0.00072368},
		{1.0552244,0.5924756,0.00068592},
		{1.0509768,0.5796379,0.00064544},
		{1.0456,0.5668,0.0006},
		{1.0390369,0.5539611,0.0005478667},
		{1.0313608,0.5411372,0.0004916},
		{1.0226662,0.5283528,0.0004354},
		{1.0130477,0.5156323,0.0003834667},
		{1.0026,0.503,0.00034},
		{0.9913675,0.4904688,0.0003072533},
		{0.9793314,0.4780304,0.00028316},
		{0.9664916,0.4656776,0.00026544},
		{0.9528479,0.4534032,0.0002518133},
		{0.9384,0.4412,0.00024},
		{0.923194,0.42908,0.0002295467},
		{0.907244,0.417036,0.00022064},
		{0.890502,0.405032,0.00021196},
		{0.87292,0.393032,0.0002021867},
		{0.8544499,0.381,0.00019},
		{0.835084,0.3689184,0.0001742133},
		{0.814946,0.3568272,0.00015564},
		{0.794186,0.3447768,0.00013596},
		{0.772954,0.3328176,0.0001168533},
		{0.7514,0.321,0.0001},
		{0.7295836,0.3093381,0.00008613333},
		{0.7075888,0.2978504,0.0000746},
		{0.6856022,0.2865936,0.000065},
		{0.6638104,0.2756245,0.00005693333},
		{0.6424,0.265,0.00004999999},
		{0.6215149,0.2547632,0.00004416},
		{0.6011138,0.2448896,0.00003948},
		{0.5811052,0.2353344,0.00003572},
		{0.5613977,0.2260528,0.00003264},
		{0.5419,0.217,0.00003},
		{0.5225995,0.2081616,0.00002765333},
		{0.5035464,0.1995488,0.00002556},
		{0.4847436,0.1911552,0.00002364},
		{0.4661939,0.1829744,0.00002181333},
		{0.4479,0.175,0.00002},
		{0.4298613,0.1672235,0.00001813333},
		{0.412098,0.1596464,0.0000162},
		{0.394644,0.1522776,0.0000142},
		{0.3775333,0.1451259,0.00001213333},
		{0.3608,0.1382,0.00001},
		{0.3444563,0.1315003,0.000007733333},
		{0.3285168,0.1250248,0.0000054},
		{0.3130192,0.1187792,0.0000032},
		{0.2980011,0.1127691,0.000001333333},
		{0.2835,0.107,0},
		{0.2695448,0.1014762,0},
		{0.2561184,0.09618864,0},
		{0.2431896,0.09112296,0},
		{0.2307272,0.08626485,0},
		{0.2187,0.0816,0},
		{0.2070971,0.07712064,0},
		{0.1959232,0.07282552,0},
		{0.1851708,0.06871008,0},
		{0.1748323,0.06476976,0},
		{0.1649,0.061,0},
		{0.1553667,0.05739621,0},
		{0.14623,0.05395504,0},
		{0.13749,0.05067376,0},
		{0.1291467,0.04754965,0},
		{0.1212,0.04458,0},
		{0.1136397,0.04175872,0},
		{0.106465,0.03908496,0},
		{0.09969044,0.03656384,0},
		{0.09333061,0.03420048,0},
		{0.0874,0.032,0},
		{0.08190096,0.02996261,0},
		{0.07680428,0.02807664,0},
		{0.07207712,0.02632936,0},
		{0.06768664,0.02470805,0},
		{0.0636,0.0232,0},
		{0.05980685,0.02180077,0},
		{0.05628216,0.02050112,0},
		{0.05297104,0.01928108,0},
		{0.04981861,0.01812069,0},
		{0.04677,0.017,0},
		{0.04378405,0.01590379,0},
		{0.04087536,0.01483718,0},
		{0.03807264,0.01381068,0},
		{0.03540461,0.01283478,0},
		{0.0329,0.01192,0},
		{0.03056419,0.01106831,0},
		{0.02838056,0.01027339,0},
		{0.02634484,0.009533311,0},
		{0.02445275,0.008846157,0},
		{0.0227,0.00821,0},
		{0.02108429,0.007623781,0},
		{0.01959988,0.007085424,0},
		{0.01823732,0.006591476,0},
		{0.01698717,0.006138485,0},
		{0.01584,0.005723,0},
		{0.01479064,0.005343059,0},
		{0.01383132,0.004995796,0},
		{0.01294868,0.004676404,0},
		{0.0121292,0.004380075,0},
		{0.01135916,0.004102,0},
		{0.01062935,0.003838453,0},
		{0.009938846,0.003589099,0},
		{0.009288422,0.003354219,0},
		{0.008678854,0.003134093,0},
		{0.008110916,0.002929,0},
		{0.007582388,0.002738139,0},
		{0.007088746,0.002559876,0},
		{0.006627313,0.002393244,0},
		{0.006195408,0.002237275,0},
		{0.005790346,0.002091,0},
		{0.005409826,0.001953587,0},
		{0.005052583,0.00182458,0},
		{0.004717512,0.00170358,0},
		{0.004403507,0.001590187,0},
		{0.004109457,0.001484,0},
		{0.003833913,0.001384496,0},
		{0.003575748,0.001291268,0},
		{0.003334342,0.001204092,0},
		{0.003109075,0.001122744,0},
		{0.002899327,0.001047,0},
		{0.002704348,0.0009765896,0},
		{0.00252302,0.0009111088,0},
		{0.002354168,0.0008501332,0},
		{0.002196616,0.0007932384,0},
		{0.00204919,0.00074,0},
		{0.00191096,0.0006900827,0},
		{0.001781438,0.00064331,0},
		{0.00166011,0.000599496,0},
		{0.001546459,0.0005584547,0},
		{0.001439971,0.00052,0},
		{0.001340042,0.0004839136,0},
		{0.001246275,0.0004500528,0},
		{0.001158471,0.0004183452,0},
		{0.00107643,0.0003887184,0},
		{0.0009999493,0.0003611,0},
		{0.0009287358,0.0003353835,0},
		{0.0008624332,0.0003114404,0},
		{0.0008007503,0.0002891656,0},
		{0.000743396,0.0002684539,0},
		{0.0006900786,0.0002492,0},
		{0.0006405156,0.0002313019,0},
		{0.0005945021,0.0002146856,0},
		{0.0005518646,0.0001992884,0},
		{0.000512429,0.0001850475,0},
		{0.0004760213,0.0001719,0},
		{0.0004424536,0.0001597781,0},
		{0.0004115117,0.0001486044,0},
		{0.0003829814,0.0001383016,0},
		{0.0003566491,0.0001287925,0},
		{0.0003323011,0.00012,0},
		{0.0003097586,0.0001118595,0},
		{0.0002888871,0.0001043224,0},
		{0.0002695394,0.0000973356,0},
		{0.0002515682,0.00009084587,0},
		{0.0002348261,0.0000848,0},
		{0.000219171,0.00007914667,0},
		{0.0002045258,0.000073858,0},
		{0.0001908405,0.000068916,0},
		{0.0001780654,0.00006430267,0},
		{0.0001661505,0.00006,0},
		{0.0001550236,0.00005598187,0},
		{0.0001446219,0.0000522256,0},
		{0.0001349098,0.0000487184,0},
		{0.000125852,0.00004544747,0},
		{0.000117413,0.0000424,0},
		{0.0001095515,0.00003956104,0},
		{0.0001022245,0.00003691512,0},
		{0.00009539445,0.00003444868,0},
		{0.0000890239,0.00003214816,0},
		{0.00008307527,0.00003,0},
		{0.00007751269,0.00002799125,0},
		{0.00007231304,0.00002611356,0},
		{0.00006745778,0.00002436024,0},
		{0.00006292844,0.00002272461,0},
		{0.00005870652,0.0000212,0},
		{0.00005477028,0.00001977855,0},
		{0.00005109918,0.00001845285,0},
		{0.00004767654,0.00001721687,0},
		{0.00004448567,0.00001606459,0},
		{0.00004150994,0.00001499,0},
		{0.00003873324,0.00001398728,0},
		{0.00003614203,0.00001305155,0},
		{0.00003372352,0.00001217818,0},
		{0.00003146487,0.00001136254,0},
		{0.00002935326,0.0000106,0},
		{0.00002737573,0.000009885877,0},
		{0.00002552433,0.000009217304,0},
		{0.00002379376,0.000008592362,0},
		{0.0000221787,0.000008009133,0},
		{0.00002067383,0.0000074657,0},
		{0.00001927226,0.000006959567,0},
		{0.0000179664,0.000006487995,0},
		{0.00001674991,0.000006048699,0},
		{0.00001561648,0.000005639396,0},
		{0.00001455977,0.0000052578,0},
		{0.00001357387,0.000004901771,0},
		{0.00001265436,0.00000456972,0},
		{0.00001179723,0.000004260194,0},
		{0.00001099844,0.000003971739,0},
		{0.00001025398,0.0000037029,0},
		{0.000009559646,0.000003452163,0},
		{0.000008912044,0.000003218302,0},
		{0.000008308358,0.0000030003,0},
		{0.000007745769,0.000002797139,0},
		{0.000007221456,0.0000026078,0},
		{0.000006732475,0.00000243122,0},
		{0.000006276423,0.000002266531,0},
		{0.000005851304,0.000002113013,0},
		{0.000005455118,0.000001969943,0},
		{0.000005085868,0.0000018366,0},
		{0.000004741466,0.00000171223,0},
		{0.000004420236,0.000001596228,0},
		{0.000004120783,0.00000148809,0},
		{0.000003841716,0.000001387314,0},
		{0.000003581652,0.0000012934,0},
		{0.000003339127,0.00000120582,0},
		{0.000003112949,0.000001124143,0},
		{0.000002902121,0.000001048009,0},
		{0.000002705645,0.000000977058,0},
		{0.000002522525,0.00000091093,0},
		{0.000002351726,0.000000849251,0},
		{0.000002192415,0.000000791721,0},
		{0.000002043902,0.00000073809,0},
		{0.000001905497,0.00000068811,0},
		{0.000001776509,0.00000064153,0},
		{0.000001656215,0.00000059809,0},
		{0.000001544022,0.000000557575,0},
		{0.00000143944,0.000000519808,0},
		{0.000001341977,0.000000484612,0},
		{0.000001251141,0.00000045181,0}
	};
	
	/** The direction vector connecting the white point to the
	 * monochromatic color of wavelength nm in xy space for each
	 * wavelength. 
	 */
	public static double[][] cmf_rgb_lin;
	
	/** The xy chromaticity values of the spectral colours. 
	 */
	public static double[][] cmf_xy;
	
	/** The normalized direction vector pointing from the
	 * white point (1/3,1/3) to the spectral chromaticity
	 * at that particular wavelength, located on the
	 * tongue-shaped spectral locus a.k.a. envelope. 
	 */
	public static double[][] cmf_whiteToLocus;
	
	static
	{
		cmf_whiteToLocus = new double[cmf_cie31.length][2];
		cmf_rgb_lin = new double[cmf_cie31.length][3];		
		cmf_xy = new double[cmf_cie31.length][2];
		
		for (int i = 0; i < cmf_cie31.length; i++)
		{
			double[] XYZ = lambdaToXYZ(indexToNM(i));
			double[] xyY = XYZtoxyY(XYZ);
			
			// Record xy coordinates.
			
			cmf_xy[i][0] = xyY[0];
			cmf_xy[i][1] = xyY[1];
			
			// Record linear truncated RGB values.
			
			double[] rgb = XYZtoRGB(XYZ);
			for (int j = 0; j < 3; j++)
			{
				if (rgb[j] < 0)
					rgb[j] = 0;
				else if (rgb[j] > 1)
					rgb[j] = 1;
			}
			cmf_rgb_lin[i] = sRGBreverseGamma(rgb);
			
			// Record white to locus direction vector
			
			cmf_whiteToLocus[i][0] = xyY[0] - 1/3.0;
			cmf_whiteToLocus[i][1] = xyY[1] - 1/3.0;			
			cmf_whiteToLocus[i] = unit(cmf_whiteToLocus[i]);
		}
	}
}
