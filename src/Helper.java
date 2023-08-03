import java.io.File;
import java.io.FileNotFoundException;
import java.util.Currency;
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
		System.out.println("Here is your file content :\n" + fileContent);
		
		return fileContent;
	}
	
	/**
	 * Based on the file content, initialize the cpu scheduler, assign the number of cores, the quantum and the processes
	 * @param fileContent: the string grabbed from the text file, should contains all the info above
	 * @return a cpu scheduler
	 */
	public static CpuScheduler initScheduler(String fileContent) {
		
	}
	
	
	
}
