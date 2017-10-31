package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.auto.arrays.*;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;

public class TestAuto extends AutoMode {
	
	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	
	public TestAuto(boolean blueSide){
		super(blueSide);
		
		// Get the left and right trajectories from auto.arrays
		// if the robot is on the blue side, run normally
		if(blueSide){
			trajectoryLeft = WallToRamHopperPath.trajectoryArray[0];
			trajectoryRight = WallToRamHopperPath.trajectoryArray[1];
		} 
		//if on the red side, invert the path
		else {
			trajectoryLeft = WallToRamHopperPath.trajectoryArray[1];
			trajectoryRight = WallToRamHopperPath.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		// Create a new TDC controller using the trajectories from 
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
	}
	
	@Override
	public void init() {
		//timer.start();
		driveBase.zeroSensors();
		
	}
	
	/* execute() should be called in autonomousPeriodic */
	@Override
	public void execute() {
		controller.update();// does the calculations and updates the driveBase
	}

	@Override
	public void reset() {
		controller.reset();
	}

	@Override
	public void end() {
		
	}

	@Override
	public void outputToSmartDashboard() {
		
		
	}

}
