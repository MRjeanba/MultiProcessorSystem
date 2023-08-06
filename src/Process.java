import java.util.Arrays;

public class Process {
	private String state;
	private String id;
	private int totalExecTime; // execTime
	private int programCounter;
	private int currentInstruction;
	private int arrivalTime;
	private int finishedAtTime;
	private int timeOfFirstCpuResponse;
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
	
	
	public String getState() {
		return state;
	}

	public String getId() {
		return id;
	}


	public int getTotalExecTime() {
		return totalExecTime;
	}


	public int getProgramCounter() {
		return programCounter;
	}
	public void setProgramCounter(int programCounter) {
		this.programCounter = ++programCounter;
	}


	public int getCurrentInstruction() {
		return currentInstruction;
	}
	public void incrementInstr() {
		this.currentInstruction = currentInstruction++;
	}


	public int getFinishedAtTime() {
		return finishedAtTime;
	}


	public void setFinishedAtTime(int finishedAtTime) {
		this.finishedAtTime = finishedAtTime;
	}


	public int getTimeOfFirstCpuResponse() {
		return timeOfFirstCpuResponse;
	}


	public void setTimeOfFirstCpuResponse(int timeOfFirstCpuResponse) {
		this.timeOfFirstCpuResponse = timeOfFirstCpuResponse;
	}


	public int getArrivalTime() {
		return arrivalTime;
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
	public int getCurrentInst() {
		return this.currentInstruction;
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
