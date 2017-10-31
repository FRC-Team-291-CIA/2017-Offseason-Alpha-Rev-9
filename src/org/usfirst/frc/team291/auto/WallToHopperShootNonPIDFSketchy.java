package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class WallToHopperShootNonPIDFSketchy extends AutoMode{

	private Timer timer;
	private boolean blueSide;
	
	public WallToHopperShootNonPIDFSketchy(boolean blueSide){
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
			state = State.DRIVE_FORWARD;
			break;

		case DRIVE_FORWARD:
			if(!driveBase.distanceIsStable()){
				driveBase.driveToDistancePID(96.0/12.0, .7, 0);
			}
			else{
				state = State.RAM_HOPPER;
				timer.reset();
				driveBase.resetPID();
				driveBase.zeroEncoders();
				System.out.println("moving to Ram Hopper");
			}

			break;

		case RAM_HOPPER:
			if(timer.get() < 1.5){
				if(blueSide) driveBase.driveToDistancePID(-56.0/12.0, .45, 90);
				else driveBase.driveToDistancePID(-56.0/12.0, .45, -90);
			}
			else{
				timer.reset();
				driveBase.setLeftRightPower(0, 0);
				state = State.PAUSE_AT_HOPPER;
				driveBase.resetPID();
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
				
				if(blueSide){
					driveBase.setLeftRightPower(.4, .55);
				}
				else{
					driveBase.setLeftRightPower(.55, .4);
				}	
			}
			else{
				driveBase.setLeftRightPower(0, 0);
			}
			state = State.SHOOT;
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
