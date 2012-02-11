package laser;

public class Line2D {

	private double[][] coordinates;
	private boolean laserOn;
	
	public Line2D (double[][] Coordinates, boolean LaserOn) {
		
		this.coordinates = Coordinates.clone();
		this.laserOn = LaserOn;
	}
	
	public double[] getStart() {
		return coordinates[0];
	}
	
	public double[] getEnd() {
		return coordinates[1];
	}
	
	public boolean getLaserSetting() {
		return laserOn;
	}
}
