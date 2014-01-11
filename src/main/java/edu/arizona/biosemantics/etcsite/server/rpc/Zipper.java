package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.File;

import edu.arizona.biosemantics.etcsite.server.Configuration;

public class Zipper {

	public void zip(String source, String destination) throws Exception {
		File sourceFile = new File(source);
		if(sourceFile.exists()) {			
			String command = Configuration.compressCommand; 
			command = command.replace("[destination]", destination);
			command = command.replace("[source.parent]", sourceFile.getParent());
			command = command.replace("[source.filename]", sourceFile.getName());
			Process process = runCommand(command);
			process.waitFor();
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
