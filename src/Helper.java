import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * THis class will help me to read the data from the text file in the project, so basically extract all the informations about the processes and also the quantum
 * @author Jean-Baptiste Garibo
 *
 */
public class Helper {	
	
	
	
	public static String getFileContent(String fileName) {
		File processFile = new File(fileName);
		Scanner fileReader;
		String fileContent = "";
		try {
			fileReader = new Scanner(processFile);
			
			while (fileReader.hasNextLine()) {
				String currLine = fileReader.nextLine();
				if(!currLine.contains("//")) {
					fileContent += currLine + "\n";
				}
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Here is your file content :\n" + fileContent);
		
		return fileContent;
	}
	
	/**
	 * Based on the file content, initialize the cpu scheduler, assign the number of cores, the quantum and the processes
	 * @param fileContent: the string grabbed from the text file, should contains all the info above
	 * @return a cpu scheduler with the cores and processes
	 */
	public static CpuScheduler initScheduler(String fileContent) {
		
		int numOfCpus = 0;
		int quantum = 0;
		LinkedList<Process> processes = new LinkedList<Process>();
		
		String[] fileContentAsArray = fileContent.split("\n");
		for (String string : fileContentAsArray) {
			
			// for the number of cores
			if (string.contains("numOfCPUs")) {
				int idx = string.indexOf('=');
				try {
					numOfCpus = Integer.parseInt(string.substring(idx+1));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("an error occured for the parse int for the number of cpus");
				}
				continue;
			}
			// for the quantum value
			if (string.contains("q=")) {
				int idx = string.indexOf('=');
				try {
					quantum = Integer.parseInt(string.substring(idx+1));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("an error occured for the parse int for the quantum");
				}
				continue;
				
			}
			
			// for the processes
			if(string.contains("p")) {
				String[] processInfo = string.split("\t");
				try {
					int arrival = Integer.parseInt(processInfo[1]);
					int execTime = Integer.parseInt(processInfo[2]);
					String num = processInfo[3].replace("[", "");
					num = num.replace("]", "");
					
					// In this case, there is no IO request 
					if (num.length() == 0) {
						processes.add(new Process(processInfo[0], arrival, execTime, new int[0]));
						
					} 
					else {
						String[] ioRequestString = num.split(",");
						int[] ioRequ = new int[ioRequestString.length];
						
						for(int i = 0; i<ioRequestString.length;i++) {
							ioRequ[i] = Integer.parseInt(ioRequestString[i]);
						}
						processes.add(new Process(processInfo[0], arrival, execTime, ioRequ));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
				
			}
		}
		return new CpuScheduler(numOfCpus, processes, quantum);

	}
	
	
	
}
