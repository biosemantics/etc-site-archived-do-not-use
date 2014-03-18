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

import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Description;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.OtherInfoOnMeta;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.ProcessedBy;
import edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup.XmlModel.Treatment;

public class XmlModelFileCreator {

	public static String[] fields =  new String[] {  "author", "year", "title", "doi", "full citation",
			"order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
			"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked",
			"strain", "strain source",
			"morphology", "phenology",  "habitat", "distribution", };
	
	protected XmlModelMaker modelMaker;
	protected BracketChecker bracketChecker = new BracketChecker();
	protected String[] descriptionTypes = { "morphology", "habitat", "distribution", "phenology" };
	protected String[] nameTypes = { "order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
			"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked" };
	protected Set<String> allLabels = new HashSet<String>();
	
	public XmlModelFileCreator(XmlModelMaker modelMaker) {
		this.modelMaker = modelMaker;
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
		
		List<OtherInfoOnMeta> otherInfoOnMetas = new LinkedList<OtherInfoOnMeta>();
		if(data.containsKey("doi") && data.get("doi") != null && !data.get("doi").trim().isEmpty()) {
			OtherInfoOnMeta otherInfoOnMeta = modelMaker.makeOtherInfoOnMeta();
			otherInfoOnMeta.setText("doi: " + data.get("doi"));
			otherInfoOnMetas.add(otherInfoOnMeta);
		}
		if(data.containsKey("full citation") && data.get("full citation") != null && !data.get("full citation").trim().isEmpty()) {
			OtherInfoOnMeta otherInfoOnMeta = modelMaker.makeOtherInfoOnMeta();
			otherInfoOnMeta.setText("full citation: " + data.get("full citation"));
			otherInfoOnMetas.add(otherInfoOnMeta);
		}
			
		//fill all data
		Treatment treatment = modelMaker.makeTreatment();
		ProcessedBy processedBy = modelMaker.makeProcessedBy();
		String formattedDate = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL).format(new Date());
		processedBy.getProcessor().get(0).setDate(formattedDate);
		processedBy.getProcessor().get(0).setOperator(username);
		processedBy.getProcessor().get(0).getSoftware().setType("Semantic Markup Input Generator");
		processedBy.getProcessor().get(0).getSoftware().setVersion("1.0");
		
		treatment.getMeta().setProcessedBy(processedBy);
		treatment.getMeta().setOtherInfoOnMeta(otherInfoOnMetas);
		treatment.getMeta().getSource().setAuthor(data.get("author"));
		treatment.getMeta().getSource().setDate(data.get("year"));
		treatment.getMeta().getSource().setTitle(data.get("title"));
		treatment.getTaxonIdentification().setFamilyName(data.get("family"));
		treatment.getTaxonIdentification().setFormaName(data.get("forma"));
		treatment.getTaxonIdentification().setGenusName(data.get("genus"));
		treatment.getTaxonIdentification().setOrderName(data.get("order"));
		treatment.getTaxonIdentification().setSectionName(data.get("section"));
		treatment.getTaxonIdentification().setSeriesName(data.get("series"));
		treatment.getTaxonIdentification().setSpeciesName(data.get("species"));
		treatment.getTaxonIdentification().setStrainName(data.get("strain"));
		treatment.getTaxonIdentification().setStrainSource(data.get("strain source"));
		treatment.getTaxonIdentification().setSubgenusName(data.get("subgenus"));
		treatment.getTaxonIdentification().setVarietyName(data.get("variety"));
		treatment.getTaxonIdentification().setUnrankedName(data.get("unkranked"));
		treatment.getTaxonIdentification().setTribeName(data.get("tribe"));
		treatment.getTaxonIdentification().setSuperfamilyName(data.get("superfamily"));
		treatment.getTaxonIdentification().setSubtribeName(data.get("subtribe"));
		treatment.getTaxonIdentification().setSubspeciesName(data.get("subspecies"));
		treatment.getTaxonIdentification().setSubsectionName(data.get("subsection"));
		treatment.getTaxonIdentification().setSuborderName(data.get("suborder"));
		treatment.getTaxonIdentification().setSubfamilyName(data.get("subfamily"));
		treatment.getTaxonIdentification().setStatus("ACCEPTED");
		
		for(String descriptionType : descriptionTypes) {
			if(data.containsKey(descriptionType)) {
				String descriptionText = data.get(descriptionType);
				if(descriptionText != null) {
					String[] paragraphs = descriptionText.split("\n");
					for(String paragraph : paragraphs) {
						Description description = modelMaker.makeDescription();
						description.setText(paragraph);
						description.setDescriptionType(descriptionType);
						treatment.getDescriptions().add(description);
						
						String bracketError = bracketChecker.checkBrackets(paragraph, descriptionType);
						if(!bracketError.isEmpty())
							modelFile.appendError(bracketError.substring(0, bracketError.length() - 2));
					}
				}
			}
		}

		modelFile.setFileName(createFileName(data, modelFile));
		modelFile.setTreatment(treatment);
		return modelFile;
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
