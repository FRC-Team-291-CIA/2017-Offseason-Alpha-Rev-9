package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.auto.arrays.BoilerPegPath;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;
import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.GearMech.GearState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class BoilerPegAndShoot10Auto extends AutoMode{

	
	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	private Timer timer;
	private boolean blueSide;
	
	public BoilerPegAndShoot10Auto(boolean blueSide){
		super(blueSide);
		this.blueSide = blueSide;
		timer = new Timer();
		// Get the left and right trajectories from auto.arrays
		// if the robot is on the blue side, run normally
		if(blueSide){
			trajectoryLeft = BoilerPegPath.trajectoryArray[0];
			trajectoryRight = BoilerPegPath.trajectoryArray[1];
		} 
		//if on the red side, invert the path
		else {
			trajectoryLeft = BoilerPegPath.trajectoryArray[1];
			trajectoryRight = BoilerPegPath.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
		
	}
	
	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_BOILER_PEG_PATH,
		DEPOSIT_GEAR,
		BACK_UP_TO_SHOOT,
		SHOOT,
	}

	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		driveBase.zeroSensors();
	}

	@Override
	public void execute() {
		switch(state){
			case INIT:
				init();
				timer.start();
				gearMech.setWantedState(GearState.IDLE);
				state = State.DRIVE_BOILER_PEG_PATH;
				break;
	
			case DRIVE_BOILER_PEG_PATH:
				if(!controller.onTarget()){// Still driving the path.
					controller.update();// does the calculations and updates the driveBase
				}
				else{ // Finished the path.
					state = State.DEPOSIT_GEAR;
					timer.reset();// Resets the timer for the next stage.
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
					if(blueSide) driveBase.driveToDistancePID(-107/12, .85, 42);
					else driveBase.driveToDistancePID(-107/12, .85, -42);
				}
				else{
					state = State.SHOOT;
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
