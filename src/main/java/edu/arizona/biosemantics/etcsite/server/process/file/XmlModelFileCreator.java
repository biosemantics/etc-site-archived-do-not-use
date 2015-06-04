package edu.arizona.biosemantics.etcsite.server.process.file;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.etcsite.shared.model.file.FileTypeEnum;
import edu.arizona.biosemantics.etcsite.shared.model.file.TaxonIdentificationEntry;
import edu.arizona.biosemantics.etcsite.shared.model.file.XmlModelFile;
import edu.arizona.biosemantics.etcsite.shared.model.process.semanticmarkup.BracketChecker;

/*import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
/*import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;*/

public class XmlModelFileCreator extends edu.arizona.biosemantics.etcsite.shared.model.process.file.XmlModelFileCreator {

	public String[] fields =  new String[] {"author", "year", "title", "doi", "full citation",
			/*"order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
			"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked",*/
			"strain number", "equivalent strain numbers", "accession number 16s rrna",
			"morphology", "phenology",  "habitat", "distribution" };
	
	protected BracketChecker bracketChecker = new BracketChecker();
	//protected String[] nameTypes = { "order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
	//		"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked" };
	protected ArrayList<String> nameTypes = new ArrayList<String>();
	protected Set<String> allLabels = new HashSet<String>();
	private XmlNamespaceManager xmlNamespaceManager = new XmlNamespaceManager();
	
	public XmlModelFileCreator() {
		this.allLabels.addAll(Arrays.asList(fields));
		for(Rank rank : Rank.values())
			this.allLabels.add(rank.name().toLowerCase() + " name");
	}
	
	public synchronized List<XmlModelFile> createXmlModelFiles(String text, String operator) {
		showMessage("text:", text);
		List<XmlModelFile> result = new LinkedList<XmlModelFile>();
		if(text.isEmpty()) return result;
		
		text = normalizeText(text);
		showMessage("text after normalization: ", text);
		List<String> treatmentTexts = getTreatmentTexts(text);
		
		HashSet<String> names = new HashSet<String> (); //TODO: use taxonNames to check for duplicate taxa, but can't do it given current code structure: only one entry is processed at a time. 
		for(String treatmentText : treatmentTexts) {
			XmlModelFile modelFile = createXmlModelFile(treatmentText, operator, names);
			result.add(modelFile);
		}
		
		return result;
	}

	private void showMessage(String title, String text) {
		/*TextView view = new TextView();
		TextPresenter presenter = new TextPresenter(view);
		view.setPresenter(presenter);
		presenter.showMessage(title, text);*/
		log(LogLevel.TRACE, title + ": " + text);
	}	

