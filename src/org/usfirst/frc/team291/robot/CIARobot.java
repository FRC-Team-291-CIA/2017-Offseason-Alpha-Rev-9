package org.usfirst.frc.team291.robot;

/*
 * CIARobot.java
 * Defines all of the subsystems for the robot.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.auto.AutoSelector;

import org.usfirst.frc.team291.subsystems.Climber;
import org.usfirst.frc.team291.subsystems.DriveBase;
import org.usfirst.frc.team291.subsystems.FuelIntake;
import org.usfirst.frc.team291.subsystems.GearMech;
import org.usfirst.frc.team291.subsystems.Hopper;
import org.usfirst.frc.team291.subsystems.Shooter;
import org.usfirst.frc.team291.vision.CIAVisionThread;

public class CIARobot {
	
	//Subsystems
	public static final DriveBase driveBase = new DriveBase();
	public static final GearMech gearMech = new GearMech();
	public static final Shooter shooter = new Shooter();
	public static final Hopper hopper = new Hopper();
	public static final FuelIntake fuelIntake = new FuelIntake();
	public static final Climber climber = new Climber();
	
	public static final AutoSelector autoSelector = new AutoSelector();
	public static final CIAVisionThread visionThread = new CIAVisionThread();
	
	public static void initRobot() {} 

}
