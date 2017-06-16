package edu.arizona.biosemantics.etcsite.server.process.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import edu.arizona.biosemantics.common.log.LogLevel;

public class TaxonNameValidator {
	
	private String invalidMessage;

	public TaxonNameValidator() {
		invalidMessage = "";
	}
	
	public boolean validate(File[] files) {
		HashMap<String, String> taxonNames = new HashMap<String, String>();
		String taxonNameErrors = "";
		for(File file : files) {
			if(file.isFile()) {
				SAXBuilder saxBuilder = new SAXBuilder();
				Document document;
				try {
					document = saxBuilder.build(file);
				} catch (JDOMException | IOException e) {
					log(LogLevel.ERROR, "SAXBuilder cannot build "+(file.getName())+ ".");
					invalidMessage = "XML format error in file " + file.getName();
					return false;
				}
				XPathFactory xPathFactory = XPathFactory.instance();
				XPathExpression<Element> taxonNameMatcher = 
						xPathFactory.compile("/bio:treatment/taxon_identification", Filters.element(), 
								null, Namespace.getNamespace("bio", "http://www.github.com/biosemantics"));
				List<Element> taxonIdentificationElements = taxonNameMatcher.evaluate(document);				
				List<Element> taxonNameElements = new LinkedList<Element>();
				List<Element> strainNumberElements = new LinkedList<Element>();
				for(Element taxonIdentificationElement : taxonIdentificationElements) {
					if(taxonIdentificationElement.getAttributeValue("status").equalsIgnoreCase("accepted")) {
						taxonNameElements.addAll(taxonIdentificationElement.getChildren("taxon_name"));
						strainNumberElements.addAll(taxonIdentificationElement.getChildren("strain_number"));
					}
				}
				String taxon = "";
				for(Element taxonName : taxonNameElements) {
					taxon += taxonName.getAttributeValue("rank") + "_" + taxonName.getText() + "_" + 
							taxonName.getAttributeValue("authority") + "_" + taxonName.getAttributeValue("date");
				}
				for(Element strainNumber : strainNumberElements) {
					taxon += strainNumber.getText();
				}
				if(!taxonNameElements.isEmpty() || !strainNumberElements.isEmpty()) {
					if(taxonNames.containsKey(taxon)){
						taxonNameErrors += "( " + taxonNames.get(taxon) + " , " + file.getName() + " ), ";
					} else {
						taxonNames.put(taxon,  file.getName());
					}
				}
			}
		}
		if(!taxonNameErrors.equals("")){
			invalidMessage = "Taxon names (name+authority+date) should be unique. There are duplicates in files " + taxonNameErrors + " please merge them or delete duplicates.";
			return false;
		}
		return true;
	}

	
	public String getInvalidMessage() {
		return invalidMessage;
	}
	
	

}
