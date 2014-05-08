import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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
	
	private File path;
	private BufferedImage img = null;
	
	private Point offset = new Point();
	private double zoom = 1;
	
	public Point[] vertices = new Point[3];
	public int vertexIndex = 0;
	
	public double pixelsPerMM = 1;
	
	public Point click = new Point();
	public Point mouse = new Point();
	
	public Color lineColor = new Color(203, 203, 203);
	
	public ImagePanel(GUI gui)
	{
		parent = gui;
	}

	public void drawLines(Graphics2D g2)
	{		
		boolean doneMeasuring = false;
		if (parent.mode == GUI.ANGLE)
		{
			if (vertexIndex == 3)
				doneMeasuring = true;
		}
		else if (vertexIndex == 2)
		{
			doneMeasuring = true;
		}
		
		g2.setColor(lineColor);
		
		for (int i = 0; i < vertexIndex - 1; i++)
			g2.drawLine(vertices[i].x, vertices[i].y, vertices[i+1].x, vertices[i+1].y);
		
		int index = vertexIndex - 1;
		Point pt = getImageCoordinates(mouse);
		if (index >= 0 && !doneMeasuring)
			g2.drawLine(vertices[index].x, vertices[index].y, pt.x, pt.y);			
	}
	
	public Point getImageCoordinates(Point screenCoordinates)
	{
		Point pt = new Point(screenCoordinates);		
		pt.x = (int)((pt.x - offset.x) / zoom);
		pt.y = (int)((pt.y - offset.y) / zoom);
		return pt;
	}
	
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
	
	public void setOffset(Point newOffset)
	{
		offset = new Point(newOffset);
	}
	
	public void translateOffset(int dx, int dy)
	{
		offset.translate(dx, dy);
	}
	
	public void setZoom(double newZoom)
	{
		if (newZoom > 0)
			zoom = newZoom;
	}
	
	public void loadImage(File imagePath)
	{
		path = imagePath;
		try 
		{
		    img = ImageIO.read(imagePath);
		    repaint();
		} 
		catch (IOException e) 
		{
			String message = "Error: could not read " + imagePath + ".";
			JOptionPane.showMessageDialog(this, message, "Read Error", JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;		
				
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
