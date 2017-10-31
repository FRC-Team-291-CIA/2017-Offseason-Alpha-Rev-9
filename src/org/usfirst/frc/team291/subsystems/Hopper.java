package org.usfirst.frc.team291.subsystems;

/*
 * Hopper.java
 * This handles the state and actions of the Hopper Mechanism.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public class Hopper extends Subsystem{
	
	private Spark indexer = new Spark(CIAConstants.hopperIndexer);
	
	private Solenoid expander = new Solenoid(CIAConstants.hopperExpansionPort);
	
	public void index(){
		indexer.set(.9);
	}
	
	public void reverse(){
		indexer.set(-.9);
	}
	
	public void setManual(double power){
		indexer.set(power);
	}
	
	public void expand(boolean expand){
		expander.set(expand);
	}
	

	@Override
	public void outputToSmartDashboard() {
		
	}

	@Override
	public void stop() {
		indexer.set(0);
	}

}
