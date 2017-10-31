package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.first.wpilibj.Timer;

public class TestSequence extends AutoMode {
	
	/* TestSequence is called in testPeriodic in order to easily
	 * run diagnostics on the robot's motors and sensors.
	 * It will:
	 * 1. Run each drive motor individually and use the encoders to make sure each runs.
	 * 		-if the encoder doesn't read for all motors the encoder is bad.
	 * 		-if the encoder doesn't read for one motor that motor is bad.
	 * 2. Test each of the pistons.
	 * 3. Test each climber motor individually.
	 * 4. Test that the gyro is connected and sample the drift rate.
	 * 5. If anything is not perfect, report it to the Drivers Station and recommend how to fix it.*/
	
	Timer t;
	private int stage = 0;
	private boolean write = true;// Used to print status only once per stage.
	
	private boolean leftEncoderReads, rightEncoderReads, leftEncoderGood, rightEncoderGood,
					leftDriveAGood, leftDriveBGood, leftDriveCGood, 
					rightDriveAGood, rightDriveBGood, rightDriveCGood, gyroConnected = false;
	
	private double gyroDriftRate = 0;
	private double acceptableDriftRate = 0.5;
	private double acceptableEncoderDiff = 1;
	
	public TestSequence(){
		t = new Timer();
		t.start();
		init();
	}

	@Override
	public void init() {
		System.out.println("\n\n---------------------------------------------------------------------");
		System.out.println("Starting test sequence...");
		System.out.println("It is recommended to put the robot up on jacks.\n");
		driveBase.zeroSensors();
	}