	//TODO: use taxonNames to check for duplicate taxa, but can't do it given current code structure: only one entry is processed at a time. 
	public XmlModelFile createXmlModelFile(String text, String operator, HashSet<String> taxonNames) {
		showMessage("get xml model file for: ", text);
		XmlModelFile modelFile = new XmlModelFile();
		nameTypes = new ArrayList<String>();
		
		//prepare data map
		Map<String, List<String>> data = new HashMap<String, List<String>>();
		String[] lines = text.split("\n");
		
		Iterator<String> lineIterator = Arrays.asList(lines).iterator();
		while(lineIterator.hasNext()) {
			String line = lineIterator.next();
			showMessage("line: ", line);
		
			int colonIndex = line.indexOf(":");
			if(colonIndex == -1) {
				modelFile.appendError("Line format invalid. Need a ':' to separate field name from its text: " + line);
				continue;
			}

			String key = line.substring(0, colonIndex).toLowerCase().trim();
			String value = line.substring(colonIndex + 1, line.length()).trim();
			showMessage("key/value ", key + " = " + value);
			
			for(String descriptionType : descriptionTypes) {
				if(key.contains(descriptionType)) {
					if(value.startsWith("#")) {
						StringBuilder valueBuilder = new StringBuilder();
						valueBuilder.append(value.replaceAll("(^#|#$)", "") + "\n"); 
						while(!line.endsWith("#") && lineIterator.hasNext()) {
							line = lineIterator.next();
							if(line.endsWith("#"))
								valueBuilder.append(line.replaceFirst("#$", "") + "\n");
							else
								valueBuilder.append(line + "\n");
						}
						value = valueBuilder.toString();
					}
					if(!key.equals(descriptionType)){
						String[] descriptionSplits = key.split("-");
						if(descriptionSplits.length > 1){
							key = descriptionSplits[1];
							if(value!=null && descriptionSplits[0]!=null){
								value = "scope:" + descriptionSplits[0] + "#" + value;
							}
						}
					}
				}
			}
			
			
			if(descriptionTypesSet.contains(key)) {
				if(!value.isEmpty()) {
					if(!data.containsKey(key)) 
						data.put(key, new LinkedList<String>());
					data.get(key).add(value);
					showMessage("Put key", key + " = " + value);
				}
			} else {
				if(data.containsKey(key)) {
					modelFile.appendError("No duplicate fields are allowed in one treatment: " + key);
				}
				showMessage("Put key", key + " = " + value);
				List<String> singleValueList = new LinkedList<String>();
				if(!value.isEmpty())
					singleValueList.add(value);
				data.put(key, singleValueList);
			}
		}
		
		this.showMessage("data", data.toString());
		
		HashSet<String> names = new HashSet<String>();
		Hashtable<Rank, String> rankedNames = new Hashtable<Rank, String>();
		for(String key : data.keySet()) {
			if(!this.allLabels.contains(key)) {
				modelFile.appendError("Don't know what " +key + " is.");
			} else {
				if(key.endsWith(" name")){	
					rankedNames.put(Rank.valueOf(key.replaceFirst("\\s+name", "").toUpperCase()), data.get(key).get(0));
					//if(!data.get(key).matches(".*?,.*?\\w+.*")) modelFile.appendError("'"+key+":"+data.get(key)+ "' does not have authority/date.");
					String fullName = data.get(key).get(0).trim();
					String taxonName = "";
					//remove the authority date part 
					if(fullName.contains(",")){
						fullName = fullName.substring(0, data.get(key).get(0).indexOf(","));
						fullName = fullName.trim();
						taxonName = fullName.split(" ")[0];
						taxonName = taxonName.trim();
					}
					if(taxonName.contains(" ")){
							modelFile.appendError("'"+taxonName + "' in '" + data.get(key).get(0) + "' is repeated in an entry, did you include genus name in species name/epithet?");
					}else{
						names.add(taxonName);
					}
					nameTypes.add(key);
				}
			}
		}
	
		//sort rankedName by rank from high to low, and find the lowest name and check if it has authority and date
		//add rankedNames to taxonNames to check for duplicates in the input
		ArrayList<String> rNames = new ArrayList<String>();
		for(int i = 0; i<100; i++){
			rNames.add("");
		}
		Enumeration<Rank> ranks = rankedNames.keys();
		while(ranks.hasMoreElements()){
			Rank r = ranks.nextElement();
			rNames.set(r.getId(), rankedNames.get(r));
		}
		StringBuffer completeName = new StringBuffer();
		String lowestName = null;
		for(String rname: rNames){
			if(!rname.isEmpty()){
				completeName.append(rname).append("/");
				lowestName = rname;
			}			
		}
		if(lowestName == null)
			modelFile.appendError("No rank and name is given.");
		else 
			if(!lowestName.matches(".*?,.*?\\w+.*")) modelFile.appendError("'"+lowestName+ "' does not have authority/date.");
		if(taxonNames.contains(completeName.toString()))
			modelFile.appendError("'"+completeName.toString()+ "' already exists. Duplicate taxa are not allowed");
		else
			taxonNames.add(completeName.toString());
		
	
		//check data required to generate error if necessary
		if(!data.containsKey("author") || data.get("author").isEmpty() || data.get("author").get(0).trim().isEmpty())
			modelFile.appendError("You need to provide an author");
		if(!data.containsKey("year") || data.get("year").isEmpty() || data.get("year").get(0).trim().isEmpty())
			modelFile.appendError("You need to provide a year");
		if(!data.containsKey("title") || data.get("title").isEmpty() || data.get("title").get(0).trim().isEmpty())
			modelFile.appendError("You need to provide a title");
		
		boolean nameValid = false;
		if(nameTypes.size()>0){
			for(String nameType : nameTypes) {
				nameValid = data.containsKey(nameType) && !data.get(nameType).isEmpty() && !data.get(nameType).get(0).trim().isEmpty();
				if(nameValid)
					break;
			}
		}
		nameValid = nameValid || (!nameValid && data.containsKey("strain number") && !data.get("strain number").isEmpty() && !data.get("strain number").get(0).trim().isEmpty()); 
		if(!nameValid)
			modelFile.appendError("You need to provide at least either a taxon rank or a strain");
		
		boolean descriptionValid = false;
		for(String descriptionType : descriptionTypes) {
			if(data.containsKey(descriptionType)) {
				for(String description : data.get(descriptionType)) {
					if(!description.trim().isEmpty())
						descriptionValid = true;
				}
			}
			if(descriptionValid)
				break;
		}
		if(!descriptionValid)
			modelFile.appendError("You need to provide at least one of the description types: morphology, phenology, habitat, or distribution");
		

		//build xml
		Document xml = createXML(data, operator, modelFile);
		modelFile.setFileName(createFileName(data, modelFile));
		modelFile.setXML(new XMLOutputter(Format.getPrettyFormat()).outputString(xml));
		return modelFile;
	}
	
