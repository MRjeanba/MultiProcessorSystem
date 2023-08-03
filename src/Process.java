import java.util.Arrays;

public class Process {
	public String state;
	public String id;
	public int totalExecTime; // execTime
	public int programCounter;
	public int arrivalTime;
	public int finishedAtTime;
	public int[] IORequestAtInstruction;
	public int ioRequestNumber;
	
	public Process(String id,int nOfInstructions, int[] instructionsRequest) {
		this.state = "ready";
		this.id = id;
		this.totalExecTime = nOfInstructions;
		this.IORequestAtInstruction = instructionsRequest;
		this.ioRequestNumber = 0;
	}
	
	/**
	 * @return the current instruction at which the process is at
	 */
	public void getCurrentInstruction() {
		
	}
	
	/**
	 * Get the instruction at which the next IO request will be executed
	 * @return the instruction number when a new IO request should be executed
	 */
	public int getIORequestInstructionNumber() {
		
		if(this.ioRequestNumber >= this.IORequestAtInstruction.length) {
			return -1;
		}
		return this.IORequestAtInstruction.length == 0 ?  -1 :  this.IORequestAtInstruction[this.ioRequestNumber];
	}
	
	
	public void stateToWaiting() {
		this.state = "waiting";

	}
	
	
	public void stateToRunning() {
		this.state = "running";
	}
	
	
	public void stateToReady() {
		this.state = "ready";
	}
	
	
	public void stateToTerminated() {
		this.state = "terminated";
	}
	
	@Override
	public String toString() {
		return "Process informations: \nid: " + this.id + "\nInstructions: " + this.totalExecTime + "\nArray of instructions: " + Arrays.toString(this.IORequestAtInstruction) +
				"\nDevices Requested: " + "\nState:" + this.state; 
	}
}
