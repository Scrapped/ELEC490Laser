package laser;

public class Line2D {

	private double[][] coordinates;
	private boolean laserOn;
	
	/**
	 * Data structure for holding straight line data
	 * 
	 * @param Coordinates - Square matrix with XY values of start and stop positions
	 * @param LaserOn - Laser power status (on or off)
	 */
	public Line2D (double[][] Coordinates, boolean LaserOn) {
		
		this.coordinates = Coordinates.clone();
		this.laserOn = LaserOn;
	}
	
	// Accessor section
	public double[] getStart() { return coordinates[0]; }
	public double[] getEnd() { return coordinates[1]; }
	public boolean getLaserSetting() { return laserOn; }
}
