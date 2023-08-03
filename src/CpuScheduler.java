import java.security.cert.CertificateFactorySpi;
import java.util.List;
import java.util.Queue;

public class CpuScheduler {
	
	public List<CPU> cpus;
	public List<Process> processes;
	public Queue<Process> readyQueue;
	public Queue<Process> waitingQueue;
	public Queue<Process> terminatedQueue;
	public int counter;
	public int waitingQCounter;
	
	// check if the cpus on the cpu scheduler do not have a process
	public boolean areAllCpusEmpty() {
		
		for (CPU cpu : cpus) {
			// cpu has a process
			if(cpu.runningProcess != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Help to check if the cpus can take processes from the ready queue
	 * @return true if cpus can take process else false
	 */
	public boolean cpusCanTakeProcess() {
		
		for (CPU cpu : cpus) {
			if (cpu.runningProcess == null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * In here we check if arrival time of processes is equal to the time, if yes we move them from the list of 
	 * processes to the ready queue. If not we do nothing in here
	 * @param currentCounter: The current time 
	 */
	public void moveProcessesToReadyQueueIfArrived(int currentCounter) {
		
		if (processes.isEmpty()) {
			return;
		}
		
		for (int i = 0; i < processes.size(); i++) {
			
			// means the current process is ready
			if(processes.get(i).arrivalTime == currentCounter) {
				readyQueue.add(processes.get(i));
				processes.remove(i);
			}
		}
	}
	
	/**
	 * Loop through the list of cpu and return the first one available
	 * @return an available cpu ready to take a process or null
	 */
	public CPU getAvailableCpu() {
		
		for (CPU cpu : cpus) {
			if(cpu.isAvailable) {
				return cpu;
			}
		}
		return null;
	}
	
	/**
	 * Move the processes in the ready queue in the available cpus
	 */
	public void moveReadyProcessesToAvailableCpu() {
		
		// while I can put processes on CPU, I move them on the available cores until no core is available or until i dont have anymore ready processes
		while(cpusCanTakeProcess() && !readyQueue.isEmpty()) {
			CPU availableCpu = getAvailableCpu();
			availableCpu.AssignProcess(readyQueue.poll());
		}
		
	}
	
	public void executeFCFS() {
		
		//if waiting queue is not empty, increment the counter and at modulo 2, pop the queue
		if(!waitingQueue.isEmpty()) {
			
			if (waitingQCounter % 2 == 0) {
				readyQueue.add(waitingQueue.poll());
				waitingQCounter = 0;
			} else {
				waitingQCounter++;
			}
			
			
		}
		
		// check if current processes on cpu are finished, if yes, move them to terminated queue
		for (CPU cpu : cpus) {
			// if true, then process is finished, move it to terminated queue and free the cpu
			if(cpu.runningProcess.programCounter > cpu.runningProcess.totalExecTime) {
				terminatedQueue.add(cpu.runningProcess);
				cpu.freeCpu();
			}
		}
		
		// Output of running processes ? 
		// if cpus processes are not done, we run the instructions, first we check for IO request
		for (CPU cpu : cpus) {
			
			// if true, then at this instruction, process should perform an IO request
			if (cpu.runningProcess.programCounter == cpu.runningProcess.getIORequestInstructionNumber()) {
				System.out.println("IO request for process: "  + cpu.runningProcess.id + " On cpu: " + cpu.id);
				cpu.executeInstruction();
				waitingQueue.add(cpu.runningProcess);
				cpu.freeCpu();
				continue;
			}
			cpu.executeInstruction();
		}
		
	}
	
	// Non preemptive
	public void FCFS() {
		
		// condition should be while queues are not empty and that cpus are all non available, then run
		while (!readyQueue.isEmpty() || !waitingQueue.isEmpty() || !areAllCpusEmpty()) {
			
			// 1- we check if some processes "arrived" at time t
			moveProcessesToReadyQueueIfArrived(counter);
			
			// 2- put the arrived processes on cpus if cpus are available
			moveReadyProcessesToAvailableCpu();
			
			// once ready processes are assigned to available cpus, I execute the instructions nonpremptively for FCFS
			executeFCFS();
			
			// increment the counter of the program
			counter++;
			
		}
		
		
	}
	
	// Non preemptive
	public void SJF() {
		
	}
	
	// display a sort of menu to the user where he choose which algo the scheduler will use
	public void run() {
		
	}

	// Non preemptive you only go out of the cpu if your time quantum has exceeded
	public void RR() {
		
	}
}
