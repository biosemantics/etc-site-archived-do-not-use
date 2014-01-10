package edu.arizona.biosemantics.etcsite.server.rpc;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.Attribute;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import com.google.gwt.xml.client.NodeList;

public class HighlightXMLWriter extends XMLWriter {

	private NodeList nodeList;

	public HighlightXMLWriter(Writer writer, OutputFormat format, NodeList nodeList) {
		super(writer, format);
		this.nodeList = nodeList;
	}
	
	@Override
	public void writeNode(Node node) throws IOException {
		for(int i=0; i<nodeList.getLength(); i++) {
			com.google.gwt.xml.client.Node highlightNode = nodeList.item(i);
			System.out.println(node.getClass());
			node = ifEqualsHiglight(node, highlightNode);
		}
		super.writeNode(node);
	}

	private Node ifEqualsHiglight(Node node, com.google.gwt.xml.client.Node highlightNode) {
		if(node instanceof DefaultElement) {
			DefaultElement defaultElement = (DefaultElement)node;
			
			boolean attributesEqual = true;
			for(int i=0; i<defaultElement.attributeCount(); i++) {
				Attribute attribute = defaultElement.attribute(i);
				com.google.gwt.xml.client.Node attributeNode = highlightNode.getAttributes().item(i);
				attributesEqual &= attribute.getName().equals(attributeNode.getNodeName()) &&
						attribute.getValue().equals(attributeNode.getNodeValue());
			}
			
			if(attributesEqual && //defaultElement.getText().equals(highlightNode.get.getTextContent()) && 
					defaultElement.getName().equals(highlightNode.getNodeName())) {
				defaultElement.setName("*** " + defaultElement.getName() + " ***");
			}
		}
		return node;
	}
}
