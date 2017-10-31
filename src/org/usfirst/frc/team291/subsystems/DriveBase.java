package org.usfirst.frc.team291.subsystems;

/*
 * DriveBase.java
 * This handles the state and actions of the Drive Train and its sensors. It owns the gyro and 
 * both encoders. 
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;
import org.usfirst.frc.team291.robot.CIARobot;
import org.usfirst.frc.team291.util.PID;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveBase extends Subsystem{
	
	private Victor leftDriveA = new Victor(CIAConstants.leftDrivePortA);
	private Victor leftDriveB = new Victor(CIAConstants.leftDrivePortB);
	private Victor rightDriveA = new Victor(CIAConstants.rightDrivePortA);
	private Victor rightDriveB = new Victor(CIAConstants.rightDrivePortB);
	private Encoder leftEncoder = new Encoder(CIAConstants.leftEncoderPortA, CIAConstants.leftEncoderPortB);
	private Encoder rightEncoder = new Encoder(CIAConstants.rightEncoderPortA, CIAConstants.rightEncoderPortB);
	private AHRS gyro;
	
	private double gyroWorkingZero = 0;
	
	private PID turnPID = new PID(CIAConstants.Pturn, CIAConstants.Iturn, CIAConstants.Dturn, CIAConstants.turnEpsilon);
	private PID pixelPID = new PID(CIAConstants.Ppixel, CIAConstants.Ipixel, CIAConstants.Dpixel, CIAConstants.pixelEpsilon);
	private PID drivePID = new PID(CIAConstants.Pdrive, CIAConstants.Idrive, CIAConstants.Ddrive, .05);
	private boolean angleIsStable = false;
	private boolean targetIsStable = false;
	private boolean distanceIsStable = false;
	
	public DriveBase(){
		leftEncoder.setDistancePerPulse(CIAConstants.driveEncoderDistancePerPulse);
		rightEncoder.setDistancePerPulse(CIAConstants.driveEncoderDistancePerPulse);
		leftDriveA.setInverted(true);
		leftDriveB.setInverted(true);
		gyro = new AHRS(I2C.Port.kMXP);
		//drivePID.setMaxOutput(.65);
		drivePID.setConstants(1.5, 0, 9.5);
		turnPID.setConstants(.05, .008, .23);
		turnPID.setMaxOutput(.6);
		System.out.println("DriveBase initialized");
	}
	
	public enum DriveState{
		AUTOAIMING, OVERRIDE, STANDBY
	}
	
	public void setWantedState(DriveState wantedState){
		switch(wantedState){
		case OVERRIDE:
			break;
		case AUTOAIMING:
			double targetXpos = CIARobot.visionThread.getCenterX();
			double targetYpos = CIARobot.visionThread.getCenterY();
			if(targetXpos != -1){
				if(targetYpos > 40 && targetYpos < 110){//robot is in sweet spot to shoot
					trackTarget();
				}
				else if(targetYpos < 40) setLeftRightPower(.4, .4);//drive forward to get into range
				else if(targetYpos > 110) setLeftRightPower(-.4, -.4);//drive backward to get into range
				else setLeftRightPower(0, 0);
			}
			else setLeftRightPower(0, 0);
			break;
			
		case STANDBY:
			setLeftRightPower(0,0);
			break;
		}
	}
	
	public void setLeftRightPower(double left, double right){
		leftDriveA.set(left);
		leftDriveB.set(left);
		rightDriveA.set(right);
		rightDriveB.set(right);
		//System.out.println("DriveBase: " + left + ", " + right);
	}
	
	//Custom Arcade Drive
	public void CIADrive(double power, double turn, boolean precisionMode){
		double multiplier = 1;
		double turnLimiter = CIAConstants.turnLimiter;
		double deadband = .05;
		
		if(precisionMode) multiplier = .65;
		
		if(turn < deadband && turn > -deadband) turn = 0;
		if(power < deadband && power > -deadband) power = 0;
		
        double left;
        double right;
        left = -(power - turn * turnLimiter);
        right = -(power + turn * turnLimiter);
        setLeftRightPower(left * multiplier, right * multiplier);
	}
	
	public void driveToDistancePID(double setpointDistance, double maxPower, double angle){
		if(!drivePID.isDone()){
			//drivePID.setConstants(SmartDashboard.getNumber("Kp Turret"), SmartDashboard.getNumber("Ki Turret"), SmartDashboard.getNumber("Kd Turret"));
			drivePID.setMaxOutput(maxPower);
			drivePID.setDesiredValue(setpointDistance);
			turnPID.setDesiredValue(angle);
			double power = drivePID.calcPID(leftEncoder.getDistance());
			double turn = turnPID.calcPID(getGyroYaw());
			setLeftRightPower(power + turn, power - turn);
			distanceIsStable = false;
		}
		else{
			setLeftRightPower(0,0);
			distanceIsStable = true;
		}
	}
	
	boolean turned = false;
	public void fastTurnAndDriveDistancePID(double setpointDistance, double maxPower, double angle){
		double currentAngle = getGyroYaw();
		if(!turned){
			//turnPID.setConstants(SmartDashboard.getNumber("Kp Turret"), SmartDashboard.getNumber("Ki Turret"), SmartDashboard.getNumber("Kd Turret"));
			turnPID.setDesiredValue(angle);
			double turnPower = turnPID.calcPID(currentAngle);
			setLeftRightPower(turnPower, -turnPower);
			if(Math.abs(currentAngle - angle) < 5) turned = true;
			else turned = false;
		}
		else if(!drivePID.isDone()){
			//drivePID.setConstants(SmartDashboard.getNumber("Kp Turret"), SmartDashboard.getNumber("Ki Turret"), SmartDashboard.getNumber("Kd Turret"));
			drivePID.setMaxOutput(maxPower);
			drivePID.setDesiredValue(setpointDistance);
			turnPID.setDesiredValue(angle);
			double power = drivePID.calcPID(leftEncoder.getDistance());
			double turn = turnPID.calcPID(getGyroYaw());
			setLeftRightPower(power + turn, power - turn);
			distanceIsStable = false;
		}
		else{
			setLeftRightPower(0,0);
			distanceIsStable = true;
		}
		
	}
	
	public void turnToAngle(double setpointAngle){
		double currentAngle = getGyroYaw();
		if(!turnPID.isDone()){
			//turnPID.setConstants(SmartDashboard.getNumber("Kp Turret"), SmartDashboard.getNumber("Ki Turret"), SmartDashboard.getNumber("Kd Turret"));
			turnPID.setDesiredValue(setpointAngle);
			double turnPower = turnPID.calcPID(currentAngle);
			setLeftRightPower(turnPower, -turnPower);
			angleIsStable = false;
		}
		else {
			setLeftRightPower(0,0);
			angleIsStable = true;
		}
	}
	
	public void trackTarget(){
		double targetXpos = CIARobot.visionThread.getCenterX();
		//pixelPID.setConstants(SmartDashboard.getNumber("Kp Turret"), SmartDashboard.getNumber("Ki Turret"), SmartDashboard.getNumber("Kd Turret"));
		if(Math.abs(targetXpos - CIAConstants.IMG_WIDTH/2) > 3){
			pixelPID.setDesiredValue(CIAConstants.IMG_WIDTH/2);
			double turnPower = pixelPID.calcPID(targetXpos);
			setLeftRightPower(-turnPower, turnPower);
			targetIsStable = false;
		}
		else {
			setLeftRightPower(0,0);
			targetIsStable = true;
		}
	}
	
	public boolean targetIsStable(){
		return targetIsStable;
	}
	public boolean angleIsStable(){
		return angleIsStable;
	}
	public boolean distanceIsStable(){
		return distanceIsStable;
	}
	
	public void resetPID(){
		distanceIsStable = false;
		angleIsStable = false;
		turned = false;
		targetIsStable = false;
	}
	
	public void stop(){
		leftDriveA.set(0);
		leftDriveB.set(0);
		rightDriveA.set(0);
		rightDriveB.set(0);
	}
	
	// Used for test mode
	public void setleftDriveA(double power){ leftDriveA.set(power); }
	public void setleftDriveB(double power){ leftDriveB.set(power); }
	public void setrightDriveA(double power){ rightDriveA.set(power); }
	public void setrightDriveB(double power){ rightDriveB.set(power); }
	
	/* Encoders */
	public double leftEncoderDistance(){
		return leftEncoder.getDistance();
	}
	public double rightEncoderDistance(){
		return rightEncoder.getDistance();
	}
	public double leftEncoderRate(){
		return leftEncoder.getRate();
	}
	public double rightEncoderRate(){
		return rightEncoder.getRate();
	}
	public void zeroEncoders(){
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	/* Gyro */
	public double getGyroYaw(){
		return gyro.getAngle() - gyroWorkingZero;
	}
	
	public void setGyroYaw(double yaw){
		gyroWorkingZero = gyro.getAngle() - yaw;
	}
	
	public boolean gyroIsConnected(){
		return gyro.isConnected();
	}
	
	public void zeroSensors(){
		setGyroYaw(0);
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	public void outputToSmartDashboard(){
		SmartDashboard.putNumber("Left Distance", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Distance", rightEncoder.getDistance());
		SmartDashboard.putNumber("Left Velocity", leftEncoder.getRate());
		SmartDashboard.putNumber("Right Velocity", rightEncoder.getRate());
		SmartDashboard.putNumber("Gyro Yaw", getGyroYaw());
		SmartDashboard.putBoolean("Gyro Connected", gyro.isConnected());
		SmartDashboard.putBoolean("Drivebase Aimed", targetIsStable);
	}

}
