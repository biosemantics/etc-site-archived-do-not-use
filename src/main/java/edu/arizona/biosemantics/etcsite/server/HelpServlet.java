package edu.arizona.biosemantics.etcsite.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.arizona.biosemantics.etcsite.client.content.home.HomePlace;
import edu.arizona.biosemantics.etcsite.shared.help.Help;

public class HelpServlet extends HttpServlet {

	private static final long serialVersionUID = 6688917446498637833L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		switch(request.getParameter("place")) {
		case "HomePlace":
			out.write(Help.getHelp(Help.Type.HOME));
			break;
		case "SemanticMarkupPlace":
			out.write(Help.getHelp(Help.Type.TEXT_CAPTURE_INPUT));
			break;
		//...
		};
		
	}
	
}