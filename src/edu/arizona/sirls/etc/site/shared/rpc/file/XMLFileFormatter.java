package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import edu.arizona.sirls.etc.site.server.rpc.HighlightXMLWriter;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatter;

public class XMLFileFormatter implements IFileFormatter {

	@Override
	public String format(String input) {
		try {
			final InputSource src = new InputSource(new StringReader(input));
			final Node document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(src).getDocumentElement();
			final Boolean keepDeclaration = Boolean.valueOf(input
					.startsWith("<?xml"));

			// May need this:
			// System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

			final DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			final DOMImplementationLS impl = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			final LSSerializer writer = impl.createLSSerializer();

			writer.getDomConfig().setParameter("format-pretty-print",
					Boolean.TRUE); // Set this to true if the output needs to be
									// beautified.
			writer.getDomConfig().setParameter("xml-declaration",
					keepDeclaration); // Set this to true if the declaration is
										// needed to be outputted.

			return writer.writeToString(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}

	public String formatAndHiglight(String content, NodeList nodeList) {		
		try {
			System.out.println("content " + content);
			Document doc = DocumentHelper.parseText(content);  
			StringWriter sw = new StringWriter();  
			OutputFormat format = OutputFormat.createPrettyPrint();  
			//XMLWriter xw = new XMLWriter(sw, format);  
			XMLWriter xw = new HighlightXMLWriter(sw, format, nodeList);  
			xw.write(doc);  
			String result = sw.toString();
			System.out.println("result " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
