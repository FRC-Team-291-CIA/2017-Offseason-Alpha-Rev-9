package org.usfirst.frc.team291.auto;

import edu.wpi.first.wpilibj.Timer;

public class DriveForwardTimeAuto extends AutoMode{
	
	Timer timer = new Timer();
	
	public DriveForwardTimeAuto(){
		timer.start();
	}

	@Override
	public void init() {
	}

	@Override
	public void execute() {
		if(timer.get() < .5) driveBase.setLeftRightPower(.4, .4);
		else driveBase.setLeftRightPower(0, 0);
		if(timer.get() > 2 && timer.get() < 10) fuelIntake.deploy(true);
		else fuelIntake.deploy(false);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void outputToSmartDashboard() {
		// TODO Auto-generated method stub
		
	}

}
