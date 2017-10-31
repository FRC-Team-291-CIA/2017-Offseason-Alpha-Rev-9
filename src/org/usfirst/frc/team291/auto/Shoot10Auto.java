package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.auto.arrays.WallToShoot10Path;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;
import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class Shoot10Auto extends AutoMode{

	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	private Timer timer;
	
	public Shoot10Auto(boolean blueSide){
		this.blueSide = blueSide;
		if(blueSide){
			trajectoryLeft = WallToShoot10Path.trajectoryArray[0];
			trajectoryRight = WallToShoot10Path.trajectoryArray[1];
		} 
		//if on the red side, invert the path
		else {
			trajectoryLeft = WallToShoot10Path.trajectoryArray[1];
			trajectoryRight = WallToShoot10Path.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		// Create a new TDC controller using the trajectories from 
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
		
		timer = new Timer();
		timer.start();
	}
	@Override
	public void init() {
		CIARobot.driveBase.zeroSensors();
		
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
			//gearMech.setWantedState(GearState.HOLDING);
			state = State.DRIVE_FORWARD;
			break;

		case DRIVE_FORWARD:
			if(timer.get() > .5) fuelIntake.deploy(true);//Wait until away from the wall to deploy
			if(!controller.onTarget()){// Still driving the path.
				controller.update();// does the calculations and updates the driveBase
			}
			else{ // Finished the path.
				state = State.SHOOT;
				timer.reset();// Resets the timer for the next stage.
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
