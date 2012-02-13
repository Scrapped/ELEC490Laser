package laser;
/*
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.File;
import java.util.ArrayList;
import javax.vecmath.*;

import org.j3d.loaders.stl.*;

public class Main {

	private static String address;
	private static boolean working;
	private static double[] normal;
	private static double[][] facet;
	private static Matrix3d[] stlTriangles;
	
	public static void main (String args[]){
		
		if (args.length == 0) {
			address = "/home/bryce/RepRap/Minimug.stl";
		}
		else {
			address = args[0];
		}
		
		normal = new double[3];
		facet = new double[3][3];
		
		try {
			// Read in the .stl file
			STLFileReader stlfr = new STLFileReader(new File(address));
			
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
			
			Slicer slicer = new Slicer(stlTriangles);
			
			ArrayList<Line2D> slicedData = slicer.getListOfLines();
			
			GCodeBuilder builder = new GCodeBuilder(slicedData);
			
			ArrayList<String> gcode = builder.getGCode();
			
			//GCodeFileWriter gcfw = new GCodeFileWriter(gcode);
			
			System.out.println(working);
		} catch (InterruptedIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
*/
