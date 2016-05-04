package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.arizona.biosemantics.common.log.LogLevel;


public class XMLFileFormatter implements IFileFormatter {

	@Override
	public String format(String input) {
		InputSource src = null;
		try {
			src = new InputSource(new ByteArrayInputStream(input.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			log(LogLevel.ERROR, "Couldn't support encoding", e);
		}
		if(src != null) {
			Node document = null;
			try {
				document = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(src).getDocumentElement();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				log(LogLevel.ERROR, "Couldn't parse xml document", e);
			}
			if(document != null) {
				Boolean keepDeclaration = Boolean.valueOf(input
						.startsWith("<?xml"));
				String formatted = this.format(document, keepDeclaration);
				if(formatted != null)
					return formatted;
			}
		}
		return input;
	}
	
	
	public String format(Node node, boolean keepDecleration) {		
		// May need this:
		// System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

		DOMImplementationRegistry registry = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | ClassCastException e) {
			log(LogLevel.ERROR, "Couldn't get DOMImplementationRegistry instance", e);
		}
		if(registry != null) {
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			LSOutput lsOutput = impl.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			lsOutput.setByteStream(out);
			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); 
			writer.getDomConfig().setParameter("xml-declaration", false);
			writer.write(node, lsOutput);
			String outString;
			try {
				outString = out.toString("UTF-8");
				return outString;
			} catch (UnsupportedEncodingException e) {
				log(LogLevel.ERROR, "Couldn't support encoding", e);
			}
		}
		return null;
	} 

	public String formatAndHiglight(String content, NodeList nodeList) {		
		/*try {
			System.out.println("content " + content);
			Document doc = DocumentHelper.parseText(content);  
			try(StringWriter sw = new StringWriter()) {
				OutputFormat format = OutputFormat.createPrettyPrint();  
				//XMLWriter xw = new XMLWriter(sw, format);  
				XMLWriter xw = new HighlightXMLWriter(sw, format, nodeList);  
				xw.write(doc);  
				String result = sw.toString();
				System.out.println("result " + result);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return content;
	}

}
