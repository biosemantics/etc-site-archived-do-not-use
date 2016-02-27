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
						return "ERROR";
					}
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
		}
		return null;
	}

	public List<String> getTreatmentTexts(Map<String, String> batchSourceDocumentInfoMap, String text) {
		List<String> result = new LinkedList<String>();
		
		boolean insideContinuousValue = false;
		
		List<String> treatmentTexts = new LinkedList<String>();
		StringBuilder treatment = new StringBuilder();
		for(String line : text.split("\n")) {
			line = line.trim();
			if(line.length()==0 && !insideContinuousValue) {
				treatmentTexts.add(treatment.toString().trim());
				treatment = new StringBuilder();
			} else {
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
		treatmentTexts.add(treatment.toString().trim());
				
		for(String treatmentText : treatmentTexts) {
			treatmentText = completeTreatmentWithSourceDocumentInformation(batchSourceDocumentInfoMap, treatmentText);
			result.add(treatmentText);
		}
		
		return result;
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
			String value = values.get(j);
			treatment.append(key + ": " + value + "\n");
		}
		return treatment.toString();
	}


	public boolean validateTaxonNames(String normalizedText) {
		for(String line : normalizedText.split("\n")) {
			line = line.trim();
			if(line.contains("name:") || line.contains("name :")){
				if(!validateName(line.split(":")[1].trim())){
					return false;
				}
			}
		}
		return true;
	}

}
