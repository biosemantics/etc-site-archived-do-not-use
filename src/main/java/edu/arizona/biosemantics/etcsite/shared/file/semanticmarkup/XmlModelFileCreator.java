package edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class XmlModelFileCreator {

	public static String[] fields =  new String[] {  "author", "year", "title", "doi", "full citation",
			"order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
			"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked",
			"strain", "strain source",
			"morphology", "phenology",  "habitat", "distribution", };
	
	protected BracketChecker bracketChecker = new BracketChecker();
	protected String[] descriptionTypes = { "morphology", "habitat", "distribution", "phenology" };
	protected String[] nameTypes = { "order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
			"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked" };
	protected Set<String> allLabels = new HashSet<String>();
	
	public XmlModelFileCreator() {
		this.allLabels.addAll(Arrays.asList(fields));
	}
	
	public List<XmlModelFile> createXmlModelFiles(String text, String username) {
		List<XmlModelFile> result = new LinkedList<XmlModelFile>();
			
		List<String> treatmentTexts = getTreatmentTexts(text);
		
		for(String treatmentText : treatmentTexts) {
			XmlModelFile modelFile = createXmlModelFile(treatmentText, username);
			result.add(modelFile);
		}
		
		return result;
	}
	
	private List<String> getTreatmentTexts(String text) {
		List<String> result = new LinkedList<String>();
		
		boolean insideContinuousValue = false;
		
		StringBuilder treatment = new StringBuilder();
		for(String line : text.split("\n")) {
			line = line.trim();
			if(line.isEmpty() && !insideContinuousValue) {
				result.add(treatment.toString());
				treatment = new StringBuilder();
			}
			else {
				treatment.append(line + "\n");
				int colonIndex = line.indexOf(":");
				if(colonIndex == -1 || insideContinuousValue) {
					if(line.endsWith("\""))
						insideContinuousValue = false;
					continue;
				} else {
					String key = line.substring(0, colonIndex).toLowerCase().trim();
					for(String descriptionType : descriptionTypes) {
						if(descriptionType.equals(key)) {
							String value = line.substring(colonIndex + 1, line.length()).trim();
							if(value.startsWith("\"")) 
								insideContinuousValue = true;
							if(value.endsWith("\""))
								insideContinuousValue = false;
						}
					}
				}
			}			
		}
		result.add(treatment.toString());
		return result;
	}

	public XmlModelFile createXmlModelFile(String text, String username) {
		XmlModelFile modelFile = new XmlModelFile();
		
		//prepare data map
		Map<String, String> data = new HashMap<String, String>();
		String[] lines = text.split("\n");
		
		Iterator<String> lineIterator = Arrays.asList(lines).iterator();
		while(lineIterator.hasNext()) {
			String line = lineIterator.next();
		
			int colonIndex = line.indexOf(":");
			if(colonIndex == -1) {
				modelFile.appendError("Line format invalid: " + line);
				continue;
			}
			String key = line.substring(0, colonIndex).toLowerCase().trim();
			String value = line.substring(colonIndex + 1, line.length()).trim();
			
			for(String descriptionType : descriptionTypes) {
				if(descriptionType.equals(key)) {
					if(value.startsWith("\"")) {
						StringBuilder valueBuilder = new StringBuilder();
						valueBuilder.append(value.substring(1) + "\n");
						while(!line.endsWith("\"") && lineIterator.hasNext()) {
							line = lineIterator.next();
							if(line.endsWith("\""))
								valueBuilder.append(line.substring(0, line.length() - 1) + "\n");
							else
								valueBuilder.append(line + "\n");
						}
						value = valueBuilder.toString();
					}
				}
			}
			if(data.containsKey(key)) {
				modelFile.appendError("No duplicate entries are allowed: " + key);
			}
			data.put(key, value.isEmpty()? null : value);
		}
		for(String key : data.keySet()) {
			if(!this.allLabels.contains(key)) {
				modelFile.appendError(key + " unknown");
			}
		}
		
		
		//check data required to generate error if necessary
		if(!data.containsKey("author") || data.get("author") == null || data.get("author").trim().isEmpty())
			modelFile.appendError("You have to provide an author");
		if(!data.containsKey("year") || data.get("year") == null || data.get("year").trim().isEmpty())
			modelFile.appendError("You have to provide a year");
		
		boolean nameValid = false;
		for(String nameType : nameTypes) {
			nameValid = data.containsKey(nameType) && data.get(nameType) != null && !data.get(nameType).trim().isEmpty();
			if(nameValid)
				break;
		}
		nameValid = nameValid || (!nameValid && data.containsKey("strain") && data.get("strain") != null && !data.get("strain").trim().isEmpty()); 
		if(!nameValid)
			modelFile.appendError("You have to provide at least either a taxon rank or a strain");
		
		boolean descriptionValid = false;
		for(String descriptionType : descriptionTypes) {
			descriptionValid = data.containsKey(descriptionType) && data.get(descriptionType) != null && !data.get(descriptionType).trim().isEmpty();
			if(descriptionValid)
				break;
		}
		if(!descriptionValid)
			modelFile.appendError("You have to provide at least one of the description types: morphology, phenology, habitat, or distribution");
		

		//build xml
		Document xml = createXML(data, username, modelFile);
		modelFile.setFileName(createFileName(data, modelFile));
		modelFile.setXML(xml.toString());
		return modelFile;
	}
	
	private Document createXML(Map<String, String> data, String username, XmlModelFile modelFile) {
		Document doc = XMLParser.createDocument();
		Element treatment = doc.createElement("treatment");
		doc.appendChild(treatment);
		Element meta = doc.createElement("meta");
		treatment.appendChild(meta);
		Element source = doc.createElement("source");
		Element author = doc.createElement("author");
		Element srcDate = doc.createElement("date");
		Element title = doc.createElement("title");
		source.appendChild(author);
		source.appendChild(srcDate);
		source.appendChild(title);
		author.appendChild(doc.createTextNode(data.get("author")));
		srcDate.appendChild(doc.createTextNode(data.get("year")));
		title.appendChild(doc.createTextNode(data.get("title")));
		meta.appendChild(source);
		Element processedBy = doc.createElement("processed_by");
		Element processor = doc.createElement("processor");
		processedBy.appendChild(processor);
		Element date = doc.createElement("date");
		processor.appendChild(date);
		String formattedDate = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL).format(new Date());
		date.appendChild(doc.createTextNode(formattedDate));
		Element software = doc.createElement("software");
		software.setAttribute("type", "Semantic Markup Input Generator");
		software.setAttribute("version", "1.0");
		processor.appendChild(software);
		Element operator = doc.createElement("operator");
		operator.appendChild(doc.createTextNode(username));
		processor.appendChild(operator);
		meta.appendChild(processedBy);
		List<String> otherInfoOnMetas = new LinkedList<String>();
		if(data.containsKey("doi") && data.get("doi") != null && !data.get("doi").trim().isEmpty()) {
			String otherInfoOnMeta = "doi: " + data.get("doi");
			otherInfoOnMetas.add(otherInfoOnMeta);
		}
		if(data.containsKey("full citation") && data.get("full citation") != null && !data.get("full citation").trim().isEmpty()) {
			String otherInfoOnMeta = "full citation: " + data.get("full citation");
			otherInfoOnMetas.add(otherInfoOnMeta);
		}
		
		if(!otherInfoOnMetas.isEmpty()) {
			for(String otherInfoOnMetaText : otherInfoOnMetas) {
				Element otherInfoOnMeta = doc.createElement("other_info_on_meta");
				otherInfoOnMeta.appendChild(doc.createTextNode(otherInfoOnMetaText));
				meta.appendChild(otherInfoOnMeta);
			}
		}
		Element taxonIdentification = doc.createElement("taxon_identification");
		treatment.appendChild(taxonIdentification);
		
		addAsElementIfExists(taxonIdentification, data, "family", "family_name", doc);
		addAsElementIfExists(taxonIdentification, data, "forma", "forma_name", doc);
		addAsElementIfExists(taxonIdentification, data, "unranked", "unranked_epithet_name", doc);
		addAsElementIfExists(taxonIdentification, data, "order", "order_name", doc);
		addAsElementIfExists(taxonIdentification, data, "suborder", "suborder_name", doc);
		addAsElementIfExists(taxonIdentification, data, "superfamily", "superfamily_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subfamily", "subfamily_name", doc);
		addAsElementIfExists(taxonIdentification, data, "tribe", "tribe_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subtribe", "subtribe_name", doc);
		addAsElementIfExists(taxonIdentification, data, "genus", "genus_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subgenus", "subgenus_name", doc);
		addAsElementIfExists(taxonIdentification, data, "section", "section_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subsection", "subsection_name", doc);
		addAsElementIfExists(taxonIdentification, data, "series", "series_name", doc);
		addAsElementIfExists(taxonIdentification, data, "species", "species_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subspecies", "subspecies_name", doc);
		addAsElementIfExists(taxonIdentification, data, "variety", "variety_name", doc);
		//addAsElementIfExists(taxonIdentification, data, "strain", "strain_name", doc);
		//addAsElementIfExists(taxonIdentification, data, "strain source", "strain_source", doc);
		taxonIdentification.setAttribute("status", "ACCEPTED");
		
		
		for(String descriptionType : descriptionTypes) {
			if(data.containsKey(descriptionType)) {
				String descriptionText = data.get(descriptionType);
				if(descriptionText != null && !descriptionText.trim().isEmpty()) {
					String[] paragraphs = descriptionText.split("\n");
					for(String paragraph : paragraphs) {
						if(!paragraph.isEmpty()) {
							Element description = doc.createElement("description");
							treatment.appendChild(description);
							description.setAttribute("type", descriptionType);
							description.appendChild(doc.createTextNode(paragraph));
							
							String bracketError = bracketChecker.checkBrackets(paragraph, descriptionType);
							if(!bracketError.isEmpty())
								modelFile.appendError(bracketError.substring(0, bracketError.length() - 2));
						}
					}
				}
			}
		}
		return doc;
	}

	private void addAsElementIfExists(Element parentElement,
			Map<String, String> data, String dataName, String xmlName, Document document) {
		if(data.containsKey(dataName) && data.get(dataName) != null && !data.get(dataName).trim().isEmpty()) {
			Element element = document.createElement(xmlName);
			parentElement.appendChild(element);
			element.appendChild(document.createTextNode(data.get(dataName)));
		}
	}

	public String createFileName(Map<String, String> data, XmlModelFile modelFile) {
		List<TaxonIdentificationEntry> taxonIdentificationEntries = new LinkedList<TaxonIdentificationEntry>();
		for(String nameType : nameTypes) {
			if(data.containsKey(nameType) && data.get(nameType) != null && !data.get(nameType).trim().isEmpty()) {
				taxonIdentificationEntries.add(new TaxonIdentificationEntry(nameType, data.get(nameType)));
			}
		}
				
		String filename = data.get("author") + data.get("year") + "_";
		for (TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
			if (filename.matches(".*(_|^)" + taxonIdentificationEntry.getRank()	+ "(_|$).*"))
				modelFile.appendError("Redundant rank '" + taxonIdentificationEntry.getRank() + "'");
			filename += taxonIdentificationEntry.getRank() + "_" + taxonIdentificationEntry.getValue() + "_";
		}
		if(data.containsKey("strain") && data.get("strain") != null && !data.get("strain").trim().isEmpty())
			filename += "strain_" + data.get("strain") + "_";
		
		filename = filename.replaceAll("_+", "_").replaceFirst("_$", ".xml");
		return filename;
	}
	
}
