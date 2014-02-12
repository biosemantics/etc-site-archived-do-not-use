package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.SAXParseException;




public class MarkupResultReader {
	
	private Pattern numericalsPattern = Pattern.compile(".*\\d+.*");
	
	public List<Character> getRangeValueCharacters(File input) throws JDOMException, IOException {
		List<Character> result = new LinkedList<Character>();
		SAXBuilder saxBuilder = new SAXBuilder();
		
		for(File file : input.listFiles()) {
			Document document = null;
			try {
				document = saxBuilder.build(file);
			} catch(JDOMParseException parseException) {
				//file not a valid xml file (e.g. config.txt)
				continue;
			}
			
			XPathFactory xpf = XPathFactory.instance();
			XPathExpression<Element> xpath = xpf.compile("/treatment/description/statement/structure/character",
			              Filters.element()); /*, null,
			              Namespace.getNamespace("xpns", "http://www.w3.org/2002/xforms")); */
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String charType = element.getAttributeValue("char_type");
				if(charType != null && charType.equals("range_value")) {
					String toValue = element.getAttributeValue("to");
					String fromValue = element.getAttributeValue("from");
					
					String name = element.getAttributeValue("name");
					if(toValue != null && !numericalsPattern.matcher(toValue).matches()) {
						result.add(new Character(toValue, name));
					}
					
					if(fromValue != null && !numericalsPattern.matcher(fromValue).matches()) {
						result.add(new Character(fromValue, name));
					}
				}
			}
		}
		return result;
	}
	
	public List<Character> getCharacters(File input) throws JDOMException, IOException {
		List<Character> result = new LinkedList<Character>();
		SAXBuilder saxBuilder = new SAXBuilder();
		for(File file : input.listFiles()) {
			Document document = null;
			try {
				document = saxBuilder.build(file);
			} catch(JDOMParseException parseException) {
				//file not a valid xml file (e.g. config.txt)
				continue;
			}
			
			XPathFactory xpf = XPathFactory.instance();
			XPathExpression<Element> xpath = xpf.compile("/treatment/description/statement/structure/character",
			              Filters.element()); 
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String charType = element.getAttributeValue("char_type");
				if(charType != null && charType.equals("range_value")) {
					String toValue = element.getAttributeValue("to");
					String fromValue = element.getAttributeValue("from");
					String category = element.getAttributeValue("name");
					result.add(new Character(toValue, category));
					result.add(new Character(fromValue, category));
				} else {
					String value = element.getAttributeValue("value");
					String category = element.getAttributeValue("name");
					result.add(new Character(value, category));
				}				
			}
		}
		return result;
	}

	public List<String> getStructures(File input) throws JDOMException, IOException {
		List<String> result = new LinkedList<String>();
		SAXBuilder saxBuilder = new SAXBuilder();
		for(File file : input.listFiles()) {
			Document document = null;
			try {
				document = saxBuilder.build(file);
			} catch(JDOMParseException parseException) {
				//file not a valid xml file (e.g. config.txt)
				continue;
			}
			
			XPathFactory xpf = XPathFactory.instance();
			XPathExpression<Element> xpath = xpf.compile("/treatment/description/statement/structure",
			              Filters.element()); 
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String name = element.getAttributeValue("name");
				String constraint = element.getAttributeValue("constraint");
				if(constraint != null)
					name = constraint + " " + name;
				result.add(name);
			}
		}
		return result;
	}

}
