import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

public class GUI extends JFrame 
{
	public static void main(String[] args) 
	{		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new GUI();
	}
	
	public static final int tableSize = 128;
	
	public static final int NONE = 0;
	public static final int ANGLE = 1;
	public static final int PRIMER = 2;
	public static final int RULER = 3;
	
	private File directory = new File ("."); // initialize dir to current dir

	public JPanel content;
	public JPanel controls;
	public JPanel stats;
	public JPanel data;
	public ImagePanel ip;
	
	public JButton[] measureButtons = new JButton[4];	
	public JButton moveButton;	
	public int mode = 0;
	
	public JLabel positionLabel;
	public JLabel ratioLabel;
	public JLabel statusBar;
	public JComboBox<String> zoomCtrl;
	public JTable table;
	
	public int tableIndex = 0;
		
	public boolean movingMode = false;	
	public double zoomStep = 0.1;	// The increase in zoom (1 = 100%) per scroll increment
	
	private KeyboardFocusManager manager;
	private MyDispatcher keyDispatcher;

	public GUI()
	{
		super("Photo Measurer");
		
		// Initialize panels

		content = new JPanel(new BorderLayout());
		controls = new JPanel(new BorderLayout());
		stats = new JPanel();
		data = new JPanel();
		ip = new ImagePanel(this);			
		
		// Add panels to content pane

		content.add(ip, BorderLayout.CENTER);		
		content.add(controls, BorderLayout.EAST);
		content.add(stats, BorderLayout.SOUTH);
		content.add(data, BorderLayout.WEST);
		
		// Add stuff to panels
		
		initControlPanel();
		initStatsPanel();
		initImagePanel();
		initDataPanel();

		setContentPane(content);
		revalidate();
		
		// Set up keyboard stuff
		
		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyDispatcher = new MyDispatcher();
		manager.addKeyEventDispatcher(keyDispatcher);
		
		// Open the window
		
		setSize(720, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

	/**
	 * Initializes the control panel, which contains the following buttons:
	 * <ul>
	 * <li>Load	- Opens JFileChooser to open image from file
	 * <li>Angle - Mark three dots to measure the angle between them
	 * <li>Primer - Set the pixel to distance ratio
	 * <li>Ruler - Measures distance
	 * <li>Move - Click and drag to navigate around image
	 * <li>Clear - Empties table
	 * </ul>
	 */
	private void initControlPanel()
	{	
		controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
		
		// Add buttons to control panel

		JButton button = new JButton("Load");
		button.addActionListener(new MyActionListener());
		controls.add(button);	
		
		controls.add(new JLabel("  "));

		measureButtons[ANGLE] = new JButton("Angle");
		measureButtons[ANGLE].addActionListener(new MyActionListener());
		controls.add(measureButtons[ANGLE]);

		measureButtons[PRIMER] = new JButton("Primer");
		measureButtons[PRIMER].addActionListener(new MyActionListener());
		controls.add(measureButtons[PRIMER]);

		measureButtons[RULER] = new JButton("Ruler");
		measureButtons[RULER].addActionListener(new MyActionListener());
		controls.add(measureButtons[RULER]);
		
		controls.add(new JLabel("  "));
		
		moveButton = new JButton("Move");
		moveButton.addActionListener(new MyActionListener());
		controls.add(moveButton);
		
		button = new JButton("Clear");
		button.addActionListener(new MyActionListener());
		controls.add(button);
	}

	/**
	 * Initializes the stats panel, which contains the following:
	 * <ul>
	 * <li>Status bar (for messages etc.)
	 * <li>Zoom JComboBox
	 * <li>Pixel coordinates of mouse 
	 * </ul>
	 */
	private void initStatsPanel()
	{
		JPanel stats1 = new JPanel();
		JPanel stats2 = new JPanel();
		JPanel stats3 = new JPanel();
		
		stats.setLayout(new BorderLayout());
		stats.add(stats1, BorderLayout.EAST);
		stats.add(stats2, BorderLayout.CENTER);
		stats.add(stats3, BorderLayout.WEST);
		
		// Add buttons etc.
		
		stats1.add(new JLabel("Zoom (%)"));
		
		String[] patternExamples = {"500", "400", "300", "200", "150", "100", "75", "50", "25", "10"};
		zoomCtrl = new JComboBox<String>(patternExamples);
		zoomCtrl.setPreferredSize(new Dimension (56, 20));
		zoomCtrl.setEditable(true);
		zoomCtrl.setSelectedIndex(5);
		zoomCtrl.addActionListener(new MyActionListener());
		stats1.add(zoomCtrl);
		
		positionLabel = new JLabel("0,0");
		stats1.add(positionLabel);			
		
		statusBar = new JLabel("Experimental.");
		stats2.add(statusBar);
	}

	/** Initializes the image panel, which holds and draws the image.
	 */
	private void initImagePanel()
	{
		ip.addMouseListener (new MyMouseListener());
		ip.addMouseMotionListener (new MyMouseListener());
		ip.addMouseWheelListener (new MyMouseListener());
	}
	
	/** Initializes the data panel, which contains a table that stores measurements. 
	 */
	private void initDataPanel()
	{	
		table = new JTable(tableSize, 1);	
		data.setLayout(new BorderLayout());		
		data.add(table.getTableHeader(), BorderLayout.PAGE_START);
		data.add(table, BorderLayout.CENTER);		
	}
	
	/** Tries to set the specified zoom. If invalid, sets the JComboBox
	 * to the current zoom.
	 * 
	 * @param value		desired zoom in percentage
	 */
	public void setZoom(String value)
	{
		try
		{
			setZoom(new Point(0, 0), Double.parseDouble(value) / 100);
		}
		catch (Exception e)
		{
			zoomCtrl.setSelectedItem("" + (int)(ip.getZoom() * 100 + 0.5));
		}
	}
	
	/** Sets the new zoom, keeping the specified point on the screen stationary.
	 * Ignores zooms <= 1%.
	 * 
	 * @param center	screen coordinates of the focus
	 * @param newZoom	desired new zoom (1 = 100%)
	 */
	public void setZoom(Point center, double newZoom)
	{					
		if (newZoom > 0.01)
		{
			// Set it so that the viewport remains centered after the zoom
									
			double changeRatio = newZoom / ip.getZoom();			
						
			Point pt = ip.getOffset();			
			int x = (int)((pt.x * changeRatio) - center.x * (changeRatio - 1));
			int y = (int)((pt.y * changeRatio) - center.y * (changeRatio - 1));
			ip.setOffset(new Point(x, y));
			
			ip.setZoom(newZoom);
			zoomCtrl.setSelectedItem("" + (int)(newZoom * 100));					
			
			repaint();
		}		
	}
	
	/** Reads image from file and loads it into ImagePanel. 
	 */
	public void loadFile()
	{	
		// Set up JFileChooser
		
		JFileChooser fc = new JFileChooser ();		
		fc.setFileFilter(new FileNameExtensionFilter("Images (GIF, PNG, JPEG, BMP)", "gif", "png", "jpeg", "jpg", "bmp"));		
		fc.setAcceptAllFileFilterUsed(false);		
		fc.setCurrentDirectory(directory);

		// Show JFileChooser dialog
		
		int result = fc.showOpenDialog(this);
		
		// Act upon JFileChooser result

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File load = fc.getSelectedFile();
			directory = load;
			if (load.canRead())			
				ip.loadImage(load);			
		}	
	}
	
	/** @param move true = click & drag to navigate; false = no navigation
	 */
	public void setMovingMode(boolean move)
	{
		if (move)
		{
			movingMode = true;
			moveButton.setText("Stop");			
		}
		else
		{
			movingMode = false;
			moveButton.setText("Move");
		}
	}
	
	/** NONE = 0;<br>
	 * ANGLE = 1;<br>
 	 * PRIMER = 2;<br>
	 * RULER = 3;<br>
	 */
	public void setMeasuringMode(int newMode)
	{
		if (mode == newMode || newMode == NONE)
		{
			writeStatus("Experimental.");
			setCursor(Cursor.getDefaultCursor());
			mode = NONE;
			ip.vertexIndex = 0;
			for (int i = 1; i <= 3; i++)
				measureButtons[i].setEnabled(true);
		}
		else
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			mode = newMode;
			for (int i = 1; i <= 3; i++)
				if (i != mode)
					measureButtons[i].setEnabled(false);
		}		
	}
	
