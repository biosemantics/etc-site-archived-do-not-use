package edu.arizona.biosemantics.etcsite.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import edu.arizona.biosemantics.etcsite.server.rpc.auth.AuthenticationService;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationResult;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.AuthenticationToken;
import edu.arizona.biosemantics.etcsite.shared.rpc.auth.IAuthenticationService;

public class PDFServlet extends HttpServlet implements Servlet {

	private static final long serialVersionUID = 1L;
	private IAuthenticationService authenticationService;

	@Inject
	public PDFServlet(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int userID = Integer.parseInt(request.getParameter("userID"));
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		if(authenticationResult.getResult()) { 	
			serve(request, response, target);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		int userID = Integer.parseInt(request.getParameter("userID"));
		String sessionID = request.getParameter("sessionID");
		String target = request.getParameter("target");
		
		AuthenticationResult authenticationResult = 
				authenticationService.isValidSession(new AuthenticationToken(userID, sessionID));
		if(authenticationResult.getResult()) { 	
			serve(request, response, target);
		}
	}

	private void serve(HttpServletRequest request, HttpServletResponse response, String target) throws ServletException,
			IOException {
		File pdfFile = new File(target);

		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "inline; filename=" + pdfFile.getName());
		response.setContentLength((int) pdfFile.length());

		FileInputStream fileInputStream = new FileInputStream(pdfFile);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}
	}

}
