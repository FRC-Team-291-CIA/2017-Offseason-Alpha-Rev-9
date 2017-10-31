package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class WallToHopperShootNonPIDF extends AutoMode{

	private Timer timer;
	private boolean blueSide;
	
	public WallToHopperShootNonPIDF(boolean blueSide){
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
		TURN_TO_HOPPER,
		RAM_HOPPER,
		PAUSE_AT_HOPPER,
		TURN_TOWARD_BOILER,
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
			if(!driveBase.distanceIsStable()){
				driveBase.driveToDistancePID(100/12, 1, 0);
			}
			else{
				state = State.TURN_TO_HOPPER;
			}

			break;

		case TURN_TO_HOPPER:
			if(!driveBase.angleIsStable()){
				if(blueSide) driveBase.turnToAngle(-90);
				else driveBase.turnToAngle(90);
			}
			else{
				state = State.RAM_HOPPER;
			}
			break;

		case RAM_HOPPER:
			if(timer.get() < 1.5) driveBase.setLeftRightPower(-.4, -.4);
			else{
				timer.reset();
				state = State.PAUSE_AT_HOPPER;
			}
			break;

		case PAUSE_AT_HOPPER:
			if(timer.get() < 1.5) driveBase.setLeftRightPower(-.25, -.25);
			else{
				state = State.TURN_TOWARD_BOILER;
				timer.reset();
			}
			break;
			
		case TURN_TOWARD_BOILER:
			if(timer.get() < 1){
				if(blueSide) driveBase.setLeftRightPower(.4, .55);
				else driveBase.setLeftRightPower(.55, .4);	
			}
			else{
				driveBase.setLeftRightPower(0, 0);
				state = State.SHOOT;
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
