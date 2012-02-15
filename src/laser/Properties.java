package laser;

public class Properties {

	private static double focalDist;
	private static double xOffset;
	private static double yOffset;
	private static double objHeight;
	private static double resolution;
	
	private static int xySpeed;
	private static int laserPower;
	
	public Properties() {
		
		resolution = 0.5;
		focalDist = 5.0;
		xOffset = 0.0;
		yOffset = 0.0;
		objHeight = 0;
		xySpeed = 3000;
		laserPower = 100;
	}
	
	public double getFocalDistance() { return focalDist; }
	public double getXOffset() { return xOffset; }
	public double getYOffset() { return yOffset; }
	public double getObjHeight() { return objHeight; }
	public double getResolution() {return resolution; }
	public int getXYSpeed() { return xySpeed; }
	public int getLaserPower() { return laserPower; }
	
	public void setFocalDistance(double newDistance) { focalDist = newDistance; }
	public void setXOffset(double newOffset) { xOffset = newOffset; }
	public void setYOffset(double newOffset) { yOffset = newOffset; }
	public void setObjHeight(double newHeight) { objHeight = newHeight; }
	public void setResolution(double newRes) { resolution = newRes; }
	public void setXYSpeed(int newSpeed) { xySpeed = newSpeed; }
	public void setLaserPower(int newPower) {
		
		if (newPower > 100) {
			laserPower = 100;
		}
		else if( newPower < 0) {
			laserPower = 0;
		}
		else {
			laserPower = newPower;
		}
	}
}
