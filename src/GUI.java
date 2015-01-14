import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
	public static final int COLOR = 4;
	
	public static final int MAKE = 0;
	public static final int MODEL = 1;
	public static final int DATE = 2;
	public static final int TIME = 3;
	public static final int SHUTTER = 4;
	public static final int FNUMBER = 5;
	public static final int FOCUS = 6;
	public static final int ISO = 7;
	public static final int BIAS = 8;
	public static final int ZOOM = 9;
	public static final int WB_RED = 10;
	public static final int WB_GREEN = 11;
	public static final int WB_BLUE = 12;
	
	public static final Font courier = new Font("Courier New", Font.PLAIN, 12);
	
	public static final int[] pointCount = {0, 3, 2, 2, 1};
	public static final String[] specLabels = {"Make", "Model", "Date", "Time", "Shutter(s)", "f/D", "Focus(mm)", "ISO", "Exp Bias(EV)", "Zoom", "WB red", "WB green", "WB blue"};
	
	public JTextField[] fieldImSpecs = new JTextField[specLabels.length];
	
	private File directory = new File(""); // initialize dir to current dir
	
	public String defaultStatus = "Become the literally I know lives.";

	public JPanel content;
	public ImagePanel ip;
	
	public JButton[] measureButtons = new JButton[5];	
	public JButton moveButton;	
	public int mode = 0;
	
	public JLabel positionLabel;
	public JLabel ratioLabel;
	public JLabel statusBar;
	public JComboBox<String> zoomCtrl;
	public JComboBox<String> renderCtrl;
	public JTable table;
	
	public JTextField fieldColor, fieldRGB;
	
	public int xorBack = Color.white.getRGB();
	public int xorFront = Color.black.getRGB();
	
	public int tableIndex = 0;
		
	public boolean movingMode = false;	
	public double zoomStep = 0.1;	// The increase in zoom (1 = 100%) per scroll increment
	
	private KeyboardFocusManager manager;
	private MyDispatcher keyDispatcher;

	public GUI()
	{
		super("Photo Measurer");
		
		setJMenuBar(createMenuBar());
		setContentPane(createContent());
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
	
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file, view, help;
		JMenuItem button;
		int menuKeyMask = InputEvent.CTRL_MASK;

		// Attempt to make MenuShortcutKeyMask valid for multiple platforms.
		try {
			menuKeyMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		} 
		catch (Exception e) 
		{			
		}

		// "File" Menu        
		file = new JMenu ("File");
		file.setMnemonic('f');
		
		button = new JMenuItem ("Load Image");
		button.setMnemonic('o');
		button.setAccelerator(KeyStroke.getKeyStroke (
				KeyEvent.VK_O, menuKeyMask));
		button.addActionListener (new MenuListener ());
		file.add(button);
		
		file.add(new JSeparator());

		button = new JMenuItem ("Exit"); // exit button
		button.setMnemonic('x');
		button.addActionListener (new MenuListener ());
		file.add(button);
		
		// "View" Menu
		
		view = new JMenu("View");
		view.setMnemonic('v');
		
		button = new JMenuItem ("Jump to Origin");
		button.setMnemonic('j');
		button.addActionListener(new MenuListener());
		view.add(button);

		// "Help" Menu
		help = new JMenu ("Help");
		help.setMnemonic('h');
		
		button = new JMenuItem ("Controls"); // controls button
		button.setMnemonic('c');
		button.addActionListener (new MenuListener());
		help.add(button);

		button = new JMenuItem ("About"); // about button
		button.setMnemonic('a');
		button.addActionListener (new MenuListener());
		help.add(button);


		// Add All Menus        
		menuBar.add (file);
		menuBar.add (view);
		menuBar.add (help);

		// Return        
		return menuBar;
	}
	
	private JPanel createContent()
	{
		// Add panels to content pane

		content = new JPanel(new BorderLayout());
		content.add(createMiddlePanel(),BorderLayout.CENTER);
		content.add(createLeftPanel(),BorderLayout.WEST);
		content.add(createRightPanel(),BorderLayout.EAST);
		//content.add(createTopPanel(),BorderLayout.NORTH);
		content.add(createBottomPanel(),BorderLayout.SOUTH);
						
		return content;
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
	private JPanel createRightPanel()
	{	
		// Control panel
		
		JPanel control = new JPanel();		
		control.setLayout(new BoxLayout(control, BoxLayout.PAGE_AXIS));
		
		// Add buttons to control panel

		JButton button = new JButton("Load");
		button.addActionListener(new MyActionListener());
		control.add(button);	
		
		control.add(new JLabel("  "));

		measureButtons[ANGLE] = new JButton("Angle");
		measureButtons[ANGLE].addActionListener(new MyActionListener());
		control.add(measureButtons[ANGLE]);

		measureButtons[PRIMER] = new JButton("Primer");
		measureButtons[PRIMER].addActionListener(new MyActionListener());
		control.add(measureButtons[PRIMER]);

		measureButtons[RULER] = new JButton("Ruler");
		measureButtons[RULER].addActionListener(new MyActionListener());
		control.add(measureButtons[RULER]);
					
		measureButtons[COLOR] = new JButton("Color");
		measureButtons[COLOR].addActionListener(new MyActionListener());
		control.add(measureButtons[COLOR]);
		
		control.add(new JLabel("  "));
		
		moveButton = new JButton("Move");
		moveButton.addActionListener(new MyActionListener());
		control.add(moveButton);
		
		button = new JButton("Clear");
		button.addActionListener(new MyActionListener());
		control.add(button);	
		
		// Image specs panel
				
		JPanel specs = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,4,2,4);
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// Image data
		
		for (int i = 0; i < specLabels.length; i++)
		{
			JLabel label = new JLabel(specLabels[i]);
			label.setFont(courier);
			
			fieldImSpecs[i] = new JTextField(10);
			fieldImSpecs[i].setEditable(false);
			fieldImSpecs[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(specs, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			
			c.weightx = 1;
			gridBagAdd(specs, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldImSpecs[i]);
			
		}
		
		gridBagSeparator(specs, c, 0, ++c.gridy, 2);
		
		// Color sensor
		
		JLabel label = new JLabel("Color");
		label.setFont(courier);		
		fieldColor = new JTextField(12);
		fieldColor.setFont(courier);
		fieldColor.setEditable(false);		
		gridBagAdd(specs, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(specs, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldColor);
		
		label = new JLabel("RGB");
		label.setFont(courier);		
		fieldRGB = new JTextField(12);
		fieldRGB.setFont(courier);
		fieldRGB.setEditable(false);		
		gridBagAdd(specs, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(specs, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldRGB);
		
		setColor(Color.white);
		fieldRGB.setBackground(Color.white);
		
		// Wrap specs
		
		JPanel specWrapper = new JPanel(new BorderLayout());
		specWrapper.add(specs, BorderLayout.NORTH);
		
		// Combine panels
		
		JPanel right = new JPanel(new BorderLayout());
		right.add(control, BorderLayout.EAST);
		right.add(specWrapper, BorderLayout.WEST);
		
		return right;
	}

	/**
	 * Initializes the stats panel, which contains the following:
	 * <ul>
	 * <li>Status bar (for messages etc.)
	 * <li>Zoom JComboBox
	 * <li>Pixel coordinates of mouse 
	 * </ul>
	 */
	private JPanel createBottomPanel()
	{
		JPanel bottom = new JPanel(new BorderLayout());
		
		JPanel stats1 = new JPanel();
		JPanel stats2 = new JPanel();
		JPanel stats3 = new JPanel();
		
		bottom.setLayout(new BorderLayout());
		bottom.add(stats1, BorderLayout.EAST);
		bottom.add(stats2, BorderLayout.CENTER);
		bottom.add(stats3, BorderLayout.WEST);
		
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
		
		statusBar = new JLabel(defaultStatus);
		stats2.add(statusBar);
		
		// Add interpolation control
		
		stats3.add(new JLabel("  "));
		
		stats3.add(new JLabel("Interpolation"));		
		String[] patternExamples2 = {"Nearest", "Linear"};
		renderCtrl = new JComboBox<String>(patternExamples2);
		renderCtrl.setPreferredSize(new Dimension (64, 20));
		renderCtrl.setEditable(false);
		renderCtrl.setSelectedIndex(0);
		renderCtrl.addActionListener(new MyActionListener());
		stats3.add(renderCtrl);
		
		return bottom;
	}

	/** Initializes the image panel, which holds and draws the image.
	 */
	private JPanel createMiddlePanel()
	{
		ip = new ImagePanel(this);
		ip.addMouseListener (new MyMouseListener());
		ip.addMouseMotionListener (new MyMouseListener());
		ip.addMouseWheelListener (new MyMouseListener());
		
		return ip;
	}
	
	/** Initializes the data panel, which contains a table that stores measurements. 
	 */
	private JPanel createLeftPanel()
	{				
		// Table for numerical data
		
		JPanel data = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(10,10,10,10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		
		JPanel tableWrapper = new JPanel(new BorderLayout());
		table = new JTable(tableSize, 2);
		tableWrapper.add(table, BorderLayout.CENTER);
		tableWrapper.add(table.getTableHeader(), BorderLayout.NORTH);
		table.getTableHeader().setReorderingAllowed(false);
				  
		gridBagAdd(data, c, 0, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, tableWrapper);
		
		return data;
	}
	
	private void gridBagAdd(JPanel panel, GridBagConstraints c, int x, int y, JComponent comp)
	{
		gridBagAdd(panel, c, x, y, 1, GridBagConstraints.FIRST_LINE_START, comp);		
	}
	
	private void gridBagAdd(JPanel panel, GridBagConstraints c, int x, int y, int width, int align, JComponent comp)
	{
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.anchor = align;		
		panel.add(comp, c);
	}	
	
	private void gridBagSeparator(JPanel panel, GridBagConstraints c, int x, int y, int width)
	{
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		JSeparator sep = new JSeparator();
		//sep.setPreferredSize(new Dimension(100, 20));
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(sep, c);
		c.fill = GridBagConstraints.NONE;
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
			zoomCtrl.setSelectedItem("" + (int)(newZoom * 100 + 0.5));					
			
			repaint();
		}		
	}
	
	/** Reads image from file and loads it into ImagePanel. 
	 */
	public void loadFile()
	{	
		manager.removeKeyEventDispatcher(keyDispatcher);
		FileDialog fd = new FileDialog(this, "Open");
		fd.setFile("*.BMP;*.GIF;*.PNG;*.JPG;*.JPEG");
		fd.setVisible(true);		
		String path = fd.getFile();	
		if (path != null)
		{
			directory = new File(fd.getDirectory());
			File load = new File(directory.getAbsoluteFile() + File.separator + path);
			directory = load;
			if (load.canRead())
			{
				ip.loadImage(load);
				refresh();
			}
		}		
		manager.addKeyEventDispatcher(keyDispatcher);
				
		/*
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
		}	*/	
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
			writeStatus(defaultStatus);
			setCursor(Cursor.getDefaultCursor());
			mode = NONE;
			ip.vertexIndex = 0;
			for (int i = 1; i < measureButtons.length; i++)
				measureButtons[i].setEnabled(true);
		}
		else
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			mode = newMode;
			for (int i = 1; i < measureButtons.length; i++)
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
		double pixels = Calc.findDistance(ip.vertices[0], ip.vertices[1]);
		
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
	
	public void setColor(Color color)
	{
    	fieldColor.setBackground(color);
    	fieldColor.setForeground(new Color(xorBack^xorFront^color.getRGB()));
    	fieldRGB.setText(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
	}
	
	public void refresh()
	{
		if (ip.mm != null)
		{
			fieldImSpecs[MODEL].setText(ip.mm.model);
			fieldImSpecs[MAKE].setText(ip.mm.make);
			fieldImSpecs[DATE].setText(Calc.date.format(ip.mm.date));
			fieldImSpecs[TIME].setText(Calc.time.format(ip.mm.date));
			fieldImSpecs[SHUTTER].setText(Calc.precise8.format(ip.mm.exposure));
			fieldImSpecs[FNUMBER].setText(Calc.precise8.format(ip.mm.fnumber));
			fieldImSpecs[FOCUS].setText(Calc.precise8.format(ip.mm.focus));
			fieldImSpecs[ISO].setText(Calc.whole.format(ip.mm.iso));
			fieldImSpecs[BIAS].setText(Calc.precise8.format(ip.mm.exposureBias));
			fieldImSpecs[ZOOM].setText(Calc.precise8.format(ip.mm.zoom));
			fieldImSpecs[WB_RED].setText(Calc.whole.format(ip.mm.wb_red));
			fieldImSpecs[WB_GREEN].setText(Calc.whole.format(ip.mm.wb_green));
			fieldImSpecs[WB_BLUE].setText(Calc.whole.format(ip.mm.wb_blue));
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
					loadFile();					
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
				else if (button.getText().equals("Color"))
				{
					setMeasuringMode(COLOR);
				}
			}
			else if (parent instanceof JComboBox<?>)
			{
				JComboBox<?> cb = (JComboBox<?>)parent;
				
				if (cb.equals(zoomCtrl))
					setZoom((String)cb.getSelectedItem());	
				else if (cb.equals(renderCtrl))
					ip.setInterpolationMethod(cb.getSelectedIndex());					
			}
		}
	}
	
	public void close ()
	{
		dispose();	
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
					// Reset the measurement mode if necessary
					
					if (ip.vertexIndex >= pointCount[mode])
						ip.vertexIndex = 0; 
					
					// Extract the coordinates of the mouse click
										
					ip.vertices[ip.vertexIndex] = ip.getImageCoordinates(e.getPoint());
					ip.vertexIndex++;
					
					if (mode == ANGLE)
					{
						if (ip.vertexIndex == 3) // Determine angle once 3 points are had
						{
							tableAppend(Calc.findAngle(ip.vertices[0], ip.vertices[1], ip.vertices[2]));
						}
					}
					else if (mode == RULER || mode == PRIMER)
					{
						if (ip.vertexIndex == 2) // Determine length once 2 points are had
						{
							if (mode == RULER)
							{
								tableAppend(Calc.findDistance(ip.vertices[0], ip.vertices[1], ip.pixelsPerMM));
							}
							else
							{
								promptPrimerDistance();
								setMeasuringMode(NONE);
							}
						}
					}
					else if (mode == COLOR)
					{
						if (ip.vertexIndex == 1) // Determine color once 1 point is had
						{
							setColor(ip.getPixel(ip.vertices[0]));
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
	
	private class MenuListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			Object parent = e.getSource();
			if (parent instanceof JMenuItem)
			{
				JMenuItem button = (JMenuItem) parent;
				String name = button.getText();
				if (name.equals("Exit"))
					close();
				else if (name.equals("Load Image"))
				{
					loadFile();
				}
				else if (name.equals("Jump to Origin"))
				{
					ip.setOffset(new Point(0, 0));
					repaint();
				}
				else if (name.equals("Controls"))
				{
					String str = "Shortcut keys:\n";
					str += "Shift + drag: move image\n";
					str += "Scroll: zoom\n";
							
					JOptionPane.showMessageDialog(GUI.this, str, "About", JOptionPane.PLAIN_MESSAGE);			
				}
				else if (name.equals("About"))
				{
					String message = "Version: 2015.01.14\n";	
					JOptionPane.showMessageDialog(GUI.this, message, "About", JOptionPane.PLAIN_MESSAGE);			
				}
			}
		}
	}
}
