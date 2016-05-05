package edu.arizona.biosemantics.etcsitehelp.server.rpc.help;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsitehelp.server.Configuration;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpService;

@SuppressWarnings("serial")
public class HelpService extends RemoteServiceServlet implements IHelpService {
	
	public String getHelp(Help help) throws IOException {
		File helpFiles = new File(Configuration.helpFiles);
		File helpFile = new File(helpFiles, help.toString() + ".json");
		String path = helpFile.getAbsolutePath();
		
		String helpContent = new String(Files.readAllBytes(Paths.get(path))).replace("\r", "").replace("\t", "  ");
		return helpContent;
	}

}
