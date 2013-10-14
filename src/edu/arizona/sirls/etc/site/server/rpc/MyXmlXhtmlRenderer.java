package edu.arizona.sirls.etc.site.server.rpc;

import com.uwyn.jhighlight.JHighlightVersion;
import com.uwyn.jhighlight.renderer.XmlXhtmlRenderer;
import com.uwyn.jhighlight.tools.StringUtils;

public class MyXmlXhtmlRenderer extends XmlXhtmlRenderer {

	@Override
	protected String getXhtmlHeader(String name) {
		if (null == name) {
			name = "";
		}

		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" + "<head>\n" + "    <meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\" />\n"
				+ "    <meta name=\"generator\" content=\"JHighlight v" + JHighlightVersion.getVersion() + " (http://jhighlight.dev.java.net)\" />\n" + "    " +
						"<style type=\"text/css\">\n" + getCssClassDefinitions() + "    </style>\n" + "</head>\n"
				+ "<body>\n" + "<code>";
	}
	
}
