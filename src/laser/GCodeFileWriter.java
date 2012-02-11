package laser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GCodeFileWriter {

	public GCodeFileWriter (ArrayList<String> gcode){
		
		try {
			FileWriter file = new FileWriter("Minitest.gcode");
			
			for (int i = 0; i < gcode.size(); i++) {
				file.write(gcode.get(i) + "\n");
			}
			
			file.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
