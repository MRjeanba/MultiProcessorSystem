import java.util.LinkedList;
import java.util.Queue;

public class CPU {
	public boolean isAvailable;
	public static int quantumCounter; // public cause all of the cpu's instance get the same quantum
	public int cpuCounter;
	public Process runningProcess;
	
	public CPU() {
		
	}
	
	
	public void executeInstruction() {
		// just increments PC of current running process
	}
}
