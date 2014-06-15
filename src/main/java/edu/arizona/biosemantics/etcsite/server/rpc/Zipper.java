package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import edu.arizona.biosemantics.etcsite.server.Configuration;

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
			File compressedFileBaseFile = new File(Configuration.compressedFileBase);
			String relativeSource = sourceFile.getAbsolutePath().replace(compressedFileBaseFile.getAbsolutePath(), "");
			command = command.replace("[source]", relativeSource);
			command = command.replace("[compressedFileBase]", compressedFileBaseFile.getAbsolutePath());
			Process process = runCommand(command);
			process.waitFor();
		}
		return effectiveDestination;
	}
	
	private Process runCommand(String command) throws Exception {
		Process p = Runtime.getRuntime().exec(command);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
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
		}
		return p;
	}

}
