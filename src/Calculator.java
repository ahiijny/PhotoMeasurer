import java.awt.Point;


public class Calculator 
{
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
	 * @return the angle between the three points at vertex B
	 */
	public static double findAngle(Point A, Point B, Point C)
	{
		Point F = new Point();
		Point G = new Point();
		F.x = A.x - B.x;
		F.y = A.y - B.y;
		G.x = C.x - B.x;
		G.y = C.y - B.y;
		double radians = Math.acos(((F.x * G.x) + (F.y * G.y)) / (scalar(F) * scalar(G)));
		return Math.toDegrees(radians);
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
}
