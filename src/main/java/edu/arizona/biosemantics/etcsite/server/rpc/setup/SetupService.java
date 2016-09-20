package edu.arizona.biosemantics.etcsite.server.rpc.setup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.client.common.ServerSetup;
import edu.arizona.biosemantics.etcsite.server.Configuration;
import edu.arizona.biosemantics.etcsite.shared.model.Setup;
import edu.arizona.biosemantics.etcsite.shared.rpc.file.setup.ISetupService;

public class SetupService extends RemoteServiceServlet implements ISetupService {
	
	public SetupService() {
		ServerSetup.getInstance().setSetup(getSetup());
	}
	
	@Override
	protected void doUnexpectedFailure(Throwable t) {
		String message = "Unexpected failure";
		log(message, t);
	    log(LogLevel.ERROR, "Unexpected failure", t);
	    super.doUnexpectedFailure(t);
	}
	
	@Override
	public Setup getSetup() {		
		String seperator = File.separator;
		Setup setup = new Setup();
		setup.setSeperator(seperator);
		setup.setFileBase(Configuration.fileBase);
		setup.setPublicFolder(Configuration.publicFolder);
		setup.setGoogleClientId(Configuration.googleClientId);
		setup.setGoogleRedirectURI(Configuration.googleRedirectURI);
		return setup;
	}
	
	@Override
	public String getNews() throws Exception {
		return readNews();
	}

	private String readNews() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream("edu/arizona/biosemantics/etcsite/news.html");
		
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
		String news = writer.toString();
		try {
			inputStream.close();
		} catch (IOException e) {
			log(LogLevel.ERROR, "Could not close news inputstream", e);
		}		
		return news;
	}

}
