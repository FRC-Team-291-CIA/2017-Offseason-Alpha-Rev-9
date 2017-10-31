package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class Shoot10AutoNonPIDF extends AutoMode{

	private Timer timer;
	
	public Shoot10AutoNonPIDF(boolean blueSide){
		this.blueSide = blueSide;
		timer = new Timer();
	}
	@Override
	public void init() {
		CIARobot.driveBase.zeroSensors();
		timer.start();
		
	}

	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_FORWARD,
		SHOOT,
	}
	
	@Override
	public void execute() {
		switch(state){
		case INIT:
			init();
			timer.start();
			//gearMech.setWantedState(GearState.HOLDING);
			state = State.DRIVE_FORWARD;
			break;

		case DRIVE_FORWARD:
			if(timer.get()<1.2){
				if(blueSide) driveBase.setLeftRightPower(.5, .62);
				else driveBase.setLeftRightPower(.62, .5);
			}
			/*if(!driveBase.distanceIsStable()){
				driveBase.driveToDistancePID(8.5, 0);
			}*/
			else{
				state = State.SHOOT;
				driveBase.setLeftRightPower(0, 0);
			}

			break;

		case SHOOT:
			shooter.setWantedState(ShooterState.AUTOSHOOTING);
			driveBase.setWantedState(DriveState.AUTOAIMING);
			break;
		}
		
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
