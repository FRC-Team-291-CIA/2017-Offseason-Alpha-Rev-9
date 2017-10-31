package org.usfirst.frc.team291.auto;

/*
 * AutoSelector.java
 * Uses a switch box connected to the DIO ports on the MXP ports
 * on the RoboRIO. 
 * @author Julia Cecchetti
 */
import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.first.wpilibj.DigitalInput;

public class AutoSelector {
	private DigitalInput A1 = new DigitalInput(CIAConstants.A1Port);//1
	private DigitalInput A2 = new DigitalInput(CIAConstants.A2Port);//2
	private DigitalInput B1 = new DigitalInput(CIAConstants.B1Port);//
	private DigitalInput B2 = new DigitalInput(CIAConstants.B2Port);
	private DigitalInput C1 = new DigitalInput(CIAConstants.C1Port);
	private DigitalInput C2 = new DigitalInput(CIAConstants.C2Port);
	private DigitalInput D1 = new DigitalInput(CIAConstants.D1Port);//Blue Side
	
	AutoMode autoMode;
	
	public int autoCase(){
		int autoCase = 0;
		if(!A1.get()) autoCase += 1;
		else if(!A2.get()) autoCase += 2;
		if(!B1.get()) autoCase += 3;
		else if(!B2.get()) autoCase += 6;
		if(!C1.get()) autoCase += 9;
		else if(!C2.get()) autoCase += 18;
		
		return autoCase;
	}
	public boolean blueAlliance(){
		return !D1.get();
	}
	
	public AutoMode selectAuto(){
		boolean blueAlliance = !D1.get();
		int mode = autoCase();
		
		switch(mode){
		case 0:
			autoMode = new DoNothingAuto(blueAlliance);
			break;
		case 1:
			autoMode = new DriveForwardTimeAuto();//GOOD
			break;
		case 2:
			autoMode = new CenterPegAutoNonPIDF(blueAlliance);//Not done!!
			break;
		case 3:
			autoMode = new CenterPegAuto(blueAlliance);//GOOD
			break;
		case 4:
			autoMode = new Shoot10Auto(blueAlliance);//GOOD
			break;
		case 5:
			autoMode = new WallToHopperShootNonPIDFSketchy(blueAlliance);//Not done 
			break;
		case 6:
			autoMode = new WallToHopperShoot(blueAlliance);//GOOD
			break;
		case 7:
			autoMode = new ChutePegAutoNonPIDF(blueAlliance);//not done
			break;
		case 8:
			autoMode = new BoilerPegAndShoot10AutoNonPIDF(blueAlliance);//not done
			break;
		default:
			autoMode = new DoNothingAuto(blueAlliance);
		}
		
		return autoMode;
	}
	

}
