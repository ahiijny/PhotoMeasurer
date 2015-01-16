

/** Static class that performs various matrix operations.
 * For succinctness, these methods do not verify inputs. If the programmer tries
 * to multiply two matrices together whose dimensions don't match up properly,
 * they might get a nice ArrayIndexOutOfBoundsException. 
 */
public class Matrix 
{	
	/** Converts the given vector (array of doubles) into
	 * the corresponding column matrix (array of array of doubles).
	 * 
	 * @param a		the vector
	 * @return the matrix of dimensions [R x 1] where R is the vector dimension
	 */
	public static double[][] getColumnMatrix(double[] a)
	{
		double[][] X = new double[a.length][1];
		
		for (int i = 0; i < a.length; i++)
				X[i][0] = a[i];
		
		return X;
	}
	
	/** Converts the given column matrix (array of array of doubles)
	 * into the corresponding vector (array of doubles).
	 * 
	 * @param X		the column matrix of dimensions [R x 1] where R is the vector dimension
	 * @return the vector of dimension R
	 */
	public static double[] getColumnVector(double[][] X)
	{
		double[] a = new double[X.length];
		
		for (int i = 0; i < X.length; i++)
			a[i] = X[i][0];
		
		return a;
	}
			
	/** Left multiplies the matrix A with B.
	 * 
	 * @param A		the left matrix
	 * @param B		the right matrix
	 * @return the product matrix AB
	 */
	public static double[][] multiply(double[][] A, double[][] B)
	{
		int rA = A.length;
		int rB = B.length;
		int cB = B[0].length;
		
		double[][] C = new double [rA][cB];
		
		for (int i = 0; i < rA; i++)
		{
			for (int j = 0; j < cB; j++)
			{
				C[i][j] = 0;
				
				for (int k = 0; k < rB; k++)
					C[i][j] += A[i][k] * B[k][j];
			}
		}
				
		return C;
	}
	
	/** Left multiplies the matrix R (usually a rotation matrix) with
	 * the given vector. This method automatically converts the
	 * vector into a column matrix to do the multiplication, and
	 * then converts the product back into a vector so that it can
	 * be returned to the caller of this method.
	 * 
	 * @param R		the matrix
	 * @param a		the vector
	 * @return	the vector corresponding with the product Ra 
	 */
	public static double[] multiply(double[][] R, double[] a)
	{
		double[][] X = getColumnMatrix(a);
		X = multiply(R, X);
		return getColumnVector(X);
	}
	
	public static double[][] add(double[][] A, double[][] B)
	{
		double[][] C = new double[A.length][A[0].length];
		
		for (int i = 0; i < C.length; i++)
			for (int j = 0; j < C[i].length; j++)
				C[i][j] = A[i][j] + B[i][j];
		
		return C;
	}
	
	public static double[][] scale(double[][] A, double scalar)
	{
		for (int i = 0; i < A.length; i++)
			for (int j = 0; j < A[i].length; j++)
				A[i][j] *= scalar;
		
		return A;
	}
	
	public static double[][] getIdentityMatrix(int dimension)
	{
		double[][] I = new double[dimension][dimension];
		for (int i = 0; i < I.length; i++)
			for (int j = 0; j < I[i].length; j++)
				I[i][j] = i == j ? 1 : 0;
		
		return I;
	}
}
