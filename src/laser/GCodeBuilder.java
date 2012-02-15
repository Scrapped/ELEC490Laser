package laser;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GCodeBuilder {
	
	private ArrayList<String> gcode;
	private Properties settings;

	public GCodeBuilder (ArrayList<Line2D> lines, Properties data) {
		
		gcode = new ArrayList<String>();
		settings = data;
		
		setup();
		determineMovement(lines);
	}
	
	private void setup() {
		gcode.add("M110");					//Turn the motors on
		gcode.add("G21");					//Set to millimeters
		gcode.add("G90");					//Set to absolute positioning
		gcode.add("M490 P0");				//Make sure laser is powered but off
		gcode.add("G28");					//Home the axes
		gcode.add("G1 Z"
				+ (settings.getFocalDistance()
				+ settings.getObjHeight())
				+ " F50.0");					//Move up to laserHeigher+objHeight at 50mm/minute rate
		gcode.add("G1 X0.0 Y0.0 F" + settings.getXYSpeed() + ".0");	//Set XY movement rate to 2500mm/minute
	}
	
	private void determineMovement(ArrayList<Line2D> lines) {
		
		DecimalFormat deciForm = new DecimalFormat();
		deciForm.setMaximumFractionDigits(3);
		
		double xVal = lines.get(0).getStart()[0] + settings.getXOffset();
		double yVal = lines.get(0).getStart()[1] + settings.getYOffset();
		
		// Move to the starting point
		gcode.add("G1 X" + xVal
				+ " Y" + yVal);
		int laserPower = (int)(settings.getLaserPower() * 254 / 100);
		
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getLaserSetting()){
				// Turn the laser on
				gcode.add("M490 P" + laserPower);
			}
			else {
				// Turn the laser off
				gcode.add("M490 P0");
			}
			// Linear interpolation
			xVal = lines.get(i).getStart()[0] + settings.getXOffset();
			yVal = lines.get(i).getStart()[1] + settings.getYOffset();
			gcode.add("G1 X" + deciForm.format(xVal)
					+ " Y" + deciForm.format(yVal));
		}
		
		gcode.add("M490 P0");
		gcode.add("M0");
	}
	
	public ArrayList<String> getGCode() {
		return gcode;
	}
}
