package org.usfirst.frc.team291.pathfollower;

/*
 * TrajectoryDriveController.java
 * Mostly stolen from team 254's 2014 code. Drives the robot along a path.
 * Uses TrajectoryFollower.java to calculate the outputs before the angle 
 * is taken into consideration.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;

import org.usfirst.frc.team291.robot.CIARobot;


public class TrajectoryDriveController extends CIARobot{

	TrajectoryFollower followerLeft;
	TrajectoryFollower followerRight;
	double lefterrorAccumulator, righterrorAccumulator = 0;
	double direction;
	double kTurn = 0/1000.0;//Not using gyro for now
	
	public TrajectoryDriveController(Trajectory trajectoryLeft, Trajectory trajectoryRight, double direction) {
		followerLeft = new TrajectoryFollower("left", trajectoryLeft);
		followerRight = new TrajectoryFollower("right", trajectoryRight);
		this.direction = direction;
		//followerLeft.configure(SmartDashboard.getDouble("Kp Turret"), 0, 0, SmartDashboard.getDouble("Ki Turret"), SmartDashboard.getDouble("Kd Turret"));
		//followerRight.configure(SmartDashboard.getDouble("Kp Turret"), 0, 0, SmartDashboard.getDouble("Ki Turret"), SmartDashboard.getDouble("Kd Turret"));
		followerLeft.configure(CIAConstants.kp, 0, 0, CIAConstants.kv, CIAConstants.ka);
		followerRight.configure(CIAConstants.kp, 0, 0, CIAConstants.kv, CIAConstants.ka);
	}

	public boolean onTarget() {
		return followerLeft.isFinishedTrajectory();
	}

	public void reset() {
		followerLeft.reset();
		followerRight.reset();
		driveBase.zeroSensors();
		lefterrorAccumulator = righterrorAccumulator = 0;
	}

	public int getFollowerCurrentSegment() {
		return followerLeft.getCurrentSegment();
	}

	public int getNumSegments() {
		return followerLeft.getNumSegments();
	}

	public void update() {
		
		if (onTarget()) {
			driveBase.setLeftRightPower(0,0);
			System.out.println("On Target");
		} 
		else {
			double distanceL = direction * driveBase.leftEncoderDistance();
			double distanceR = direction * driveBase.rightEncoderDistance();

			double speedLeft = direction * followerLeft.calculate(distanceL);
			double speedRight = direction * followerRight.calculate(distanceR);
			
			lefterrorAccumulator += followerLeft.last_error_;
			righterrorAccumulator += followerRight.last_error_;

			double goalHeading = followerLeft.getHeading();
			double observedHeading = -driveBase.getGyroYaw();
			//SmartDashboard.putNumber("ObservedHeading", observedHeading);
			//SmartDashboard.putNumber("GoalHeading", goalHeading);
			//SmartDashboard.putNumber("leftError", lefterrorAccumulator);
			//SmartDashboard.putNumber("rightError", righterrorAccumulator);
			double angleDiffRads = observedHeading - goalHeading;
			double angleDiff = Math.toDegrees(angleDiffRads);
			double turn = kTurn * angleDiff;
			
			driveBase.setLeftRightPower(speedLeft + turn, speedRight - turn);
		}
	}

}