	private void updateMove(Point now)
	{
		int dx = now.x - ip.click.x;
		int dy = now.y - ip.click.y;

		ip.click = now;
		ip.translateOffset(dx, dy);
	}
	
	/** @param text	the string to be displayed in the status bar
	 */
	public void writeStatus(String text)
	{
		statusBar.setText(text);
	}
	
	/** Adds the specified value to the next cell. Resets to top if
	 * tableIndex exceeds the length of the table.
	 */
	public void tableAppend(double value)
	{
		table.getModel().setValueAt(value + "", tableIndex, 0);
		tableIndex++;
		if (tableIndex >= tableSize)
			tableIndex = 0;	
	}
	
	/** Calls up the JOptionPane to input primer distance. 
	 */
	public void promptPrimerDistance()
	{
		double pixels = Calculator.findDistance(ip.vertices[0], ip.vertices[1]);
		
		String message = "Input the real length of segment: ";
		String title = "Length Measurement Scalar"; 
		String value = JOptionPane.showInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
		try
		{
			double mm = Double.parseDouble(value);
			ip.pixelsPerMM = pixels / mm;
			
		}
		catch (Exception e)
		{
			// Invalid input
		}
	}	

	/** Action Listener for the buttons.
	 */
	private class MyActionListener implements ActionListener
	{			
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Object parent = e.getSource ();

