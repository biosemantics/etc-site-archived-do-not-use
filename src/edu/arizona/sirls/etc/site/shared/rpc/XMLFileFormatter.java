package edu.arizona.sirls.etc.site.shared.rpc;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

public class XMLFileFormatter implements IFileFormatter {

	@Override
	public String format(String input) {
	       try {
	    	   
	            final InputSource src = new InputSource(new StringReader(input));
	            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
	            final Boolean keepDeclaration = Boolean.valueOf(input.startsWith("<?xml"));

	        //May need this: System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");


	            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
	            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
	            final LSSerializer writer = impl.createLSSerializer();

	            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
	            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration); // Set this to true if the declaration is needed to be outputted.

	            return writer.writeToString(document);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	}
	
}
