package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.auto.arrays.ChutePegPath;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;
import org.usfirst.frc.team291.subsystems.GearMech.GearState;

import edu.wpi.first.wpilibj.Timer;

public class ChutePegAuto extends AutoMode {

	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	private Timer timer;
	
	public ChutePegAuto(boolean blueSide){
		super(blueSide);
		timer = new Timer();
		// Get the left and right trajectories from auto.arrays
		// if the robot is on the blue side, run normally
		if(blueSide){
			trajectoryLeft = ChutePegPath.trajectoryArray[0];
			trajectoryRight = ChutePegPath.trajectoryArray[1];
		} 
		//if on the red side, invert the path
		else {
			trajectoryLeft = ChutePegPath.trajectoryArray[1];
			trajectoryRight = ChutePegPath.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
		
	}
	
	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_CHUTE_PEG_PATH,
		DEPOSIT_GEAR,
		BACK_UP,
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
			timer.start();
			//gearMech.setWantedState(GearState.HOLDING);
			state = State.DRIVE_CHUTE_PEG_PATH;
			driveBase.zeroSensors();
			break;
			
		case DRIVE_CHUTE_PEG_PATH:
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
			if(timer.get() < 1.5) driveBase.setLeftRightPower(-.4, -.4);
			else state = State.FINISH;
			break;
			
		case FINISH:
			driveBase.stop();
			//gearMech.open(false);
			//gearMech.setWantedState(GearState.STANDBY);
			System.out.println("Completed ChutePegAuto!");
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