	private Document createXML(Map<String, List<String>> data, String username, XmlModelFile modelFile) {
		
		Element treatment = new Element("treatment");
		/*root.addContent(new Element(article));
		Elements in which it is possible to set attributes:
		Attribute att1 = new Attribute("classe", "A1");
		article.setAttribute(att1);*/
		
		Document doc = new Document(treatment);
		Element meta = new Element("meta");
		treatment.addContent(meta);
		Element source = new Element("source");
		Element author = new Element("author");
		Element srcDate = new Element("date");
		Element title = new Element("title");
		source.addContent(author);
		source.addContent(srcDate);
		source.addContent(title);
		if(data.get("author") != null && !data.get("author").isEmpty())
			author.setText(data.get("author").get(0));
		if(data.get("year") != null && !data.get("year").isEmpty())
			srcDate.setText(data.get("year").get(0));
		if(data.get("title") != null && !data.get("title").isEmpty())
			title.setText(data.get("title").get(0));
		meta.addContent(source);
		Element processedBy = new Element("processed_by");
		Element processor = new Element("processor");
		processedBy.addContent(processor);
		Element date = new Element("date");
		processor.addContent(date);
		String formattedDate = DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(new Date());
		date.setText(formattedDate);
		Element software = new Element("software");
		software.setAttribute("type", "Text Capture Input Generator");
		software.setAttribute("version", "1.0");
		processor.addContent(software);
		Element operator = new Element("operator");
		operator.setText(username);
		processor.addContent(operator);
		meta.addContent(processedBy);
		
		if(data.get("doi") != null && !data.get("doi").isEmpty() && !data.get("doi").get(0).trim().isEmpty()) {
			Element otherInfoOnMeta = new Element("other_info_on_meta");
			otherInfoOnMeta.setText(data.get("doi").get(0));
			otherInfoOnMeta.setAttribute("type", "doi");
			meta.addContent(otherInfoOnMeta);
		}
		if(data.get("full citation") != null && !data.get("full citation").isEmpty() && !data.get("full citation").get(0).trim().isEmpty()) {
			Element otherInfoOnMeta = new Element("other_info_on_meta");
			otherInfoOnMeta.setText(data.get("full citation").get(0));
			otherInfoOnMeta.setAttribute("type", "citation");
			meta.addContent(otherInfoOnMeta);
		}
		/*List<String> otherInfoOnMetas = new LinkedList<String>();
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
				Element otherInfoOnMeta = new Element("other_info_on_meta");
				otherInfoOnMeta.setTextotherInfoOnMetaText));
				meta.addContent(otherInfoOnMeta);
			}
		}*/
		Element taxonIdentification = new Element("taxon_identification");
		treatment.addContent(taxonIdentification);
		Collections.sort(nameTypes, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				a = a.replaceFirst(" name$", "");
				b = b.replaceFirst(" name$", "");
				try {
					Rank aRank = Rank.valueOf(a.toUpperCase());
					Rank bRank = Rank.valueOf(b.toUpperCase());
					return aRank.getId() - bRank.getId();
				} catch(Exception e) {
					return -Integer.MAX_VALUE;
				}
			}
		});
		for(String nameType: nameTypes){
			String rank = nameType.replaceFirst(" name$", "");
			String nameString = data.get(nameType).get(0).trim();
			String name = nameString;
			String authority = null;
			String ndate = null;
			if(nameString.contains(" ")){
				name = nameString.substring(0, nameString.indexOf(" ")); //first word
				String authorityStr = nameString.substring(nameString.indexOf(" ")).trim();
				if(authorityStr.contains(",")){//, separates authority and date
					authority = authorityStr.substring(0, authorityStr.indexOf(",")).trim();
					ndate = authorityStr.substring(authorityStr.indexOf(",")+1).trim();
				}
			}
			Element element = new Element("taxon_name");
			taxonIdentification.addContent(element);
			element.setText(name);
			element.setAttribute("rank", rank);
			if(authority!=null){
				element.setAttribute("authority", authority);
				if(ndate!=null) element.setAttribute("date", ndate);
				else element.setAttribute("date", "n.d");
			} else {
				if(data.get("authority") != null && !data.get("authority").isEmpty())
					element.setAttribute("authority", data.get("author").get(0));
				if(data.get("year") != null && !data.get("year").isEmpty())
					element.setAttribute("date", data.get("year").get(0));
			}
		}

		if(data.get("strain number") != null && !data.get("strain number").isEmpty() && !data.get("strain number").get(0).trim().isEmpty()) {
			Element element = new Element("strain_number");
			taxonIdentification.addContent(element);
			element.setText(data.get("strain number").get(0));
			if(data.get("equivalent strain numbers") != null && !data.get("equivalent strain numbers").isEmpty() && !data.get("equivalent strain numbers").get(0).trim().isEmpty()) element.setAttribute("equivalent_strain_numbers", data.get("equivalent strain numbers").get(0));
			if(data.get("accession number 16s rrna") != null && !data.get("accession number 16s rrna").isEmpty() && !data.get("accession number 16s rrna").get(0).trim().isEmpty()) element.setAttribute("accession_number_16s_rrna", data.get("accession number 16s rrna").get(0));
		}

		taxonIdentification.setAttribute("status", "ACCEPTED");
		/*addAsElementIfExists(taxonIdentification, data, "class", "class_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subclass", "subclass_name", doc);
		addAsElementIfExists(taxonIdentification, data, "order", "order_name", doc);
		addAsElementIfExists(taxonIdentification, data, "suborder", "suborder_name", doc);
		addAsElementIfExists(taxonIdentification, data, "superfamily", "superfamily_name", doc);
		addAsElementIfExists(taxonIdentification, data, "family", "family_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subfamily", "subfamily_name", doc);
		addAsElementIfExists(taxonIdentification, data, "tribe", "tribe_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subtribe", "subtribe_name", doc);
		addAsElementIfExists(taxonIdentification, data, "genus", "genus_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subgenus", "subgenus_name", doc);
		addAsElementIfExists(taxonIdentification, data, "section", "section_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subsection", "subsection_name", doc);
		addAsElementIfExists(taxonIdentification, data, "series", "series_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subseries", "subseries_name", doc);
		addAsElementIfExists(taxonIdentification, data, "species", "species_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subspecies", "subspecies_name", doc);
		addAsElementIfExists(taxonIdentification, data, "variety", "variety_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subvarietas", "series_name", doc);
		addAsElementIfExists(taxonIdentification, data, "forma", "forma_name", doc);
		addAsElementIfExists(taxonIdentification, data, "subforma", "subforma_name", doc);
		addAsElementIfExists(taxonIdentification, data, "unranked", "unranked_epithet_name", doc);*/
		//addAsElementIfExists(taxonIdentification, data, "strain", "strain_name", doc);
		//addAsElementIfExists(taxonIdentification, data, "strain source", "strain_source", doc);

		for(String descriptionType : descriptionTypes) {
			if(data.containsKey(descriptionType)) {
				List<String> descriptionTexts = data.get(descriptionType);
				for(String descriptionText : descriptionTexts) {
					if(descriptionText != null && !descriptionText.trim().isEmpty()) {
						String scope = "";
						if(descriptionText.startsWith("scope:")){
							String[] desc = descriptionText.split("#");
							scope = desc[0].replace("scope:", "");
							descriptionText = desc[1];
						}
						String[] paragraphs = descriptionText.split("\n");
						for(String paragraph : paragraphs) {
							if(!paragraph.isEmpty()) {
								Element description = new Element("description");
								treatment.addContent(description);
								description.setAttribute("type", descriptionType);
								if(!scope.isEmpty()){
									description.setAttribute("scope", scope);
								}
								description.setText(paragraph);
								
								String bracketError = bracketChecker.checkBrackets(paragraph, descriptionType);
								if(!bracketError.isEmpty())
									modelFile.appendError(bracketError.substring(0, bracketError.length() - 2));
							}
						}
					}
				}
			}
		}
		/* for debug purpose: to generate some random invalid xml files.
		if(Math.random()>0.5){
		Element element = new Element("bad_element");
		taxonIdentification.addContent(element);
		}*/
		xmlNamespaceManager.setXmlSchema(doc, FileTypeEnum.TAXON_DESCRIPTION);
		
		return doc;
	}

	private void addAsElementIfExists(Element parentElement,
			Map<String, String> data, String dataName, String xmlName, Document document) {
		if(data.containsKey(dataName) && !data.get(dataName).isEmpty() && !data.get(dataName).trim().isEmpty()) {
			Element element = new Element(xmlName);
			parentElement.addContent(element);
			element.setText(data.get(dataName));
		}
	}

	public String createFileName(Map<String, List<String>> data, XmlModelFile modelFile) {
		List<TaxonIdentificationEntry> taxonIdentificationEntries = new LinkedList<TaxonIdentificationEntry>();
		for(String nameType : nameTypes) {
			if(data.get(nameType) != null && !data.get(nameType).isEmpty() && !data.get(nameType).get(0).trim().isEmpty()) {
				taxonIdentificationEntries.add(new TaxonIdentificationEntry(Rank.valueOf(nameType.replaceFirst(" name$", "").toUpperCase()), 
						data.get(nameType).get(0)));
			}
		}
		Collections.sort(taxonIdentificationEntries);
		
		if(data.get("author") != null && !data.get("author").isEmpty() && data.get("year") != null && !data.get("year").isEmpty()) {
			String filename = data.get("author").get(0) + data.get("year").get(0) + "_";
			for (TaxonIdentificationEntry taxonIdentificationEntry : taxonIdentificationEntries) {
				if (filename.matches(".*(_|^)" + taxonIdentificationEntry.getRank()	+ "(_|$).*"))
					modelFile.appendError("Redundant rank '" + taxonIdentificationEntry.getRank() + "'");
				filename += taxonIdentificationEntry.getRank() + "_" + taxonIdentificationEntry.getValue() + "_";
			}
			if(data.containsKey("strain number") && !data.get("strain number").isEmpty() && !data.get("strain number").get(0).trim().isEmpty())
				filename += "strain_" + data.get("strain number") + "_";
			
			filename = filename.replaceAll("_+", "_").replaceFirst("_$", ".xml");
			return filename;
		}
		return "";
	}
	
}
