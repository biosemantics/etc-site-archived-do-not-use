package edu.arizona.biosemantics.etcsite.shared.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import edu.arizona.biosemantics.etcsite.shared.rpc.IFileFormatter;

public class XMLFileFormatter implements IFileFormatter {

	@Override
	public String format(String input) {
		try {
			InputSource src = new InputSource(new ByteArrayInputStream(input.getBytes("UTF-8")));
			Node document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(src).getDocumentElement();
			Boolean keepDeclaration = Boolean.valueOf(input
					.startsWith("<?xml"));
			input = this.format(document, keepDeclaration);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return input;
	}
	
	
	public String format(Node node, boolean keepDecleration) {
		try {			
			// May need this:
			// System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");

			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			LSSerializer writer = impl.createLSSerializer();
			LSOutput lsOutput = impl.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			lsOutput.setByteStream(out);
			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); 
			writer.getDomConfig().setParameter("xml-declaration", false);
			writer.write(node, lsOutput);
			String outString = out.toString("UTF-8");
			//System.out.println(outString);
			return outString;
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
