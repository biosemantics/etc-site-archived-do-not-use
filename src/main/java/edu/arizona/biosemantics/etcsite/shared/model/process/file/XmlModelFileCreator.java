package edu.arizona.biosemantics.etcsite.shared.model.process.file;

import java.util.LinkedList;
import java.util.List;

/**
 * The shared part of XmlModelFileCreator, necessary to split text already on client side into treatments
 * in order to show progress.
 * @author rodenhausen
 *
 */
public class XmlModelFileCreator {

	protected String[] descriptionTypes = { "morphology", "habitat", "distribution", "phenology" };
	
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
						if(descriptionType.equals(key)) {
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

	public String copyAuthorityAndDate(String normalizedText) {
		String result = "";
		String authorityDate = "unspecified, unspecified";
		for(String line : normalizedText.split("\n")) {
			line = line.trim();
			if(line.contains("name:") || line.contains("name :")){
				String newAuthorityDate = getAuthorityDate(line);
				if(newAuthorityDate!=null){
					authorityDate = newAuthorityDate;
				}else{
					line = line.concat(" "+authorityDate);
				}
			}
			result = result.concat(line+"\n");
		}
		return result;
	}

	private String getAuthorityDate(String line) {
		String colonSplits[] = line.split(":");
		if(colonSplits.length>1){
			colonSplits[1] = colonSplits[1].trim();
			String names[] = colonSplits[1].split(" ", 2);
			if(names.length>1){
				return names[1];
			}
		}
		return null;
	}

}
