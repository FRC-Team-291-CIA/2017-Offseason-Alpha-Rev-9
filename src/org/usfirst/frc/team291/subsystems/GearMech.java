package org.usfirst.frc.team291.subsystems;

/*
 * GearMech.java
 * This handles the state and actions of the Gear Mechanism.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearMech extends Subsystem {
	
	private Solenoid pivot = new Solenoid(CIAConstants.gearPivotPort);
	private Spark intakeWheels = new Spark(CIAConstants.gearIntakePort);
	private DigitalInput gearLimit = new DigitalInput(CIAConstants.gearLimit);
	
	private double IntakeBufferTime = .75;
	private Timer gearTimer = new Timer();
	private SystemState systemState = SystemState.STANDBY;
	
	public GearMech(){
		gearTimer.start();
	}
	
	public static enum GearState{
		ACQUIRING, SCORING, IDLE
	}
	private enum SystemState{
		INTAKING, STOWING, DEPOSITING, HOLDING, STANDBY
	}
	
	public void setWantedState(GearState wantedState){
		if(wantedState == GearState.ACQUIRING){
			if(hasGear() && systemState == SystemState.INTAKING){
				systemState = SystemState.STOWING;
				gearTimer.reset();
			}
			else if(systemState == SystemState.STOWING && gearTimer.get() < IntakeBufferTime) systemState = SystemState.STOWING;
			else if(hasGear()) systemState = SystemState.HOLDING;
			else systemState = SystemState.INTAKING;	
		}
		else if(wantedState == GearState.SCORING){
			systemState = SystemState.DEPOSITING;
		}
		else{
			if(hasGear()) systemState = SystemState.HOLDING;
			else systemState = SystemState.STANDBY;
		}
		
		update();
	}
	
	public void update(){
		switch(systemState){
		case INTAKING:
			intakeWheels.set(.75);
			extend(true);//set Gear Mech down
			break;
		case STOWING:
			intakeWheels.set(.4);
			extend(false);
			break;
		case HOLDING:
			intakeWheels.set(0);//Lightly power the wheels to hold the gear
			extend(false);//pull the Gear Mech up
			break;
		case DEPOSITING:
			intakeWheels.set(-.4);
			extend(true);
			break;
		case STANDBY:
			intakeWheels.set(0);
			extend(false);
			break;
		}
	}
	
	boolean overridden = false;
	boolean lastOverride = false;
	
	public void override(boolean override){
		if(override){
			if(!lastOverride){// Only toggle once every button press. 
				overridden = !overridden;// Reverse the Pivot State
			}
		}
		lastOverride = override;
	}
	
	public boolean isOverridden(){
		return overridden;
	}
	
	public void extend(boolean out){
		pivot.set(out);
	}

	public boolean hasGear(){
		return !gearLimit.get();
	}
	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putBoolean("Gear Limit", hasGear());
	}

	@Override
	public void stop() {
		setWantedState(GearState.IDLE);
	}

}
