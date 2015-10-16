package edu.arizona.biosemantics.etcsitehelp.server.rpc.help;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.etcsitehelp.server.Configuration;
import edu.arizona.biosemantics.etcsitehelp.shared.help.Help;
import edu.arizona.biosemantics.etcsitehelp.shared.rpc.help.IHelpService;

public class HelpService extends RemoteServiceServlet implements IHelpService {
	
	public SafeHtml getHelp(Help help) throws IOException {
		File helpFiles = new File(Configuration.helpFiles);
		File helpFile = new File(helpFiles, help.toString() + ".html");
		
		return SafeHtmlUtils.fromTrustedString(new String(Files.readAllBytes(Paths.get(helpFile.getAbsolutePath())), StandardCharsets.UTF_8));
	}

}
