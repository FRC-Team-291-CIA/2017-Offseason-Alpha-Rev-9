package org.usfirst.frc.team291.subsystems;

/*
 * Shooter.java
 * This handles the state and actions of the Shooter Mechanism and green ring light.
 * @author Julia Cecchetti
 */

import org.usfirst.frc.team291.robot.CIAConstants;
import org.usfirst.frc.team291.robot.CIARobot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Subsystem{
	
	private Spark rightMotor= new Spark(CIAConstants.rightShooterPort);
	
	private Encoder flyWheelEncoder = new Encoder(CIAConstants.flywheelEncoderPortA, CIAConstants.flywheelEncoderPortB);
	
	private Relay ringLight = new Relay(0);
	
	private Timer ringLightTimer;
	private boolean lastFlash = false;
	private boolean flywheelsStable = false;
	private boolean unjam = false;
	
	private double fudgeFactor = 0;
	
	public Shooter(){
		flyWheelEncoder.setDistancePerPulse(CIAConstants.flyWheelEncoderRadiansPerPulse);
		ringLightTimer = new Timer();
	}
	
	public static enum ShooterState{
		FLASHING, AUTOSHOOTING, MANUALSHOOTING, AUTOAIMING, STANDBY;
	}

	public void setWantedState(ShooterState wantedState){
		switch(wantedState){
			case STANDBY:
				setPower(0);
				CIARobot.hopper.stop();
				resetFudge();
				flywheelsStable = false;
				//fall through
			case FLASHING:
				if(CIARobot.gearMech.hasGear()) flashRingLight(true);
				else{
					flashRingLight(false);
				}
				break;
			case AUTOSHOOTING:
				if(unjam) CIARobot.hopper.reverse();
				else if(flywheelsStable && CIARobot.driveBase.targetIsStable()) CIARobot.hopper.index();
				else CIARobot.hopper.stop();
				//fall through
			case AUTOAIMING:
				setRingLightOn(true);
				double setpoint;
				int mapIndex = (int) (CIARobot.visionThread.getCenterY()/5 - 7);
				if(mapIndex > 0 && mapIndex < CIAConstants.setpointMap.length){
					setpoint = CIAConstants.setpointMap[mapIndex];
					//setpoint = SmartDashboard.getNumber("Kp Turret");
					setPower(bangBang(setpoint + fudgeFactor));
					//System.out.println(setpoint);
				}
				else{
					setPower(0);
					flywheelsStable = false;
				}
				break;
			case MANUALSHOOTING:
				if(unjam) CIARobot.hopper.reverse();
				else if(flywheelsStable) CIARobot.hopper.index();
				else CIARobot.hopper.stop();
				setPower(bangBang(175 + fudgeFactor));
				break;
		}
	}
	
	/*Local methods*/
	private void setPower(double power){
		rightMotor.set(power);
	}
	
	private void setRingLightOn(boolean on){
		if(on) ringLight.set(Value.kForward);
		else ringLight.set(Value.kOff);
	}
	
	private void flashRingLight(boolean flash){
		if(flash && !lastFlash){
			ringLightTimer.reset();
			ringLightTimer.start();
		}
		lastFlash = flash;
		double t = ringLightTimer.get();
		
		if(t < .125) setRingLightOn(true);
		else if(t < .25) setRingLightOn(false);
		else if(t < .375) setRingLightOn(true);
		else if(t < .5) setRingLightOn(false);
		else if(t < .625) setRingLightOn(true);
		else if(t < .75) setRingLightOn(false);
		else if(t < .875) setRingLightOn(true);
		else setRingLightOn(false);		
	}
	
	private boolean lastFudgeUp = false;
	public void fudgeUp(boolean fudge){
		if(fudge){// mmm... fudge
			if(!lastFudgeUp){// Only fudge once every button press. 
				fudgeFactor += 3;// fudge up 3
			}
		}
		lastFudgeUp = fudge;		
		
	}
	private boolean lastFudgeDown = false;
	public void fudgeDown(boolean fudge){
		if(fudge){
			if(!lastFudgeDown){// Only fudge once every button press. 
				fudgeFactor -= 3;// fudge down 3
			}
		}
		
		lastFudgeDown = fudge;
	}
	
	private void resetFudge(){
		fudgeFactor = 0;
	}
	
	private double bangBang(double targetSpeed){
		double output;
		double currentSpeed = flyWheelEncoder.getRate();//current Speed in rad/sec
		if(targetSpeed - currentSpeed  < 20) flywheelsStable = true;
		else flywheelsStable = false;
		if(currentSpeed < targetSpeed){
			output = 1;
		}
		else{
			output = 0;
		}
		return output;
	}
	
	public void unjam(boolean unjam){
		this.unjam = unjam;
	}
	
	@Override
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("Fly Wheel Encoder", flyWheelEncoder.getRate());
		SmartDashboard.putBoolean("Flywheels Stable", flywheelsStable);
	}

	@Override
	public void stop() {
		setPower(0);
	}
	

}
