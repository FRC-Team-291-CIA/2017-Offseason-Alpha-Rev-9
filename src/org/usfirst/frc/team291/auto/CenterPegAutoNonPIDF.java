package org.usfirst.frc.team291.auto;

import org.usfirst.frc.team291.subsystems.GearMech.GearState;

import edu.wpi.first.wpilibj.Timer;

public class CenterPegAutoNonPIDF extends AutoMode {

	private Timer timer;
	
	public CenterPegAutoNonPIDF(boolean blueSide){
		super(blueSide);
		timer = new Timer();
	}
	
	private State state = State.INIT;
	
	private enum State {
		INIT,
		DRIVE_FORWARD,
		DEPOSIT_GEAR,
		BACK_UP,
		FINISH,
		DONE;
	}
	
	@Override
	public void init() {
		driveBase.zeroSensors();
		
	}

	@Override
	public void execute() {
		switch(state){
			case INIT:
				init();
				timer.start();
				gearMech.setWantedState(GearState.ACQUIRING);
				state = State.DRIVE_FORWARD;
				break;
	
			case DRIVE_FORWARD:
				if(timer.get() < 3){
					driveBase.driveToDistancePID(10, .5, 0);
				}
				else{
					state = State.DEPOSIT_GEAR;
					timer.reset();
				}
				break;
	
			case DEPOSIT_GEAR:
				if(timer.get() < 0.7) gearMech.setWantedState(GearState.SCORING);
				else{
					state = State.BACK_UP;
					timer.reset();
				}
				break;
	
			case BACK_UP:
				if(timer.get() < 1) driveBase.setLeftRightPower(-.4, -.4);
				else state = State.FINISH;
				break;
	
			case FINISH:
				driveBase.stop();
				//gearMech.open(false);
				gearMech.setWantedState(GearState.IDLE);
				System.out.println("Completed ChutePegAuto!");
				state = State.DONE;
				break;
	
			case DONE:
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
