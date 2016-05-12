package edu.arizona.biosemantics.etcsite.shared.model.process.file;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.core.java.util.Arrays;

/**
 * The shared part of XmlModelFileCreator, necessary to split text already on client side into treatments
 * in order to show progress.
 * @author rodenhausen
 *
 */
public class XmlModelFileCreator {

	
	protected String[] descriptionTypes = { "morphology", "habitat", "distribution", "phenology" };
	protected Set<String> descriptionTypesSet;
	
	public XmlModelFileCreator() {
		descriptionTypesSet = new HashSet<String>();
		for(String descriptionType : descriptionTypes)
			descriptionTypesSet.add(descriptionType);
	}
	
	public String normalizeText(String text) {
		text = text.trim();
		text = text.replaceAll("\\r\\n", "\n");
		text = text.replaceAll("\\r", "\n"); 
		text = text.replaceAll("\\n{3,}", "\n\n");
		return text;
	}
	
	public List<String> getTreatmentTexts(String text) {
		List<String> result = new LinkedList<String>();
		
		boolean insideContinuousValue = false;
		
		StringBuilder treatment = new StringBuilder();
		for(String line : text.split("\n")) {
			line = line.trim();
			if(line.length()==0 && !insideContinuousValue) {
				result.add(treatment.toString());
				treatment = new StringBuilder();
			}
			else {
				treatment.append(line + "\n");
				int colonIndex = line.indexOf(":");
				if(colonIndex == -1 || insideContinuousValue) {
					if(line.endsWith("#"))
						insideContinuousValue = false;
					continue;
				} else {
					String key = line.substring(0, colonIndex).toLowerCase().trim();
					for(String descriptionType : descriptionTypes) {
						if(key.contains(descriptionType)) {
							String value = line.substring(colonIndex + 1, line.length()).trim();
							if(value.startsWith("#")) 
								insideContinuousValue = true;
							if(value.endsWith("#"))
								insideContinuousValue = false;
						}
					}
				}
			}			
		}
		String atreatment = treatment.toString().trim();
		result.add(atreatment); //replace all non-visible characters proceeding/trailing the treatment.
		return result;
	}
	
	public boolean validateName(String name) {
		if(!name.contains(",")){
			return false;
		}else{
			String names[] = name.split(",");
			if(names.length != 2){
				return false;
			}
			String nameAuthority = names[0].trim();
			String date = names[1].trim();
			if(date.contains(" ")){
				return false;
			}
			if(!nameAuthority.contains(" ")){
				return false;
			}
		}
		return true;
	}

	public String copyAuthorityAndDate(String normalizedText) {
		String result = "";
		String authorityDate = null;
		int lineNumber = 1;
		for(String line : normalizedText.split("\n")) {
			line = line.trim();
			if(line.contains("name:") || line.contains("name :")){
				String newAuthorityDate = getAuthorityDate(line);
				if(newAuthorityDate!=null){
					authorityDate = newAuthorityDate;
				}else{
					if(authorityDate == null){
						return "ERROR"+ line;
					}
					else
					    line = line.concat(" "+authorityDate);
				}
			}
			result = result.concat(line+"\n");
			lineNumber++;
		}
		return result;
	}

	private String getAuthorityDate(String line) {
		String colonSplits[] = line.split(":");
		if(colonSplits.length > 1){
			colonSplits[1] = colonSplits[1].trim();
			String names[] = colonSplits[1].split(" ", 2);
			if(names.length > 1){
				return names[1];
			}
			else return null;
		}
		return null;
	}

	public List<String> getTreatmentTexts(Map<String, String> batchSourceDocumentInfoMap, String text) throws Exception {
		List<String> result = new LinkedList<String>();
		
		boolean insideContinuousValue = false;
		
		List<String> treatmentTexts = new LinkedList<String>();
		List<String> treatmentLines = new LinkedList<String>();
		for(String line : text.split("\n")) {
			line = line.trim();
			
			if(line.length()==0 && !insideContinuousValue) {
				treatmentTexts.add(collapseToString(treatmentLines));
				treatmentLines = new LinkedList<String>();
			} else {
				addTreatmentLine(treatmentLines, line);
				int colonIndex = line.indexOf(":");
				if(colonIndex == -1 || insideContinuousValue) {
					if(line.endsWith("#"))
						insideContinuousValue = false;
					continue;
				} else {
					String key = line.substring(0, colonIndex).toLowerCase().trim();
					for(String descriptionType : descriptionTypes) {
						if(key.contains(descriptionType)) {
							String value = line.substring(colonIndex + 1, line.length()).trim();
							if(value.startsWith("#")) 
								insideContinuousValue = true;
							if(value.endsWith("#"))
								insideContinuousValue = false;
						}
					}
				}
			}
		}
		treatmentTexts.add(collapseToString(treatmentLines));
				
		for(String treatmentText : treatmentTexts) {
			treatmentText = completeTreatmentWithSourceDocumentInformation(batchSourceDocumentInfoMap, treatmentText);
			result.add(treatmentText);
		}
		
		return result;
	}

