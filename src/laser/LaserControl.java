package laser;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.vecmath.Matrix3d;

import org.j3d.loaders.stl.STLFileReader;

import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class LaserControl extends JFrame{

	private JFrame frame;
	private final JFileChooser jfc = new JFileChooser();
	private JLabel label_Indicator;
	private JLabel label_FileName;
	private JButton button_LoadFile;
	private JButton button_Gcode;
	private JButton button_SaveFile;
	
	private static boolean working;
	private static boolean fileLoaded;
	private static double[] normal;
	private static double[][] facet;
	private static Matrix3d[] stlTriangles;
	private static ArrayList<String> gcode;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LaserControl window = new LaserControl();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LaserControl() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		fileLoaded = false;
		normal = new double[3];
		facet = new double[3][3];
		gcode = new ArrayList<String>();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{125, 125, 125};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		label_Indicator = new JLabel("File Name:");
		GridBagConstraints gbc_label_Indicator = new GridBagConstraints();
		gbc_label_Indicator.insets = new Insets(40, 0, 20, 5);
		gbc_label_Indicator.gridx = 0;
		gbc_label_Indicator.gridy = 0;
		frame.getContentPane().add(label_Indicator, gbc_label_Indicator);
		
		label_FileName = new JLabel("...");
		GridBagConstraints gbc_label_FileName = new GridBagConstraints();
		gbc_label_FileName.gridwidth = 2;
		gbc_label_FileName.insets = new Insets(40, 0, 20, 5);
		gbc_label_FileName.gridx = 1;
		gbc_label_FileName.gridy = 0;
		frame.getContentPane().add(label_FileName, gbc_label_FileName);
		
		button_LoadFile = new JButton("Load File");
		button_LoadFile.addActionListener(new LoadFileListener());
		GridBagConstraints gbc_button_LoadFile = new GridBagConstraints();
		gbc_button_LoadFile.insets = new Insets(0, 0, 0, 5);
		gbc_button_LoadFile.gridx = 0;
		gbc_button_LoadFile.gridy = 1;
		frame.getContentPane().add(button_LoadFile, gbc_button_LoadFile);
		
		button_Gcode = new JButton("Generate Gcode");
		button_Gcode.addActionListener(new GCodeListener());
		GridBagConstraints gbc_button_Gcode = new GridBagConstraints();
		gbc_button_Gcode.insets = new Insets(0, 0, 0, 5);
		gbc_button_Gcode.gridx = 1;
		gbc_button_Gcode.gridy = 1;
		frame.getContentPane().add(button_Gcode, gbc_button_Gcode);
		
		button_SaveFile = new JButton("Save File");
		button_SaveFile.addActionListener(new SaveFileListener());
		GridBagConstraints gbc_button_SaveFile = new GridBagConstraints();
		gbc_button_SaveFile.gridx = 2;
		gbc_button_SaveFile.gridy = 1;
		frame.getContentPane().add(button_SaveFile, gbc_button_SaveFile);
	}
	
	/**
	 *	Load File Button ActionListener to open up a file, check if it's
	 *	an STL file and then split it into Matrix3d object for better
	 *	management
	 */
	class LoadFileListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			
			// Open the file chooser
			int returnVal = jfc.showOpenDialog(LaserControl.this);

			// If we choose "Open" instead of "Cancel"
	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = jfc.getSelectedFile();
	            
	            String fileName = file.getName();
	            String[] splitfn = fileName.split("\\.");
	            
	            // Make sure we're reading an stl file
	            if (splitfn[splitfn.length - 1].toLowerCase().compareTo("stl") == 0) {
	            	
	            	label_FileName.setText(fileName);
	            	
	            	try {
		            	// Read in the .stl file
						STLFileReader stlfr = new STLFileReader(file);
						
						int numObj = stlfr.getNumOfObjects();
						int[] numFacets = stlfr.getNumOfFacets();
						
						// Break down the 3D object into it's individual triangles
						for (int i = 0; i < numObj; i++) {
							
							// Rewrites it if more than one object. Fix later
							stlTriangles = new Matrix3d[numFacets[i]];
							
							for (int j = 0; j < numFacets[i]; j++) {
								
								working = stlfr.getNextFacet(normal, facet);
								
								if (!working) {
									System.exit(-1);
								}
								
								// Store the data in a matrix and set the Z value for
								// every point to 50mm
								stlTriangles[j] = new Matrix3d(facet[0][0],
										facet[0][1], 50.0, facet[1][0],
										facet[1][1], 50.0, facet[2][0],
										facet[2][1], 50.0);
							}
						}
						
						stlfr.close();
						fileLoaded = true;
						
					} catch (InterruptedIOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        }
		}
	}

	/**
	 * Action Listener for the Generate GCode button. Goes through the
	 * motions of creating gcode from the supplied gcode;
	 */
	class GCodeListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if (fileLoaded) {
				
				Slicer slicer = new Slicer(stlTriangles);
				
				ArrayList<Line2D> slicedData = slicer.getListOfLines();
				
				GCodeBuilder builder = new GCodeBuilder(slicedData);
				
				gcode = builder.getGCode();
			}
		}
	}

	/**
	 * Action Listener for the Save File 
	 */
	class SaveFileListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			try {
				int returnVal = jfc.showSaveDialog(LaserControl.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					
					GCodeFileWriter gcfw = new GCodeFileWriter(gcode, jfc.getSelectedFile());
				}
			} catch (HeadlessException error) {
				// heh...
			}
		}
	}
}
