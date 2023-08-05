import java.security.cert.CertificateFactorySpi;
import java.util.LinkedList;
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
	
	public CpuScheduler(int nOfCpu, LinkedList<Process> pList, int quantum) {
		CPU.setQuantum(quantum);
		LinkedList<CPU> cpuList = new LinkedList<>();
		for (int i = 0; i < nOfCpu; i++) {
			cpuList.add(new CPU(i));
		}
		this.cpus = cpuList;
		this.processes = pList;
		this.readyQueue = new LinkedList<>();
		this.waitingQueue = new LinkedList<>();
		this.terminatedQueue = new LinkedList<>();
		
	}
	
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
				processes.get(i).stateToReady();
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
			Process nextRunningProcess = readyQueue.poll();
			nextRunningProcess.stateToRunning();
			availableCpu.AssignProcess(nextRunningProcess);
		}
		
	}
	
	public void executeFCFS() {
		
		//if waiting queue is not empty, increment the counter and at modulo 2, pop the queue
		if(!waitingQueue.isEmpty()) {
			
			if (waitingQCounter % 2 == 0) {
				Process finishedIoProcess = waitingQueue.poll();
				System.out.println("");
				finishedIoProcess.stateToReady();
				readyQueue.add(finishedIoProcess);
				waitingQCounter = 0;
			} else {
				waitingQCounter++;
			}
			
			
		}
		
		// check if current processes on cpu are finished, if yes, move them to terminated queue
		for (CPU cpu : cpus) {
			
			if (cpu.runningProcess!= null) {
				// if true, then process is finished, move it to terminated queue and free the cpu
				if(cpu.runningProcess.programCounter > cpu.runningProcess.totalExecTime) {
					cpu.runningProcess.stateToTerminated();
					terminatedQueue.add(cpu.runningProcess);
					cpu.freeCpu();
				}	
			}
		}
		
		// Output of running processes ? 
		// if cpus processes are not done, we run the instructions, first we check for IO request
		for (CPU cpu : cpus) {
			if (cpu.runningProcess != null) {
				// if true, then at this instruction, process should perform an IO request
				if (cpu.runningProcess.programCounter == cpu.runningProcess.getIORequestInstructionNumber()) {
					System.out.println("IO request for process: "  + cpu.runningProcess.id + " On cpu: " + cpu.id);
					cpu.executeInstruction();
					cpu.runningProcess.stateToWaiting();
					waitingQueue.add(cpu.runningProcess);
					cpu.freeCpu();
					continue;
				}
				cpu.executeInstruction();	
			}
		}
		
		System.out.println("Tick number: " + counter);
		for (CPU cpu : cpus) {
			System.out.println("cpu id: " + cpu.id);
			if (cpu.runningProcess != null) {
				System.out.println("\tProcess inside this cpu: " + cpu.runningProcess.showProgress() + "\n---------------------------");

			} else {
				System.out.println("\tNo process on this cpu..."+ "\n---------------------------");
			}
		}
		System.out.println("\n_____________________________");

		
	}
	
	// Non preemptive
	public void FCFS() {
		
		// condition should be while queues are not empty and that cpus are all non available, then run
		while (!readyQueue.isEmpty() || !waitingQueue.isEmpty() || !areAllCpusEmpty() || !processes.isEmpty()) {
			
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
