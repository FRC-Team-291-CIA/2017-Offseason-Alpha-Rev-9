package org.usfirst.frc.team291.subsystems;

/*
 * Climber.java
 * This handles the actions of the Climber Mechanism.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;
import edu.wpi.first.wpilibj.Spark;

public class Climber extends Subsystem{
	
	private Spark leftMotor = new Spark(CIAConstants.leftClimberPort);
	private Spark rightMotor = new Spark(CIAConstants.rightClimberPort);
	
	public void setPower(double power){
		double deadband = 0.1;
		if(power < deadband) power = 0;
		leftMotor.set(power);
		rightMotor.set(-power);
	}
	
	@Override
	public void outputToSmartDashboard() {
		
	}

	@Override
	public void stop() {
		
	}

	public void setLeftMotor(double power) {
		// TODO Auto-generated method stub
		
	}

	public void setRightMotor(double power) {
		// TODO Auto-generated method stub
		
	}

}
