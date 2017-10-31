package org.usfirst.frc.team291.subsystems;

/*
 * FuelIntake.java
 * This handles the state and actions of the Fuel Intake Mechanism.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.first.wpilibj.Solenoid;
//import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;

public class FuelIntake extends Subsystem{
	
	private Solenoid deploy = new Solenoid(CIAConstants.fuelIntakeDeploymentPort);
	private Spark intakeRoller = new Spark(CIAConstants.fuelIntakePort);
	
	private Timer timer = new Timer();
	
	public FuelIntake(){
		timer.start();
	}
	
	public void deploy(boolean activate){
		deploy.set(activate);
	}
	
	private boolean finishedCycle = false;
	public void clearFuel(boolean clear){
		if(clear && !finishedCycle){
			if(timer.get() < .2) intakeRoller.set(-.5);
			else if(timer.get() < 1) intakeRoller.set(1);
			else finishedCycle = true;
		}
		else{
			timer.reset();
			finishedCycle = false;
		}
	}
	
	public void intake(boolean in){
		if(in) intakeRoller.set(1);
		else intakeRoller.set(0);
	}
	
	@Override
	public void outputToSmartDashboard() {
		
	}

	@Override
	public void stop() {
		intakeRoller.set(0);
		deploy(false);
	}

}
