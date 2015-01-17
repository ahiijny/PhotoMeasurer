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
		
		new GUI("Photo Measurer", 1280, 720);
	}		
	
	// Mode labels
	
	public static final int NONE = 0;
	public static final int ANGLE = 1;
	public static final int PRIMER = 2;
	public static final int RULER = 3;
	public static final int COLOR = 4;
	
	// Image Spec Labels
	
	public static final int IM_MAKE = 0;
	public static final int IM_MODEL = 1;
	public static final int IM_WIDTH = 2;
	public static final int IM_HEIGHT = 3;
	public static final int IM_DATE = 4;
	public static final int IM_TIME = 5;
	public static final int IM_SHUTTER = 6;
	public static final int IM_FNUMBER = 7;
	public static final int IM_FOCUS = 8;
	public static final int IM_ISO = 9;
	public static final int IM_BIAS = 10;
	public static final int IM_WB_RED = 11;
	public static final int IM_WB_GREEN = 12;
	public static final int IM_WB_BLUE = 13;
	
	// Angle Labels
	
	public static final int AN_ANGLE = 0;
	
	// Primer Labels
	
	public static final int PR_SCALE = 0;
	
	// Ruler Labels
	
	public static final int RU_LENGTH = 0;
	public static final int RU_SCALE = 1;
	
	// Measurement Panel Fields
		
	public static final Font fontCourier = new Font("Courier New", Font.PLAIN, 12);
	public static final Font fontHeader = new Font("Verdana", Font.PLAIN, 12);
	
	public static final int[] pointCount = {0, 3, 2, 2, 1};
	public static final String[] labelsSpecs = {"Make", "Model", "Width", "Height", "Date", "Time", "Shutter(s)", "f/D", "Focus(mm)", "ISO", "Exp Bias(EV)", "WB red", "WB green", "WB blue"};
	public static final String[] labelsAngle = {"Degrees"};
	public static final String[] labelsPrimer = {"Pixels/length"};
	public static final String[] labelsRuler = {"Length", "Pixels/length"};
	
	public JTextField[] fieldsImSpecs = new JTextField[labelsSpecs.length];
	public JTextField[] fieldsAngle = new JTextField[labelsAngle.length];
	public JTextField[] fieldsPrimer = new JTextField[labelsPrimer.length];
	public JTextField[] fieldsRuler = new JTextField[labelsRuler.length];
	
	// Color Measurement Panel Fields
	
	public JTextField[] fieldXYZ, fieldLambdaFitXYZ, fieldLambdaTrunc, fieldLambdaFitxy; 
	public JTextField fieldColor, fieldRGB, fieldTableIndex, fieldTableSize;
	
	// Listeners
	
	public MyActionListener myActionListener = new MyActionListener();	
	public MenuListener menuListener = new MenuListener();
	private KeyboardFocusManager manager;
	private MyDispatcher keyDispatcher;
				
	// Content Panes

	public JPanel content;
	public JPanel paneCenter, paneLeft, paneRight, paneBottom, paneTop, paneSpecWrapper;
	public JPanel[] panesMeasurement = new JPanel[5];
	public ImagePanel ip;
	
	// Control Panel
	
	public JButton[] buttonsMeasure = new JButton[5];
	public JToggleButton buttonLog;
	public JButton buttonMove;	
	public int mode = 0;
	
	// Dynamic Displays
	
	public String defaultStatus = "Become the literally I know lives.";
	public JLabel positionLabel;
	public JLabel ratioLabel;
	public JLabel statusBar;
	public JComboBox<String> zoomCtrl;
	public JComboBox<String> renderCtrl;
	public JTable table;	
		
	// Buttons
		
	public JButton buttonTableIndex, buttonTableSize, buttonLengthScale;
	
	// Other Stuff
	
	private File directory = new File(""); // initialize dir to current dir	
			
	public int xorBack = Color.white.getRGB();
	public int xorFront = Color.black.getRGB();
	
	public int tableIndex = 0;
	public int tableSize = 128;
	public int tableCols = 6;
		
	public boolean movingMode = false;
	public boolean isLogging = true;
	public double zoomStep = 0.1;	// The increase in zoom (1 = 100%) per scroll increment
	
	public Logger logger;
	
	public String title;

	public GUI(String title, int width, int height)
	{
		super(title);
		this.title = title;		
		
		setJMenuBar(createMenuBar());
		setContentPane(createContent());
		logger = new Logger(table);
		refresh();
		
		// Set up keyboard stuff
		
		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyDispatcher = new MyDispatcher();
		manager.addKeyEventDispatcher(keyDispatcher);
		
		// Open the window
		
		setSize(width, height);
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
		
		button = new JMenuItem ("Export to CSV");
		button.setMnemonic('x');
		button.setAccelerator(KeyStroke.getKeyStroke (
				KeyEvent.VK_X, menuKeyMask));
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
		
		for (int i = 0; i < 5; i++)
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
		int cols = 14;
		
		// Header
		
		JLabel label = new JLabel ("Image metadata");
		label.setFont(fontHeader);
		gridBagAdd(specs, c, 0, c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		// Image data
		
		for (int i = 0; i < labelsSpecs.length; i++)
		{
			label = new JLabel(labelsSpecs[i]);
			label.setFont(fontCourier);
			
			fieldsImSpecs[i] = new JTextField(cols);
			fieldsImSpecs[i].setEditable(false);
			fieldsImSpecs[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(specs, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			
			c.weightx = 1;
			gridBagAdd(specs, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldsImSpecs[i]);
			
		}
		
		gridBagSeparator(specs, c, 0, ++c.gridy, 2);
		
		return specs;
	}
	
	private JPanel getMeasurementPanel(int panelMode)
	{
		if (panelMode == ANGLE)
			return getAnglePanel();
		else if (panelMode == PRIMER)
			return getPrimerPanel();
		else if (panelMode == RULER)
			return getRulerPanel();
		else if (panelMode == COLOR)
			return getColorPanel();
		else
			return new JPanel();
	}
	
	private JPanel getAnglePanel()
	{				
		// Angle panel
		
		JPanel angle = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,4,2,4);
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		int cols = 14;
		
		// Angle data
		
		for (int i = 0; i < labelsAngle.length; i++)
		{
			JLabel label = new JLabel(labelsAngle[i]);
			label.setFont(fontCourier);
			
			fieldsAngle[i] = new JTextField(cols);
			fieldsAngle[i].setEditable(false);
			fieldsAngle[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(angle, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			c.weightx = 1;
			gridBagAdd(angle, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldsAngle[i]);			
		}
		
		gridBagSeparator(angle, c, 0, ++c.gridy, 2);
				
		return angle;
	}
	
	private JPanel getRulerPanel()
	{				
		// Ruler panel
		
		JPanel ruler = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,4,2,4);
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		int cols = 13;
		
		// Ruler data
		
		for (int i = 0; i < labelsRuler.length; i++)
		{
			JLabel label = new JLabel(labelsRuler[i]);
			label.setFont(fontCourier);
			
			fieldsRuler[i] = new JTextField(cols);
			fieldsRuler[i].setEditable(false);
			fieldsRuler[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(ruler, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			c.weightx = 1;
			gridBagAdd(ruler, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldsRuler[i]);			
		}
		
		gridBagSeparator(ruler, c, 0, ++c.gridy, 2);
				
		return ruler;
	}
	
	private JPanel getPrimerPanel()
	{				
		// Primer panel
		
		JPanel primer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,4,2,4);
		c.gridx = c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		int cols = 13;
		
		// Primer data
		
		for (int i = 0; i < labelsPrimer.length; i++)
		{
			JLabel label = new JLabel(labelsPrimer[i]);
			label.setFont(fontCourier);
			
			fieldsPrimer[i] = new JTextField(cols);
			fieldsPrimer[i].setBackground(Color.white);
			
			c.weightx = 0;
			gridBagAdd(primer, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			c.weightx = 1;
			gridBagAdd(primer, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldsPrimer[i]);			
		}
		
		buttonLengthScale = new JButton("Set");
		buttonLengthScale.addActionListener(myActionListener);
		c.fill = GridBagConstraints.NONE;
		gridBagAdd(primer, c, 0, ++c.gridy, 2, GridBagConstraints.CENTER, buttonLengthScale);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		gridBagSeparator(primer, c, 0, ++c.gridy, 2);
		
		return primer;
	}
	
	private JPanel getColorPanel()
	{
		JPanel color = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(2,4,2,4);
		c.fill = GridBagConstraints.HORIZONTAL;
		int cols = 11;
				
		// RGB Color sensor
		
		JLabel label = new JLabel("Color");
		label.setFont(fontCourier);		
		fieldColor = new JTextField(cols);
		fieldColor.setFont(fontCourier);
		fieldColor.setEditable(false);		
		gridBagAdd(color, c, 0, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldColor);
		
		label = new JLabel("RGB");
		label.setFont(fontCourier);		
		fieldRGB = new JTextField(cols);
		fieldRGB.setFont(fontCourier);
		fieldRGB.setEditable(false);		
		gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldRGB);		
		fieldRGB.setBackground(Color.white);		
		
		// RGB to XYZ
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		fieldXYZ = new JTextField[3];
		String[] labels = {"X (red)", "Y (green)", "Z (blue)"};
						
		for (int i = 0; i < 3; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldXYZ[i] = new JTextField(cols);
			fieldXYZ[i].setFont(fontCourier);
			fieldXYZ[i].setEditable(false);
			fieldXYZ[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldXYZ[i]);
		}		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// XYZ Curve Fitting Method
		
		label = new JLabel("Wavelength - XYZ Curve Fitting Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaFitXYZ = new JTextField[4];
		labels = new String[] {"Best \u03BB(nm)", "SE of X fit(nm)", "SE of Y fit(nm)", "SE of Z fit(nm)"};
		
		for (int i = 0; i < 4; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaFitXYZ[i] = new JTextField(cols);
			fieldLambdaFitXYZ[i].setFont(fontCourier);
			fieldLambdaFitXYZ[i].setEditable(false);
			fieldLambdaFitXYZ[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaFitXYZ[i]);
		}
						
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// xy fitting method
	
		label = new JLabel("Wavelength - xy Fitting Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaFitxy = new JTextField[2];
		labels = new String[] {"Best \u03BB(nm)", "SSE of fit [0..1]"};
		
		for (int i = 0; i < 2; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaFitxy[i] = new JTextField(cols);
			fieldLambdaFitxy[i].setFont(fontCourier);
			fieldLambdaFitxy[i].setEditable(false);
			fieldLambdaFitxy[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaFitxy[i]);
		}
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// Inverse truncation method
		
		label = new JLabel("Wavelength - Inverse Truncation Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaTrunc = new JTextField[2];
		labels = new String[] {"Best \u03BB(nm)", "SSE of fit [0..1]"};
		
		for (int i = 0; i < 2; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaTrunc[i] = new JTextField(cols);
			fieldLambdaTrunc[i].setFont(fontCourier);
			fieldLambdaTrunc[i].setEditable(false);
			fieldLambdaTrunc[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaTrunc[i]);
		}
		
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
	
	public void clearRow(int row)
	{							
		if (row != -1)
		{
			TableModel tm = table.getModel();
			for (int i = 0; i < tableCols; i++)
				tm.setValueAt("", row, i);
		}
	}
	
	public void clearImFields()
	{
		for (int i = 0; i < fieldsImSpecs.length; i++)
			fieldsImSpecs[i].setText("");
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
		int temp = c.fill;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(sep, c);
		c.fill = temp;
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
			double angle = Calc.findAngle(ip.vertices[0], ip.vertices[1], ip.vertices[2]);
			displayAngle(angle);
			if (isLogging)
				tableAppend(angle);
		}
		else if (mode == RULER)
		{
			double length = Calc.findDistance(ip.vertices[0], ip.vertices[1], ip.pixelsPerMM);
			displayRuler(length);
			if (isLogging)
				tableAppend(length);
		}
		else if (mode == PRIMER)
		{			
			promptPrimerDistance();
			displayPrimer();
		}
		else if (mode == COLOR)
		{
			Color color = ip.getPixel(ip.vertices[0]);
			int[] rgb = Calc.getIntRGB(color);			
			double[] XYZ = Calc.RGBtoXYZ(rgb);
			displayColor(color, rgb, XYZ);
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
	
	public void inputLengthScale()
	{
		try
		{
			double pixelsPerMM = Double.parseDouble(fieldsPrimer[PR_SCALE].getText());
			ip.pixelsPerMM = pixelsPerMM;
		}
		catch (Exception e)
		{			
		}
		refreshRightPanel();
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
		manager.removeKeyEventDispatcher(keyDispatcher); // remove keyboard bindings temporally
		
		// File dialog prompt
		
		FileDialog fd = new FileDialog(this, "Open");
		fd.setFile("*.BMP;*.GIF;*.PNG;*.JPG;*.JPEG");
		fd.setVisible(true);		
		String path = fd.getFile();	
		
		// If path selected, open that image.
		
		if (path != null)
		{
			directory = new File(fd.getDirectory());
			File load = new File(directory.getAbsoluteFile() + File.separator + path);
			directory = load;
			
			// Open image
			
			if (load.canRead())
			{
				clearImFields(); // Reset im fields
				ip.loadImage(load);
				setTitle(title + " - " + path);
				refresh();
			}
		}		
		manager.addKeyEventDispatcher(keyDispatcher); // return keyboard bindings
				
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
	
	/** Exports the data in the table to csv. 
	 */
	public void export()
	{
		manager.removeKeyEventDispatcher(keyDispatcher); // remove keyboard bindings temporally
		
		// File dialog prompt
		
		FileDialog fd = new FileDialog(this, "Save", FileDialog.SAVE);
		fd.setFile(logger.path.getAbsolutePath());
		fd.setVisible(true);		
		String path = fd.getFile();
		
		// If path selected, save to that path.
		
		if (path != null)
		{		
			directory = new File(fd.getDirectory());
			File save = new File(directory.getAbsoluteFile() + File.separator + path);
			logger.setPath(save);
			
			// Write to file
			
			try
			{
				logger.log();
				logger.write();
				//String message = "Successfully exported to " + path + ".";
				//JOptionPane.showMessageDialog(GUI.this, message, "Export Data", JOptionPane.INFORMATION_MESSAGE);			
			}
			catch (Exception ex)
			{
				String message = ex.getMessage();
				JOptionPane.showMessageDialog(GUI.this, message, "Export Data", JOptionPane.WARNING_MESSAGE);
				ex.printStackTrace();
			}						
		}
		
		manager.addKeyEventDispatcher(keyDispatcher); // return keyboard bindings
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
	 * COLOR = 4;<br>
	 */
	public void setMeasuringMode(int newMode)
	{
		int oldMode = mode;		
				
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
		refresh();
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
	
	public void displayAngle(double degrees)
	{
		fieldsAngle[AN_ANGLE].setText(Calc.precise12.format(degrees));
	}
	
	public void displayPrimer()
	{
		fieldsPrimer[PR_SCALE].setText(Calc.precise12.format(ip.pixelsPerMM));
	}
	
	public void displayRuler(double length)
	{
		fieldsRuler[RU_LENGTH].setText(Calc.precise12.format(length));
		fieldsRuler[RU_SCALE].setText(Calc.precise12.format(ip.pixelsPerMM));
	}
	
	public void displayColor(Color color, int[] rgb, double[] XYZ)
	{
		// Set RGB color		
		
    	fieldColor.setBackground(color);
    	fieldColor.setForeground(new Color(xorBack^xorFront^color.getRGB()));
    	fieldRGB.setText(rgb[0] + "," + rgb[1] + "," + rgb[2]);
    	
    	// Set XYZ    	
    	    	
    	for (int i = 0; i < fieldXYZ.length; i++)
    	{
    		fieldXYZ[i].setText(Calc.precise8.format(XYZ[i]));
    		fieldXYZ[i].setCaretPosition(0);
    	}  
    	
    	// Get wavelength from XYZ curve fitting method
    	
    	double[] results = Calc.getPrimaryWavelengthFitXYZ(XYZ);
    	fieldLambdaFitXYZ[0].setText(Calc.whole.format(results[0]));    	
    	for (int i = 1; i < 4; i++)
    	{
    		fieldLambdaFitXYZ[i].setText(Calc.precise8.format(results[i]));
    		fieldLambdaFitXYZ[i].setCaretPosition(0);
    	}  
    	
    	// Get wavelength from xy fitting method
    	
    	results = Calc.getPrimaryWavelengthFitxy(XYZ);
    	fieldLambdaFitxy[0].setText(Calc.whole.format(results[0]));
    	fieldLambdaFitxy[1].setText(Calc.precise8.format(results[1]));
    	
    	// Get wavelength from inverse truncation method
    	
    	results = Calc.getPrimaryWavelengthInverseTrunc(rgb);
    	fieldLambdaTrunc[0].setText(Calc.whole.format(results[0]));
    	fieldLambdaTrunc[1].setText(Calc.precise8.format(results[1]));
	}
	
	public void refresh()
	{
		refreshRightPanel();
		refreshLeftPanel();
		revalidate();
		repaint();
	}
	
	public void refreshRightPanel()
	{
		// Refresh Image Specs panel
		
		if (ip.mm != null)
		{
			try
			{
				fieldsImSpecs[IM_MODEL].setText(ip.mm.model);
				fieldsImSpecs[IM_MAKE].setText(ip.mm.make);
				fieldsImSpecs[IM_WIDTH].setText(Calc.whole.format(ip.mm.size.width));
				fieldsImSpecs[IM_HEIGHT].setText(Calc.whole.format(ip.mm.size.height));
				fieldsImSpecs[IM_DATE].setText(Calc.date.format(ip.mm.date));
				fieldsImSpecs[IM_TIME].setText(Calc.time.format(ip.mm.date));
				fieldsImSpecs[IM_SHUTTER].setText(Calc.precise8.format(ip.mm.exposure));
				fieldsImSpecs[IM_FNUMBER].setText(Calc.precise8.format(ip.mm.fnumber));
				fieldsImSpecs[IM_FOCUS].setText(Calc.precise8.format(ip.mm.focus));
				fieldsImSpecs[IM_ISO].setText(Calc.whole.format(ip.mm.iso));
				fieldsImSpecs[IM_BIAS].setText(Calc.precise8.format(ip.mm.exposureBias));
				fieldsImSpecs[IM_WB_RED].setText(Calc.whole.format(ip.mm.wb_red));
				fieldsImSpecs[IM_WB_GREEN].setText(Calc.whole.format(ip.mm.wb_green));
				fieldsImSpecs[IM_WB_BLUE].setText(Calc.whole.format(ip.mm.wb_blue));
			}
			catch (Exception e)
			{				
			}
		}
		if (mode == PRIMER)
			fieldsPrimer[PR_SCALE].setText(Calc.precise12.format(ip.pixelsPerMM));
		else if (mode == RULER)
			fieldsRuler[RU_SCALE].setText(Calc.precise12.format(ip.pixelsPerMM));
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
					writeStatus("Select a known length (e.g. across a ruler).");
					setMeasuringMode(PRIMER);					
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
				else if (button.equals(buttonLengthScale))
				{
					inputLengthScale();
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
				else if (e.getKeyCode() == KeyEvent.VK_DELETE)				
					clearRow(table.getSelectedRow());				
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
				else if (name.equals("Export to CSV"))
				{
					export();
				}
				else if (name.equals("Jump to Origin"))
				{
					ip.setOffset(new Point(0, 0));
					repaint();
				}
				else if (name.equals("Formatting"))
				{
					String str = "Angle[1]: {angle in degrees}\n";
					str += "Length[1]: {length}\n";
					str += "Color[6]: {R, G, B, X, Y, Z}\n";
							
					JOptionPane.showMessageDialog(GUI.this, str, "Data Format", JOptionPane.PLAIN_MESSAGE);			
				}
				else if (name.equals("Controls"))
				{
					String str = "Shortcut keys:\n";
					str += "Shift + drag: move image\n";
					str += "Scroll: zoom\n";
					str += "Delete: clear selected row\n";
							
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
