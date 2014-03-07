package edu.arizona.biosemantics.etcsite.shared.file.semanticmarkup;

import java.util.List;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public class XmlModel {
		
	public interface Treatment {
		@PropertyName("meta")
		Meta getMeta();
		@PropertyName("taxon_identification")
		TaxonIdentification getTaxonIdentification();
		@PropertyName("description")
		List<Description> getDescriptions();
		
		@PropertyName("meta")
		void setMeta(Meta meta);
		@PropertyName("taxon_identification")
		void setTaxonIdentification(TaxonIdentification tid);
		@PropertyName("description")
		void setDescriptions(List<Description> descriptions);
	}
	
	public interface Meta {
		@PropertyName("source")
		Source getSource();
		@PropertyName("source")
		void setSource(Source source);
	}
		 	
	public interface Source{
		@PropertyName("author")
		String getAuthor();
		@PropertyName("date")
		String getDate();
		@PropertyName("title")
		String getTitle();
		
		@PropertyName("author")
		void setAuthor(String author);
		@PropertyName("date")
		void setDate(String date);
		@PropertyName("title")
		void setTitle(String title);
	}
	
	public interface TaxonIdentification{
		@PropertyName("@status")
		String getStatus();
		@PropertyName("order_name")
		String getOrderName();
		@PropertyName("suborder_name")
		String getSuborderName();
		@PropertyName("superfamily_name")
		String getSuperfamilyName();
		@PropertyName("family_name")
		String getFamilyName();
		@PropertyName("subfamily_name")
		String getSubfamilyName();
		@PropertyName("tribe_name")
		String getTribeName();
		@PropertyName("subtribe_name")
		String getSubtribeName();
		@PropertyName("genus_name")
		String getGenusName();
		@PropertyName("subgenus_name")
		String getSubgenusName();
		@PropertyName("section_name")
		String getSectionName();
		@PropertyName("subsection_name")
		String getSubsectionName();
		@PropertyName("series_name")
		String getSeriesName();
		@PropertyName("species_name")
		String getSpeciesName();
		@PropertyName("subspecies_name")
		String getSubspeciesName();
		@PropertyName("variety_name")
		String getVarietyName();
		@PropertyName("forma_name")
		String getFormaName();
		@PropertyName("unranked_name")
		String getUnrankedName();
		@PropertyName("strain_name")
		String getStrainName();
		@PropertyName("strain_source")
		String getStrainSource();
		
		@PropertyName("@status")
		String setStatus(String status);
		@PropertyName("order_name")
		void setOrderName(String ordername);
		@PropertyName("suborder_name")
		void setSuborderName(String subordername);
		@PropertyName("superfamily_name")
		void setSuperfamilyName(String superfamilyname);
		@PropertyName("family_name")
		void setFamilyName(String familyname);
		@PropertyName("subfamily_name")
		void setSubfamilyName(String subfamilyname);
		@PropertyName("tribe_name")
		void setTribeName(String tribename);
		@PropertyName("subtribe_name")
		void setSubtribeName(String subtribename);
		@PropertyName("genus_name")
		void setGenusName(String gname);
		@PropertyName("subgenus_name")
		void setSubgenusName(String subgenusname);
		@PropertyName("section_name")
		void setSectionName(String sectionname);
		@PropertyName("subsection_name")
		void setSubsectionName(String subsectionname);
		@PropertyName("series_name")
		void setSeriesName(String seriesname);
		@PropertyName("species_name")
		void setSpeciesName(String sname);
		@PropertyName("subspecies_name")
		void setSubspeciesName(String subspeciesname);
		@PropertyName("variety_name")
		void setVarietyName(String varietyname);
		@PropertyName("forma_name")
		void setFormaName(String formaname);
		@PropertyName("unranked_name")
		void setUnrankedName(String unranked);
		@PropertyName("strain_name")
		void setStrainName(String strainname);
		@PropertyName("strain_source")
		void setStrainSource(String strainsource);
	}
	
	public interface Description{
		@PropertyName("@type")
		String getDescriptionType();
		@PropertyName("")
		String getText();
		
		@PropertyName("@type")
		void setDescriptionType(String type);
		@PropertyName("")
		void setText(String text);
	}

}