			if (parent instanceof JButton)
			{
				JButton button = (JButton)parent;
				if (button.getText().equals("Load"))
				{
					manager.removeKeyEventDispatcher(keyDispatcher);
					loadFile();
					manager.addKeyEventDispatcher(keyDispatcher);
				}
				else if (button.getText().equals("Clear"))
				{
					TableModel model = table.getModel();
					for (int i = 0; i < model.getRowCount(); i++)
						model.setValueAt("", i, 0);
					table.setEditingRow(0);
					tableIndex = 0;
				}
				else if (button.getText().equals("Angle"))
				{
					setMeasuringMode(ANGLE);
				}
				else if (button.getText().equals("Primer"))
				{
					setMeasuringMode(PRIMER);
					writeStatus("Select a known length (e.g. across a ruler).");
				}
				else if (button.getText().equals("Ruler"))
				{
					setMeasuringMode(RULER);
				}
				else if (button.getText().matches("Move|Stop"))
				{
					setMovingMode(!movingMode);
				}				
			}
			else if (parent instanceof JComboBox<?>)
			{
				JComboBox<?> cb = (JComboBox<?>)parent;
				
				if (cb.equals(zoomCtrl))
					setZoom((String)cb.getSelectedItem());			
			}
		}
	}
	
	private class MyMouseListener extends MouseAdapter
	{
		@Override
		public void mousePressed (MouseEvent e)	
		{
			ip.click = e.getPoint();
			
			if (!movingMode) // Do not do anything if in moving mode
			{
				if (mode != NONE) // If in a measuring mode
				{
					if (mode == ANGLE) // Reset angle mode after 3 clicks
					{
						if (ip.vertexIndex == 3)
							ip.vertexIndex = 0;
					}
					else if (ip.vertexIndex == 2) // Reset primer or ruler mode after 2 clicks
						ip.vertexIndex = 0;
					
					ip.vertices[ip.vertexIndex] = ip.getImageCoordinates(e.getPoint());
					ip.vertexIndex++;
					
					if (mode == ANGLE)
					{
						if (ip.vertexIndex == 3) // Determine angle once 3 points are had
						{
							tableAppend(Calculator.findAngle(ip.vertices[0], ip.vertices[1], ip.vertices[2]));
						}
					}
					else
					{
						if (ip.vertexIndex == 2) // Determine length once 2 points are had
						{
							if (mode == RULER)
							{
								tableAppend(Calculator.findDistance(ip.vertices[0], ip.vertices[1], ip.pixelsPerMM));
							}
							else
							{
								promptPrimerDistance();
								setMeasuringMode(NONE);
							}
						}
					}					
				}
			}			
					
		}

		@Override
		public void mouseDragged (MouseEvent e)
		{	
			Point now = e.getPoint();

			if (movingMode) // if in moving mode
				updateMove (now);
			repaint();			
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			ip.mouse = e.getPoint();
			ip.repaint();
			Point pt = ip.getImageCoordinates(e.getPoint());
			positionLabel.setText(pt.x + "," + pt.y);			
		}
		
		public void mouseWheelMoved (MouseWheelEvent e)
		{
			// Increase or decrease zoom value, as necessary
			
			double newZoom = (-e.getWheelRotation() * zoomStep) + ip.getZoom();
			setZoom(e.getPoint(), newZoom);
			repaint();
		}
	}
	
	/** Listens to and acts upon keyboard inputs. This KeyEventDispatcher
	 * is temporally disabled when JFileChooser is brought up, so that all
	 * keyboard inputs are isolated to the JFileChooser, and so that nothing
	 * happens while the user is using the JFileChooser.
	 */
	private class MyDispatcher implements KeyEventDispatcher 
	{
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) 
		{			
			boolean consumed = false;

			if (e.getID() == KeyEvent.KEY_PRESSED) 
			{
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)				
					setMovingMode(true);		
			} 
			else if (e.getID() == KeyEvent.KEY_RELEASED) 
			{
				if (e.getKeyCode() == KeyEvent.VK_SHIFT)
				{
					setMovingMode(false);
				}
			}  
			return consumed;
		}
	}
}
