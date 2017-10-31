package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.auto.arrays.CenterPegPath;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;
import org.usfirst.frc.team291.subsystems.GearMech.GearState;

import edu.wpi.first.wpilibj.Timer;

public class CenterPegAuto extends AutoMode {
	
	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	private Timer timer;
	
	public CenterPegAuto(boolean blueSide){
		super(blueSide);
		timer = new Timer();
		// Get the left and right trajectories from auto.arrays
		// if the robot is on the blue side, run normally
		if(blueSide){
			trajectoryLeft = CenterPegPath.trajectoryArray[0];
			trajectoryRight = CenterPegPath.trajectoryArray[1];
		} 
		//if on the red side, invert the path
		else {
			trajectoryLeft = CenterPegPath.trajectoryArray[1];
			trajectoryRight = CenterPegPath.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		// Create a new TDC controller using the trajectories from 
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
		timer.start();
	}
	
	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_CENTER_PEG_PATH,
		DEPOSIT_GEAR,
		BACK_UP,
		DEPLOY_INTAKE,
		FINISH,
		DONE;
	}
	
	@Override
	public void init() {
		driveBase.zeroSensors();
	}

	@Override
	public void execute() {
		switch(state){
			case INIT:
				init();
				gearMech.setWantedState(GearState.ACQUIRING);
				state = State.DRIVE_CENTER_PEG_PATH;
				break;
	
			case DRIVE_CENTER_PEG_PATH:
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
					state = State.BACK_UP;
					timer.reset();
				}
				break;
	
			case BACK_UP:
				if(timer.get() < .75){ 
					driveBase.setLeftRightPower(-.4, -.4);
					gearMech.setWantedState(GearState.SCORING);
				}
				else{
					driveBase.setLeftRightPower(0, 0);
					state = State.DEPLOY_INTAKE;
					timer.reset();
				}
				break;
			
			case DEPLOY_INTAKE:
				if(timer.get() < 3){
					fuelIntake.deploy(true);
				}
				else{
					fuelIntake.deploy(false);
					state = State.FINISH;
				}
				break;
	
			case FINISH:
				driveBase.stop();
				gearMech.setWantedState(GearState.IDLE);
				System.out.println("Completed CenterPegAuto!");
				state = State.DONE;
				break;
	
			case DONE:
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
