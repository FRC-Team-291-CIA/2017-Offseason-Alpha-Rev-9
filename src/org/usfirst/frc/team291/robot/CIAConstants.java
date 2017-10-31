package org.usfirst.frc.team291.robot;

/*
 * CIAConstants.java
 * Defines constants used all around the robot.
 * @author Julia Cecchetti
 */

public class CIAConstants {
	
	// Motor Port Mappings
	public static final int gearIntakePort = 0;
	public static final int rightShooterPort = 1;//PWM is split to right and left motors
	
	public static final int leftClimberPort = 2;
	public static final int rightClimberPort = 3;

	public static final int rightDrivePortA = 4;
	public static final int rightDrivePortB = 5;
	public static final int leftDrivePortA = 6;
	public static final int leftDrivePortB = 7;
	
	public static final int hopperIndexer = 8;
	public static final int fuelIntakePort = 9;
	
	// Sensor DIO port Mappings
	public static final int flywheelEncoderPortA = 0;
	public static final int flywheelEncoderPortB = 1;
	
	public static final int leftEncoderPortA = 4;
	public static final int leftEncoderPortB = 5;
	public static final int rightEncoderPortA = 2;
	public static final int rightEncoderPortB = 3;
	
	public static final int gearLimit = 6;
	
	//DIO MXP pins, used to select the autonomous mode
	public static final int A1Port = 10;
	public static final int A2Port = 11;
	public static final int B1Port = 12;
	public static final int B2Port = 13;
	public static final int C1Port = 14;
	public static final int C2Port = 15;
	public static final int D1Port = 16;
	public static final int D2Port = 17;
	
	// Drive Encoders are configured to read in feet
	public static final double driveEncoderDistancePerPulse = (786/(9500*.52575))/144/1.085;//3.985
	//Fly Wheel Encoder is configured to read in radians (don't ask why)
	public static final double flyWheelEncoderRadiansPerPulse = (2*Math.PI*18/(24*20));
	
	// Solenoid port Mappings
	public static final int gearPivotPort = 0;
	public static final int fuelIntakeDeploymentPort = 7;
	public static final int hopperExpansionPort = 1;
	
	/*-------------JOYSTICKS----------------*/
	
	//Operator Joystick Mappings
	public static final int gearIntakeButton = 5;//left bumper
	public static final int gearExtakeButton = 6;//right bumper
	public static final int fuelIntakeButton = 7;//left trigger
	public static final int clearFuelButton = 3;
	public static final int hopperExpansionButton = 8;//right trigger
	public static final int fuelIntakeDeployButton = 4;
	public static final int gearSensorOverrideButton = 1;//To be used if sensor is stuck closed
	
	public static final int climberAxis = 1;//left stick up
	
	//Driver Joystick Mappings
	public static final int throttleAxis = 1;//left vertical axis
	public static final int headingAxis = 2;//right horizontal axis
	
	public static final int precisionModeButton = 8;//right trigger
	
	public static final int unjammerButton = 1;//runs indexer belts backwards
	public static final int autoShootButton = 2;
	
	public static final int gearMechOverrideButton = 10;//upper right small button

	public static final int manualShootButton = 8;//right trigger
	public static final int manualIndexerButton = 4;
	
	// Vision stuffs
	public static final int IMG_WIDTH = 160;
	public static final int IMG_HEIGHT = 120;
	public static final double[] hsvThresholdHue = {58.0, 91.0};
	public static final double[] hsvThresholdSaturation = {48.0, 255.0};
	public static final double[] hsvThresholdValue = {30.0, 100.0};

	// Useful Constants used around the robot
	public static final double turnLimiter = 0.7;// Default max turn power in DriveBase
	public static final double precisionModeLimiter = 0.5;// Used in DriveBase to go slower
	public static final double maxFlyWheelSpeed = 330;
	
	//PID and PIDF constants 
	public static final double Pturn = 0;
	public static final double Iturn = 0;
	public static final double Dturn = 0;
	public static final int turnEpsilon = 2;
	public static final double Ppixel = .03;//.01
	public static final double Ipixel = .002;//.002
	public static final double Dpixel = 0;
	public static final int pixelEpsilon = 3;
	public static final double Pdrive = .6;
	public static final double Idrive = 0;
	public static final double Ddrive = .2;
	public static final double driveEpsilon = .33;
	public static final double kp = 5;
	public static final double kd = 0;
	public static final double kv = .0833;
	public static final double ka = .0556;
	
	//Measured Flywheel Setpoint Map for shooter
	public static final double[] setpointMap = {
			165.5,//45 pixels
			170,//50
			171,//55
			173,//60
			173,//65
			178,//70
			187,//75
			190,//80
			200,//85 Not tuned well past this point
			200,//90
			207,//95
			215,//100
			218,//105
			220//110
	};
	
	
}
