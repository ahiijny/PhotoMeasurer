package ahiijny.photomeasurer;
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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/** TODO: Add scroll bar for panels if display screen is too small.
 */
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
	public static final int PROFILER = 5;
	public static final int AREA = 6;
	
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
	
	// Profiler Labels
	
	public static final int PF_X1 = 0;
	public static final int PF_Y1 = 1;
	public static final int PF_X2 = 2;
	public static final int PF_Y2 = 3;
	
	public static final int PF_EXTRAPOLATE = 0;
	public static final int PF_SLOPE_LOCK = 1;
	public static final int PF_SELECT_ALL = 2;
	public static final int PF_X_COORD = 0;
	public static final int PF_Y_COORD = 1;
	public static final int PF_T_COORD = 2;
	public static final int PF_SRED = 3;
	public static final int PF_SGREEN = 4;
	public static final int PF_SBLUE = 5;
	public static final int PF_LINRED = 6;
	public static final int PF_LINGREEN = 7;
	public static final int PF_LINBLUE = 8;
	public static final int PF_X = 9;
	public static final int PF_Y = 10;
	public static final int PF_Z = 11;
	public static final int PF_LAM_XYZ = 12;
	public static final int PF_LAM_xy = 13;
	public static final int PF_LAM_RGB = 14;
	public static final int PF_LAM_SAT_EXTRAP = 15;
	
	// Area Labels
	
	public static final int AR_SRGB_STEP = 0;
	public static final int AR_LINRGB_STEP = 1;
	public static final int AR_XYZ_STEP = 2;
	public static final int AR_LAMBDA_STEP = 3;
	
	public static final int AR_SRED = 0;
	public static final int AR_SGREEN = 1;
	public static final int AR_SBLUE = 2;
	public static final int AR_LINRED = 3;
	public static final int AR_LINGREEN = 4;
	public static final int AR_LINBLUE = 5;
	public static final int AR_X = 6;
	public static final int AR_Y = 7;
	public static final int AR_Z = 8;
	public static final int AR_LAM_XYZ = 9;
	public static final int AR_LAM_xy = 10;
	public static final int AR_LAM_RGB = 11;
	public static final int AR_LAM_SAT_EXTRAP = 12;
	
	// Measurement Panel Fields
		
	public static final Font fontCourier = new Font("Courier New", Font.PLAIN, 12);
	public static final Font fontHeader = new Font("Verdana", Font.PLAIN, 12);
	
	public static final int[] measurePointCount = {0, 3, 2, 2, 1, 2, -1};
	public static final String[] labelsSpecs = {"Make", "Model", "Width", "Height", "Date", "Time", "Shutter(s)", "f/D", "Focus(mm)", "ISO", "Exp Bias(EV)", "WB red", "WB green", "WB blue"};
	public static final String[] labelsAngle = {"Degrees"};
	public static final String[] labelsPrimer = {"Pixels/length"};
	public static final String[] labelsRuler = {"Length", "Pixels/length"};
	public static final String[] labelsProfiler = {"Extrapolate", "Slope Lock", "Select All"};
	public static final String[] labelsProfilerSpinner = {"x1", "y1", "x2", "y2"};
	public static final String[] labelsArea = {"sRGB step", "linRGB step", "XYZ step", "lambda step"};
	
	public static final String[] labelsProfilerParams = {"x coord", "y coord", "t coord", "sRed", "sGreen", "sBlue", "linRed", "linGreen", "linBlue", "X", "Y", "Z", "\u03BB (XYZ fit)", "\u03BB (xyY fit)", "\u03BB (RGB fit)", "\u03BB (sat extrap)"};
	public static final String[] labelsAreaParams = {"sRed", "sGreen", "sBlue", "linRed", "linGreen", "linBlue", "X", "Y", "Z", "\u03BB (XYZ fit)", "\u03BB (xyY fit)", "\u03BB (RGB fit)", "\u03BB (sat extrap)"};
		
	public JTextField[] fieldsImSpecs = new JTextField[labelsSpecs.length];
	public JTextField[] fieldsAngle = new JTextField[labelsAngle.length];
	public JTextField[] fieldsPrimer = new JTextField[labelsPrimer.length];
	public JTextField[] fieldsRuler = new JTextField[labelsRuler.length];
	public JTextField[] fieldsArea = new JTextField[labelsArea.length];
	public JSpinner[] spinnersProfiler = new JSpinner[4];
	
	public JRadioButton[] buttonsProfilerOptions = new JRadioButton[labelsProfiler.length];
	public JToggleButton[] buttonsProfilerParams = new JToggleButton[labelsProfilerParams.length];
	public JToggleButton[] buttonsAreaParams = new JToggleButton[labelsAreaParams.length];
		
	// Profiler Panel Fields
	
	public JTextField fieldPixelsPerSample;
	
	// Color Measurement Panel Fields
	
	public JTextField[] fieldXYZ, fieldLambdaFitXYZ, fieldLambdaTrunc, fieldLambdaFitxy, fieldLambdaSatExtrap; 
	public JTextField fieldColor, fieldRGB, fieldTableIndex, fieldTableSize;
	
	// Listeners
	
	public MyActionListener myActionListener = new MyActionListener();	
	public MenuListener menuListener = new MenuListener();
	private KeyboardFocusManager manager;
	private MyDispatcher keyDispatcher;
				
	// Content Panes

	public JSplitPane content, paneCenter;
	public JPanel paneLeft, paneRight, paneBottom, paneTop, paneSpecWrapper;
	public JPanel[] panesMeasurement = new JPanel[measurePointCount.length];
	public ImagePanel ip;
	
	// Control Panel
	
	public JButton[] buttonsMeasure = new JButton[measurePointCount.length];
	public JToggleButton buttonLog, buttonAreaSelecting;
	public JButton buttonMove;	
	public int mode = 0;
	
	// Dynamic Displays
	
	public String defaultStatus = "Become the literally I know lives.";
	public JLabel positionLabel;
	public JLabel ratioLabel;
	public JLabel statusBar;
	public JComboBox<String> zoomCtrl;
	public JComboBox<String> renderCtrl;
	public JScrollPane tableWrapper;
	public JTable table;	
		
	// Buttons
		
	public JButton buttonTableIndex, buttonTableSize, buttonLengthScale, 
				   buttonSampleRate, buttonLogProfiles, buttonHistogramSet, 
				   buttonHistogramDefault, buttonLogArea;
	
	// Other Stuff
	
	private File directory = new File(""); // initialize dir to current dir	
			
	public static int xorBack = Color.white.getRGB();
	public static int xorFront = Color.black.getRGB();
	
	public int tableIndex = 0;
	public int tableRows = 128;
	public int tableCols = 10;
		
	public boolean movingMode = false;
	public boolean isLogging = true;
	public double zoomStep = 0.1;	// The increase in zoom (1 = 100%) per scroll increment
	
	public Logger logger;
	public Plotter plotter;
	
	public String title;
	public String path;

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
		JMenu file, data, view, help;
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
		
		// "Data" Menu
		
		data = new JMenu("Data");
		data.setMnemonic('d');
		
		button = new JMenuItem ("Log image name");
		button.setMnemonic('n');
		button.setAccelerator(KeyStroke.getKeyStroke (
				KeyEvent.VK_N, menuKeyMask));
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log image metadata");
		button.setMnemonic('m');
		button.setAccelerator(KeyStroke.getKeyStroke (
				KeyEvent.VK_M, menuKeyMask));
		button.addActionListener (menuListener);
		data.add(button);				
		
		data.add(new JSeparator());	
		
		button = new JMenuItem ("Log profile endpoints");
		button.setMnemonic('e');
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log all profiles");
		button.setMnemonic('p');
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log pixels per length");
		button.setMnemonic('x');
		button.addActionListener (menuListener);
		data.add(button);		
						
		data.add(new JSeparator());
		
		button = new JMenuItem ("Log spectrum XYZ");
		button.setMnemonic('z');
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log spectrum xyY");
		button.setMnemonic('y');
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log spectrum RGB_lin");
		button.setMnemonic('l');
		button.addActionListener (menuListener);
		data.add(button);
		
		button = new JMenuItem ("Log spectrum sRGB");
		button.setMnemonic('r');
		button.addActionListener (menuListener);
		data.add(button);
		
		// "View" Menu
		
		view = new JMenu("View");
		view.setMnemonic('v');
		
		button = new JMenuItem ("Jump to Origin");
		button.setMnemonic('j');
		button.addActionListener(menuListener);
		view.add(button);
		
		button = new JMenuItem ("Reset Zoom");
		button.setMnemonic('z');
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
		menuBar.add (data);
		menuBar.add (view);
		menuBar.add (help);

		// Return        
		return menuBar;
	}
	
	private JSplitPane createContent()
	{
		// Init Measurement Panels
		
		for (int i = 0; i < measurePointCount.length; i++)
			panesMeasurement[i] = getMeasurementPanel(i);
		
		// Create content panes
		
		paneCenter = createCenterPanel();
		paneLeft = createLeftPanel();
		paneRight = createRightPanel();
		paneBottom = createBottomPanel();
		paneTop = createTopPanel();
		
		// Add panels to main pane and left pane
		
		JPanel main = new JPanel(new BorderLayout());
		JPanel left = new JPanel(new BorderLayout());
		
		main.add(paneCenter, BorderLayout.CENTER);		
		main.add(paneRight, BorderLayout.EAST);
		main.add(paneBottom, BorderLayout.SOUTH);
		main.add(paneTop, BorderLayout.NORTH);
		left.add(paneLeft, BorderLayout.WEST);
		
		// Add panes to split pane
		
		content = new JSplitPane();
		content.setLeftComponent(left);
		content.setRightComponent(main);
		content.setDividerSize(4);
		content.addPropertyChangeListener(new MyPropertyChangeListener());
						
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
	private JSplitPane createCenterPanel()
	{		
		ip = new ImagePanel(this);
		ip.addMouseListener (new MyMouseListener());
		ip.addMouseMotionListener (new MyMouseListener());
		ip.addMouseWheelListener (new MyMouseListener());
		
		plotter = new Plotter(this);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setBottomComponent(ip);
		splitter.setTopComponent(plotter);
		splitter.setDividerSize(4);
		splitter.setDividerLocation(0);
		splitter.addPropertyChangeListener(new MyPropertyChangeListener());
		
		return splitter;
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
			
		table = new JTable(tableRows, tableCols);		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		tableWrapper = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableWrapper.setPreferredSize(new Dimension(170, 1));
		
		table.getTableHeader().setReorderingAllowed(false);				
				  
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = c.weightx = 1;
		gridBagAdd(data, c, 0, ++c.gridy, 3, GridBagConstraints.FIRST_LINE_START, tableWrapper);
				
		refreshLeftPanel();
		
		return data;
	}
	
	private JPanel createTopPanel()
	{
		return new JPanel();
	}
	
	private JPanel getControlPanel()
	{
		// Control panel
		
		JPanel control = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(0,2,0,2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		
		// Add buttons to control panel

		JButton button = new JButton("Load");
		button.addActionListener(myActionListener);
		gridBagAdd(control, c, 0, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, button);
		
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, new JLabel("  "));
		
		buttonLog = new JToggleButton("Log");
		buttonLog.setSelected(true);
		buttonLog.addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonLog);
		
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, new JLabel("  "));

		buttonsMeasure[ANGLE] = new JButton("Angle");
		buttonsMeasure[ANGLE].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[ANGLE]);

		buttonsMeasure[PRIMER] = new JButton("Primer");
		buttonsMeasure[PRIMER].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[PRIMER]);

		buttonsMeasure[RULER] = new JButton("Ruler");
		buttonsMeasure[RULER].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[RULER]);
					
		buttonsMeasure[COLOR] = new JButton("Color");
		buttonsMeasure[COLOR].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[COLOR]);
		
		buttonsMeasure[PROFILER] = new JButton("Profiler");
		buttonsMeasure[PROFILER].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[PROFILER]);
		
		buttonsMeasure[AREA] = new JButton("Area");
		buttonsMeasure[AREA].addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonsMeasure[AREA]);
		
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, new JLabel("  "));
		
		buttonMove = new JButton("Move");
		buttonMove.addActionListener(myActionListener);
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, buttonMove);
		
		button = new JButton("Clear");
		button.addActionListener(myActionListener);
		c.weighty = 1;
		gridBagAdd(control, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, button);
		
		// Wrapper
		
		return control;
	}
	
	private JPanel getImageSpecsPanel()
	{
		// Image specs panel
		
		JPanel specs = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1,4,1,4);
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
		else if (panelMode == PROFILER)
			return getProfilerPanel();
		else if (panelMode == AREA)
			return getAreaPanel();
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
		int cols = 12;
		
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
		int cols = 12;
		
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
		// Color Panel
		
		JPanel color = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(1,4,1,4);
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
		
		gridBagSeparator(color, c, 0, ++c.gridy, 2);
		
		// Saturation Extrapolation
		
		label = new JLabel("Wavelength - Sat Extrapolation Method"); 
		gridBagAdd(color, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		fieldLambdaSatExtrap = new JTextField[2];
		labels = new String[] {"Best \u03BB(nm)", "SE of fit (nm)"};
		
		for (int i = 0; i < 2; i++)
		{
			label = new JLabel(labels[i]);
			label.setFont(fontCourier);	
			
			fieldLambdaSatExtrap[i] = new JTextField(cols);
			fieldLambdaSatExtrap[i].setFont(fontCourier);
			fieldLambdaSatExtrap[i].setEditable(false);
			fieldLambdaSatExtrap[i].setBackground(Color.white);
			
			gridBagAdd(color, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			gridBagAdd(color, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldLambdaSatExtrap[i]);
		}
		
		return color;
	}
	
	private JPanel getProfilerPanel()
	{
		// Profiler panel
		
		JPanel profiler = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(1,4,1,4);
		c.fill = GridBagConstraints.HORIZONTAL;
		int cols = 7;
		
		// Initialize components	
						
		JLabel[] labels = new JLabel[4];		
		
		for (int i = 0; i < spinnersProfiler.length; i++)
		{
			SpinnerModel model = new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
			spinnersProfiler[i] = new JSpinner(model);
			spinnersProfiler[i].addChangeListener(new MyChangeListener());
			JFormattedTextField ftf = ((JSpinner.DefaultEditor)spinnersProfiler[i].getEditor()).getTextField();
			ftf.setColumns(cols);
		}
		
		for (int i = 0; i < labelsProfilerSpinner.length; i++)
			labels[i] = new JLabel(labelsProfilerSpinner[i]);
		
		// Init profiler parameter toggle buttons
		
		for (int i = 0; i < labelsProfilerParams.length; i++)
		{
			buttonsProfilerParams[i] = new JToggleButton(labelsProfilerParams[i]);
			buttonsProfilerParams[i].addActionListener(myActionListener);
			buttonsProfilerParams[i].setMargin(new Insets(2,0,2,0));
			buttonsProfilerParams[i].setBackground(Plotter.colors[i]);
			buttonsProfilerParams[i].setForeground(Color.black);
		}
		
		// Add line specification fields
		
		c.weightx = 0;
		gridBagAdd(profiler, c, 0, 0, 1, GridBagConstraints.FIRST_LINE_START, labels[PF_X1]);
		gridBagAdd(profiler, c, 2, 0, 1, GridBagConstraints.FIRST_LINE_START, labels[PF_X2]);
		gridBagAdd(profiler, c, 0, 1, 1, GridBagConstraints.FIRST_LINE_START, labels[PF_Y1]);
		gridBagAdd(profiler, c, 2, 1, 1, GridBagConstraints.FIRST_LINE_START, labels[PF_Y2]);
		
		c.weightx = 0;
		gridBagAdd(profiler, c, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, spinnersProfiler[PF_X1]);
		gridBagAdd(profiler, c, 3, 0, 1, GridBagConstraints.FIRST_LINE_START, spinnersProfiler[PF_X2]);
		gridBagAdd(profiler, c, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, spinnersProfiler[PF_Y1]);
		gridBagAdd(profiler, c, 3, 1, 1, GridBagConstraints.FIRST_LINE_START, spinnersProfiler[PF_Y2]);
		
		gridBagSeparator(profiler, c, 0, ++c.gridy, 4);		
				
		// Add additional options
		
		c.insets = new Insets(-2,4,-2,4);
		
		for (int i = 0; i < labelsProfiler.length; i++)
		{
			buttonsProfilerOptions[i] = new JRadioButton(labelsProfiler[i]);
			buttonsProfilerOptions[i].addActionListener(myActionListener);
			gridBagAdd(profiler, c, 0, ++c.gridy, 4, GridBagConstraints.CENTER, buttonsProfilerOptions[i]);
		}
		buttonsProfilerOptions[PF_EXTRAPOLATE].setSelected(true);
		buttonsProfilerOptions[PF_SLOPE_LOCK].setSelected(false);
		buttonsProfilerOptions[PF_SELECT_ALL].setSelected(false);
		
		c.insets = new Insets(2,4,2,4);
		
		gridBagSeparator(profiler, c, 0, ++c.gridy, 4);	
		
		// Add sample rate field
		
		JLabel label = new JLabel("Pixels per sample");
		label.setFont(fontCourier);
		fieldPixelsPerSample = new JTextField();
		gridBagAdd(profiler, c, 0, ++c.gridy, 3, GridBagConstraints.FIRST_LINE_START, label);
		gridBagAdd(profiler, c, 3, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldPixelsPerSample);
						
		// Add set button
		
		buttonSampleRate = new JButton("Set");
		buttonSampleRate.addActionListener(myActionListener);
		c.fill = GridBagConstraints.NONE;
		gridBagAdd(profiler, c, 0, ++c.gridy, 4, GridBagConstraints.CENTER, buttonSampleRate);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		gridBagSeparator(profiler, c, 0, ++c.gridy, 4);
		
		c.insets = new Insets(0,4,0,4);
		
		// Add profiler params
		
		int starty = c.gridy;
		int startx = 0;
		int dy = 0;
						
		for (int i = PF_SRED; i < buttonsProfilerParams.length; i++)
		{
			gridBagAdd(profiler, c, startx, starty + ++dy, 2, GridBagConstraints.CENTER, buttonsProfilerParams[i]);
			if (i == PF_LINBLUE)
			{
				startx = 2;
				dy = 0;
			}
		}
		
		// Add log button
		
		c.gridy = starty + PF_LINBLUE;	
		c.insets = new Insets(2,4,2,4);		
		
		gridBagSeparator(profiler, c, 0, ++c.gridy, 4);	
		c.fill = GridBagConstraints.NONE;
		
		buttonLogProfiles = new JButton("Log Profiles");
		buttonLogProfiles.addActionListener(myActionListener);
		
		gridBagAdd(profiler, c, 0, ++c.gridy, 4, GridBagConstraints.CENTER, buttonLogProfiles);
				
		return profiler;
	}
	
	private JPanel getAreaPanel()
	{
		// Profiler panel
		
		JPanel area = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets = new Insets(1,4,1,4);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		
		// Buttons
		
		buttonAreaSelecting = new JToggleButton("Selecting Area");
		buttonAreaSelecting.setSelected(false);
		buttonAreaSelecting.addActionListener(myActionListener);
		
		gridBagAdd(area, c, 0, c.gridy, 2, GridBagConstraints.CENTER, buttonAreaSelecting);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		gridBagSeparator(area, c, 0, ++c.gridy, 2);
		
		// Histogram settings
		
		JLabel label = new JLabel("Histogram settings");
		label.setFont(fontHeader);
		gridBagAdd(area, c, 0, ++c.gridy, 2, GridBagConstraints.FIRST_LINE_START, label);
		
		for (int i = 0; i < labelsArea.length; i++)
		{
			label = new JLabel(labelsArea[i]);
			label.setFont(fontCourier);
			gridBagAdd(area, c, 0, ++c.gridy, 1, GridBagConstraints.FIRST_LINE_START, label);
			
			fieldsArea[i] = new JTextField();
			gridBagAdd(area, c, 1, c.gridy, 1, GridBagConstraints.FIRST_LINE_START, fieldsArea[i]);
		}
		
		// Histogram buttons
		
		c.fill = GridBagConstraints.NONE;
		buttonHistogramSet = new JButton ("Set Steps");
		buttonHistogramDefault = new JButton ("Default");
		buttonHistogramSet.addActionListener(myActionListener);
		buttonHistogramDefault.addActionListener(myActionListener);
		gridBagAdd(area, c, 0, ++c.gridy, 2, GridBagConstraints.CENTER, buttonHistogramSet);
		gridBagAdd(area, c, 0, ++c.gridy, 2, GridBagConstraints.CENTER, buttonHistogramDefault);
		
		gridBagSeparator(area, c, 0, ++c.gridy, 2);
		
		// Init area params
		
		for (int i = 0; i < labelsAreaParams.length; i++)
		{
			buttonsAreaParams[i] = new JToggleButton(labelsAreaParams[i]);
			buttonsAreaParams[i].addActionListener(myActionListener);
			buttonsAreaParams[i].setMargin(new Insets(2,0,2,0));
			buttonsAreaParams[i].setBackground(Plotter.colors[i+3]);
			buttonsAreaParams[i].setForeground(Color.black);
		}
		
		// Add area params
		
		c.fill = GridBagConstraints.HORIZONTAL;
		int starty = ++c.gridy;
		int startx = 0;
		int dy = 0;
						
		for (int i = AR_SRED; i < buttonsAreaParams.length; i++)
		{
			gridBagAdd(area, c, startx, starty + ++dy, 1, GridBagConstraints.CENTER, buttonsAreaParams[i]);
			if (i == AR_LINBLUE)
			{
				startx = 1;
				dy = 0;
			}
		}
		
		// Add log button
		
		c.gridy = starty + AR_LINBLUE + 3;	
		c.insets = new Insets(2,4,2,4);		
		
		gridBagSeparator(area, c, 0, ++c.gridy, 4);	
		c.fill = GridBagConstraints.NONE;
		
		buttonLogArea = new JButton("Log Histograms");
		buttonLogArea.addActionListener(myActionListener);
		
		gridBagAdd(area, c, 0, ++c.gridy, 4, GridBagConstraints.CENTER, buttonLogArea);
		
		return area;
	}
	
	public void clearTable()
	{
		DefaultTableModel dtm = new DefaultTableModel(tableRows, tableCols);
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
			double[] resultsXYZ = Calc.getPrimaryWavelengthFitXYZ(XYZ);
			double[] resultsxy = Calc.getPrimaryWavelengthFitxy(XYZ);
			double[] resultsRGB = Calc.getPrimaryWavelengthInverseTrunc(rgb);
			double[] resultsSatExtrap = Calc.getPrimaryWavelengthSatExtrap(XYZ);
			
			displayColor(color, rgb, XYZ, resultsXYZ, resultsxy, resultsRGB, resultsSatExtrap);
			
			if (isLogging)
			{
				tableSet("" + rgb[0], 0);
				tableSet("" + rgb[1], 1);
				tableSet("" + rgb[2], 2);
				tableSet("" + XYZ[0], 3);
				tableSet("" + XYZ[1], 4);
				tableSet("" + XYZ[2], 5);
				tableSet("" + (int)resultsXYZ[0], 6);
				tableSet("" + (int)resultsxy[0], 7);
				tableSet("" + (int)resultsRGB[0], 8);
				tableSet("" + (int)resultsSatExtrap[0], 9);
				tableIncrement();
			}
		}	
		else if (mode == PROFILER)
		{
			// Ensure that the first point is to the left of the right point
			if (ip.vertices[0].x > ip.vertices[1].x)
			{
				Point temp = new Point(ip.vertices[0]);
				ip.vertices[0].setLocation(ip.vertices[1]);
				ip.vertices[1].setLocation(temp);
			}
			plotter.setEndPoints(ip.vertices[0], ip.vertices[1], true);			
			displayProfiler(ip.vertices[0], ip.vertices[1]);	
			plotter.repaint();
		}
		else if (mode == AREA)
		{	
			plotter.sampleArea(ip.getAreaSelection());
			plotter.repaint();
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
			Dimension size = ip.getSize();
			setZoom(new Point(size.width/2, size.height/2), Double.parseDouble(value) / 100);
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
			setTableRowCount(size);
		}
		catch(Exception e)
		{			
		}
		refreshLeftPanel();		
	}
	
	public void inputProfiler()
	{
		try
		{
			if (mode == PROFILER)
			{				
				int x1 = ((Integer)spinnersProfiler[PF_X1].getValue()).intValue();
				int y1 = ((Integer)spinnersProfiler[PF_Y1].getValue()).intValue();
				int x2 = ((Integer)spinnersProfiler[PF_X2].getValue()).intValue();
				int y2 = ((Integer)spinnersProfiler[PF_Y2].getValue()).intValue();
																
				ip.vertices[0] = new Point(x1, y1);
				ip.vertices[1] = new Point(x2, y2);							
				
				ip.vertexIndex = 2;
				
				double pixelsPerSample = Double.parseDouble(fieldPixelsPerSample.getText());
				plotter.pixelsPerSample = pixelsPerSample;
				
				plotter.setEndPoints(ip.vertices[0], ip.vertices[1], true);
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
		refresh();
	}
	
	public void inputHistogramSteps()
	{
		try
		{
			if (mode == AREA)
			{
				double srgb_step = Double.parseDouble(fieldsArea[AR_SRGB_STEP].getText());
				double linrgb_step = Double.parseDouble(fieldsArea[AR_LINRGB_STEP].getText());
				double xyz_step = Double.parseDouble(fieldsArea[AR_XYZ_STEP].getText());
				double lam_step = Double.parseDouble(fieldsArea[AR_LAMBDA_STEP].getText());
				
				plotter.histogramStep[AR_SRGB_STEP] = srgb_step;
				plotter.histogramStep[AR_LINRGB_STEP] = linrgb_step;
				plotter.histogramStep[AR_XYZ_STEP] = xyz_step;
				plotter.histogramStep[AR_LAMBDA_STEP] = lam_step;
				
				plotter.sampleArea(ip.getAreaSelection());
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
		}
		refresh();
	}
	
	public void setTableRowCount(int rows)
	{
		if (rows > 0)
		{
			tableRows = rows;
			DefaultTableModel dtm = (DefaultTableModel)table.getModel();
			dtm.setRowCount(rows);
			table.setModel(dtm);
		}
	}
	
	public void setTableColCount(int cols)
	{
		if (cols >= 0)
		{
			tableCols = cols;
			DefaultTableModel dtm = (DefaultTableModel)table.getModel();
			dtm.setColumnCount(cols);
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
		if (index >= 0 && index < tableRows)
			tableIndex = index;
	}
	
	public void logMetadata()
	{
		try
		{
			if (ip.mm.model == null)
				throw new Exception();
			
			tableSet("" + ip.mm.model, 0);
			tableSet("" + ip.mm.make, 1);
			tableIncrement();
			
			tableSet("Width", 0);
			tableSet("" + ip.mm.size.width, 1);
			tableIncrement();
			
			tableSet("Height", 0);
			tableSet("" + ip.mm.size.height, 1);
			tableIncrement();
			
			tableSet("Date", 0);
			tableSet("" + Calc.date.format(ip.mm.date), 1);
			tableSet("" + Calc.time.format(ip.mm.date), 2);
			tableIncrement();
			
			tableSet("Shutter(s)", 0);
			tableSet("" + ip.mm.exposure, 1);
			tableIncrement();
			
			tableSet("f/D", 0);
			tableSet("" + ip.mm.fnumber, 1);
			tableIncrement();
			
			tableSet("Focus", 0);
			tableSet("" + ip.mm.focus, 1);
			tableIncrement();
			
			tableSet("ISO", 0);
			tableSet("" + ip.mm.iso, 1);
			tableIncrement();
			
			tableSet("Bias(EV)", 0);
			tableSet("" + ip.mm.exposureBias, 1);
			tableIncrement();
			
			tableSet("White Balance", 0);
			tableSet("" + ip.mm.wb_red, 1);
			tableSet("" + ip.mm.wb_green, 2);
			tableSet("" + ip.mm.wb_red, 3);
			tableIncrement();
		}
		catch (Exception e)
		{				
		}
	}
	
	public void logName()
	{
		try
		{
			tableSet(ip.mm.file.getName(), 0);
			tableIncrement();
		}
		catch (Exception e)
		{				
		}
	}
	
	public void log_profile_endpoints()
	{
		try
		{
			if (mode == PROFILER)
			{
				tableSet("x1", 0);
				tableSet("" + plotter.p1.x, 1);
				tableIncrement();
				
				tableSet("y1", 0);
				tableSet("" + plotter.p1.y, 1);
				tableIncrement();
				
				tableSet("x2", 0);
				tableSet("" + plotter.p2.x, 1);
				tableIncrement();
				
				tableSet("y2", 0);
				tableSet("" + plotter.p2.y, 1);
				tableIncrement();
			}
		}
		catch (Exception e)
		{				
		}
	}
	
	public void logHistograms()
	{
		try
		{		
			// Edit lock
			plotter.editLock = true;
			
			// Count required columns			
			int counter = labelsAreaParams.length;
			
			// Increase table size if necessary			
			if (counter + 3 > tableCols)
				setTableColCount(counter + 3);
			
			// Supplementary data
			
			tableSet("n", 0);
			tableSet("" + plotter.getAreaPointCount(), 1);
			tableIncrement();
			
			double[] XYZ = plotter.getSummedXYZ();
			
			tableSet("sum X", 0);
			tableSet("" + XYZ[0], 1);
			tableIncrement();
			
			tableSet("sum Y", 0);
			tableSet("" + XYZ[1], 1);
			tableIncrement();
			
			tableSet("sum Z", 0);
			tableSet("" + XYZ[2], 1);
			tableIncrement();
			
			// Set Headers
			
			int col = 0;
			
			tableSet("Magnitude", col++);
			
			for (int i = AR_SRED; i <= AR_LINBLUE; i++)
				tableSet(labelsAreaParams[i], col++);
			
			tableSet("Magnitude", col++);
			
			for (int i = AR_X; i <= AR_Z; i++)
				tableSet(labelsAreaParams[i], col++);
			
			tableSet("Magnitude", col++);
			
			for (int i = AR_LAM_XYZ; i <= AR_LAM_SAT_EXTRAP; i++)
				tableSet(labelsAreaParams[i], col++);
			
			tableIncrement();
			
			// Iterate through data
			
			int startIndex = tableIndex;
			
			// RGB data
			
			int n = plotter.area_data[AR_SRED].length;
								
			for (int i = 0; i < n; i++)
			{
				tableSet("" + plotter.getHistogramValue(AR_SRED, i), 0); // histogram entries
				tableIncrement();
			}
			
			tableIndex = startIndex;
			for (int i = 0; i < n; i++)
			{
				for (int j = AR_SRED; j <= AR_LINBLUE; j++)				
					tableSet("" + plotter.area_data[j][i], j+1);
				tableIncrement();
			}
				
			
			// XYZ data
			
			n = plotter.area_data[AR_X].length;
			
			tableIndex = startIndex;
			for (int i = 0; i < n; i++)
			{
				tableSet("" + plotter.getHistogramValue(AR_X, i), AR_X+1); // histogram entries
				tableIncrement();
			}
			
			tableIndex = startIndex;
			for (int i = 0; i < n; i++)
			{
				for (int j = AR_X; j <= AR_Z; j++)			
					tableSet("" + plotter.area_data[j][i], j+2);
				tableIncrement();
			}
			
			// Lambda data
			
			n = plotter.area_data[AR_LAM_XYZ].length;
			
			tableIndex = startIndex;
			for (int i = 0; i < n; i++)
			{
				tableSet("" + plotter.getHistogramValue(AR_LAM_XYZ, i), AR_LAM_XYZ+2); // histogram entries
				tableIncrement();
			}
			
			tableIndex = startIndex;
			for (int i = 0; i < n; i++)
			{
				for (int j = AR_LAM_XYZ; j <= AR_LAM_SAT_EXTRAP; j++)			
					tableSet("" + plotter.area_data[j][i], j+3);
				tableIncrement();
			}
			
			// Release edit lock
			plotter.editLock = false;
		}
		catch (Exception e)
		{				
		}
	}
	
	public void logProfiles()
	{
		try
		{		
			// Edit lock
			plotter.editLock = true;						
			
			// We need x-coordinates, y-coordinates, and t-coordinates too.			
			plotter.profilePlotEnabled[PF_X_COORD] = true;
			plotter.profilePlotEnabled[PF_Y_COORD] = true;
			plotter.profilePlotEnabled[PF_T_COORD] = true;
			
			// Count required columns			
			int counter = 0;
			
			for (int i = 0; i < labelsProfilerParams.length; i++)
				if (plotter.profilePlotEnabled[i])
					counter++;			
			
			// Store all sampled parameters in table columns
			System.out.println("Count = " + counter);
			
			// Increase table size if necessary			
			if (counter > tableCols)
				setTableColCount(counter);
			
			// Store indices of sampled params			
			int params[] = new int[counter];
			counter = 0;
			
			for (int i = 0; i < labelsProfilerParams.length; i++)
				if (plotter.profilePlotEnabled[i])
					params[counter++] = i;
			
			// Set Headers
			
			for (int i = 0; i < params.length; i++)
				tableSet(labelsProfilerParams[params[i]], i);
			tableIncrement();
			
			// Iterate through data
			
			System.out.println("n = " + plotter.profile_data[0].length);
			
			for (int i = 0; i < plotter.profile_data[0].length; i++)
			{
				tableSet("" + plotter.profile_data[0][i], 0);
				
				for (int j = 1; j < params.length; j++)
					tableSet("" + plotter.profile_data[params[j]][i], j);
				
				tableIncrement();				
			}	
			
			// Release edit lock
			plotter.editLock = false;
		}
		catch (Exception e)
		{				
		}
	}
	
	public void log_cmf_XYZ()
	{
		tableSet("lambda(nm)", 0);
		tableSet("X", 1);
		tableSet("Y", 2);
		tableSet("Z", 3);
		tableIncrement();
		
		for (int i = 0; i < Calc.cmf_XYZ.length; i++)
		{
			tableSet("" + Calc.indexToNM(i), 0);
			tableSet("" + Calc.cmf_XYZ[i][0], 1);
			tableSet("" + Calc.cmf_XYZ[i][1], 2);
			tableSet("" + Calc.cmf_XYZ[i][2], 3);
			tableIncrement();
		}
	}
	
	public void log_cmf_xyY()
	{
		tableSet("lambda(nm)", 0);
		tableSet("x", 1);
		tableSet("y", 2);
		tableSet("Y", 3);
		tableIncrement();
		
		for (int i = 0; i < Calc.cmf_xy.length; i++)
		{
			tableSet("" + Calc.indexToNM(i), 0);
			tableSet("" + Calc.cmf_xy[i][0], 1);
			tableSet("" + Calc.cmf_xy[i][1], 2);
			tableSet("" + Calc.cmf_XYZ[i][1], 3);
			tableIncrement();
		}	
	}
	
	public void log_cmf_RGB_lin()
	{
		tableSet("lambda(nm)", 0);
		tableSet("R_lin", 1);
		tableSet("G_lin", 2);
		tableSet("B_lin", 3);
		tableIncrement();
		
		for (int i = 0; i < Calc.cmf_rgb_lin.length; i++)
		{
			tableSet("" + Calc.indexToNM(i), 0);
			tableSet("" + Calc.cmf_rgb_lin[i][0], 1);
			tableSet("" + Calc.cmf_rgb_lin[i][1], 2);
			tableSet("" + Calc.cmf_rgb_lin[i][2], 3);
			tableIncrement();
		}		
	}
	
	public void log_cmf_sRGB()
	{
		tableSet("lambda(nm)", 0);
		tableSet("sR", 1);
		tableSet("sG", 2);
		tableSet("sB", 3);
		tableIncrement();
		
		for (int i = 0; i < Calc.cmf_rgb_lin.length; i++)
		{
			double[] srgb = Calc.sRGBgamma(Calc.cmf_rgb_lin[i]);
			tableSet("" + Calc.indexToNM(i), 0);
			tableSet("" + srgb[0], 1);
			tableSet("" + srgb[1], 2);
			tableSet("" + srgb[2], 3);
			tableIncrement();
		}	
	}	
	public void log_pixels_per_length()
	{
		tableSet("pixels/length", 0);
		tableSet("" + ip.pixelsPerMM, 1);
		tableIncrement();
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
				this.path = path;
				setTitle(title + " - " + path);				
				refresh();
				if (mode != AREA)
					plotter.refresh();
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
		fd.setFile(logger.path.getName());
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
	
	public void setSelectAllProfiles(boolean selectAll)
	{
		plotter.editLock = true;
		for (int i = PF_SRED; i < labelsProfilerParams.length; i++)
		{
			buttonsProfilerParams[i].setSelected(selectAll);
			plotter.setProfilePlotEnabled(i, selectAll);
			System.out.println(labelsProfilerParams[i] + " - " + plotter.profilePlotEnabled[i]);
		}
		plotter.editLock = false;
		
		refresh();
	}
	
	/** NONE = 0;<br>
	 * ANGLE = 1;<br>
 	 * PRIMER = 2;<br>
	 * RULER = 3;<br>
	 * COLOR = 4;<br>
	 * PROFILER = 5;<br>
	 * AREA = 5;<br>
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
			
			// Hide profiler if necessary
			
			if (newMode == PROFILER || newMode == AREA)
				plotter.hide();
		}
		else // Selecting a new mode
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			mode = newMode;
			
			// Show profiler if necessary
			
			if (mode == PROFILER  || newMode == AREA)
				plotter.show();
			
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
	
	public String getStatus()
	{
		return statusBar.getText();
	}
		
	/** @param text	the string to be displayed in the status bar
	 */
	public void writeStatus(String text)
	{
		statusBar.setText(text);
	}
	
	public void resetTitle()
	{
		setTitle(title + " - " + path);
	}
	
	/** Adds the specified value to the next cell. Doubles the
	 * size of the table if tableIndex exceeds the length of the table.
	 */
	public void tableAppend(double value)
	{
		table.getModel().setValueAt(value + "", tableIndex, 0);
		tableIndex++;
		if (tableIndex >= tableRows)
			setTableRowCount(2 * tableRows);
		refreshLeftPanel();
	}
	
	/** Sets the value at the specified column in the table,
	 * at the current row.
	 */
	public void tableSet(String str, int col)
	{
		table.getModel().setValueAt(str, tableIndex, col);
	}
	
	/** Advances the tableIndex to the next row. Doubles the 
	 * size of the table if tableIndex exceeds the length
	 * of the table.
	 */
	public void tableIncrement()
	{		
		tableIndex++;
		if (tableIndex >= tableRows)
			setTableRowCount(2 * tableRows);
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
	
	public void displayArea()
	{
		for (int i = AR_SRGB_STEP; i <= AR_LAMBDA_STEP; i++)
			fieldsArea[i].setText(Calc.precise12.format(plotter.histogramStep[i]));
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
	
	public void displayColor(Color color, int[] rgb, double[] XYZ, double[] resultsXYZ, double[] resultsxy, double[] resultsRGB, double[] resultsSatExtrap)
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
    	    	
    	fieldLambdaFitXYZ[0].setText(Calc.whole.format(resultsXYZ[0]));    	
    	for (int i = 1; i < 4; i++)
    	{
    		fieldLambdaFitXYZ[i].setText(Calc.precise8.format(resultsXYZ[i]));
    		fieldLambdaFitXYZ[i].setCaretPosition(0);
    	}  
    	
    	// Get wavelength from xy fitting method
    	    	
    	fieldLambdaFitxy[0].setText(Calc.whole.format(resultsxy[0]));
    	fieldLambdaFitxy[1].setText(Calc.precise8.format(resultsxy[1]));
    	
    	// Get wavelength from inverse truncation method
    	    	
    	fieldLambdaTrunc[0].setText(Calc.whole.format(resultsRGB[0]));
    	fieldLambdaTrunc[1].setText(Calc.precise8.format(resultsRGB[1]));
    	
    	// Get wavelength from saturation extrapolation method
    	
    	fieldLambdaSatExtrap[0].setText(Calc.whole.format(resultsSatExtrap[0]));
    	fieldLambdaSatExtrap[1].setText(Calc.precise8.format(resultsSatExtrap[1]));
	}
	
	public void displayProfiler(Point p1, Point p2)
	{
		plotter.editLock = true;
		spinnersProfiler[PF_X1].setValue(p1.x);
		spinnersProfiler[PF_Y1].setValue(p1.y);
		spinnersProfiler[PF_X2].setValue(p2.x);
		spinnersProfiler[PF_Y2].setValue(p2.y);
		plotter.editLock = false;
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
		else if (mode == PROFILER)
		{						
			displayProfiler(plotter.p1, plotter.p2);
			fieldPixelsPerSample.setText(Calc.precise12.format(plotter.pixelsPerSample));
			if (ip.vertexIndex == 0)
			{
				ip.vertexIndex = 2;
				ip.vertices[0] = plotter.p1;
				ip.vertices[1] = plotter.p2;								
			}
		}
		else if (mode == AREA)
			displayArea();
	}
	
	public void refreshLeftPanel()
	{
		fieldTableIndex.setText("" + tableIndex);
		fieldTableSize.setText("" + tableRows);
	}
	
	private class MyPropertyChangeListener implements PropertyChangeListener
	{
		@Override
		public void propertyChange(PropertyChangeEvent e) 
		{
			JSplitPane pane = (JSplitPane)e.getSource();
			if (pane.equals(content))
				{
				int size = pane.getDividerLocation();
				if (size < 175)
					pane.setDividerLocation(175);
				tableWrapper.setPreferredSize(new Dimension(size - 5, 1));
				tableWrapper.revalidate();
			}
			else if (pane.equals(paneCenter))
			{
				
			}
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
				else if (button.getText().equals("Profiler"))
				{
					writeStatus("Select the line along which to profile.");
					setMeasuringMode(PROFILER);
				}
				else if (button.getText().equals("Area"))
				{
					writeStatus("Select an area. Drag mouse for curved edges. Click mouse for straight edges. Double click to end selection.");
					setMeasuringMode(AREA);
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
				else if (button.equals(buttonSampleRate))
				{
					inputProfiler();
				}
				else if (button.equals(buttonLogProfiles))
				{
					logProfiles();
				}
				else if (button.equals(buttonLogArea))
				{
					logHistograms();
				}
				else if (button.equals(buttonHistogramSet))
				{
					inputHistogramSteps();
				}
				else if (button.equals(buttonHistogramDefault))
				{
					plotter.setDefaultHistogramStep();
					refresh();
				}
			}
			else if (parent instanceof JRadioButton)
			{
				JRadioButton button = (JRadioButton)parent;
				if (button.equals(buttonsProfilerOptions[PF_EXTRAPOLATE]))
					plotter.extrapolate = button.isSelected();
				else if (button.equals(buttonsProfilerOptions[PF_SLOPE_LOCK]))
					plotter.slopeLock = button.isSelected();
				else if (button.equals(buttonsProfilerOptions[PF_SELECT_ALL]))
					setSelectAllProfiles(button.isSelected());
			}
			else if (parent instanceof JToggleButton)
			{
				JToggleButton button = (JToggleButton)parent;
				if (button.equals(buttonLog))
					isLogging = buttonLog.isSelected();
				else if (button.equals(buttonAreaSelecting))
				{
					if (!plotter.editLock)
					{
						if (!button.isSelected())
							ip.areaSelectionFinalize(); // Finalize area selection if this is unselected
						else
						{
							plotter.editLock = true; 	// Force button unselected; can't start until you click
							button.setSelected(false);
							plotter.editLock = false;
						}
					}
				}				
				else
				{
					if (!plotter.editLock)
					{
						for (int i = 0; i < labelsProfilerParams.length; i++)
						{
							if (button.equals(buttonsProfilerParams[i]))
							{							
								plotter.setProfilePlotEnabled(i, button.isSelected());
								refresh();
							}
						}
						for (int i = 0; i < labelsAreaParams.length; i++)
						{
							if (button.equals(buttonsAreaParams[i]))
							{							
								plotter.setAreaPlotEnabled(i, button.isSelected());
								refresh();
							}
						}
					}
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
	
	private class MyChangeListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e) 
		{
			Object parent = e.getSource ();

			if (parent instanceof JSpinner)
			{
				JSpinner spinner = (JSpinner)parent;
				
				if (plotter.editLock == false)
				{
					plotter.editLock = true;
					
					if (plotter.slopeLock)
					{						
						// Find previous slope
						
						int deltax = plotter.getDeltaX();
						int deltay = plotter.getDeltaY();
						
						// Maintain delta x if one field is changed
						
						if (spinner.equals(spinnersProfiler[PF_X1]))
						{
							int x1 = ((Integer)spinnersProfiler[PF_X1].getValue()).intValue();
							spinnersProfiler[PF_X2].setValue(x1 + deltax);
						}
						else if (spinner.equals(spinnersProfiler[PF_Y1]))
						{
							int y1 = ((Integer)spinnersProfiler[PF_Y1].getValue()).intValue();
							spinnersProfiler[PF_Y2].setValue(y1 + deltay);
						}
						else if (spinner.equals(spinnersProfiler[PF_X2]))
						{
							int x2 = ((Integer)spinnersProfiler[PF_X2].getValue()).intValue();
							spinnersProfiler[PF_X1].setValue(x2 - deltax);
						}
						else if (spinner.equals(spinnersProfiler[PF_Y2]))
						{
							int y2 = ((Integer)spinnersProfiler[PF_Y2].getValue()).intValue();
							spinnersProfiler[PF_Y1].setValue(y2 - deltay);
						}		
					}
									
					// Update values	
												
					inputProfiler();
					plotter.editLock = false;
				}
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
			ip.mouseClick(e);
		}

		@Override
		public void mouseDragged (MouseEvent e)
		{	
			ip.mouseDragged(e);
			repaint();			
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			ip.mouseMoved(e);
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
				{
					int[] rows = table.getSelectedRows();
					for (int i = 0; i < rows.length; i++)
						clearRow(rows[i]);
				}
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
				else if (name.equals("Log image metadata"))
				{
					logMetadata();
				}
				else if (name.equals("Log image name"))
				{
					logName();
				}
				else if (name.equals("Log profile endpoints"))
				{
					log_profile_endpoints();
				}
				else if (name.equals("Log all profiles"))
				{
					if (mode == PROFILER)
					{
						buttonsProfilerOptions[PF_SELECT_ALL].setSelected(true);
						setSelectAllProfiles(true);
						logProfiles();
					}
				}
				else if (name.equals("Log pixels per length"))
				{
					log_pixels_per_length();
				}
				else if (name.equals("Log spectrum XYZ"))
				{
					log_cmf_XYZ();
				}
				else if (name.equals("Log spectrum xyY"))
				{
					log_cmf_xyY();
				}
				else if (name.equals("Log spectrum RGB_lin"))
				{
					log_cmf_RGB_lin();
				}
				else if (name.equals("Log spectrum sRGB"))
				{
					log_cmf_sRGB();
				}				
				else if (name.equals("Jump to Origin"))
				{
					ip.setOffset(new Point(0, 0));
					repaint();
				}
				else if (name.equals("Reset Zoom"))
				{
					setZoom("100");
				}
				else if (name.equals("Formatting"))
				{
					String str = "Angle[1]: {angle in degrees}\n";
					str += "Length[1]: {length}\n";
					str += "Color[10]: {R, G, B, X, Y, Z, \u03BB (XYZ fit), \u03BB (xy fit), \u03BB (RGB fit), \u03BB (SatExrap fit)}\n";
					str += "Spectrum XYZ [472][4]: {wavelength(nm), X, Y, Z}\n";
					str += "Spectrum xyY [472][4]: {wavelength(nm), x, y, Y}\n";
					str += "Spectrum sRGB[472][4]: {wavelength(nm), R[0..1], G[0..1], B[0..1]}\n";
					str += "Spectrum RGB_lin[472][4]: {wavelength(nm), R[0..1], G[0..1], B[0..1]}\n";
					JOptionPane.showMessageDialog(GUI.this, str, "Data Format", JOptionPane.PLAIN_MESSAGE);			
				}
				else if (name.equals("Controls"))
				{
					String str = "Shift + drag: move image\n";
					str += "Scroll: zoom\n";
					str += "Delete: clear selected rows\n";
							
					JOptionPane.showMessageDialog(GUI.this, str, "Shortcut Keys", JOptionPane.PLAIN_MESSAGE);			
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
	
	public static Color getXORColor(Color color)
	{
		return new Color(xorBack^xorFront^color.getRGB());
	}
}