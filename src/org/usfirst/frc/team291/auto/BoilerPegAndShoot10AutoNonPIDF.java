package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.GearMech.GearState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class BoilerPegAndShoot10AutoNonPIDF extends AutoMode{

	private Timer timer;
	private boolean blueSide;
	
	public BoilerPegAndShoot10AutoNonPIDF(boolean blueSide){
		super(blueSide);
		this.blueSide = blueSide;
		timer = new Timer();
		// Get the left and right trajectories from auto.arrays
		// if the robot is on the blue side, run normally
	}
	
	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_FORWARD,
		TURN_TOWARD_PEG,
		DRIVE_TO_PEG,
		DEPOSIT_GEAR,
		BACK_UP_TO_SHOOT,
		SHOOT,
	}

	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		switch(state){
			case INIT:
				init();
				timer.start();
				gearMech.setWantedState(GearState.IDLE);
				state = State.DRIVE_FORWARD;
				
				break;
	
			case DRIVE_FORWARD:
				if(!driveBase.distanceIsStable()){
					driveBase.driveToDistancePID(70/12, .55, 0);
					System.out.println("driving forward");
				}
				else{
					state = State.TURN_TOWARD_PEG;
					driveBase.resetPID();
				}
				
				break;
				
			case TURN_TOWARD_PEG:
				if(!driveBase.angleIsStable()){
					System.out.println("turning");
					if(blueSide) driveBase.turnToAngle(30);
					else driveBase.turnToAngle(-30);
				}
				else{
					state = State.DRIVE_TO_PEG;
					driveBase.zeroEncoders();
					driveBase.resetPID();
				}
				break;
				
			case DRIVE_TO_PEG:
				if(!driveBase.distanceIsStable()){
					System.out.println("driving to peg");
					if(blueSide) driveBase.driveToDistancePID(61/12, .55, 30);
					else driveBase.driveToDistancePID(61/12, .55, -30);
				}
				else{
					state = State.DEPOSIT_GEAR;
					driveBase.resetPID();
					timer.reset();
				}
				break;
				
			case DEPOSIT_GEAR:
				if(timer.get() < 0.7) gearMech.setWantedState(GearState.SCORING);
				else{
					state = State.BACK_UP_TO_SHOOT;
					timer.reset();
					driveBase.zeroEncoders();
				}
				break;
	
			case BACK_UP_TO_SHOOT:
				if(!driveBase.distanceIsStable()){
					if(blueSide) driveBase.driveToDistancePID(-107/12, .55, 42);
					else driveBase.driveToDistancePID(-107/12, .55, -42);
				}
				else{
					state = State.SHOOT;
					driveBase.resetPID();
				}
				break;
				
			case SHOOT:
				CIARobot.shooter.setWantedState(ShooterState.AUTOSHOOTING);
				CIARobot.driveBase.setWantedState(DriveState.AUTOAIMING);
				break;
		}
		
	}

	@Override
	public void reset() {	
	}

	@Override
	public void end() {
	}

	@Override
	public void outputToSmartDashboard() {
	}

}
