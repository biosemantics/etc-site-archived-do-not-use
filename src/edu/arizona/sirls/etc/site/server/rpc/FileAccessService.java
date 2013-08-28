package edu.arizona.sirls.etc.site.server.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.sirls.etc.site.client.AuthenticationToken;
import edu.arizona.sirls.etc.site.server.Configuration;
import edu.arizona.sirls.etc.site.shared.rpc.IAuthenticationService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileAccessService;
import edu.arizona.sirls.etc.site.shared.rpc.IFileService;

public class FileAccessService extends RemoteServiceServlet implements IFileAccessService {

	private static final long serialVersionUID = 5956919724639140570L;
	private IAuthenticationService authenticationService = new AuthenticationService();
	
	@Override
	public boolean setFileContent(AuthenticationToken authenticationToken, String target, String content) {
		boolean result = false;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			if(file.exists() && file.isFile()) {
				try {
					Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
					writer.append(content);
					writer.flush();
					writer.close();
					result = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public String getFileContent(AuthenticationToken authenticationToken,
			String target) {
		String result = null;
		if(authenticationService.isValidSession(authenticationToken).getResult() && !target.trim().isEmpty()) { 
			File file = new File(Configuration.fileBase + "//" + authenticationToken.getUsername() + "//" + target);
			if(file.exists() && file.isFile()) {
				try {
					StringBuilder  stringBuilder = new StringBuilder();
					String line = null;
					BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
					while((line = reader.readLine() ) != null ) {
				        stringBuilder.append(line);
				        stringBuilder.append("\n");
				    }
				    result = stringBuilder.toString();
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
