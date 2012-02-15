package laser;

import javax.vecmath.*;
import java.util.ArrayList;

public class Slicer {
	
	private Matrix3d[] object;
	
	private double xMax;
	private double xMin;
	private double yMax;
	private double yMin;
	private double tempXMax;
	private double tempXMin;
	private double tempYMax;
	private double tempYMin;
	private double resolution; // Distance between points in millimeters
	
	private Point2d a;
	private Point2d b;
	private Point2d c;
	private Point2d current;
	
	// I don't know why ArrayList won't take a primitive int type
	private ArrayList<ValidCoords> points;
	private ArrayList<Line2D> lines;
	
	public Slicer (Matrix3d[] obj, double newRes) {
		
		resolution = newRes;
		points = new ArrayList<ValidCoords>();
		xMax = -1000;
		yMax = -1000;
		xMin = 1000; // Magic number (honestly, if you're trying to print a 1 meter wide object you're doing it wrong)
		yMin = 1000; // Magic number
		
		object = obj;
		
		findLimits();
		
		createListOfPoints();
		
		convertPointsToLines();
	}
	
	/**
	 * Determine the maximum and minimum X and Y values for the
	 * whole STL object.
	 */
	private void findLimits() {
		
		// Find the top and bottom of the object
		for (int i = 0; i < object.length; i++) {
			
			// Find the greatest Y value in the object
			tempYMax = Math.max(object[i].m01, object[i].m11);
			tempYMax = Math.max(object[i].m21, tempYMax);
			
			// Find the lowest Y value in the object
			tempYMin = Math.min(object[i].m01, object[i].m11);
			tempYMin = Math.min(object[i].m21, tempYMin);
			
			if (tempYMax > yMax) { yMax = tempYMax; }
			if (tempYMin < yMin) { yMin = tempYMin; }
			
			//Find the greatest X value in the object
			tempXMax = Math.max(object[i].m00, object[i].m10);
			tempXMax = Math.max(object[i].m20, tempXMax);
			
			//Find the lowest X value in the object
			tempXMin = Math.max(object[i].m00, object[i].m10);
			tempXMin = Math.max(object[i].m20, tempXMin);
			
			if (tempXMax > xMax) { xMax = tempXMax; }
			if (tempXMin < xMin) { xMin = tempXMin; }
			
		}
		
		// Add a bit to the end in case the object has a straight line from top to bottom
		xMax = xMax + resolution;
	}
	
	/**
	 * Convert the STL object from a list of 3x3 Matrices to a list
	 * of 2D points spaced every 0.5mm. Also attach a boolean to the
	 * point to determine whether it is a gap or a valid point.
	 */
	private void createListOfPoints() {
		
		boolean validPoint = true;
		
		// Yes it's nasty, it's a rough draft
		for (double i = yMax; i >= yMin; i = i - resolution) {
			for (double j = xMin; j <= xMax; j = j + resolution) {
				for (int k = 0; k < object.length; k++) {
					
					// Determine XY lengths for normalizing
					double xLength = xMax - xMin;
					double yLength = yMax - yMin;
					validPoint = true;
					
					a = new Point2d(object[k].m00/xLength, object[k].m01/yLength);
					b = new Point2d(object[k].m10/xLength, object[k].m11/yLength);
					c = new Point2d(object[k].m20/xLength, object[k].m21/yLength);
					current = new Point2d(j/xLength, i/yLength);
					
					// Algorithm obtained from http://www.blackpawn.com/texts/pointinpoly/default.html
					// for the Barycentric Technique
					current.sub(a);
					c.sub(a);
					b.sub(a);
					
					//u = ((b.b)(cur.c)-(b.c)(cur.b)) / ((c.c)(b.b) - (c.b)(b.c))
					double u = (((b.x*b.x + b.y*b.y)*(current.x*c.x + current.y*c.y)
									-(b.x*c.x + b.y*c.y)*(current.x*b.x + current.y*b.y))
								/((c.x*c.x + c.y*c.y)*(b.x*b.x + b.y*b.y)
									-(c.x*b.x + c.y*b.y)*(b.x*c.x + b.y*c.y)));
					
					//v = ((c.c)(cur.b)-(c.b)(cur.c)) / ((c.c)(b.b) - (c.b)(b.c))
					double v = (((c.x*c.x + c.y*c.y)*(current.x*b.x + current.y*b.y)
									-(c.x*b.x + c.y*b.y)*(current.x*c.x + current.y*c.y))
								/((c.x*c.x + c.y*c.y)*(b.x*b.x + b.y*b.y)
									-(c.x*b.x + c.y*b.y)*(b.x*c.x + b.y*c.y)));
					
					//Notice now that if u or v < 0 then we've walked in the wrong direction 
					//and must be outside the triangle. Also if u or v > 1 then we've walked 
					//too far in a direction and are outside the triangle. Finally if u + v > 1 
					//then we've crossed the edge BC again leaving the triangle.
					if (v > 0 && u > 0 && v < 1 && u < 1 && (u + v) < 1) {
						validPoint = false;
						break;
					}
				}
				
				//double xCoord = j-xMin;
				//double yCoord = i-yMin;
				points.add(new ValidCoords(j,i,validPoint));
			}
		}
	}
	
	/**
	 * Convert the new list of points into a list of 2D lines. Because
	 * the gcode used in the RepRap is all linear interpolation we can
	 * use these to easily create organised and understandable gcode.
	 */
	private void convertPointsToLines() {
		
		ValidCoords startPoint = points.get(0);
		double[][] coordinates;
		boolean laserSetting = startPoint.getPointValidity();
		ValidCoords currentPoint;
		
		lines = new ArrayList<Line2D>();
		
		// Creates a list of 2D lines for the gcode to interpolate
		for (int i = 1; i < points.size(); i++) {
			
			coordinates = new double[2][2];
			currentPoint = points.get(i);
			
			if (currentPoint.getPointValidity() != laserSetting) {
				
				coordinates[0][0] = startPoint.getXValue();
				coordinates[0][1] = startPoint.getYValue();
				coordinates[1][0] = (currentPoint.getXValue() - resolution);
				coordinates[1][1] = currentPoint.getYValue();
				lines.add(new Line2D(coordinates, laserSetting));
				
				laserSetting = !laserSetting;
				startPoint = points.get(i);
			}
		}
	}
	
	public ArrayList<Line2D> getListOfLines() {
		return lines;
	}
}
