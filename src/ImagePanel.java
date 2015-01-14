import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ImagePanel extends JPanel 
{
	private GUI parent;
	private Object interpolationMethod = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
	
	private BufferedImage img = null;
	public MetadataManager mm;
	
	private Point offset = new Point();
	private double zoom = 1;
	
	public Point[] vertices = new Point[3];
	public int vertexIndex = 0;
	
	public double pixelsPerMM = 1;
	
	public Point click = new Point();
	public Point mouse = new Point();
	
	public Color lineColor = Color.black;
	public Color xorBack = Color.white;
	
	/** Creates a new ImagePanel.
	 * 
	 * @param gui	the parent GUI
	 */
	public ImagePanel(GUI gui)
	{
		parent = gui;
	}

	/** Draws the lines of a selection (e.g. angle, ruler)
	 * if necessary.
	 * 
	 * @param g2	the Graphics context in which to paint
	 */
	private void drawLines(Graphics2D g2)
	{		
		boolean doneMeasuring = vertexIndex >= GUI.pointCount[parent.mode];
		
		g2.setColor(xorBack);
		g2.setXORMode(lineColor);
		
		for (int i = 0; i < vertexIndex - 1; i++)
			g2.drawLine(vertices[i].x, vertices[i].y, vertices[i+1].x, vertices[i+1].y);
		
		int index = vertexIndex - 1;
		Point pt = getImageCoordinates(mouse);
		if (index >= 0 && !doneMeasuring)
			g2.drawLine(vertices[index].x, vertices[index].y, pt.x, pt.y);
		
		g2.setPaintMode();
	}
	
	/** Converts screen coordinates to pixel coordinates on the image,
	 * based on current zoom and offset.
	 * 
	 * <p><ul>
	 * <li><code>Zoom</code> specifies a multiplier for pixel size.
	 * <li><code>Offset</code> specifies the number of <i>screen</i> pixels to add.
	 * So, if you translate the image leftwards and upwards, offset decreases.
	 * </ul><p>
	 * 
	 * To convert from screen coordinates to image coordinates:
	 * <ul>
	 * <li>Take location of mouse (screen coordinates).
	 * <li>Revert (i.e. subtract) offset. You now have number of screen
	 * pixels from the image's (0,0) pixel.
	 * <li>Revert (i.e. divide) zoom. You now have number of image pixels
	 * from the image's (0,0) pixels; i.e. image coordinates.
	 * 
	 * @param screenCoordinates		screen pixels relative to top left of component
	 * @return image pixel coordinates
	 */
	public Point getImageCoordinates(Point screenCoordinates)
	{
		Point pt = new Point(screenCoordinates);		
		pt.x = (int)((pt.x - offset.x) / zoom);
		pt.y = (int)((pt.y - offset.y) / zoom);
		return pt;
	}
	
	/** Converts pixel coordinates on the image to screen coordinates,
	 * based on current zoom and offset.
	 * 
	 * <p><ul>
	 * <li><code>Zoom</code> specifies a multiplier for pixel size.
	 * <li><code>Offset</code> specifies the number of <i>screen</i> pixels to add.
	 * So, if you translate the image rightwards and downwards, offset increases.
	 * </ul><p>
	 * 
	 * To convert from image coordinates to screen coordinates:
	 * <ul>
	 * <li>Take image coordinates.
	 * <li>Apply (i.e. multiply) zoom. You now have number of screen
	 * pixels from the image's (0,0) pixel.
	 * <li>Apply (i.e. add) offset. You now have the number of screen pixels
	 * from the component's (0,0) pixel; i.e. screen coordinates.
	 * 
	 * @param imageCoordinates		image pixel coordinates
	 * @return screen pixels relative to top left of component
	 */
	public Point getScreenCoordinates(Point imageCoordinates)
	{
		Point pt = new Point(imageCoordinates);		
		pt.x = (int)(pt.x * zoom + offset.x);
		pt.y = (int)(pt.y * zoom + offset.y);
		return pt;
	}
	
	public Point getOffset()
	{
		return offset;
	}
	
	public double getZoom()
	{
		return zoom;
	}		
	
	public Color getPixel(Point point)
	{
		int rgb;
		
		try
		{
			rgb = img.getRGB(point.x, point.y);
		}
		catch (Exception e)
		{		
			rgb = Color.gray.getRGB();
		}
		
		return new Color(rgb);
	}
	
	/** Sets the offset in terms of screen pixels.
	 * 
	 * @param newOffset
	 */
	public void setOffset(Point newOffset)
	{
		offset = new Point(newOffset);
	}
	
	public void translateOffset(int dx, int dy)
	{
		offset.translate(dx, dy);
	}
	
	/** Sets the interpolation method for the rendering. Note: Bicubic is
	 * much slower than the other two, and doesn't look very different 
	 * than bilinear.
	 * <p>
	 * <ul>
	 * <li> 0 = Nearest Neighbor
	 * <li> 1 = Bilinear
	 * <li> 2 = Bicubic
	 * </ul>
	 * 
	 * @param method	the interpolation method
	 */
	public void setInterpolationMethod(int method)
	{
		if (method == 1)
			interpolationMethod = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		else if (method == 2)
			interpolationMethod = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		else
			interpolationMethod = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;	
	}
	
	public void setZoom(double newZoom)
	{
		if (newZoom > 0)
			zoom = newZoom;
	}
	
	public void loadImage(File imagePath)
	{
		try 
		{
		    img = ImageIO.read(imagePath);
		    mm = new MetadataManager(imagePath);
		    mm.printAllTags();
		    repaint();
		} 
		catch (IOException e) 
		{
			mm = null;
			String message = "Error: could not read " + imagePath + ".";
			JOptionPane.showMessageDialog(this, message, "Read Error", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// Set Rendering Hints
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationMethod);
		
		// Set Graphics AffineTransform for current zoom and offset
		
		AffineTransform at = AffineTransform.getScaleInstance(zoom, zoom);		
		AffineTransform at2 = AffineTransform.getTranslateInstance(offset.x, offset.y);
		at.preConcatenate(at2);
		g2.transform(at);
		if (img != null)
		{
			g2.drawImage(img, 0, 0, null);			
		}	
		drawLines(g2);				
	}
}