	private void addTreatmentLine(List<String> treatmentLines, String line) throws Exception {		
		if(treatmentLines.isEmpty()) {
			int colonIndex = line.indexOf(":");
			if(colonIndex == -1) {
				throw new Exception("A treatment does not begin with a valid field. "
						+ "Did you forget to delimit morphology text by #? The problem occurs in line \"" + 
						(line.length() > 50 ? line.substring(0, 50) : line) + "\"");
			} else {
				String key = line.substring(0, colonIndex).toLowerCase().trim();
				if(!DescriptionFields.getAll().contains(key))
					throw new Exception("A treatment does not begin with a valid field. "
							+ "Did you forget to delimit morphology text by #? The problem occurs in line \"" + 
							(line.length() > 50 ? line.substring(0, 50) : line) + "\"");
			}
		}
		treatmentLines.add(line);
	}

	private String collapseToString(List<String> lines) {
		StringBuilder treatment = new StringBuilder();
		for(String line : lines) 
			treatment.append(line + "\n");
		return treatment.toString().trim();
	}

	private String completeTreatmentWithSourceDocumentInformation(Map<String, String> batchSourceDocumentInfoMap, String treatmentText) {
		List<String> keys = new LinkedList<String>();
		List<String> values = new LinkedList<String>();

		boolean insideContinuousValue = false;
		StringBuilder valueBuilder = new StringBuilder();
		for(String line : treatmentText.split("\n")) {
			//System.out.println(line);
			line = line.trim();
			int colonIndex = line.indexOf(":");
			if(colonIndex == -1 && insideContinuousValue) {
				valueBuilder.append(line + "\n");
				if(line.endsWith("#")) {
					insideContinuousValue = false;
					values.add(valueBuilder.toString());
				}
				continue;
			} else {
				if(insideContinuousValue) {
					String value = line;
					valueBuilder.append(value + "\n");
					if(value.endsWith("#")) {
						insideContinuousValue = false;
						values.add(valueBuilder.toString());
					}
				} else {
					if (colonIndex==-1) continue;
					String key = line.substring(0, colonIndex).toLowerCase().trim();
					String value = line.substring(colonIndex + 1, line.length()).trim();
					keys.add(key);
					for(String descriptionType : descriptionTypes) {
						if(key.contains(descriptionType)) {
							if(value.startsWith("#")) 
								insideContinuousValue = true;
							if(value.endsWith("#"))
								insideContinuousValue = false;
						}
					}
					if(!insideContinuousValue)
						values.add(value);
					else
						valueBuilder.append(value + "\n");
				}
			}
		}
		
		int i=0;
		for(String key : batchSourceDocumentInfoMap.keySet()) {
			if(!keys.contains(key) && !batchSourceDocumentInfoMap.get(key).isEmpty()) {
				keys.add(i, key);
				values.add(i, batchSourceDocumentInfoMap.get(key));
				i++;
			}
		}

		StringBuilder treatment = new StringBuilder();
		for(int j=0; j<keys.size(); j++) {
			String key = keys.get(j);
			if(values.size()<=j) continue;
			String value = values.get(j);
			treatment.append(key + ": " + value + "\n");
		}
		return treatment.toString();
	}


	public void validateTaxonNamesFormat(String text) throws Exception {
		for(String line : text.split("\n")) {
			line = line.trim();
			if(line.contains("name:") || line.contains("name :")){
				String colonSplits[] = line.split(":");
				if(colonSplits.length > 1){	
					if(!validateName(colonSplits[1].trim()))
						throw new Exception(line);
					
				} else 
					throw new Exception(line);
			}
		}
	}
	
	public void validateTaxonNames(String text) throws Exception {
		List<String> treatmentTexts = this.getTreatmentTexts(text);
		Set<String> taxonNameIds = new HashSet<String>();
		for(String treatmentText : treatmentTexts) {
			String taxonNameId = this.getTaxonNameID(treatmentText);
			if(!taxonNameIds.contains(taxonNameId))
				taxonNameIds.add(taxonNameId);
			else
				throw new Exception(taxonNameId);
		}	
	}

	private String getTaxonNameID(String treatmentText) {
		String id = "";
		for(String line : treatmentText.split("\n")) {
			line = line.trim();
			if(line.contains("name:") || line.contains("name :")){
				String colonSplits[] = line.split(":");
				String name = colonSplits[0].trim();
				String nameSplit[] = name.split(" ");
				String rank = nameSplit[0].trim();
				String value = colonSplits[1].trim();
				String valueSplit[] = value.split(",");
				String valueAuthority = valueSplit[0].trim();
				String date = valueSplit[1].trim();
				String valueAuthoritySplit[] = valueAuthority.split(" ");
				value = valueAuthoritySplit[0].trim();
				String authority = valueAuthoritySplit[1].trim();
				
				if(!id.isEmpty())
					id += "_";
				id += rank + "_" + value + "_" + authority + "_" + date;
			}
		}
		return id;
	}

}
