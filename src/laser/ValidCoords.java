package laser;

public class ValidCoords {

	private double xVal;
	private double yVal;
	private boolean validPoint;
	
	public ValidCoords (double x, double y, boolean valid) {
		 
		this.xVal = x;
		this.yVal = y;
		this.validPoint = valid;
	}
	
	public double getXValue() {
		return xVal;
	}
	
	public double getYValue() {
		return yVal;
	}
	
	public boolean getPointValidity() {
		return validPoint;
	}
}
