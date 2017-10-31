package org.usfirst.frc.team291.vision;

/*
 * CIAVisionThread.java
 * This processes the images in a separate thread to track the goal. 
 * Uses several methods to identify the target.
 * @author Julia Cecchetti
 */

import java.util.ArrayList;

import org.opencv.core.Mat;

import org.usfirst.frc.team291.robot.CIAConstants;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CIAVisionThread extends Thread {
	
	private double centerX, centerY = 0.0;
	private boolean enabled;
	
	UsbCamera camera;
	private final GripPipeline gripPipeline = new GripPipeline();;
	private final Object imgLock = new Object();
	private final CvSink cvSink = new CvSink("CIA CvSink");
	private final Mat image = new Mat();
	
	private boolean print = true;
	private double xTolerance = 6;//Maximum difference in x positions for a pair of targets
	private double widthTolerance = 11;//Max difference in width for a pair of targets
	private boolean foundTarget = false;
	
	public CIAVisionThread(){
		super("CIA Vision Thread");
		setDaemon(true);
		
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(CIAConstants.IMG_WIDTH, CIAConstants.IMG_HEIGHT);
		camera.setExposureManual(0);
		camera.setWhiteBalanceManual(4500);

		cvSink.setSource(camera);//Very important
		enabled = true;
		start();
	}
	
	public void run(){
		while (!Thread.interrupted()) {
			long frameTime = cvSink.grabFrame(image);
			
			if (frameTime == 0) { // There was an error, report it
				System.out.println("ERROR! The Camera is Not Connected!!");
				try { // If there is an error, sleep so the console is not continuously spamed.
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			else if(enabled){ // No errors, and thread is enabled. Process the image
				gripPipeline.process(image);
				
				if(print){
					System.out.println("CIAVisionThread is working properly. Woooo!");// Celebrate no errors!
					print = false;
				}
				
				ArrayList<double[]> data;
				double[][] realTarget = new double[2][5];
				foundTarget = false;
				//System.out.println(gripPipeline.targetData().size());
				if(gripPipeline.targetData().size() > 1) {// Make sure it sees at least two objects
					
					synchronized (imgLock) {
						/*Format of targetData: {centerX, centerY, width, height, width/height, area}*/
						data = new ArrayList<double[]>(gripPipeline.targetData());// make a copy of targetData
					}
					
					sortByIndex(data, 0);// sort the objects by x position
					//System.out.println(data.size());
					for(int i = 0; i < data.size() - 1; i++){
						//System.out.println(realTarget[0]);
						if(Math.abs(data.get(i)[0] - data.get(i+1)[0]) < xTolerance){//test relative x positions
							if(Math.abs(data.get(i)[2] - data.get(i+1)[2]) < widthTolerance){//test relative widths
								if(realTarget[0][0] > 0.0){//check whether array has already been filled with a different target
									if((realTarget[0][2] < data.get(i)[2])){
										realTarget[0] = data.get(i);
										realTarget[1] = data.get(i+1);
										System.out.println("found second");
									}
								} 
								else{
									realTarget[0] = data.get(i);
									realTarget[1] = data.get(i+1);
									foundTarget = true;
								}
							}
						}
					}
					if(foundTarget){//only set x and y if a target was found
						centerX = (realTarget[0][0] + realTarget[1][0]) / 2;//Average the x positions of the two strips of tape
						centerY = (realTarget[0][1] < realTarget[1][1]) ? realTarget[0][1] : realTarget[1][1];// Give y positions of upper piece of tape
					}
					else{
						centerX = -1;
						//if target is not found, only reset x so the flywheels stay up to speed according to 
						//the last known Y position. This is because the ring light tends to dim periodically and lose the target.
					}
				}
				else{//if the robot does not see at least 2 objects
					centerX = -1;
				}
			}
			else { // If thread is disabled, sleep to conserve CPU
				centerX = -1;
				centerY = -1;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sortByIndex(ArrayList<double[]> list, int indexToSort){
		for (int i = 0; i < list.size() - 1; i++) {
			int minIndex = i;// Index of smallest remaining value.
			for (int j = i+1; j < list.size(); j++) {
				if (list.get(minIndex)[indexToSort] > list.get(j)[indexToSort]) {
					minIndex = j;  // Store index of new minimum
				}
			}
			if (minIndex != i) { 
				// Switch current element with smallest remaining element.
				double[] temp = list.get(i);
				list.set(i, list.get(minIndex));
				list.set(minIndex, temp);
			}
		}
	}
	
	//Getters and Setters
	public synchronized double getCenterX(){
		return centerX;
	}
	
	public synchronized double getCenterY(){
		return centerY;
	}
	
	public void setEnabled(boolean enable){
		enabled = enable;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public boolean cameraIsConnected(){
		return camera.isConnected();
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("centerX", centerX);
		SmartDashboard.putNumber("centerY", centerY);
		//SmartDashboard.putNumber("Number of Targets", gripPipeline.targetData().size());
		SmartDashboard.putBoolean("Target Found", foundTarget);
		SmartDashboard.putBoolean("Camera Connected", cameraIsConnected());
	}
}
