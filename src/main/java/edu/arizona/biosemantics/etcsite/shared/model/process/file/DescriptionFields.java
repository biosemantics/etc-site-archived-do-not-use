package edu.arizona.biosemantics.etcsite.shared.model.process.file;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.common.taxonomy.Rank;

public class DescriptionFields {

	public static Set<String> getAll() {
		Set<String> result = new HashSet<String>();
		String[] fields =  new String[] {"author", "year", "title", "doi", "full citation",
				/*"order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", 
				"section", "subsection", "series", "species", "subspecies", "variety", "forma", "unranked",*/
				"strain number","equivalent strain numbers", "accession number 16s rrna", "accession number genome sequence",
				"previous or new taxonomic names","morphology", "phenology",  "habitat", "distribution" };
		result.addAll(Arrays.asList(fields));
		for(Rank rank : Rank.values())
			result.add(rank.name().toLowerCase() + " name");
		return result;
	}
	
}
