package org.usfirst.frc.team291.auto;

public class turnTuningAuto extends AutoMode{

	@Override
	public void init() {
		//driveBase.setGyroYaw(0);
		
	}

	@Override
	public void execute() {
		//driveBase.turnToAngle(90);
		driveBase.driveToDistancePID(10.5, .55, 0);
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
