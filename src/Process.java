import java.util.Arrays;

public class Process {
	public String state;
	public String id;
	public int totalExecTime; // execTime
	public int programCounter;
	public int currentInstruction;
	public int arrivalTime;
	public int finishedAtTime;
	public int timeOfFirstCpuResponse;
	public int[] IORequestAtInstruction;
	public int ioRequestNumber;
	
	public Process(String id,int arrivalTime, int nOfInstructions, int[] instructionsRequest) {
		this.state = "new";
		this.id = id;
		this.arrivalTime = arrivalTime;
		this.totalExecTime = nOfInstructions;
		this.IORequestAtInstruction = instructionsRequest;
		this.ioRequestNumber = 0;
		this.programCounter = 1;
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
	
	public int getTurnaroundTime() {
		return this.finishedAtTime - this.arrivalTime;
	}
	
	public int getCpuResponseTime() {
		return this.timeOfFirstCpuResponse - this.arrivalTime;
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
	public void setArrivalTime(int arrival) {
		this.arrivalTime = arrival;
	}
	public void setFinishedTime(int finishedTime) {
		this.finishedAtTime = finishedTime;
	}
	
	@Override
	public String toString() {
		return "\n\tid: " + this.id + "\n\tInstructions: " + this.totalExecTime + "\n\tArray of instructions: " + Arrays.toString(this.IORequestAtInstruction) +
			 "\n\tState:" + this.state+"\n"; 
	}
	public String showProgress() {
		return "\n\tid: " + this.id + "\n\tPC: " + this.programCounter + "\n\tTotal execution time to do: " +this.totalExecTime +   "\n\tArray of instructions: " + Arrays.toString(this.IORequestAtInstruction) +
				 "\n\tState:" + this.state; 
	}
}
