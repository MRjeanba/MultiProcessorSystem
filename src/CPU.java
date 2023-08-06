
public class CPU {
	
	public int id;
	public boolean isAvailable;
	public static int quantumCounter; // static cause all of the cpu's instance get the same quantum
	public int cpuCounter;
	public int quantumComparator;
	public Process runningProcess;
	
	public CPU(int id) {
		this.id = id;
		this.isAvailable = true;
		this.runningProcess = null;
		
	}
	
	public static void setQuantum(int quantum) {
		CPU.quantumCounter = quantum;
	}
	
	// this method assign a process to the cpu instance and set its availability to false
	public void AssignProcess(Process p) {
		this.runningProcess = p;
		this.isAvailable = false;
	}
	
	public void freeCpu() {
		this.runningProcess = null;
		this.isAvailable = true;
		this.quantumComparator = 0;
	}
	
	
	public void executeInstruction() {
		this.runningProcess.programCounter++;
		this.cpuCounter++;
		this.runningProcess.currentInstruction++;
		this.quantumComparator++;
	}
	
	/**
	 * 
	 * @param systemCounter the actual time it took to the cpu scheduler to execute all the processes
	 * @return the ratio of time cpu core / cpu scheduler
	 */
	public double computeCpuUtilization(int systemCounter) {
		double ratio =  ((double) this.cpuCounter / (double) --systemCounter);
		return ratio;
	}
}
