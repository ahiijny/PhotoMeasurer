
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Logger 
{
	public File path = new File("data.csv");
	public StringBuffer csv;
	public BufferedWriter out;
	public JTable table;
	
	public Logger(JTable table) 
	{
		this.table = table;		
	}
	
	public void log()
	{
		csv = new StringBuffer(0);
		
		// Retrieve data from table
		
		DefaultTableModel dtm = (DefaultTableModel)table.getModel();
		int rows = dtm.getRowCount(); 
		int cols = dtm.getColumnCount();

		// Iterate through rows and columns, writing data to StringBuffer
		
		for (int i = 0 ; i < rows ; i++) // For each row
		{
	        for (int j = 0 ; j < cols ; j++) // For each column
	        {
	        	// Write cell entry to StringBuffer
	        	
	        	String entry = (String)dtm.getValueAt(i,j);
	        	if (entry == null)
	        		entry = "";
	            csv.append(entry);
	            
	            // Column delimiter	            
	            if (j < cols - 1)
	            	csv.append(","); 
	        }
	        
	        // Row delimiter	        
	        if (i < rows - 1)
	        	csv.append("\r\n");
		}
	}
	
	public void setPath(File path)
	{
		this.path = path;
	}
		
	public void write() throws Exception
	{	
		try 
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			out.write(csv.toString());
			out.flush();
			out.close();
		} 
		catch (Exception e) 
		{
			throw e;
		}
	}
}
