package org.usfirst.frc.team291.auto;

/*
 * AutoMode.java
 * Super Class for all of the autos.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIARobot;

public abstract class AutoMode extends CIARobot {
	
	protected boolean blueSide;// Used to invert the path. Paths are made for blue and inverted for red
	
	public AutoMode(boolean blueSide){
		this.blueSide = blueSide;
	}
	
	public AutoMode() {
		
	}
	
	public abstract void init();
	
	public abstract void execute();
	
	public abstract void reset();
	
	public abstract void end();
	
	public abstract void outputToSmartDashboard();
	
}
