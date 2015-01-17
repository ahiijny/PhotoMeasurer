import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDescriptor;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDirectory;
import com.drew.metadata.jpeg.JpegDirectory;


public class MetadataManager 
{
	public File file;
	public Metadata metadata;
	public Dimension size;
	
	public Date date;
	
	/** Exposure time in seconds. 
	 */
	public double exposure; 
	
	/** The actual F-number(F-stop) of lens when the image was taken. 
	 * The f-number N is given by N = f/D where f is the focal length
	 * and D is the diameter of the entrance pupil. 
	 */
	public double fnumber;
	public double iso;
	public double exposureBias;
	public double focus;
	
	/** Focal lengths of digital cameras with a sensor smaller than the 
	 * surface of a 35mm film can be converted to their 35mm equivalent 
	 * using the focal length multiplier. (www.dpreview.com/glossary/optical/focal-length)  
	 */
	public double focus35;
	public double digital_zoom;
	public double distance;
	public int wb_red;
	public int wb_green;
	public int wb_blue;
	
	public String make;
	public String model;
	
	public MetadataManager(File file) 
	{
		this.file = file;
		try 
		{
			metadata = ImageMetadataReader.readMetadata(file);			
			extractData();		
		} 
		catch (ImageProcessingException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (MetadataException e) 
		{
			e.printStackTrace();
		}

	}
	
	private void extractData() throws MetadataException
	{
		// Extract JPEG data
		
		JpegDirectory jpeg = metadata.getDirectory(JpegDirectory.class);
		if (jpeg != null)
		{
			int width = jpeg.getImageWidth();			
			int height = jpeg.getImageHeight();
			size = new Dimension(width, height);				
		}
		
		// Extract Exif IFD0 data
						
		ExifIFD0Directory ifd = metadata.getDirectory(ExifIFD0Directory.class);
		if (ifd != null)
		{			
			make = ifd.getString(ExifIFD0Directory.TAG_MAKE);
			model = ifd.getString(ExifIFD0Directory.TAG_MODEL);
		}
		
		// Extract Exif SubIFD data
		
		ExifSubIFDDirectory subifd = metadata.getDirectory(ExifSubIFDDirectory.class);
		if (subifd != null)
		{
			ExifSubIFDDescriptor labels = new ExifSubIFDDescriptor(subifd);
			exposure = subifd.getDouble(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
			exposureBias = subifd.getDouble(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS);
			fnumber = subifd.getDouble(ExifSubIFDDirectory.TAG_FNUMBER);
			iso = subifd.getDouble(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
			focus = subifd.getDouble(ExifSubIFDDirectory.TAG_FOCAL_LENGTH);
			focus35 = subifd.getDouble(ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH);
			digital_zoom = subifd.getDouble(ExifSubIFDDirectory.TAG_DIGITAL_ZOOM_RATIO);
			date = subifd.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		}
		
		// Extract Panasonic Makenote data
		
		PanasonicMakernoteDirectory pana = metadata.getDirectory(PanasonicMakernoteDirectory.class);
		if (pana != null)
		{
			PanasonicMakernoteDescriptor labels = new PanasonicMakernoteDescriptor(pana); 
			wb_red = pana.getInt(PanasonicMakernoteDirectory.TAG_WB_RED_LEVEL);
			wb_green = pana.getInt(PanasonicMakernoteDirectory.TAG_WB_GREEN_LEVEL);
			wb_blue = pana.getInt(PanasonicMakernoteDirectory.TAG_WB_BLUE_LEVEL);			
			iso = pana.getDouble(PanasonicMakernoteDirectory.TAG_PROGRAM_ISO);
		}
	}
	
	public void printAllTags()
	{
		for (Directory directory : metadata.getDirectories())
		    for (Tag tag : directory.getTags()) 
		        System.out.println(tag);
	}
	
	public void printData()
	{
		String str = "";
		str += "Focal length (35 mm equivalent) = " + focus35 + "\n";
		str += "Focal length = " + focus + "\n";		
		str += "F-number = " + fnumber + "\n";
		str += "Iso speed setting = " + iso + "\n";
		str += "Exposure = " + exposure + "\n";
		str += "Zoom = " + digital_zoom + "\n";
		str += "Exposure bias = " + exposureBias + "\n";
		str += "White balance(red) = " + wb_red + "\n";
		str += "White balance(green) = " + wb_green + "\n";
		str += "White balance(blue) = " + wb_blue + "\n";
		
		System.out.println(str);
	}

}