package edu.arizona.biosemantics.etcsite.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


public class Zipper {

	public String zip(String source, String destination) throws Exception {
		String effectiveDestination = destination;
		
		for(int i=0; i<100; i++) {
			File destinationFile = new File(effectiveDestination);
			if(!destinationFile.exists())
				break;
			
			boolean deleteResult = false;
			try {
				deleteResult = destinationFile.delete();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(!deleteResult) {
				int lastDot = destination.lastIndexOf(".");
				if(lastDot != -1)
					effectiveDestination = destination.substring(0, lastDot) + "(" + i + ")" + destination.substring(lastDot, destination.length());
				else
					effectiveDestination = destination + "(" + i + ")";
			}
		}
		
		
		File sourceFile = new File(source);
		if(sourceFile.exists()) {
			String command = Configuration.compressCommand; 
			command = command.replace("[destination]", effectiveDestination);
			String sourceFileName = sourceFile.getName();
			String sourceParent = sourceFile.getParentFile().getAbsolutePath();
			command = command.replace("[source]", sourceFileName);
			command = command.replace("[sourceParent]", sourceParent);
			Process process = runCommand(command);
			process.waitFor();
		}
		return effectiveDestination;
	}
	
	//don't want to get into using ProcessBuilder because command would have to be parsed for its parts to be fed to ProcessBuilder
	private Process runCommand(String command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
				.getInputStream()))) {
			
			// read the output from the command
			String s = "";
			int i = 0;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
		}
		
		try(BufferedReader errInput = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

			// read the errors from the command
			String e = "";
			while ((e = errInput.readLine()) != null) {
				System.out.println(e);
			}
		}
		return p;
	}

}