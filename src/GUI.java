import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
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
	public JComboBox zoomCtrl;
	public JTable table;
	
	public int tableIndex = 0;
		
	public boolean movingMode = false;	
	public double zoomStep = 0.1;	
	
	private KeyboardFocusManager manager;
	private MyDispatcher keyDispatcher;

	public GUI()
	{
		super("Photo Measurer");
		
		// Setting content panes and panels

		content = new JPanel(new BorderLayout());
		controls = new JPanel(new BorderLayout());
		stats = new JPanel();
		data = new JPanel();
		ip = new ImagePanel(this);				

		content.add(ip, BorderLayout.CENTER);		
		content.add(controls, BorderLayout.EAST);
		content.add(stats, BorderLayout.SOUTH);
		content.add(data, BorderLayout.WEST);
		
		initControlPanel();
		initStatsPanel();
		initImagePanel();
		initDataPanel();

		setContentPane(content);
		revalidate();
		
		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyDispatcher = new MyDispatcher();
		manager.addKeyEventDispatcher(keyDispatcher);
		
		setSize(720, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

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
		zoomCtrl = new JComboBox(patternExamples);
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

	private void initImagePanel()
	{
		ip.addMouseListener (new MyMouseListener());
		ip.addMouseMotionListener (new MyMouseListener());
		ip.addMouseWheelListener (new MyMouseListener());
	}
	
	private void initDataPanel()
	{	
		table = new JTable(tableSize, 1);	
		data.setLayout(new BorderLayout());		
		data.add(table.getTableHeader(), BorderLayout.PAGE_START);
		data.add(table, BorderLayout.CENTER);		
	}
	
	private void setZoom(String value)
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
	
	private void setZoom(Point center, double newZoom)
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
	
	public void updateMove(Point now)
	{
		int dx = now.x - ip.click.x;
		int dy = now.y - ip.click.y;

		ip.click = now;
		ip.translateOffset(dx, dy);
	}
	
	public void writeStatus(String text)
	{
		statusBar.setText(text);
	}
	
	public void tableAppend(double value)
	{
		table.getModel().setValueAt(value + "", tableIndex, 0);
		tableIndex++;
		if (tableIndex >= tableSize)
			tableIndex = 0;	
	}
	
	public void promptPrimerDistance()
	{
		double pixels = Calculator.findDistance(ip.vertices[0], ip.vertices[1]);
		
		String message = "Input the real length of segment (mm): ";
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
			else if (parent instanceof JComboBox)
			{
				JComboBox cb = (JComboBox)parent;
				
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
			
			if (!movingMode)
			{
				if (mode != NONE)
				{
					if (mode == ANGLE)
					{
						if (ip.vertexIndex == 3)
							ip.vertexIndex = 0;
					}
					else if (ip.vertexIndex == 2)
						ip.vertexIndex = 0;
					
					ip.vertices[ip.vertexIndex] = ip.getImageCoordinates(e.getPoint());
					ip.vertexIndex++;
					
					if (mode == ANGLE)
					{
						if (ip.vertexIndex == 3)
						{
							tableAppend(Calculator.findAngle(ip.vertices[0], ip.vertices[1], ip.vertices[2]));
						}
					}
					else
					{
						if (ip.vertexIndex == 2)
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
