package edu.arizona.sirls.etc.site.server.rpc;

import java.io.File;

import edu.arizona.sirls.etc.site.server.Configuration;

public class Zipper {

	public void zip(String username, String target) throws Exception {
		String sourceDirectory = Configuration.fileBase + "//" + username + "//" + target;
		String tempFile = Configuration.zipFileBase + "//" + username + "//" + target + ".tar";
		String targetFile = Configuration.zipFileBase + "//" + username + "//" + target + ".tar.gz";
		String commandTar = "7za.exe a -ttar " + tempFile + " " + sourceDirectory;
		String commandGz = "7za.exe a -tgzip " + targetFile + " " + tempFile;
		runCommand(commandTar);
		runCommand(commandGz);
		File file = new File(tempFile);
		file.delete();
	}
	
	private void runCommand(String command) throws Exception {
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
	}

}
