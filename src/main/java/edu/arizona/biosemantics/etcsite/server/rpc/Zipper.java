package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;

public class Zipper {

	public void zip(String source, String destination) throws Exception {
		File sourceFile = new File(source);
		if(sourceFile.exists()) {
			String command = "tar -zcvf " + destination + " --directory=" + sourceFile.getParent() + " " + sourceFile.getName();
			Process process = runCommand(command);
			process.waitFor();
		
		
		/*String sourceDirectory = filePath;
		String tempFile = filePath + ".tar";
		String destinationFile = filePath + ".tar.gz";
		String commandTar = "7za.exe a -ttar " + tempFile + " " + sourceDirectory;
		String commandGz = "7za.exe a -tgzip " + destinationFile + " " + tempFile;
		Process process = runCommand(commandTar);
		process.waitFor();
		process = runCommand(commandGz);
		process.waitFor();
		File file = new File(tempFile);
		file.delete();*/
		}
	}
	
	private Process runCommand(String command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		/*BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		
		BufferedReader errInput = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
		
		// read the output from the command
		String s = "";
		int i = 0;
		while ((s = stdInput.readLine()) != null) {
			System.out.println(s);
		}
		
		// read the errors from the command
		String e = "";
		while ((e = errInput.readLine()) != null) {
			System.out.println(s);
		}*/
		return p;
	}

}
