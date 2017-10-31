package org.usfirst.frc.team291.auto;

/**
 * WallToHopperShoot.java
 * This auto mode uses path planning to go from the wall to trigger the hopper with the
 * hopper expansion pistons and shoot while loading balls.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.auto.arrays.WallToHopperPath;
import org.usfirst.frc.team291.pathfollower.Trajectory;
import org.usfirst.frc.team291.pathfollower.TrajectoryDriveController;
import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.Timer;

public class WallToHopperShoot extends AutoMode{

	private TrajectoryDriveController controller;
	private Trajectory trajectoryLeft;
	private Trajectory trajectoryRight;
	private Timer timer;
	
	public WallToHopperShoot(boolean blueSide){
		timer = new Timer();
		if(blueSide){
			trajectoryLeft = WallToHopperPath.trajectoryArray[0];
			trajectoryRight = WallToHopperPath.trajectoryArray[1];
		}
		//if on the red side, invert the path
		else {
			trajectoryLeft = WallToHopperPath.trajectoryArray[1];
			trajectoryRight = WallToHopperPath.trajectoryArray[0];
			trajectoryLeft.setInvertedY(true);
			trajectoryRight.setInvertedY(true);
		}
		controller = new TrajectoryDriveController(trajectoryLeft, trajectoryRight, 1.0);
		timer.start();
	}
	@Override
	public void init() {
		CIARobot.driveBase.zeroSensors();
	}

	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_TO_HOPPER,
		SHOOT,
	}
	
	@Override
	public void execute() {
		switch(state){
		case INIT:
			init();
			//gearMech.setWantedState(GearState.HOLDING);
			state = State.DRIVE_TO_HOPPER;
			break;

		case DRIVE_TO_HOPPER:
			if(timer.get() > .5) fuelIntake.deploy(true);//Wait until away from the wall to deploy
			if(!controller.onTarget()){// Still driving the path.
				controller.update();// does the calculations and updates the driveBase
			}
			else{ // Finished the path.
				state = State.SHOOT;
				hopper.expand(true);//trigger the hopper
			}
			break;

		case SHOOT:
			shooter.setWantedState(ShooterState.AUTOSHOOTING);
			driveBase.setWantedState(DriveState.AUTOAIMING);
			if(timer.get() > 10){//Mix up the fuel a bit
				fuelIntake.clearFuel(true);
				if(timer.get() < 12) hopper.expand(false);
				else if(timer.get() < 13) hopper.expand(true);
				else if(timer.get() < 14) hopper.expand(false);
			}
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
