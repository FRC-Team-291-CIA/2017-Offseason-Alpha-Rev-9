package org.usfirst.frc.team291.robot;

import org.usfirst.frc.team291.auto.AutoMode;
import org.usfirst.frc.team291.auto.TestSequence;
import org.usfirst.frc.team291.subsystems.DriveBase.DriveState;
import org.usfirst.frc.team291.subsystems.GearMech.GearState;
import org.usfirst.frc.team291.subsystems.Shooter.ShooterState;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CIACompetition extends IterativeRobot {
	
	//Joysticks
	public static final Joystick driver = new Joystick(0);
	public static final Joystick operator = new Joystick(1);
	
	boolean gearMechOverride = false;

	AutoMode autoMode;
	
	@Override
	public void robotInit() {
		CIARobot.initRobot();
	}

	@Override
	public void autonomousInit() {
		autoMode = CIARobot.autoSelector.selectAuto();
		CIARobot.driveBase.setGyroYaw(0);
		CIARobot.driveBase.zeroEncoders();
		autoMode.reset();
	}
	
	@Override
	public void autonomousPeriodic(){
		CIARobot.visionThread.setEnabled(true);
		autoMode.execute();
		autoMode.outputToSmartDashboard();
	}
	
	@Override
	public void robotPeriodic(){
		CIARobot.driveBase.outputToSmartDashboard();
		CIARobot.shooter.outputToSmartDashboard();
		CIARobot.gearMech.outputToSmartDashboard();
		CIARobot.visionThread.outputToSmartDashboard();
		SmartDashboard.putNumber("Auto Case", CIARobot.autoSelector.autoCase());
		SmartDashboard.putBoolean("Alliance", CIARobot.autoSelector.blueAlliance());
	}
	
	@Override
	public void teleopInit() {
		if (autoMode != null) autoMode.end();
	}
	
	@Override
	public void teleopPeriodic(){
		/*Driver Code*/
		if(driver.getRawButton(CIAConstants.autoShootButton)){
			CIARobot.visionThread.setEnabled(true);
			CIARobot.shooter.setWantedState(ShooterState.AUTOSHOOTING);
			CIARobot.driveBase.setWantedState(DriveState.AUTOAIMING);
			CIARobot.shooter.unjam(driver.getRawButton(CIAConstants.unjammerButton));
		}
		else{
			boolean manualShooting;
			if(driver.getRawButton(CIAConstants.manualShootButton)){
				CIARobot.shooter.setWantedState(ShooterState.MANUALSHOOTING);
				CIARobot.shooter.unjam(driver.getRawButton(CIAConstants.unjammerButton));
				manualShooting = true;
			}
			else{
				CIARobot.shooter.setWantedState(ShooterState.STANDBY);
				manualShooting = false;
			}
			CIARobot.driveBase.CIADrive(driver.getRawAxis(CIAConstants.throttleAxis), driver.getRawAxis(CIAConstants.headingAxis), manualShooting);
			CIARobot.visionThread.setEnabled(true);
		}
		CIARobot.gearMech.override(driver.getRawButton(CIAConstants.gearMechOverrideButton));
		CIARobot.shooter.fudgeUp(driver.getPOV(0) == 0);//POV is pressed Up
		CIARobot.shooter.fudgeDown(driver.getPOV(0) == 180);//POV is pressed down
		
		/*Operator Code*/
		if(!CIARobot.gearMech.isOverridden()){//If gear mech is not overridden by driver, operator controls gear mech
			if(operator.getRawButton(CIAConstants.gearIntakeButton)) CIARobot.gearMech.setWantedState(GearState.ACQUIRING);
			else if(operator.getRawButton(CIAConstants.gearExtakeButton)) CIARobot.gearMech.setWantedState(GearState.SCORING);
			else CIARobot.gearMech.setWantedState(GearState.IDLE);
		}
		else{//if driver has overridden the gear mech
			if(driver.getRawButton(CIAConstants.gearIntakeButton)) CIARobot.gearMech.setWantedState(GearState.ACQUIRING);
			else if(driver.getRawButton(CIAConstants.gearExtakeButton)) CIARobot.gearMech.setWantedState(GearState.SCORING);
			else CIARobot.gearMech.setWantedState(GearState.IDLE);
		}
		CIARobot.fuelIntake.intake(operator.getRawButton(CIAConstants.fuelIntakeButton));
		CIARobot.fuelIntake.deploy(operator.getRawButton(CIAConstants.fuelIntakeDeployButton));
		CIARobot.fuelIntake.clearFuel(operator.getRawButton(CIAConstants.clearFuelButton));
		CIARobot.hopper.expand(operator.getRawButton(CIAConstants.hopperExpansionButton));
		CIARobot.climber.setPower(operator.getRawAxis(CIAConstants.climberAxis));
	}
	
	@Override
	public void disabledInit(){
		
	}
	
	@Override
	public void disabledPeriodic(){
		CIARobot.visionThread.setEnabled(false);
	}
	
	@Override
	public void testInit(){
		autoMode = new TestSequence();
	}
	
	@Override
	public void testPeriodic(){
		autoMode.execute();
	}
}