	@Override
	public void execute() {
		switch(stage){
		case 0:
			if(t.get() < .5){
				if(write){
					System.out.println("Encoders and Drive Motors:");
					System.out.println("Testing drive motors " + CIAConstants.leftDrivePortA + " and " + CIAConstants.rightDrivePortA + "...");
					write = false;
				}
				driveBase.setleftDriveA(.4);
				driveBase.setrightDriveA(-.4);
				
				
				if(driveBase.leftEncoderRate() > 0.05) {
					leftEncoderReads = leftDriveAGood = true;
				}
				if(driveBase.rightEncoderRate() > 0.05) {
					rightEncoderReads = rightDriveAGood = true;
				}
			}
			else{
				if(rightEncoderReads && leftEncoderReads){
					double encoderDiff = driveBase.rightEncoderDistance() - driveBase.leftEncoderDistance();
					if (Math.abs(encoderDiff) < acceptableEncoderDiff) leftEncoderGood = rightEncoderGood = true;
					else if(encoderDiff >= acceptableEncoderDiff) rightEncoderGood = true;
					else leftEncoderGood = true;
				}
				driveBase.setLeftRightPower(0, 0);
				write = true;
				t.reset();
				stage++;
			}
			break;
			
		case 1: // Pause
			if(t.get() > .6){
				t.reset();
				stage++;
			}
			break;
			
		case 2:
			if(t.get() < .5){
				if(write){
					System.out.println("Testing drive motors " + CIAConstants.leftDrivePortB + " and " + CIAConstants.rightDrivePortB + "...");
					write = false;
				}
				driveBase.setleftDriveB(-.4);
				driveBase.setrightDriveB(.4);
				
				if(driveBase.leftEncoderRate() < -0.05) leftEncoderReads = leftDriveBGood = true;
				if(driveBase.rightEncoderRate() < -0.05) rightEncoderReads = rightDriveBGood = true;
			}
			else{
				if(rightEncoderReads && leftEncoderReads){
					double encoderDiff = driveBase.rightEncoderDistance() - driveBase.leftEncoderDistance();
					if (Math.abs(encoderDiff) < acceptableEncoderDiff) leftEncoderGood = rightEncoderGood = true;
					else if(encoderDiff >= acceptableEncoderDiff) rightEncoderGood = true;
					else leftEncoderGood = true;
				}
				driveBase.setLeftRightPower(0, 0);
				write = true;
				t.reset();
				stage++;
			}
			
			break;
			
		case 3: // Pause
			if(t.get() > .6){
				t.reset();
				stage++;
			}
			break;
			
		case 4:
			stage++;
			break;
			
		case 5:
			if(t.get() < 1){
				if(write){
					System.out.println("Testing Gear Mechanism...\nExtending...");
					write = false;
				}
				gearMech.extend(true);
			}
			else{
				write = true;
				t.reset();
				stage++;
			}
			break;
			
		case 6:
			if(t.get() < 1){
				if(write){
					System.out.println("Retracting...");
					write = false;
				}
				gearMech.extend(true);
			}
			else{
				System.out.println("Closing...\nRetracting...");
				gearMech.extend(false);
				gearMech.extend(false);
				write = true;
				t.reset();
				stage++;
			}
			break;
		
			
		case 7: // Pause
			if(t.get() > .6){
				t.reset();
				stage++;
			}
			break;
			
		case 8:
			if(t.get() < 1){
				if(write){
					System.out.println("Testing left climber motor, port " + CIAConstants.leftClimberPort + "...");
					write = false;
				}
				//climber.setLeftMotor(.5);
			}
			else{
				//climber.stop();
				write = true;
				t.reset();
				stage++;
			}
			break;
		
		case 9: // Pause
			if(t.get() > .6){
				t.reset();
				stage++;
			}
			break;
			
		case 10:
			if(t.get() < 1){
				if(write){
					System.out.println("Testing right climber motor, port " + CIAConstants.rightClimberPort + "...\n");
					write = false;
				}
				//climber.setRightMotor(.5);
			}
			else{
				//climber.stop();
				write = true;
				t.reset();
				stage++;
			}
			break;
		
		case 11:
			if(driveBase.gyroIsConnected() == true){
				if(t.get() < 20){
					if(write){
						System.out.println("Testing Gyro.\nDO NOT TOUCH THE ROBOT!!!!\nSampling Gyro drift...");
						driveBase.setGyroYaw(0);
						gyroConnected = true;
						write = false;
					}
				}
				else{
					gyroDriftRate = driveBase.getGyroYaw()*3.0;// In degrees per minute
					write = true;
					t.reset();
					stage++;
				}
			}
			else{
				gyroConnected = false;
				write = true;
				t.reset();
				stage++;
			}
			break;
		
		case 12:
			System.out.println("\nDiagnostic sequence complete.\n");
			if(leftEncoderGood && rightEncoderGood && leftDriveAGood && leftDriveBGood && leftDriveCGood && rightDriveAGood && 
					rightDriveBGood && rightDriveCGood && gyroConnected && gyroDriftRate < .5){
				
				System.out.println("Robot is in perfect working order.");
				System.out.println("Gyro drift rate acceptable at " + gyroDriftRate + " degrees/minute.");
			}
			else if(!leftEncoderGood && !rightEncoderGood && !leftDriveAGood && !leftDriveBGood && !leftDriveCGood && !rightDriveAGood && 
					!rightDriveBGood && !rightDriveCGood && !gyroConnected && !(gyroDriftRate < .5)){
						System.out.println("The robot is completely broken. Nothing works.");
						System.out.println("It is recommended that you crawl into a corner, give up and blame Mr. Fleming");
					}
			else{
				if(!leftEncoderGood){
					System.out.println("Left Encoder is not reading as it should.");
					if(!leftEncoderReads) System.out.println("It is reading zero. Check wiring for loose connections.");
					else System.out.println("It still works, but it reads significantly less than the right encoder.");
				}

				if(!rightEncoderGood){
					System.out.println("Right Encoder is not reading as it should.");
					if(!rightEncoderReads) System.out.println("It is reading zero. Check wiring for loose connections.");
					else System.out.println("It still works, but it reads significantly less than the left encoder.");
				}
				if(leftEncoderReads){
					if(!leftDriveAGood) System.out.println("Left Motor at port " + CIAConstants.leftDrivePortA + " is not working!!!!");
					if(!leftDriveBGood) System.out.println("Left Motor at port " + CIAConstants.leftDrivePortB + " is not working!!!!");
				}
				if(rightEncoderReads){
					if(!rightDriveAGood) System.out.println("Right Motor at port " + CIAConstants.rightDrivePortA + " is not working!!!!");
					if(!rightDriveBGood) System.out.println("Right Motor at port " + CIAConstants.rightDrivePortB + " is not working!!!!");
				}
				
				if(!gyroConnected) System.out.println("Gyro is not Connected!!!");
				else if(Math.abs(gyroDriftRate) >= acceptableDriftRate){
					System.out.println("Gyro drift rate is too large at " + gyroDriftRate + " degrees/minute.");
					System.out.println("Recalibration Recommended.");
				}
			}
			System.out.println("---------------------------------------------------------------\n");
			stage++;
			break;
		}
			
	}

	@Override
	public void reset() {
		t.reset();
		stage = 0;
		write = true;
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
