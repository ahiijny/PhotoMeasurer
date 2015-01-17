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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
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
	
	public static final int NONE = 0;
	public static final int ANGLE = 1;
	public static final int PRIMER = 2;
	public static final int RULER = 3;
	public static final int COLOR = 4;
	public static final int WAVELENGTH = 5;
	
	public static final int MAKE = 0;
	public static final int MODEL = 1;
	public static final int WIDTH = 2;
	public static final int HEIGHT = 3;
	public static final int DATE = 4;
	public static final int TIME = 5;
	public static final int SHUTTER = 6;
	public static final int FNUMBER = 7;
	public static final int FOCUS = 8;
	public static final int ISO = 9;
	public static final int BIAS = 10;
	public static final int WB_RED = 11;
	public static final int WB_GREEN = 12;
	public static final int WB_BLUE = 13;
		
	public static final Font fontCourier = new Font("Courier New", Font.PLAIN, 12);
	public static final Font fontHeader = new Font("Verdana", Font.PLAIN, 12);
	
	public static final int[] pointCount = {0, 3, 2, 2, 1};
	public static final String[] specLabels = {"Make", "Model", "Width", "Height", "Date", "Time", "Shutter(s)", "f/D", "Focus(mm)", "ISO", "Exp Bias(EV)", "WB red", "WB green", "WB blue"};
	
	public MyActionListener myActionListener = new MyActionListener();	
	public MenuListener menuListener = new MenuListener();
	
	public JTextField[] fieldImSpecs = new JTextField[specLabels.length];	
	private File directory = new File(""); // initialize dir to current dir	
	public String defaultStatus = "Become the literally I know lives.";

	public JPanel content;
	public JPanel paneCenter, paneLeft, paneRight, paneBottom, paneTop, paneSpecWrapper;
	public JPanel[] panesMeasurement = new JPanel[6];
	public ImagePanel ip;
	
	public JButton[] buttonsMeasure = new JButton[5];	
	public JButton buttonMove;	
	public int mode = 0;
	
	public JLabel positionLabel;
	public JLabel ratioLabel;
	public JLabel statusBar;
	public JComboBox<String> zoomCtrl;
	public JComboBox<String> renderCtrl;
	public JTable table;
	
	public JTextField[] fieldXYZ, fieldLambdaCurve, fieldLambdaExtrap, fieldLambdaTrunc; 
	public JTextField fieldColor, fieldRGB, fieldTableIndex, fieldTableSize;
	
	public JButton buttonTableIndex, buttonTableSize;
	
	public JToggleButton buttonLog;
	
	public int xorBack = Color.white.getRGB();
	public int xorFront = Color.black.getRGB();
	
	public int tableIndex = 0;
	public int tableSize = 128;
	public int tableCols = 6;
		
	public boolean movingMode = false;
	public boolean isLogging = true;
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
		button.addActionListener (menuListener);
		file.add(button);
		
		file.add(new JSeparator());

		button = new JMenuItem ("Exit"); // exit button
		button.setMnemonic('x');
		button.addActionListener (menuListener);
		file.add(button);
		
		// "View" Menu
		
		view = new JMenu("View");
		view.setMnemonic('v');
		
		button = new JMenuItem ("Jump to Origin");
		button.setMnemonic('j');
		button.addActionListener(menuListener);
		view.add(button);

		// "Help" Menu
		help = new JMenu ("Help");
		help.setMnemonic('h');
		
		button = new JMenuItem ("Controls"); // controls button
		button.setMnemonic('c');
		button.addActionListener (menuListener);
		help.add(button);
		
		button = new JMenuItem ("Formatting"); // controls button
		button.setMnemonic('f');
		button.addActionListener (menuListener);
		help.add(button);

		button = new JMenuItem ("About"); // about button
		button.setMnemonic('a');
		button.addActionListener (menuListener);
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
		// Init Measurement Panels
		
		for (int i = 0; i < 6; i++)
			panesMeasurement[i] = getMeasurementPanel(i);
		
		// Create content panes
		
		paneCenter = createCenterPanel();
		paneLeft = createLeftPanel();
		paneRight = createRightPanel();
		paneBottom = createBottomPanel();
		
		// Add panels to content pane
		
		content = new JPanel(new BorderLayout());
		content.add(paneCenter, BorderLayout.CENTER);
		content.add(paneLeft, BorderLayout.WEST);
		content.add(paneRight, BorderLayout.EAST);
		content.add(paneBottom, BorderLayout.SOUTH);
						
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
		JPanel control = getControlPanel();	
		JPanel specs = getImageSpecsPanel();		
		JPanel measurementPanel = panesMeasurement[mode];
		
		// Wrap specs
						
		paneSpecWrapper = new JPanel(new BorderLayout());
		paneSpecWrapper.add(specs, BorderLayout.NORTH);
		paneSpecWrapper.add(measurementPanel, BorderLayout.CENTER);
		
		JPanel wrapperWrapper = new JPanel(new BorderLayout());
		wrapperWrapper.add(paneSpecWrapper, BorderLayout.NORTH);
		
		// Combine panels
		
		JPanel right = new JPanel(new BorderLayout());
		right.add(control, BorderLayout.EAST);
		right.add(wrapperWrapper, BorderLayout.WEST);
		
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
		zoomCtrl.addActionListener(myActionListener);
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
		renderCtrl.addActionListener(myActionListener);
		stats3.add(renderCtrl);
		
		return bottom;
	}

	/** Initializes the image panel, which holds and draws the image.
	 */
	private JPanel createCenterPanel()
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
		c.insets = new Insets(2,2,2,2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		
		// Index set
		
		JLabel label = new JLabel("Input index:");
		fieldTableIndex = new JTextField(5);
		buttonTableIndex = new JButton("Set");
		buttonTableIndex.addActionListener(myActionListener);
		
		gridBagAdd(data, c, 0, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(data, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldTableIndex);
		gridBagAdd(data, c, 2, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonTableIndex);
		
		// Size set
		
		label = new JLabel("Table size:");
		fieldTableSize = new JTextField(5);
		buttonTableSize = new JButton("Set");
		buttonTableSize.addActionListener(myActionListener);
		
		gridBagAdd(data, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(data, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldTableSize);
		gridBagAdd(data, c, 2, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonTableSize);
		
		// Table
			
		table = new JTable(tableSize, tableCols);		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		JScrollPane tableWrapper = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableWrapper.setPreferredSize(new Dimension(170, 1));
		System.out.println(tableWrapper.getPreferredSize());
		
		table.getTableHeader().setReorderingAllowed(false);				
				  
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = c.weightx = 1;
		gridBagAdd(data, c, 0, ++c.gridy, 3, GridBagConstraints.FIRST_LINE_START, tableWrapper);
				
		refreshLeftPanel();
		
		return data;
	}
	
	private JPanel getControlPanel()
	{
		// Control panel
		
		JPanel control = new JPanel();		
		control.setLayout(new BoxLayout(control, BoxLayout.PAGE_AXIS));
		
		// Add buttons to control panel

		JButton button = new JButton("Load");
		button.addActionListener(myActionListener);
		control.add(button);	
		
		control.add(new JLabel("  "));
		
		buttonLog = new JToggleButton("Log");
		buttonLog.setSelected(true);
		buttonLog.addActionListener(myActionListener);
		control.add(buttonLog);
		
		control.add(new JLabel("  "));

		buttonsMeasure[ANGLE] = new JButton("Angle");
		buttonsMeasure[ANGLE].addActionListener(myActionListener);
		control.add(buttonsMeasure[ANGLE]);

		buttonsMeasure[PRIMER] = new JButton("Primer");
		buttonsMeasure[PRIMER].addActionListener(myActionListener);
		control.add(buttonsMeasure[PRIMER]);

		buttonsMeasure[RULER] = new JButton("Ruler");
		buttonsMeasure[RULER].addActionListener(myActionListener);
		control.add(buttonsMeasure[RULER]);
					
		buttonsMeasure[COLOR] = new JButton("Color");
		buttonsMeasure[COLOR].addActionListener(myActionListener);
		control.add(buttonsMeasure[COLOR]);
		
		control.add(new JLabel("  "));
		
		buttonMove = new JButton("Move");
		buttonMove.addActionListener(myActionListener);
		control.add(buttonMove);
		
		button = new JButton("Clear");
		button.addActionListener(myActionListener);
		control.add(button);
		
		return control;
	}
	
	private JPanel getImageSpecsPanel()
	{
		// Image specs panel
		
		JPanel specs = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,4,2,4);
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// Header
		
		JLabel label = new JLabel ("Image metadata");
		label.setFont(fontHeader);
		gridBagAdd(specs, c, 0, c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		// Image data
		
		for (int i = 0; i < specLabels.length; i++)
		{
			label = new JLabel(specLabels[i]);
			label.setFont(fontCourier);
			
			fieldImSpecs[i] = new JTextField(14);
			fieldImSpecs[i].setEditable(false);
			fieldImSpecs[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(specs, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			
			c.weightx = 1;
			gridBagAdd(specs, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldImSpecs[i]);
			
		}
		
		gridBagSeparator(specs, c, 0, ++c.gridy, 2);
		
		return specs;
	}
	
	private JPanel getMeasurementPanel(int panelMode)
	{
		if (panelMode == COLOR)
			return getColorPanel();
		else
			return new JPanel();
	}
	
	private JPanel getColorPanel()
	{
		JPanel color = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(2,4,2,4);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// RGB Color sensor
		
		JLabel label = new JLabel("Color");
		label.setFont(fontCourier);		
		fieldColor = new JTextField(13);
		fieldColor.setFont(fontCourier);
		fieldColor.setEditable(false);		
		gridBagAdd(color, c, 0, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldColor);
		
		label = new JLabel("RGB");
		label.setFont(fontCourier);		
		fieldRGB = new JTextField(13);
		fieldRGB.setFont(fontCourier);
		fieldRGB.setEditable(false);		
		gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldRGB);		
		fieldRGB.setBackground(Color.white);		
		
		// RGB to XYZ
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		fieldXYZ = new JTextField[3];
		String[] labels = {"X", "Y", "Z"};
						
		for (int i = 0; i < 3; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldXYZ[i] = new JTextField(13);
			fieldXYZ[i].setFont(fontCourier);
			fieldXYZ[i].setEditable(false);
			fieldXYZ[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldXYZ[i]);
		}
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// Curve Fitting Method
		
		label = new JLabel("Wavelength - Curve Fitting Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaCurve = new JTextField[4];
		labels = new String[] {"Best \u03BB(nm)", "SE of X fit(nm)", "SE of Y fit(nm)", "SE of Z fit(nm)"};
		
		for (int i = 0; i < 4; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaCurve[i] = new JTextField(13);
			fieldLambdaCurve[i].setFont(fontCourier);
			fieldLambdaCurve[i].setEditable(false);
			fieldLambdaCurve[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaCurve[i]);
		}
						
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// Inverse truncation method
	
		label = new JLabel("Wavelength - Inverse Truncation Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaTrunc = new JTextField[2];
		labels = new String[] {"Best \u03BB(nm)", "SSE of fit"};
		
		for (int i = 0; i < 2; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaTrunc[i] = new JTextField(13);
			fieldLambdaTrunc[i].setFont(fontCourier);
			fieldLambdaTrunc[i].setEditable(false);
			fieldLambdaTrunc[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaTrunc[i]);
		}
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// Saturation Extrapolation Method
		
		label = new JLabel("Wavelength - Sat Extrapolation Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaExtrap = new JTextField[2];
		labels = new String[] {"Best \u03BB(nm)", "SSE of fit"};
		
		for (int i = 0; i < 2; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaExtrap[i] = new JTextField(13);
			fieldLambdaExtrap[i].setFont(fontCourier);
			fieldLambdaExtrap[i].setEditable(false);
			fieldLambdaExtrap[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaExtrap[i]);
		}
		
		displayColor(Color.white);
		
		return color;
	}
	
	public void clearTable()
	{
		DefaultTableModel dtm = new DefaultTableModel(tableSize, tableCols);
		table.setModel(dtm);
		table.setEditingRow(0);
		tableIndex = 0;
		refreshLeftPanel();
	}
	
	protected void gridBagAdd(JPanel panel, GridBagConstraints c, int x, int y, int width, int align, JComponent comp)
	{
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.anchor = align;		
		panel.add(comp, c);
	}	
	
	protected void gridBagSeparator(JPanel panel, GridBagConstraints c, int x, int y, int width)
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
	
	/** Assumes that all of the required points are already
	 * present in ip.vertices[]. 
	 * 
	 * @param mode 		the measurement type
	 */
	protected void makeMeasurement(int mode)
	{
		if (mode == ANGLE)
		{
			if (isLogging)
				tableAppend(Calc.findAngle(ip.vertices[0], ip.vertices[1], ip.vertices[2]));
		}
		else if (mode == RULER)
		{
			if (isLogging)
				tableAppend(Calc.findDistance(ip.vertices[0], ip.vertices[1], ip.pixelsPerMM));
		}
		else if (mode == PRIMER)
		{
			promptPrimerDistance();
			setMeasuringMode(NONE);
		}
		else if (mode == COLOR)
		{
			displayColor(ip.getPixel(ip.vertices[0]));
		}		
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
	
	public void inputTableSize()
	{
		try
		{
			int size = Integer.parseInt(fieldTableSize.getText());
			setTableSize(size);
		}
		catch(Exception e)
		{			
		}
		refreshLeftPanel();		
	}
	
	public void setTableSize(int size)
	{
		if (size >= 0)
		{
			tableSize = size;
			DefaultTableModel dtm = (DefaultTableModel)table.getModel();
			dtm.setRowCount(size);
			table.setModel(dtm);
		}
	}
	
	public void inputTableIndex()
	{
		try
		{
			int index = Integer.parseInt(fieldTableIndex.getText());
			setTableIndex(index);
			
		}
		catch (Exception e)
		{			
		}
		refreshLeftPanel();
	}
	
	public void setTableIndex(int index)
	{
		if (index >= 0 && index < tableSize)
			tableIndex = index;
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
			buttonMove.setText("Stop");			
		}
		else
		{
			movingMode = false;
			buttonMove.setText("Move");
		}
	}
	
	/** NONE = 0;<br>
	 * ANGLE = 1;<br>
 	 * PRIMER = 2;<br>
	 * RULER = 3;<br>
	 */
	public void setMeasuringMode(int newMode)
	{
		int oldMode = mode;
		System.out.println(oldMode + " " + newMode);			
				
		// Set the mode
				
		if (mode == newMode || newMode == NONE) // Either unselect current mode or set mode to NONE
		{
			writeStatus(defaultStatus);
			setCursor(Cursor.getDefaultCursor());
			mode = NONE;
			ip.vertexIndex = 0;
			for (int i = 1; i < buttonsMeasure.length; i++)
				buttonsMeasure[i].setEnabled(true);
		}
		else // Selecting a new mode
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			mode = newMode;
			
			// Disable all other buttons
			
			for (int i = 1; i < buttonsMeasure.length; i++)
				if (i != mode)
					buttonsMeasure[i].setEnabled(false);
		}	
		
		// Change right panel to reflect current mode
		
		paneSpecWrapper.remove(panesMeasurement[oldMode]);
		paneSpecWrapper.add(panesMeasurement[mode], BorderLayout.SOUTH);
		revalidate();
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
			setTableSize(2 * tableSize);
		refreshLeftPanel();
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
	
	public void displayColor(Color color)
	{
		// Set RGB color
		
		int[] rgb = Calc.getIntRGB(color);
    	fieldColor.setBackground(color);
    	fieldColor.setForeground(new Color(xorBack^xorFront^color.getRGB()));
    	fieldRGB.setText(rgb[0] + "," + rgb[1] + "," + rgb[2]);
    	
    	// Get XYZ
    	
    	double[] XYZ = Calc.RGBtoXYZ(rgb);
    	
    	fieldXYZ[0].setText(Calc.precise8.format(XYZ[0]));
    	fieldXYZ[1].setText(Calc.precise8.format(XYZ[1]));
    	fieldXYZ[2].setText(Calc.precise8.format(XYZ[2])); 
    	
    	// Get wavelength from curve fitting method
    	
    	double[] results = Calc.getPrimaryWavelengthCurveFit(XYZ);
    	fieldLambdaCurve[0].setText(Calc.whole.format(results[0]));
    	fieldLambdaCurve[1].setText(Calc.precise8.format(results[1]));
    	fieldLambdaCurve[2].setText(Calc.precise8.format(results[2]));
    	fieldLambdaCurve[3].setText(Calc.precise8.format(results[3]));
    	
    	// Get wavelength from inverse truncation method
    	
    	results = Calc.getPrimaryWavelengthCurveFit(XYZ);
    	fieldLambdaTrunc[0].setText(Calc.whole.format(results[0]));
    	fieldLambdaTrunc[1].setText(Calc.precise8.format(results[1]));
    	
    	// Get wavelength from saturation extrapolation method
    	
    	results = Calc.getPrimaryWavelengthSatExtrap(XYZ);
    	fieldLambdaExtrap[0].setText(Calc.whole.format(results[0]));
    	fieldLambdaExtrap[1].setText(Calc.precise8.format(results[1]));
	}
	
	public void refresh()
	{
		if (ip.mm != null)
		{
			try
			{
				fieldImSpecs[MODEL].setText(ip.mm.model);
				fieldImSpecs[MAKE].setText(ip.mm.make);
				fieldImSpecs[WIDTH].setText(Calc.whole.format(ip.mm.size.width));
				fieldImSpecs[HEIGHT].setText(Calc.whole.format(ip.mm.size.height));
				fieldImSpecs[DATE].setText(Calc.date.format(ip.mm.date));
				fieldImSpecs[TIME].setText(Calc.time.format(ip.mm.date));
				fieldImSpecs[SHUTTER].setText(Calc.precise8.format(ip.mm.exposure));
				fieldImSpecs[FNUMBER].setText(Calc.precise8.format(ip.mm.fnumber));
				fieldImSpecs[FOCUS].setText(Calc.precise8.format(ip.mm.focus));
				fieldImSpecs[ISO].setText(Calc.whole.format(ip.mm.iso));
				fieldImSpecs[BIAS].setText(Calc.precise8.format(ip.mm.exposureBias));
				fieldImSpecs[WB_RED].setText(Calc.whole.format(ip.mm.wb_red));
				fieldImSpecs[WB_GREEN].setText(Calc.whole.format(ip.mm.wb_green));
				fieldImSpecs[WB_BLUE].setText(Calc.whole.format(ip.mm.wb_blue));
			}
			catch (Exception e)
			{				
			}
		}	
		refreshLeftPanel();
	}
	
	public void refreshLeftPanel()
	{
		fieldTableIndex.setText("" + tableIndex);
		fieldTableSize.setText("" + tableSize);
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
					clearTable();
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
				else if (button.equals(buttonTableIndex))
				{
					inputTableIndex();
				}
				else if (button.equals(buttonTableSize))
				{
					inputTableSize();
				}
			}
			else if (parent instanceof JToggleButton)
			{
				JToggleButton button = (JToggleButton)parent;
				if (button.equals(buttonLog))
					isLogging = buttonLog.isSelected();
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
					
					// Make the measurement if the required number of points are had
					
					if (ip.vertexIndex == pointCount[mode])
						makeMeasurement(mode);
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
				else if (name.equals("Formatting"))
				{
					String str = "Length[1]: {length}\n";
					str += "Angle[1]: {angle in degrees}\n";
					str += "Color[6]: {R, G, B, X, Y, Z}\n";
							
					JOptionPane.showMessageDialog(GUI.this, str, "Data Format", JOptionPane.PLAIN_MESSAGE);			
				}
				else if (name.equals("Controls"))
				{
					String str = "Shortcut keys:\n";
					str += "Shift + drag: move image\n";
					str += "Scroll: zoom\n";
							
					JOptionPane.showMessageDialog(GUI.this, str, "Controls", JOptionPane.PLAIN_MESSAGE);			
				}
				else if (name.equals("About"))
				{
					String message = "Version: 2015.01.14\n";	
					message += "Using the CIE 1931 2-deg D65 XYZ color space.";
					JOptionPane.showMessageDialog(GUI.this, message, "About", JOptionPane.PLAIN_MESSAGE);			
				}
			}
		}
	}
}
