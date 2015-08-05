package edu.arizona.biosemantics.etcsite.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.arizona.biosemantics.common.log.LogLevel;


public class Zipper implements IZipper {

	public String zip(String source, String destination) {
		String effectiveDestination = destination;
		
		for(int i=0; i<100; i++) {
			File destinationFile = new File(effectiveDestination);
			if(!destinationFile.exists())
				break;
			
			boolean deleteResult = false;
			try {
				deleteResult = destinationFile.delete();
			} catch(Exception e) {
				log(LogLevel.ERROR, "Couldn't delete file", e);
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
			Process process;
			try {
				process = runCommand(command);
				process.waitFor();
			} catch (IOException e) {
				log(LogLevel.ERROR, "Couldn't run zip command", e);
				return null;
			} catch (InterruptedException e) {
				log(LogLevel.ERROR, "Couldn't wait for zip command", e);
				return null;
			}
		}
		return effectiveDestination;
	}
	
	//don't want to get into using ProcessBuilder because command would have to be parsed for its parts to be fed to ProcessBuilder
	private Process runCommand(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		try(BufferedReader stdInput = new BufferedReader(new InputStreamReader(p
				.getInputStream()))) {
			
			// read the output from the command
			String s = "";
			int i = 0;
			while ((s = stdInput.readLine()) != null) {
				log(LogLevel.INFO, "Zipper out: " + s);
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't close reader", e);
		}
		
		try(BufferedReader errInput = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

			// read the errors from the command
			String e = "";
			while ((e = errInput.readLine()) != null) {
				log(LogLevel.INFO, "Zipper err: " + e);
			}
		} catch (IOException e) {
			log(LogLevel.ERROR, "Couldn't close reader", e);
		}
		return p;
	}

	@Override
	public void unzip(String source, String destination) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
