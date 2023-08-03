
public class CPU {
	
	public int id;
	public boolean isAvailable;
	public static int quantumCounter; // public cause all of the cpu's instance get the same quantum
	public int cpuCounter;
	public Process runningProcess;
	
	public CPU() {
		
	}
	
	// this method assign a process to the cpu instance and set its availability to false
	public void AssignProcess(Process p) {
		this.runningProcess = p;
		this.isAvailable = false;
	}
	
	public void freeCpu() {
		this.runningProcess = null;
		this.isAvailable = true;
	}
	
	
	public void executeInstruction() {
		this.runningProcess.programCounter++;
		this.cpuCounter++;
		
	}
}
