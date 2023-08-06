
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class CpuScheduler {
	
	public List<CPU> cpus;
	public List<Process> processes;
	public Queue<Process> readyQueue;
	public PriorityQueue<Process> sjfReadyQueue;
	public Queue<Process> waitingQueue;
	public Queue<Process> terminatedQueue;
	public int counter;
	public int waitingQCounter;
	
	// this class is used by the sjf algorithm to have a priority queue in function of the execution time of the process
	class ProcessComparator implements Comparator<Process> {

		@Override
		public int compare(Process p1, Process p2) {
			if (p1.totalExecTime > p2.totalExecTime)
                return 1;
            else if (p1.totalExecTime < p2.totalExecTime)
                return -1;
                            return 0;

		}
		
	}
	
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
		this.sjfReadyQueue = new PriorityQueue<>(new ProcessComparator());
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
		
		for (int i = 0; i <= processes.size(); i++) {
			//System.out.println("procesees size:" + processes.size() + "\n i value: "+ i);
			if (processes.size() == 1 && processes.get(0).arrivalTime == counter) {
				processes.get(0).stateToReady();
				processes.get(0).setArrivalTime(currentCounter);
				sjfReadyQueue.add(processes.get(0));
				readyQueue.add(processes.get(0));
				processes.remove(processes.get(0));
				break;
			}
			// means the current process is ready
			if(processes.get(i).arrivalTime == currentCounter) {
				processes.get(i).stateToReady();
				processes.get(i).setArrivalTime(currentCounter);
				sjfReadyQueue.add(processes.get(i));
				readyQueue.add(processes.get(i));
				processes.remove(processes.get(i));
				i=0;
			}
		}
		
		//processes.clear();
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
			// here we get the first response of the cpu to the process
			if (nextRunningProcess.currentInstruction == 0) {
				nextRunningProcess.timeOfFirstCpuResponse = counter;
			}
			nextRunningProcess.stateToRunning();
			availableCpu.AssignProcess(nextRunningProcess);
		}
		
	}
	/**
	 * Similar to moveReadyProcessesToAvailableCpu except that it will replace running processes if their execution
	 * time is higher than the ones in the ready queue
	 */
	public void moveReadyProcessesToCpuSjf() {
		
		// while I can put processes on CPU, I move them on the available cores until no core is available or until i dont have anymore ready processes
		while(cpusCanTakeProcess() && !sjfReadyQueue.isEmpty()) {
			CPU availableCpu = getAvailableCpu();
			Process nextRunningProcess = sjfReadyQueue.poll();
			// here we get the first response of the cpu to the process
			if (nextRunningProcess.currentInstruction == 0) {
				nextRunningProcess.timeOfFirstCpuResponse = counter;
			}
			nextRunningProcess.stateToRunning();
			availableCpu.AssignProcess(nextRunningProcess);
		}
		
		if (!sjfReadyQueue.isEmpty()) {
			
			for (CPU cpu : cpus) {
				// if the condition is true, we need to swap theses two processes or just move 
				if (cpu.isAvailable && !sjfReadyQueue.isEmpty()) {
					cpu.AssignProcess(sjfReadyQueue.poll());
					continue;
				}
				else if ((!cpu.isAvailable &&!sjfReadyQueue.isEmpty())  &&  cpu.runningProcess.totalExecTime > sjfReadyQueue.peek().totalExecTime) {
					System.out.println("swap running process: " + cpu.runningProcess.id + " with process: " + sjfReadyQueue.peek().id);
					cpu.runningProcess.stateToReady();
					sjfReadyQueue.add(cpu.runningProcess);
					cpu.freeCpu();
					cpu.AssignProcess(sjfReadyQueue.poll());
					continue;
				}
			}
			
		}

	}
	public void manageIoRequests() {
		//if waiting queue is not empty, increment the counter and at modulo 2, pop the queue
				if(!waitingQueue.isEmpty()) {
					System.out.println("\twaiting q counter  = " + waitingQCounter);
					if ((waitingQCounter + 1) % 2 == 0) {
						Process finishedIoProcess = waitingQueue.poll();
						System.out.println("IO request fullfilled on next iteration for process: " + finishedIoProcess.id);
						finishedIoProcess.stateToReady();
						readyQueue.add(finishedIoProcess);
						sjfReadyQueue.add(finishedIoProcess);
						waitingQCounter = 0;
					} else {
						waitingQCounter++;
					}
					
					
				}
	}
	
	public void checkIfProcessFinished() {
		// check if current processes on cpu are finished, if yes, move them to terminated queue
				for (CPU cpu : cpus) {
					
					if (cpu.runningProcess!= null) {
						// if true, then process is finished, move it to terminated queue and free the cpu
						if(cpu.runningProcess.programCounter > cpu.runningProcess.totalExecTime) {
							cpu.runningProcess.stateToTerminated();
							cpu.runningProcess.setFinishedTime(counter);
							terminatedQueue.add(cpu.runningProcess);
							cpu.freeCpu();
						}
					}
				}
	}
	
	public void outputCurrentTick() {
		System.out.println("\tTick number: " + counter);
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
	
	
	
	public void executeFCFS() {
		
		manageIoRequests();
		
		// check if current processes on cpu are finished, if yes, move them to terminated queue
		checkIfProcessFinished();
		
		outputCurrentTick();

		// Output of running processes ? 
		// if cpus processes are not done, we run the instructions, first we check for IO request
		for (CPU cpu : cpus) {
			if (cpu.runningProcess != null) {
				// if true, then at this instruction, process should perform an IO request
				if (cpu.runningProcess.currentInstruction == cpu.runningProcess.getIORequestInstructionNumber()) {
					System.out.println("IO request for process: "  + cpu.runningProcess.id + " On cpu: " + cpu.id);
					cpu.executeInstruction();
					cpu.runningProcess.stateToWaiting();
					cpu.runningProcess.ioRequestNumber++;
					waitingQueue.add(cpu.runningProcess);
					cpu.freeCpu();
					continue;
				}
				cpu.executeInstruction();	
			}
		}
		
	}
	
	public void executeRR() {
		
		// check if IO is sucessful or not
		manageIoRequests();
		
		// check if current processes on cpu are finished, if yes, move them to terminated queue
		checkIfProcessFinished();
		
		outputCurrentTick();

		// Process progression for Round Robin
		for (CPU cpu : cpus) {
			if (cpu.runningProcess != null) {
				
				// check if current quantum comparator modulo quantum equals 0, if yes move process to ready Q
				if ((cpu.quantumComparator + 1) % CPU.quantumCounter == 0) {
					cpu.runningProcess.stateToReady();
					System.out.println("Process " + cpu.runningProcess.id + " Moved to ready queue after quantum of: " + ++cpu.quantumComparator + "\nthis is the state of the process: " + cpu.runningProcess.state);
					readyQueue.add(cpu.runningProcess);
					System.out.println("ready queue content: " + readyQueue.toString());
					cpu.freeCpu();
					continue;
				}
				// if true, then at this instruction, process should perform an IO request
				if (cpu.runningProcess.currentInstruction == cpu.runningProcess.getIORequestInstructionNumber()) {
					System.out.println("IO request for process: "  + cpu.runningProcess.id + " On cpu: " + cpu.id);
					cpu.executeInstruction();
					cpu.runningProcess.stateToWaiting();
					cpu.runningProcess.ioRequestNumber++;
					waitingQueue.add(cpu.runningProcess);
					cpu.freeCpu();
					continue;
				}
				cpu.executeInstruction();	
			}
		}

	}
	
	public void executeSJF() {
		// check if IO is sucessful or not
		manageIoRequests();
		
		// check if current processes on cpu are finished, if yes, move them to terminated queue
		checkIfProcessFinished();
		
		outputCurrentTick();

		// Output of running processes ? 
		// if cpus processes are not done, we run the instructions, first we check for IO request
		for (CPU cpu : cpus) {
			if (cpu.runningProcess != null) {
				// if true, then at this instruction, process should perform an IO request
				if (cpu.runningProcess.currentInstruction == cpu.runningProcess.getIORequestInstructionNumber()) {
					System.out.println("IO request for process: "  + cpu.runningProcess.id + " On cpu: " + cpu.id);
					cpu.executeInstruction();
					cpu.runningProcess.stateToWaiting();
					cpu.runningProcess.ioRequestNumber++;
					waitingQueue.add(cpu.runningProcess);
					cpu.freeCpu();
					continue;
				}
				cpu.executeInstruction();	
			}
		}
	}
	
	/**
	 * Compute the avg waiting time of all the processes once terminated
	 * @return an double holding the avg waiting time of the processes
	 */
	public double computeAvgWaitTime() {
		int numerator = 0;
		
		for (Process finishedProcess : terminatedQueue) {
			numerator+= (finishedProcess.getTurnaroundTime() - finishedProcess.totalExecTime);
		}
		return ((double)numerator/(double)terminatedQueue.size());
	}
	
	/**
	 * This method just output the performance of the current algorithm used
	 * @param algoName the name of the algorithm used
	 */
	public void printPerformance(String algoName) {
		System.out.println("__________________________________________________________________\nPrinting the performance details of the " + algoName +" scheduling algorihtm:");
		for (CPU cpu : cpus) {
			System.out.printf("CPU utilization for cpu "+ cpu.id +": ");
			System.out.printf("%.2f%n" , cpu.computeCpuUtilization(counter));
		}
		System.out.println("__________________________________________________________________\nAverage waiting time for the processes:");
		System.out.printf("%.2f time unit", computeAvgWaitTime());
		System.out.println("\n__________________________________________________________________\nPrinting the turnaround time for each process");
		
		for (Process p : terminatedQueue) {
			System.out.println(p.id+": " + p.getTurnaroundTime() + " time unit");
		}
		System.out.println("\n__________________________________________________________________\nPrinting the CPU response time for each process");
		for (Process p : terminatedQueue) {
			System.out.println(p.id+": " + p.getCpuResponseTime() + " time unit");
		}
	}
	
	// Non preemptive
	public void FCFS() {
		
		// condition should be while queues are not empty and that cpus are all non available, then run
		while (!readyQueue.isEmpty() || !waitingQueue.isEmpty() || !areAllCpusEmpty() || !processes.isEmpty()) {
			
			// 1- we check if some processes "arrived" at time t
			moveProcessesToReadyQueueIfArrived(counter);
			
			// 2- put the arrived processes on cpus if cpus are available
			moveReadyProcessesToAvailableCpu();
			
			// Output of running processes ?
			
			// once ready processes are assigned to available cpus, I execute the instructions nonpremptively for FCFS
			executeFCFS();
			
			// increment the counter of the program
			counter++;
			
		}
		
		printPerformance("FCFS");
		
	}
	
	// Non preemptive
	public void SJF() {
		
		// condition should be while queues are not empty and that cpus are all non available, then run
		while (!sjfReadyQueue.isEmpty() || !waitingQueue.isEmpty() || !areAllCpusEmpty() || !processes.isEmpty()) {
			
			// 1- we check if some processes "arrived" at time t
			moveProcessesToReadyQueueIfArrived(counter);
			System.out.println("content of the ready queue: " + sjfReadyQueue.toString());

			// 2- put the arrived processes on cpus if cpus are available
			moveReadyProcessesToCpuSjf();
			
			// once ready processes are assigned to available cpus, I execute the instructions nonpremptively for FCFS
			executeSJF();

			// increment the counter of the program
			counter++;
		}
		printPerformance("SJF");

		
	}
	
	// display a sort of menu to the user where he choose which algo the scheduler will use
	public void run() {
		int userInput = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to the CPU scheduler simulation!\nTo simulate one of the scheduling algorithm type one of these number:\n"
				+ "1: First Come First Served\n"
				+ "2: Shortest Job First\n"
				+ "3: Round Robin");
		
		do {
			try {
				userInput = Integer.parseInt(sc.nextLine());
				if ( userInput > 3 || userInput < 1 ) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Oops, please only enter a number between 1-3 inclusive:");
			}
		} while( userInput > 3 || userInput < 1);
		
		switch (userInput) {
		case 1:
			FCFS();
			break;
		case 2:
			SJF();
			break;
		case 3:
			RR();
			break;
		default:
			break;
		}
		sc.close();
	
	}

	// Non preemptive you only go out of the cpu if your time quantum has exceeded
	public void RR() {
		// condition should be while queues are not empty and that cpus are all non available, then run
		while (!readyQueue.isEmpty() || !waitingQueue.isEmpty() || !areAllCpusEmpty() || !processes.isEmpty()) {
			
			// 1- we check if some processes "arrived" at time t
			moveProcessesToReadyQueueIfArrived(counter);
			
			// 2- put the arrived processes on cpus if cpus are available
			moveReadyProcessesToAvailableCpu();
			
			// once ready processes are assigned to available cpus, I execute the instructions nonpremptively for FCFS
			executeRR();

			// increment the counter of the program
			counter++;
		}
		
		printPerformance("RR");

	}
}
