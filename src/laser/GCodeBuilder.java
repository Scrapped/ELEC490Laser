package laser;

import java.util.ArrayList;

public class GCodeBuilder {
	
	private ArrayList<String> gcode;

	public GCodeBuilder (ArrayList<Line2D> lines) {
		
		gcode = new ArrayList<String>();
		
		setup();
		determineMovement(lines);
	}
	
	private void setup() {
		gcode.add("M110");					//Turn the motors on
		gcode.add("G21");					//Set to millimeters
		gcode.add("G90");					//Set to absolute positioning
		gcode.add("M490 P0");				//Make sure laser is powered but off
		gcode.add("G28");					//Home the axes
		gcode.add("G1 Z50.0 F50.0");		//Move up to Z50mm at 50mm/minute rate
		gcode.add("G1 X0.0 Y0.0 F2500.0");	//Set XY movement rate to 2500mm/minute
	}
	
	private void determineMovement(ArrayList<Line2D> lines) {
		
		// Move to the starting point
		gcode.add("G1 X" + String.valueOf(lines.get(0).getStart()[0]));
		
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).getLaserSetting()){
				// Turn the laser on
				gcode.add("M490 P254");
			}
			else {
				// Turn the laser off
				gcode.add("M490 P0");
			}
			// Linear interpolation
			gcode.add("G1 X" + String.valueOf(lines.get(i).getEnd()[0])
					+ " Y" + String.valueOf(lines.get(i).getEnd()[1]));
		}
		
		gcode.add("M490 P0");
		gcode.add("M0");
	}
	
	public ArrayList<String> getGCode() {
		return gcode;
	}
}
