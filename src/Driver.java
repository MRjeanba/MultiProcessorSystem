
public class Driver {

	public static String fileName = "input.txt";

	public static void main(String[] args) {
		String fileContent =  Helper.getFileContent(fileName);
		CpuScheduler scheduler = Helper.initScheduler(fileContent);

		scheduler.run();
	}

}
