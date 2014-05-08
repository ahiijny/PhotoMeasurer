import java.awt.Point;


public class Calculator 
{
	/** http://chemistry.about.com/od/workedchemistryproblems/a/scalar-product-vectors-problem.htm
	 * 
	 * A dot B = |A||B|cos theta
	 * A dot B = (A.x)(B.x) + (A.y)(B.y)
	 * 
	 * Therefore theta = acos(((A.x)(B.x) + (A.y)(B.y)) / |A| |B|)
	 *  
	 * @param A			one arm
	 * @param B			the center point
	 * @param C			another arm
	 * @return the angle between the three points at vertex B
	 */
	public static double findAngle (Point A, Point B, Point C)
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
	
	public static double scalar(Point vector)
	{
		return Math.sqrt((vector.x * vector.x) + (vector.y * vector.y));
	}
	
	public static double findDistance(Point A, Point B)
	{
		return findDistance(A, B, 1);
	}
	
	public static double findDistance(Point A, Point B, double pixelsPerMM)
	{
		double distance = Math.sqrt(Math.pow((A.x - B.x), 2) + Math.pow((A.y - B.y), 2));
		distance /= pixelsPerMM;
		return distance;
	}

}
