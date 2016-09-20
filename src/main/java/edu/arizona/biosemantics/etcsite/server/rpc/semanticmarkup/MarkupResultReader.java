package edu.arizona.biosemantics.etcsite.server.rpc.semanticmarkup;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.ling.transform.IInflector;

public class MarkupResultReader {
	
	public static class Character {

		private String value;
		private String category;
		private String iri;
		
		public Character(String value, String category, String iri) {
			this.value = value;
			this.category = category;
			this.iri = iri;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}
			
		public String getIri() {
			return iri;
		}

		public void setIri(String iri) {
			this.iri = iri;
		}

		@Override
		public String toString() {
			return this.category + ": " + this.value + " ( " + iri + " )";
		}
	}
	
	public static class BiologicalEntity {

		private String constraint;
		private String name;
		private String type;
		private String iri;
		
		public BiologicalEntity(String name, String constraint, String type, String iri) {
			this.constraint = constraint;
			this.name = name;
			this.type = type;
			this.iri = iri;
		}
				
		public String getConstraint() {
			return constraint;
		}

		public void setConstraint(String constraint) {
			this.constraint = constraint;
		}

		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getIri() {
			return iri;
		}

		public void setIri(String iri) {
			this.iri = iri;
		}

		@Override
		public String toString() {
			return this.name + ": " + this.type + " ( " + iri + " )";
		}

		public boolean hasConstraint() {
			return this.constraint != null && !constraint.isEmpty();
		}
	}
	
	
	private Pattern numericalsPattern = Pattern.compile(".*\\d+.*");
	private IInflector inflector;
	
	@Inject
	public MarkupResultReader(IInflector inflector) {
		this.inflector = inflector;
	}
	
	public List<Character> getRangeValueCharacters(File input, boolean includeNumericals) throws JDOMException, IOException {
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
			XPathExpression<Element> xpath = xpf.compile("/bio:treatment/description/statement/biological_entity/character",
			              Filters.element(), null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String charType = element.getAttributeValue("char_type");
				if(charType != null && charType.equals("range_value")) {
					String toValue = element.getAttributeValue("to");
					String fromValue = element.getAttributeValue("from");
					String iri = element.getAttributeValue("ontologyid");
					
					String name = element.getAttributeValue("name");
					if(toValue != null && (includeNumericals || !numericalsPattern.matcher(toValue).matches())) {
						result.add(new Character(toValue, name, iri));
					}
					
					if(fromValue != null && (includeNumericals || !numericalsPattern.matcher(fromValue).matches())) {
						result.add(new Character(fromValue, name, iri));
					}
				}
			}
		}
		return result;
	}
	
	public List<Character> getCharacters(File input, boolean includeNumericals) throws JDOMException, IOException {
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
			XPathExpression<Element> xpath = xpf.compile("/bio:treatment/description/statement/biological_entity/character",
			              Filters.element(), null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String charType = element.getAttributeValue("char_type");
				String category = element.getAttributeValue("name");
				String iri = element.getAttributeValue("ontologyid");
				if(charType != null && charType.equals("range_value")) {
					String toValue = element.getAttributeValue("to");
					String fromValue = element.getAttributeValue("from");
					
					if(toValue != null && (includeNumericals || !numericalsPattern.matcher(toValue).matches())) {
						result.add(new Character(toValue, category, iri));
					}
					if(fromValue != null && (includeNumericals || !numericalsPattern.matcher(fromValue).matches())) {
						result.add(new Character(fromValue, category, iri));
					}
				} else {
					String value = element.getAttributeValue("value");
					if(value != null && (includeNumericals || !numericalsPattern.matcher(value).matches())) {
						result.add(new Character(value, category, iri));
					}
				}
			}
		}
		return result;
	}

	public List<BiologicalEntity> getBiologicalEntities(File input) throws JDOMException, IOException {
		List<BiologicalEntity> result = new LinkedList<BiologicalEntity>();
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
			XPathExpression<Element> xpath = xpf.compile("/bio:treatment/description/statement/biological_entity",
			              Filters.element(), null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String name = element.getAttributeValue("name");
				String constraint = element.getAttributeValue("constraint");
				String type = element.getAttributeValue("type");
				String iri = element.getAttributeValue("ontologyid");
				result.add(new BiologicalEntity(name, constraint, type, iri));
			}
		}
		return result;
	}

	public List<String> getSentences(File input, String value) throws JDOMException, IOException {
		value = value.toLowerCase();
		List<String> sentences = new LinkedList<String>();
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
			XPathExpression<Element> xpath = xpf.compile("/bio:treatment/description/statement/text",
			              Filters.element(), null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics")); 
			List<Element> elements = xpath.evaluate(document);
			for(Element element : elements) {
				String text = element.getValue().toLowerCase();
				String plural = inflector.getPlural(value).toLowerCase();
				
				if(text.matches(".*\\b" + value + "|" + plural + "\\b.*"))
					sentences.add(text);
			}
		}
		return sentences;
	}

}
