package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;

public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 6688917446498637833L;
	private IAuthenticationService authenticationService;

	@Inject
	public DownloadServlet(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		int userID = Integer.parseInt(request.getParameter("userID"));
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		String directory = request.getParameter("directory");
		
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		if(authenticationResult.getResult()) { 	
			int BUFFER = 1024 * 100;
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + "\"" + 
					target.substring(target.lastIndexOf(File.separator) + 1, target.length()) + "\"");	

			ServletOutputStream outputStream = null;
			try {
				outputStream = response.getOutputStream();
			} catch (IOException e) {
				String message = "Couldn't get output stream";
				log(message, e);
				log(LogLevel.ERROR, message, e);
			}
			byte[] fileBytes;
			fileBytes = getFile(userID, target);
			
			response.setContentLength(Long.valueOf(fileBytes.length).intValue());
			response.setBufferSize(BUFFER);
			if(outputStream != null) {
				try {
					outputStream.write(fileBytes);
					outputStream.flush();
					//outputStream.close();
				} catch(IOException e) {
					String message = "Couldn't write download to output stream";
					log(message, e);
					log(LogLevel.ERROR, message, e);
				}
			}
		}
	}

	private byte[] getFile(int userID, String target) {		
		//Path path = Paths.get(Configuration.fileBase + File.separator + username + File.separator + target);
		Path path = Paths.get(target);
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			String message = "Couldn't read file content";
			log(message, e);
			log(LogLevel.ERROR, message, e);
		}
		return data;
	}
}