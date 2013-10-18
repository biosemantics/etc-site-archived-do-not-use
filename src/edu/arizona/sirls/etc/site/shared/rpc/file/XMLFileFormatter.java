package edu.arizona.sirls.etc.site.shared.rpc.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import edu.arizona.sirls.etc.site.server.rpc.HighlightXMLWriter;
import edu.arizona.sirls.etc.site.shared.rpc.IFileFormatter;

public class XMLFileFormatter implements IFileFormatter {

	@Override
	public String format(String input) {
		try {
			InputSource src = new InputSource(new ByteArrayInputStream(input.getBytes("UTF-8")));
			Node document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(src).getDocumentElement();
			Boolean keepDeclaration = Boolean.valueOf(input
					.startsWith("<?xml"));

			// May need this:
			// System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			LSOutput lsOutput = impl.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			lsOutput.setByteStream(out);
			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
			writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); 
			writer.write(document, lsOutput);
			return out.toString("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}
	
	
	public String format(Node node) {
		try {			
			final DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			final DOMImplementationLS impl = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			final LSSerializer writer = impl.createLSSerializer();

			writer.getDomConfig().setParameter("format-pretty-print",
					Boolean.TRUE); // Set this to true if the output needs to be
									// beautified.
			writer.getDomConfig().setParameter("xml-declaration",
					false); // Set this to true if the declaration is
										// needed to be outputted.

			return writer.writeToString(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 

	public String formatAndHiglight(String content, NodeList nodeList) {		
		/*try {
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
		}*/
		return content;
	}

}
